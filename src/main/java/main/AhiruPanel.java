package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import entities.*;
import userinterface.*;
import utilities.*;
import entities.Gun;

public class AhiruPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 1L;
	private final static int PWIDTH = 715;
	private final static int PHEIGHT = 630;
	private static final int NO_DELAYS_PER_YIELD = 16;
	private final int MAX_FRAME_SKIPS = 5; 
	
	private final int GUN_CENTER_A = 0;
	private final int GUN_LEFT_1 = 1;
	private final int GUN_LEFT_2 = 2;
	private final int GUN_LEFT_3 = 3;
	private final int GUN_LEFT_4 = 4;
	private final int GUN_LEFT_5 = 5;
	private final int GUN_RIGHT_1 = 6;
	private final int GUN_RIGHT_2 = 7;
	private final int GUN_RIGHT_3 = 8;
	private final int GUN_RIGHT_4 = 9;
	private final int GUN_RIGHT_5 = 10;
	
	private final int GUN_LANE_1 = 0;
	private final int GUN_LANE_2 = 1;
	private final int GUN_LANE_3 = 2;
	private final int GUN_LANE_4 = 3;
	
	private final int SCENE_SPLASH = 1;
	private final int SCENE_LEVEL_SELECTION = 2;
	private final int SCENE_LEVEL = 3;

	private double rate = 0.0;
	private double progress = 0.0;
	private long period = 10000000L;
	private long sleepTime = 0;
	private int currentLevel = 0;
	private int scene = 0;
	private int userScore = 0;
	private int userScoreOfFive = 0;
	private int missed = 0;
	private int gunState = 0;
	private int gunLane = 0;
	private int currentTargetY = 0;
	private int noOfShots = 0;
	private int yOfMouse = 0;

	private Thread animator;
	private Graphics dbg;
	private Image dbImage = null;
	private Random rand = new Random();
	
	private volatile boolean running = false;
	private volatile boolean isCurrentLevelEnded = false;
	private volatile boolean gameStarted = false;
	private volatile boolean isPlaying = false;
	private volatile boolean gameOver = false;
	private volatile boolean isPaused = false;
	private volatile boolean isInstructionsOpen = false;
	private volatile boolean mouseOutOfBounds = false;
	
	//entities
	private WorldCreation worldCreated;
	
	private List<Gun> currentGun;
	private List<Gun> gunA;
	private List<Gun> gunB;
	private List<Gun> gunC;
	private List<Ammo> shots;
	private List<Score> scoreBoard;
	private HashMap<String,Integer> iceTimer;
	private int[] starCount;
	private HashMap<Integer, Stars[]> starMap;
	
	private Ammo currentAmmo;
	private int currentAmmoNumber;
	private int openedLevels = 1;
	private Ammo ball = new AmmoBall(0,0);
	private Ammo ap = new AmmoAP(0,0);
	private Ammo ice = new AmmoIce(0,0);
		
	private ImageLoader imgLoader;
	private SoundLoader soundLoader;
	
	private static int loaded = 0;
	private BufferedImage loadingScreen;
	private BufferedImage[] loadingDuck = new BufferedImage[7];
	
	private Point gunMouth;
	
	public AhiruPanel(){
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT)); //sets the panel's size
		setBackground(Color.WHITE);//set the bg to white
		setFocusable(true);//permits the handling events to the panel
		requestFocus();
		
		initLoadingScreen();

		shots = new ArrayList<Ammo>(); 
		scoreBoard = new ArrayList<Score>();
		iceTimer = new HashMap<String,Integer>();
		
		worldCreated = new WorldCreation();
		soundLoader = new SoundLoader();
		soundLoader.playFX("crowd");

		//for debugging purposes only
		//to skip splashscreen and level selection
		//gameStarted = true;
		//isPlaying = true;
		//currentLevel = 1 ;

		//scene = SCENE_LEVEL;
		scene = SCENE_SPLASH;
		
		worldCreated.load(scene, currentLevel);
		
		currentAmmo = ball;
		currentAmmoNumber = 2;
		gunLane = GUN_LANE_1;

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e){

				if(e.getButton() == MouseEvent.BUTTON1){
					int mouseX = e.getX();//current mouse x
					int mouseY = e.getY();//current mouse y
					
					//create a rectangle for collision detections
					Rectangle mouseRec = new Rectangle(mouseX, mouseY, 1, 1);
					
					if(scene == SCENE_SPLASH && mouseRec.intersects(worldCreated.getMenu().getBounds())){
						//splashscreen: checks if the user clicked the play button

						soundLoader.playFX("pressed");
						
						worldCreated.unload(scene);
						worldCreated.load(SCENE_LEVEL_SELECTION, currentLevel);
						scene = SCENE_LEVEL_SELECTION;
						gameStarted = true;
						
						reloadRatings();
					}else if(scene == SCENE_LEVEL_SELECTION){
						//levelselect: what level does the user want to play
						openedLevels = SaveAndLoad.getLevels();
						currentAmmo = ball;

						if(mouseRec.intersects(worldCreated.getLevels().get(0).getBounds()) && openedLevels > 0 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 1;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(1).getBounds()) && openedLevels > 1 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 2;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(2).getBounds()) && openedLevels > 2 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 3;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(3).getBounds()) && openedLevels > 3 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 4;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(4).getBounds()) && openedLevels > 4 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 5;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(5).getBounds()) && openedLevels > 5 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 6;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(6).getBounds()) && openedLevels > 6 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 7;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(7).getBounds()) && openedLevels > 7 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 8;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(8).getBounds()) && openedLevels > 8 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 9;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(9).getBounds()) && openedLevels > 9 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 10;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(10).getBounds()) && openedLevels > 10 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 11;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getLevels().get(11).getBounds()) && openedLevels > 11 && !isInstructionsOpen){
							soundLoader.playFX("pressed");
							currentLevel = 12;
							unloadAndLoad();
							resumeGame();
						}else if(mouseRec.intersects(worldCreated.getBackground().getBounds2())){
							isInstructionsOpen = true;
							worldCreated.getInstruction().setCurrentPage(1);
						}
						
						if(isInstructionsOpen){
							Instruction inst = worldCreated.getInstruction();
							if(mouseRec.intersects(inst.getBounds1())){
								soundLoader.playFX("pressed");
								if(inst.getCurrentPage()==1){
									inst.setCurrentPage(6);
								}else{
									inst.setCurrentPage(inst.getCurrentPage()-1);
								}
								inst.setImage("inst"+inst.getCurrentPage());
							}
							
							else if(mouseRec.intersects(inst.getBounds3())){
								soundLoader.playFX("pressed");
								if(inst.getCurrentPage()==6){
									inst.setCurrentPage(1);
								}else{
									inst.setCurrentPage(inst.getCurrentPage()+1);
								}

								inst.setImage("inst"+inst.getCurrentPage());
							}else if(mouseRec.intersects(inst.getBounds2())){
								soundLoader.playFX("pressed");
								isInstructionsOpen = false;
							}
						}
					}else if(scene == SCENE_LEVEL && !mouseOutOfBounds && !isCurrentLevelEnded && !isPaused){
							//create shot and its path
						
							int bullsEyeX = worldCreated.getBullsEye().getX();
							int bullsEyeY = worldCreated.getBullsEye().getY();
							
							if(worldCreated.getBackground().getBounds1().contains(mouseRec)){
								pauseGame();
								soundLoader.playFX("pressed");
							}
							else if(worldCreated.getAmmos().get(2).getBounds().contains(mouseRec)){
								currentAmmo = ball;
								currentAmmoNumber = 2;
								soundLoader.playFX("changeAmmo");
							}
							else if(worldCreated.getAmmos().get(1).getBounds().contains(mouseRec)){
								currentAmmo = ap;
								currentAmmoNumber = 1;
								soundLoader.playFX("changeAmmo");
							}
							else if(worldCreated.getAmmos().get(0).getBounds().contains(mouseRec)){
								currentAmmo = ice;
								currentAmmoNumber = 0;
								soundLoader.playFX("changeAmmo");
							}
							else if(worldCreated.getAmmoStorage().get(currentAmmoNumber) > 0){
								
								if(currentAmmo == ball){
	
									soundLoader.playFX("ball");
									shots.add(new AmmoBall(0,0));
									shots.get(noOfShots).setRectangleWidth(10);
									shots.get(noOfShots).setRectangleHeight(10);
									shots.get(noOfShots).create("ball");
									shots.get(noOfShots).setPower(1);
								}else if(currentAmmo == ap){
	
									soundLoader.playFX("ap");
									shots.add(new AmmoAP(0,0));
									shots.get(noOfShots).setRectangleWidth(10);
									shots.get(noOfShots).setRectangleHeight(10);
									shots.get(noOfShots).create("ap");
									shots.get(noOfShots).setPower(2);
								}else if(currentAmmo == ice){
	
									soundLoader.playFX("ice");
									shots.add(new AmmoIce(0,0));
									shots.get(noOfShots).setRectangleWidth(10);
									shots.get(noOfShots).setRectangleHeight(10);
									shots.get(noOfShots).create("ice");
									shots.get(noOfShots).setPower(1);
								}
								
								shots.get(noOfShots).setX(gunMouth.x);
								shots.get(noOfShots).setY(gunMouth.y);
		
								shots.get(noOfShots).setPath(getLinePath(gunMouth.x, gunMouth.y, bullsEyeX, bullsEyeY));
								shots.get(noOfShots).setPathIterator(0);
								
								noOfShots++;
							}		
							
							if(currentAmmo == ice && worldCreated.getAmmoStorage().get(0) <= 0){
	
								if(worldCreated.getAmmoStorage().get(1) > 0){
									currentAmmo = ap;
									currentAmmoNumber = 1;
								}else{
									currentAmmo = ball;
									currentAmmoNumber = 2;
								}
							}
							
							if(currentAmmo == ap && worldCreated.getAmmoStorage().get(1) <= 0){
	
								if(worldCreated.getAmmoStorage().get(2) > 0){
									currentAmmo = ball;
									currentAmmoNumber = 2;
								}else{
									currentAmmo = ice;
									currentAmmoNumber = 0;
								}
							}
	
							if(currentAmmo == ball && worldCreated.getAmmoStorage().get(2) <= 0){
	
								if(worldCreated.getAmmoStorage().get(0) > 0){
									currentAmmo = ice;
									currentAmmoNumber = 0;
								}else{
									currentAmmo = ap;
									currentAmmoNumber = 1;
								}
							}
						}
						
						if(isCurrentLevelEnded && isPlaying){
	
							if(worldCreated.getScoreBoard().getBounds1().contains(mouseRec)){
								soundLoader.playFX("pressed");
								nullify();
								worldCreated.load(scene, currentLevel);
								isCurrentLevelEnded = false;
								isPlaying = true;
	
								currentAmmo = ball;
								currentAmmoNumber = 2;
								resumeGame();
								
							}else if(worldCreated.getScoreBoard().getBounds2().contains(mouseRec)){
								soundLoader.playFX("pressed");
								nullify();
								
								worldCreated.load(SCENE_LEVEL_SELECTION, currentLevel);
								scene = SCENE_LEVEL_SELECTION;
	
	
								currentAmmo = ball;
								currentAmmoNumber = 2;
								reloadRatings();
							}
						}
						
						if(isPaused){
	
							if(worldCreated.getPauseBoard().getBounds2().contains(mouseRec)){
								soundLoader.playFX("pressed");
								nullify();
								worldCreated.load(scene, currentLevel);
								
								isCurrentLevelEnded = false;
								isPlaying = true;
								resumeGame();
	
								currentAmmo = ball;
								currentAmmoNumber = 2;
								
							}else if(worldCreated.getPauseBoard().getBounds3().contains(mouseRec)){
								soundLoader.playFX("pressed");
								nullify();
								
								worldCreated.load(SCENE_LEVEL_SELECTION, currentLevel);
								scene = SCENE_LEVEL_SELECTION;
								resumeGame();
	
								currentAmmo = ball;
								currentAmmoNumber = 2;
								reloadRatings();
								
							}else if(worldCreated.getPauseBoard().getBounds1().contains(mouseRec)){
								soundLoader.playFX("pressed");
								resumeGame();
							}
						}
					}
			}

			private void unloadAndLoad() {
				worldCreated.unload(scene);
				worldCreated.load(SCENE_LEVEL, currentLevel);
				
				gunA = worldCreated.getGunA();
				gunB = worldCreated.getGunB();
				gunC = worldCreated.getGunC();
				
				currentGun = gunA;
				
				isCurrentLevelEnded = false;
				scene =  SCENE_LEVEL;
				isPlaying = true;
			}

			private void nullify() {
				isPlaying = false;
				
				userScore = 0;
				userScoreOfFive = 0;
				missed = 0;
				rate = 0;
				noOfShots = 0;
				shots.clear();
				scoreBoard.clear();
				gunLane = GUN_LANE_1;

				worldCreated.unload(scene);
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e){
				int mouseX = e.getX();//current mouse x
				int mouseY = e.getY();//current mouse y

				yOfMouse = e.getY();
				if(isPlaying && !isCurrentLevelEnded){
					if(!isPaused){
						if(mouseX < 350 && mouseX >= 300){//CENTER
							gunState = GUN_CENTER_A;
							gunMouth = new Point(335,490);
						}else if(mouseX < 300 && mouseX >= 250){//LEFT STARTS HERE
							gunState = GUN_LEFT_1;
							gunMouth = new Point(320,495);
						}else if(mouseX < 250 && mouseX >= 200){
							gunState = GUN_LEFT_2;
							gunMouth = new Point(305,500);
						}else if(mouseX < 200 && mouseX >= 150){
							gunState = GUN_LEFT_3;
							gunMouth = new Point(290,505);
						}else if(mouseX < 150 && mouseX >= 100){
							gunState = GUN_LEFT_4;
							gunMouth = new Point(275,510);
						}else if(mouseX < 100 && mouseX >= 50){
							gunState = GUN_LEFT_5;
							gunMouth = new Point(260,515);
							
						}else if(mouseX > 350 && mouseX < 400){ //RIGHT STARTS HERE
							gunState = GUN_RIGHT_1;
							gunMouth = new Point(350,495);
						}else if(mouseX > 400 && mouseX < 450){
							gunState = GUN_RIGHT_2;
							gunMouth = new Point(365,500);
						}else if(mouseX > 450 && mouseX < 500){
							gunState = GUN_RIGHT_3;
							gunMouth = new Point(380,505);
						}else if(mouseX > 400 && mouseX < 550){
							gunState = GUN_RIGHT_4;
							gunMouth = new Point(395,510);
						}else if(mouseX > 550 && mouseX < 600){
							gunState = GUN_RIGHT_5;
							gunMouth = new Point(410,515);
						}	
						
						if(mouseY > 400)
							worldCreated.getBullsEye().setX(mouseX - 10);
					}
				}
			}
		});
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_SPACE){//q to pause
					pauseGame();
				}else if(keyCode == KeyEvent.VK_W && !isPaused){
					//control the crosshair thru W and S

					if(gunLane < 3){
						gunLane++;
					}else if(gunLane >= 3){
						gunLane = 0;
					}
					
					if(gunLane == GUN_LANE_1){
						currentGun = gunA;
						currentTargetY = 400;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_2){
						currentGun = gunB;
						currentTargetY = 300;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_3){
						currentGun = gunC;
						currentTargetY = 200;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_4){
						currentGun = gunC;
						currentTargetY = 130;
						worldCreated.getBullsEye().setY(currentTargetY);
					}
					
				}else if(keyCode == KeyEvent.VK_S && !isPaused){
					//control the crosshair thru W and S
					if(gunLane > 0){
						gunLane--;
					}else if(gunLane <= 0){
						gunLane = 3;
					}
					
					if(gunLane == GUN_LANE_1){
						currentGun = gunA;
						currentTargetY = 400;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_2){
						currentGun = gunB;
						currentTargetY = 300;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_3){
						currentGun = gunC;
						currentTargetY = 200;
						worldCreated.getBullsEye().setY(currentTargetY);
					}else if(gunLane == GUN_LANE_4){
						currentGun = gunC;
						currentTargetY = 130;
						worldCreated.getBullsEye().setY(currentTargetY);
					}

				}
			}
			
		});
	}//end of constructor

	private void initLoadingScreen() {
		imgLoader = new ImageLoader("loading.txt");
		loadingScreen = imgLoader.getImage("loading");
		loadingDuck[0] = imgLoader.getImage("loadingDuck1");
		loadingDuck[1] = imgLoader.getImage("loadingDuck2");
		loadingDuck[2] = imgLoader.getImage("loadingDuck3");
		loadingDuck[3] = imgLoader.getImage("loadingDuck4");
		loadingDuck[4] = imgLoader.getImage("loadingDuck5");
		loadingDuck[5] = imgLoader.getImage("loadingDuck6");
		loadingDuck[6] = imgLoader.getImage("loadingDuck7");
	}
	
	private ArrayList<Point2D> getLinePath(int x1, int y1, int x2, int y2){
		//creates line path between two points
		Line2D line = new Line2D.Double(x1, y1, x2, y2); //create a line
		Point2D current;//point that will be created
		
		ArrayList<Point2D> path = new ArrayList<Point2D>(); // points that came from collected points bet 2 points
		
		for(Iterator<Point2D> iter = new LineIterator(line); iter.hasNext();){
			//look for all the points, assign it to the point will be created
			//and add it to the arraylist path
			current = iter.next();
			path.add(current);

		}
		
		//a new path object will be added to the object ArrayList<Path>
		return path;
	}//end of createLinePath(int,int,int,int)
	
	public void addNotify(){
		super.addNotify();
		startGame();
	}//end of addNotify()

	// Initialize and start the thread
	private void startGame()
	{
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start( );
		}
	} // end of startGame( )
	
	// called by the user to stop execution
	public void stopGame()
	{ 
		running = false; 
	} // end of stopGame( )
	
	public void pauseGame(){
		isPaused = true;
	}// end of pauseGame( )
	
	public void resumeGame(){
		isPaused = false;
	}// end of resumeGame( )

	private void calculate(){
		rate = (double)userScore / (double)worldCreated.getTotalDucks(currentLevel);
		
	}
	
	private void collisionDetection(int i, Ammo shot) {
		boolean isHit = false;
		try{
			for (int j = 0; j < worldCreated.getDuckPopulationL(); j++) {
				Duck duck = worldCreated.getDuckLeft().get(j);

				if(duck.getBounds().contains(shot.getBounds()) && duck.isAlive()){
					
					duck.setLife(duck.getLife() - shot.getPower());	
					
					
					if(duck.getLife() == 2){
						soundLoader.playFX("helm");
						duck.create("knightLeft2");
						
					}else if(duck.getLife() == 1){
						soundLoader.playFX("helm");
						duck.create("easyLeft");
						
					}else if(duck.getLife() <= 0){
						duck.setAlive(false);
						
						if(!duck.isAlive()){
							soundLoader.playFX("woodHit");
							scoreIt();
						}
					}
					isHit = true;
					shotRemover(i,shot);
				}
				
			}

			for (int j = 0; j < worldCreated.getDuckPopulationR(); j++) {
				Duck duck = worldCreated.getDuckRight().get(j);
				
				if(duck.getBounds().contains(shot.getBounds()) && duck.isAlive()){
					
					duck.setLife(duck.getLife() - shot.getPower());		

					
					if(duck.getLife() == 2){
						soundLoader.playFX("helm");
						duck.create("knightRight2");
						
					}if(duck.getLife() == 1){
						duck.create("easyRight");
						soundLoader.playFX("helm");
						
					}else if(duck.getLife() <= 0){
						duck.setAlive(false);
						
						if(!duck.isAlive()){
							scoreIt();
							soundLoader.playFX("woodHit");
						}			
					}

					isHit = true;
					shotRemover(i,shot);
				}
			}
			
			if(!isHit){
				shotRemover(i,shot);
			}

		}catch(Exception e){
			//e.printStackTrace();
		}
	}

	private void scoreIt() {
		
		userScore++;
		int grouping[] = {6, 11, 16, 21, 26, 31, 36, 41, 46, 51, 56, 61, 66, 71, 76, 81, 86, 91, 96, 0};
		int groupingDist[] = {85,120,155,190,225,260, 295, 330, 365, 400, 435, 470, 505, 540, 575, 610};
		
		if(userScore % 5 == 0 && userScore > 0){

			scoreBoard.add(new Score(scoreBoard.get(userScore-5).getX()-5, 65));
			scoreBoard.get(userScore-1).setImage(worldCreated.getScoreFive());
		}else{
			
			if(userScore == 1){
				scoreBoard.add(new Score(50, 60));
				scoreBoard.get(userScore-1).setImage(worldCreated.getScoreStick());
			
			}else if(userScore == grouping[userScoreOfFive]){	

				scoreBoard.add(new Score(groupingDist[userScoreOfFive], 60));
				scoreBoard.get(userScore-1).setImage(worldCreated.getScoreStick());

				userScoreOfFive++;
			}else if(userScore % 5 != 0){
				int beforeScore = (scoreBoard.get(userScore-2).getX()+8);
				
				scoreBoard.add(new Score(beforeScore, 60));
				scoreBoard.get(userScore-1).setImage(worldCreated.getScoreStick());
			}
		}
		
		setGameProgress();
	}

	private void setGameProgress() {
		int tmp = userScore + missed;
		progress = (double)tmp / worldCreated.getTotalDucks(currentLevel);
		progress = 150 * progress;
		
		worldCreated.getGameProgress().setX((int)(660 - progress));
	}

	private void shotRemover(int i, Ammo shot) {
		worldCreated.getAmmoStorage().set(currentAmmoNumber, worldCreated.getAmmoStorage().get(currentAmmoNumber)-1);
		shots.remove(i);
		noOfShots--;
	}

	public static void setLoadingProgress(int inc){
		loaded = loaded + inc;
		
		if(loaded > 6){
			loaded = 0;
		}
	}

	private void gameUpdate(){
		soundLoader.update(null);
		if(!isPaused && !gameOver){
			if(isPlaying && !isCurrentLevelEnded){ //only because all of the animated objects can only be seen on each levels

				if(currentLevel < 10)
					worldCreated.getLvlNo()[0].setImage("levelNumberZero");
				else
					worldCreated.getLvlNo()[0].setImage("levelNumber"+currentLevel/10);
				
				if(currentLevel%10 == 0)
					worldCreated.getLvlNo()[1].setImage("levelNumberZero");
				else	
					worldCreated.getLvlNo()[1].setImage("levelNumber"+(currentLevel%10));
				
				if(yOfMouse > 450){
					mouseOutOfBounds = false;
				}else if(yOfMouse <= 450){
					mouseOutOfBounds = true;
				}
				
				for (int i = 0; i < worldCreated.getDuckPopulationL(); i++) {
					Duck duck = worldCreated.getDuckLeft().get(i);
				
					int currentDuckX = duck.getX();
					boolean isIced = false;
					
					try{
						if(worldCreated.getWaves().get(0).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 375){
							duck.setSpeed(1);
							isIced = true;
						}else if(worldCreated.getWaves().get(1).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 275){
							duck.setSpeed(1);
							isIced = true;
						}else if(worldCreated.getWaves().get(2).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 175){
							duck.setSpeed(1);
							isIced = true;
						}
					}catch(IndexOutOfBoundsException ioe){
						//ioe.printStackTrace();
					}
					
					if(!isIced){
						if(duck.getClass() == DuckClown.class)
							duck.setSpeed(3);
						else if(duck.getClass() == DuckHelm.class)
							duck.setSpeed(2);
						else if(duck.getClass() == DuckKnight.class)
							duck.setSpeed(2);
					}
					
					if(duck.isAlive()){
						duck.setX(currentDuckX-duck.getSpeed());
					}
					
					if(currentDuckX < -100){
						worldCreated.getDuckLeft().remove(i);
						worldCreated.setDuckPopulationL(worldCreated.getDuckPopulationL()-1);
						missed++;

						setGameProgress();
						int boo = rand.nextInt(3)+1;
						soundLoader.playFX("boo"+boo);
					}
				}
				
				for (int i = 0; i < worldCreated.getDuckPopulationR(); i++) {
					Duck duck = worldCreated.getDuckRight().get(i);
					
					int currentDuckX = duck.getX();		
					boolean isIced = false;
					try{
						if(worldCreated.getWaves().get(0).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 375){
							duck.setSpeed(1);
							isIced = true;
						}else if(worldCreated.getWaves().get(1).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 275){
							duck.setSpeed(1);
							isIced = true;
						}else if(worldCreated.getWaves().get(2).isIced() && (duck.getX() >= 0 && duck.getX() <= 700) && duck.getY() == 175){
							duck.setSpeed(1);
							isIced = true;
						}
					}catch(IndexOutOfBoundsException ioe){
						//ioe.printStackTrace();
					}
					
					if(!isIced){
						if(duck.getClass() == DuckClown.class)
							duck.setSpeed(3);
						else if(duck.getClass() == DuckHelm.class)
							duck.setSpeed(2);
					}
					
					if(duck.isAlive()){
						duck.setX(currentDuckX+duck.getSpeed());
					}
					
					if(duck.getX() > 700){
						worldCreated.getDuckRight().remove(i);
						worldCreated.setDuckPopulationR(worldCreated.getDuckPopulationR()-1);
						missed++;

						setGameProgress();
						int boo = rand.nextInt(3)+1;
						soundLoader.playFX("boo"+boo);
					}
				}

				for (int i = 0; i < noOfShots; i++) {

					Ammo shot = shots.get(i);
					
					ArrayList<Point2D> shotPath = shot.getPath();
					int pathIterator = shot.getPathIterator();
					
					if(pathIterator < shotPath.size()) {
						
							shot.setX((int)shotPath.get(pathIterator).getX());
							shot.setY((int)shotPath.get(pathIterator).getY());

							shot.setPathIterator(shot.getPathIterator()+7);

							
							if(shot.getY() <= 410 && shot.getY() > 350 && gunLane == GUN_LANE_1){
								if(currentAmmo == ice && !worldCreated.getWaves().get(0).isIced()){
									worldCreated.getWaves().get(0).setIced(true);
									soundLoader.playFX("iceCracking");
									iceTimer.put("0",0);
								}

								collisionDetection(i, shot);
							}else if(shot.getY() <= 310 && shot.getY() > 250 && gunLane == GUN_LANE_2){
								if(currentAmmo == ice && !worldCreated.getWaves().get(1).isIced()){
									worldCreated.getWaves().get(1).setIced(true);
									soundLoader.playFX("iceCracking");
									iceTimer.put("1",0);
								}

								collisionDetection(i, shot);
							}else if(shot.getY() <= 210 && shot.getY() > 150 && gunLane == GUN_LANE_3){
								if(currentAmmo == ice  && !worldCreated.getWaves().get(2).isIced()){
									worldCreated.getWaves().get(2).setIced(true);
									soundLoader.playFX("iceCracking");
									iceTimer.put("2",0);
								}

								collisionDetection(i, shot);
							}else if(shot.getY() <= 140 && shot.getY() > 0 && gunLane == GUN_LANE_4){

								collisionDetection(i, shot);
							}else if(shot.getPathIterator() >= shot.getPath().size()){

								shotRemover(i, shot);
							}
					}
				}
				
				if(userScore+missed >= worldCreated.getTotalDucks(currentLevel) || worldCreated.getAmmoStorage().get(0) <= 0 &&  worldCreated.getAmmoStorage().get(1) <= 0 
						&&  worldCreated.getAmmoStorage().get(2) <= 0 ){
					calculate();
					isCurrentLevelEnded = true;
					SaveAndLoad.setData(currentLevel, userScore, rate);
					
					if(rate > .5){
						SaveAndLoad.setLevels(SaveAndLoad.getLevels()+1);
					}
					
					SaveAndLoad.save();
					reloadRatings();
					int score = SaveAndLoad.getScoreData().get(currentLevel);
					if(score < 10)
						worldCreated.getBestScore()[0].setImage("levelNumberZero");
					else
						worldCreated.getBestScore()[0].setImage("levelNumber"+score/10);
					
					if(score%10 == 0)
						worldCreated.getBestScore()[1].setImage("levelNumberZero");
					else	
						worldCreated.getBestScore()[1].setImage("levelNumber"+(score%10));
				}
			}

		}
	}//end of gameUpdate()

	private void switcheroo(Duck duck) {
		if(duck.getSpeed() == 3){
			int lanes[] = {375,275,175};
			
			if(duck.getY() == 375){
				if(currentLevel == 9)
					duck.setY(lanes[1]);
				else{
					duck.setY(lanes[rand.nextInt(2)+1]);
				}
					
			}else if(duck.getY() == 275){
				if(currentLevel == 9)
					duck.setY(lanes[0]);
				else{
					if(rand.nextInt(2) == 1)
						duck.setY(lanes[0]);
					else
						duck.setY(lanes[2]);
				}
			}else if(duck.getY() == 175){
			
				if(rand.nextInt(2) == 1)
					duck.setY(lanes[0]);
				else
					duck.setY(lanes[1]);
			
			}
		}
	}
	
	private void gameRender(){
		if(dbImage == null){
			dbImage = createImage(PWIDTH, PHEIGHT);
			
			if(dbImage == null){
				System.out.println("dbImage is null");
				return;
			}else
				dbg = dbImage.getGraphics();
		}
		
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		
		try{

			if(!isPlaying || isCurrentLevelEnded){
				dbg.drawImage(loadingScreen, 0, 0, null);
				dbg.drawImage(loadingDuck[loaded], 0, 0, null);
			}
			
			if(scene == SCENE_SPLASH && !gameStarted){
				//draws only the splashscreen elements				
				worldCreated.getBackground().draw(dbg);				
				worldCreated.getSplashScreen().draw(dbg);
				worldCreated.getMenu().draw(dbg);
				dbg.setColor(Color.BLACK);
				dbg.drawString("Programmer/Artist: John G. Aparejado", 450, 550);
				
				
			}else if(scene == SCENE_LEVEL_SELECTION && gameStarted && !isPlaying){
				//draws only the level select elements
				worldCreated.getBackground().draw(dbg);
				
				for (int i = 0; i < worldCreated.getLevels().size(); i++) {
					worldCreated.getLevels().get(i).draw(dbg);
				}

				for (int i = 0; i < 12; i++) {					
					for (int j = 0; j < starMap.get(i).length; j++) {
						starMap.get(i)[j].draw(dbg);
					}
				}
				
				if(isInstructionsOpen){
					worldCreated.getInstruction().draw(dbg);
				}
				
			}else if(scene == SCENE_LEVEL && gameStarted && isPlaying){
				//draws only the level elements
				worldCreated.getBackground().draw(dbg);
				worldCreated.getWoodUpper().draw(dbg);
				worldCreated.getGameProgress().draw(dbg);
								
				for (int i = 0; i < worldCreated.getDuckPopulationL(); i++) {
					Duck duck = worldCreated.getDuckLeft().get(i);
					
					duck.draw(dbg);
					if(rand.nextInt(250) < 1)
						switcheroo(duck);
					
					if(!duck.isAlive()){
						int state = worldCreated.getStateIteratorL().get(i);
						
						if(state < 12){
							duck.setImage(worldCreated.getDuckStatesL(state/2));
							worldCreated.getStateIteratorL().set(i, state+1);
						}else{
							worldCreated.getDuckLeft().remove(i);
							worldCreated.setDuckPopulationL(worldCreated.getDuckPopulationL()-1);
							worldCreated.getStateIteratorL().remove(i);
						}
					}
					duck = null;
				}
				
				for (int i = 0; i < worldCreated.getDuckPopulationR(); i++) {
					Duck duck = worldCreated.getDuckRight().get(i);
					
					duck.draw(dbg);
					if(rand.nextInt(250) < 1)
						switcheroo(duck);
					
					if(!duck.isAlive()){
						int state = worldCreated.getStateIteratorR().get(i);
						
						if(state < 12){
							duck.setImage(worldCreated.getDuckStatesR(state/2));
							worldCreated.getStateIteratorR().set(i, state+1);
						}else{
							worldCreated.getDuckRight().remove(i);
							worldCreated.setDuckPopulationR(worldCreated.getDuckPopulationR()-1);
							worldCreated.getStateIteratorR().remove(i);
						}
					}
					duck = null;
				}
				
				for (int i = 0; i < worldCreated.getWoods().size(); i++) {
					
					worldCreated.getWoods().get(i).draw(dbg);
					worldCreated.getWaves().get(i).draw(dbg);
					
					if(worldCreated.getWaves().get(i).isIced()){
						worldCreated.getWaves().get(i).setImage("waveIced");
					}else{
						worldCreated.getWaves().get(i).setImage("wave1");
					}
				}
				
				for (int i = 0; i < iceTimer.size(); i++) {
					int iced = 0;
					if(iceTimer.get(""+i) != null){
						iced = iceTimer.get(""+i);
					}
					iced = iced + 1;
					iceTimer.remove(""+i);
					iceTimer.put(""+i, iced);
					
					if(iceTimer.get(""+i) == 400){
						worldCreated.getWaves().get(i).setIced(false);
					}
					iced = 0;
				}
				
				worldCreated.getBullsEye().draw(dbg);
				currentGun.get(gunState).draw(dbg);

				for (int i = 0; i < noOfShots; i++) {
					shots.get(i).draw(dbg);		
				}
				
				for (int i = 0; i < userScore; i++) {
					scoreBoard.get(i).draw(dbg);
				}
				
				int ammoTypes = 0;
				if(currentLevel < 3){
					ammoTypes = 2;
				}else if(currentLevel >= 3 && currentLevel <= 8){
					ammoTypes = 1;
				}

				for (int i = worldCreated.getAmmos().size()-1; i >= ammoTypes; i--) {
					Ammo ammo = worldCreated.getAmmos().get(i);
					ammo.draw(dbg);
					
					int tens = worldCreated.getAmmoStorage().get(i)/10;
					int ones = worldCreated.getAmmoStorage().get(i)%10;
					int y = 530;
					int x = 555;
						
					if(tens > 0)
						dbg.drawImage(worldCreated.getNumbers()[tens], x + (i*50), y, null);
					else
						dbg.drawImage(worldCreated.getNumbers()[0], x + (i*50), y, null);
					
					if(ones > 0)
						dbg.drawImage(worldCreated.getNumbers()[ones], (x+10) + (i*50), y, null);
					else
						dbg.drawImage(worldCreated.getNumbers()[0], (x+10) + (i*50), y, null);
					
					tens = 0;
					ones = 0;
					y = 0;
					x = 0;
				}
				
				for(int i = 0; i < worldCreated.getLvlNo().length;i++){
					LevelNumber lvlNo = worldCreated.getLvlNo()[i];
					lvlNo.draw(dbg);
					lvlNo = null;
				}
				
				if(isCurrentLevelEnded && isPlaying){
					
					if(rate < .4){
						worldCreated.getScoreBoard().setImage("bestScoreBoard0");
					}else if(rate < .6 && rate >= .40){
						worldCreated.getScoreBoard().setImage("bestScoreBoard1");
					}else if(rate < .99 && rate >= .60){
						worldCreated.getScoreBoard().setImage("bestScoreBoard2");
					}else{
						worldCreated.getScoreBoard().setImage("bestScoreBoard3");
					}
					worldCreated.getScoreBoard().draw(dbg);

					for(int i = 0; i < worldCreated.getBestScore().length;i++){
						LevelNumber bestScore = worldCreated.getBestScore()[i];
						bestScore.draw(dbg);
						bestScore = null;
					}
					
				}

				if(mouseOutOfBounds){
					dbg.drawImage(WorldCreation.getImageLoader().getImage("redLine"), 0, 460, null);
				}
				if(isPaused){
					PauseBoard pauseBoard = worldCreated.getPauseBoard();
					pauseBoard.draw(dbg);
				}
				
			}
		
		}catch(NullPointerException npe){
			//npe.printStackTrace();
		}
		catch(IndexOutOfBoundsException iobe){

			//iobe.printStackTrace();
			JOptionPane.showMessageDialog(null, "Application crashed! Exiting...");
			System.exit(0);
		}
	}//end of gameRender()

	public void run() {
		
		long beforeTime, afterTime, timeDiff;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		
		beforeTime = System.nanoTime();

		running = true;
		
		while(running){
			gameUpdate();
			gameRender();
			paintScreen();
			
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = ((period - timeDiff) - overSleepTime );

			if(sleepTime > 0){

				try{
					Thread.sleep((sleepTime/1000000L));
				}catch(InterruptedException ie){}
				
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}else{
				excess -= sleepTime;
				overSleepTime = 0L;

				if(++noDelays >= NO_DELAYS_PER_YIELD){
					Thread.yield();
					noDelays = 0;
				}
			}
			
			beforeTime = System.nanoTime();
			
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS)){
				excess -= period;
				gameUpdate();
				skips++;
			}
		}
		System.exit(0);
	}//end of run()
	
	private void paintScreen(){
		Graphics g;
		try{
			g = this.getGraphics();
			if(g != null && dbImage != null){
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}catch(Exception e){}
	}//end of paintScreen()

	private void reloadRatings() {
		SaveAndLoad.load();
		
		starCount = new int[12];
		starMap = new HashMap<Integer, Stars[]>();
		
		for(int i = 1;i <= 12;i++){
			
			if(SaveAndLoad.getRatingData().get(i)==null){
				starCount[i-1] = 0;
			}else{

				if(SaveAndLoad.getRatingData().get(i) < .4){
					starCount[i-1] = 0;
				}else if(SaveAndLoad.getRatingData().get(i) >= .4 && SaveAndLoad.getRatingData().get(i) < .6){
					starCount[i-1] = 1;
				}else if(SaveAndLoad.getRatingData().get(i) < .99 && SaveAndLoad.getRatingData().get(i) >= .60){
					starCount[i-1] = 2;
				}else if(SaveAndLoad.getRatingData().get(i) == 1){
					starCount[i-1] = 3;
				}
			}
		}
		
		for(int i = 0;i < starCount.length;i++){
			Stars[] starSet = new Stars[3];
			
			for (int j = 0; j < 3; j++) {
				if(j < starCount[i]){
					starSet[j] = new FullStar();
				}else{

					starSet[j] = new EmptyStar();
				}
			}
			
			starMap.put(i, starSet);
		}
		
		int starSetY = 0;
		int starSetX = 45;
		int starSetDistanceX = 0;
		
		for (int i = 0; i < 12; i++) {	
			
			if(i % 4 == 0 || i == 0){
				starSetY = starSetY + 145 +((i/2)*6);
				starSetDistanceX = 0;
			}
			
			for (int j = 0; j < 3; j++) {
				
				starSetX = (j*1) * 44 + starSetDistanceX;
				
				
				starMap.get(i)[j].setX(starSetX+45);
				starMap.get(i)[j].setY(starSetY);
								
			}
			starSetDistanceX += 165;
		}
	}
}
