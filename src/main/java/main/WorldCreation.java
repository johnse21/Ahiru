package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.*;
import settings.Wave;
import settings.Wood;
import settings.WoodUpper;
import userinterface.*;
import utilities.ImageLoader;

import static utilities.GameConstants.*;

public class WorldCreation {
	
	private int duckPopulationL = 0;
	private int duckPopulationR = 0;
	private int ammoStored[][] = {{20,0,0,0},  {30,0,0,0},  {20,20,0,0},  {30,35,0,0}, {25,30,0,0},  {60,25,0,0},  	{25,50,0,0},  {40,70,0,0},  {20,50,20,0},  {20,30,25,0},  {20,40,40,0}, {20,45,55,0}};
	private int duckSpawns[][] = {{10,0,0,0,0},{15,5,0,0,0},{10,25,0,0,0},{5,45,0,0,0},{5,35,5,0,0} ,{0,30,25,0,0} ,{0,15,40,0,0},{0,20,50,0,0},{0,20,30,10,0},{0,10,10,25,0},{0,0,30,40,0},{0,0,44,55,0}};

	private static ImageLoader imgLoader;
	private Background background;
	private SplashScreen splashScreen;
	private MainMenu menu;
	private WoodUpper woodUpper;
	private BullsEye bullsEye;
	private State state;
	private ScoreBoard scoreBoard;
	private PauseBoard pauseBoard;
	private GameProgress gameProgress;
	private Instruction instruction;
	private List<Wood> woods;
	private List<Wave> waves;
	private List<Levels> levels;
	private List<Gun> gunA;
	private List<Gun> gunB;
	private List<Gun> gunC;
	private List<Duck> duckLeft;
	private List<Duck> duckRight;
	private List<Integer> stateIteratorL;
	private List<Integer> stateIteratorR;
	
	private LevelNumber lvlNo[];
	private LevelNumber bestScore[];
	private List<BufferedImage> duckStatesL;
	private List<BufferedImage> duckStatesR;
	private BufferedImage[] numbers;
	//for display only
	private List<Ammo> ammos;
	private List<Integer> ammoStorage;
	private BufferedImage scoreStick;
	private BufferedImage scoreFive;
	
	private Random rand = new Random();
	
	public WorldCreation(){}

	public void load(int scene, int currentLevel){
		//load materials
		if(scene == SCENE_SPLASH){
			showScreenState("splashScreen.txt");
			//create objects for splash screen/first scene
			createBackground("backgroundintro");

			splashScreen = new SplashScreen();

			menu = new MainMenu();
			
		}else if(scene == SCENE_LEVEL_SELECTION){
			showScreenState("levelSelect.txt");
			//create objects for level selection
			createBackground("levelselectbg");

			instruction = new Instruction();

			createLevels();
			
			
		}else if(scene == SCENE_LEVEL){
			imgLoader = new ImageLoader("level"+currentLevel+".txt");
			state = new State();
			//create objects for level
			createBackground("background");

			createScoreStick();

			scoreBoard = new ScoreBoard();

			pauseBoard = new PauseBoard();

			lvlNo = new LevelNumber[2];
			lvlNo[0] = new LevelNumber("levelNumberZero", 640, 25);
			lvlNo[1] = new LevelNumber("levelNumberZero", 655, 25);

			bestScore = new LevelNumber[2];
			bestScore[0] = new LevelNumber("levelNumberZero", 430, 240);
			bestScore[1] = new LevelNumber("levelNumberZero", 445, 240);

			gameProgress = new GameProgress();
			
			createDuckStates();
			setGame(currentLevel);
			createDucks(currentLevel);
			createAmmos(currentLevel);			
			createNumbers();
		}
	}//end of load(Scene)w

	private void createScoreStick() {
		scoreStick = imgLoader.getImage("stick");
		scoreFive  = imgLoader.getImage("stick5");
	}

	private void createLevels() {
		levels = new ArrayList<Levels>();

		for (int levelIter = 0; levelIter < 12; levelIter++) {
			levels.add(new Levels());
			levels.get(levelIter).create("lvl"+(levelIter+1));
			if(levelIter < 4){
				levels.get(levelIter).setX(35 + (levelIter * 165));
				levels.get(levelIter).setY(40);
			}else if(levelIter < 8){
				levels.get(levelIter).setX(35 + ((levelIter-4) * 165));
				levels.get(levelIter).setY(200);
			}else{
				levels.get(levelIter).setX(35 + ((levelIter-8) * 165));
				levels.get(levelIter).setY(365);
			}
		}
	}

	private void createBackground(String backgroundintro) {
		background = new Background(backgroundintro);
	}

	private void showScreenState(String s) {
		imgLoader = new ImageLoader(s);
	}

	private void createNumbers() {
		numbers = new BufferedImage[10];
		
		numbers[0] = imgLoader.getImage("zero");
		numbers[1] = imgLoader.getImage("one");
		numbers[2] = imgLoader.getImage("two");
		numbers[3] = imgLoader.getImage("three");
		numbers[4] = imgLoader.getImage("four");
		numbers[5] = imgLoader.getImage("five");
		numbers[6] = imgLoader.getImage("six");
		numbers[7] = imgLoader.getImage("seven");
		numbers[8] = imgLoader.getImage("eight");
		numbers[9] = imgLoader.getImage("nine");
	}

	private void createAmmos(int currentLevel) {

		ammos = new ArrayList<Ammo>();
		ammoStorage = new ArrayList<Integer>();
		
		int ball = ammoStored[currentLevel-1][0];
		int ap = ammoStored[currentLevel-1][1];
		int ice = ammoStored[currentLevel-1][2];

		ammos.add(new AmmoIce());
		ammoStorage.add(ice);
		
		ammos.add(new AmmoAP());
		ammoStorage.add(ap);
		
		ammos.add(new AmmoBall());
		ammoStorage.add(ball);
	}

	private void createDuckStates() {

		duckStatesL = new ArrayList<BufferedImage>(6);
		duckStatesR = new ArrayList<BufferedImage>(6);
		 
		duckStatesL.add(imgLoader.getImage("easyLeftFallen1"));
		duckStatesL.add(imgLoader.getImage("easyLeftFallen2"));
		duckStatesL.add(imgLoader.getImage("easyLeftFallen3"));
		duckStatesL.add(imgLoader.getImage("easyLeftFallen4"));
		duckStatesL.add(imgLoader.getImage("easyLeftFallen5"));
		duckStatesL.add(imgLoader.getImage("easyLeftFallen6"));

		duckStatesR.add(imgLoader.getImage("easyRightFallen1"));
		duckStatesR.add(imgLoader.getImage("easyRightFallen2"));
		duckStatesR.add(imgLoader.getImage("easyRightFallen3"));
		duckStatesR.add(imgLoader.getImage("easyRightFallen4"));
		duckStatesR.add(imgLoader.getImage("easyRightFallen5"));
		duckStatesR.add(imgLoader.getImage("easyRightFallen6"));
	}
	
	private void setGame(int level) {
		//objects excluding the ducks will be created here
		woods = new ArrayList<Wood>();
		waves = new ArrayList<Wave>();
		int noOfWavesAndWoods = 0;

		if(level == 1){
			noOfWavesAndWoods = 1;
		}else if(level == 2 || level == 3 || level == 5 || level == 9){
			noOfWavesAndWoods = 2;
		}else if(level == 4 || level == 6  || level == 7 || level == 8 || level == 10 || level == 11 || level == 12){
			noOfWavesAndWoods = 3;
		}

		for (int i = 0; i < noOfWavesAndWoods; i++) {
			
			waves.add(new Wave(-15,430 - (i*100)));
			waves.get(i).create("wave1");
			
			woods.add(new Wood(450 - (i*100)));
			woods.get(i).create("wood1");
		}
			
		woodUpper = new WoodUpper(0);
		woodUpper.create("woodUpper");
		gunA = new ArrayList<Gun>();
		gunB = new ArrayList<Gun>();
		gunC = new ArrayList<Gun>();
		
		String[] gunsA = {"gunCenter1","gunLeftA1","gunLeftA2","gunLeftA3","gunLeftA4","gunLeftA5"
						 ,"gunRightA1","gunRightA2","gunRightA3","gunRightA4","gunRightA5"};

		String[] gunsB = {"gunCenter2","gunLeftB1","gunLeftB2","gunLeftB3","gunLeftB4","gunLeftB5"
						 ,"gunRightB1","gunRightB2","gunRightB3","gunRightB4","gunRightB5"};

		String[] gunsC = {"gunCenter3","gunLeftC1","gunLeftC2","gunLeftC3","gunLeftC4","gunLeftC5"
						 ,"gunRightC1","gunRightC2","gunRightC3","gunRightC4","gunRightC5"};
		
		for (int i = 0; i < 11; i++) {
			
			gunA.add(new Gun());
			gunA.get(i).create(gunsA[i]);

			gunB.add(new Gun());
			gunB.get(i).create(gunsB[i]);

			gunC.add(new Gun());
			gunC.get(i).create(gunsC[i]);
		}
		
		bullsEye = new BullsEye();
		bullsEye.create("bullsEye");
	}//end of setGame

	private void createDucks(int currentLevel) {
		//duck factory
		duckLeft = new ArrayList<Duck>();
		duckRight = new ArrayList<Duck>();
		stateIteratorL = new ArrayList<Integer>();
		stateIteratorR = new ArrayList<Integer>();
		
		for	(int iterDuckType = 0; iterDuckType < 4; iterDuckType++){

			for (int duckSpawnsIter = 0; duckSpawnsIter < duckSpawns[currentLevel-1][iterDuckType]; duckSpawnsIter++) { //(noOfDucks[currentLevel-1]) get the no of ducks that will be assigned to each level

				int laneNo = getLaneNo(currentLevel);
				int lane = getLane(laneNo);

				setupDucks(iterDuckType, lane);
			}
		}
	}//createDucks

	private void setupDucks(int iterDuckType, int lane) {
		int distance = 0;
		String number = "";

		if(iterDuckType == EASY){
			number = "";
			distance = 200;
		}else if(iterDuckType == HELM){
			number = "";
			distance = 300;
		}else if(iterDuckType == KNIGHT){
			number = "1";
			distance = 400;
		}else if(iterDuckType == CLOWN){
			number = "";
			distance = 700;
		}

		int direction = rand.nextInt(2)+1;//generate a random number( 1 or 2) left or right

		if(direction == FACE_LEFT){//if 1 is generated, a duck facing left will be created.

			if(iterDuckType == EASY){
				duckLeft.add(DuckFactory.of().getDuck(EASY_DUCK,1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == HELM){
				duckLeft.add(DuckFactory.of().getDuck(HELM_DUCK,1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == KNIGHT){
				duckLeft.add(DuckFactory.of().getDuck(KNIGHT_DUCK,1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == CLOWN){
				duckLeft.add(DuckFactory.of().getDuck(CLOWN_DUCK,1200, lane));//create and add a new Duck Object
			}

			duckLeft.get(duckPopulationL).create(duckLeft.get(duckPopulationL).getImgSrcName()+"Left"+number);//get the prevAddedDuck and create its image
			stateIteratorL.add(0);

			//this will create the x-distances between ducks
			if(duckPopulationL > 0){
				duckLeft.get(duckPopulationL).setX(duckLeft.get(duckPopulationL-1).getX() + (rand.nextInt(200)+distance));
			}

			duckPopulationL++;//add the population of duck facing left


		}else if(direction == FACE_RIGHT){//same goes here

			if(iterDuckType == 0){
				duckRight.add(DuckFactory.of().getDuck(EASY_DUCK,-1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == 1){
				duckRight.add(DuckFactory.of().getDuck(HELM_DUCK,-1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == 2){
				duckRight.add(DuckFactory.of().getDuck(KNIGHT_DUCK,-1200, lane));//create and add a new Duck Object
			}else if(iterDuckType == 3){
				duckRight.add(DuckFactory.of().getDuck(CLOWN_DUCK,-1200, lane));//create and add a new Duck Object
			}

			duckRight.get(duckPopulationR).create(duckRight.get(duckPopulationR).getImgSrcName()+"Right"+number);
			stateIteratorR.add(0);

			if(duckPopulationR > 0){
				duckRight.get(duckPopulationR).setX(duckRight.get(duckPopulationR-1).getX() - (rand.nextInt(200)+distance));
			}

			duckPopulationR++;
		}
	}


	private int getLane(int laneNo) {
		int lane = 0;
		if(laneNo == 0){
			lane = 375;
		}else if(laneNo == 1){
			lane = 275;
		}else if(laneNo == 2){
			lane = 175;
		}
		return lane;
	}

	private int getLaneNo(int currentLevel) {
		int laneNo = 0;
		if(currentLevel == 1){
			laneNo = 0;
		}else if(currentLevel == 2 || currentLevel == 3 || currentLevel == 5 || currentLevel == 9){
			laneNo = rand.nextInt(2);
		}else if(currentLevel == 4 || currentLevel == 6 || currentLevel == 7 || currentLevel == 8 || currentLevel == 10 || currentLevel == 11 || currentLevel == 12){
			laneNo = rand.nextInt(3);
		}
		return laneNo;
	}

	public void unload(int scene){
		//unload materials
		if(scene == SCENE_SPLASH){
			//dispose materials from splash before accessing levelselect
			imgLoader = null;
			background = null;
			splashScreen = null;
			
		}else if(scene == SCENE_LEVEL_SELECTION){
			//dispose materials from levelselect before accessing a certain level or going back to splash
			imgLoader = null;
			background = null;
			levels = null;
			
		}else if(scene == SCENE_LEVEL){
			//dispose materials from level before going to any scenes\
			imgLoader = null;
			background = null;
			scoreStick = null;
			scoreFive  = null;
			scoreBoard = null;
			pauseBoard = null;
			
			lvlNo[0] = null;
			lvlNo[1] = null;
			
			gameProgress = null;
			numbers = null;

			duckStatesL = null;
			duckStatesR = null;

			waves = null;
			woods = null;
			woodUpper = null;

			gunA = null;
			gunB = null;			
			gunC = null;
		
			bullsEye = null;
			
			duckLeft = null;
			duckRight = null;
			duckPopulationL = 0;
			duckPopulationR = 0;
			stateIteratorL = null;
			stateIteratorR = null;
			ammos = null;
			ammoStorage = null;
		}
		
		System.gc();
	}//end of unload(Scene)

	public int getDuckPopulationL() {
		return duckPopulationL;
	}

	public int getDuckPopulationR() {
		return duckPopulationR;
	}

	public void setDuckPopulationL(int duckPopulationL) {
		this.duckPopulationL = duckPopulationL;
	}

	public void setDuckPopulationR(int duckPopulationR) {
		this.duckPopulationR = duckPopulationR;
	}

	public Background getBackground() {
		return background;
	}

	public Instruction getInstruction() {
		return instruction;
	}
	
	public SplashScreen getSplashScreen() {
		return splashScreen;
	}

	public MainMenu getMenu() {
		return menu;
	}

	public WoodUpper getWoodUpper() {
		return woodUpper;
	}

	public BullsEye getBullsEye() {
		return bullsEye;
	}

	public List<Wood> getWoods() {
		return woods;
	}

	public List<Wave> getWaves() {
		return waves;
	}

	public List<Levels> getLevels() {
		return levels;
	}

	public List<Gun> getGunA() {
		return gunA;
	}

	public List<Gun> getGunB() {
		return gunB;
	}

	public List<Gun> getGunC() {
		return gunC;
	}

	public List<Duck> getDuckLeft() {
		return duckLeft;
	}

	public List<Duck> getDuckRight() {
		return duckRight;
	}
	
	public State getState(){
		return state;
	}
	
	public List<Integer> getStateIteratorL(){
		return stateIteratorL;
	}
	
	public List<Integer> getStateIteratorR(){
		return stateIteratorR;
	}
	
	public BufferedImage getDuckStatesL(int state){
		return duckStatesL.get(state);
	}

	public BufferedImage getDuckStatesR(int state){
		return duckStatesR.get(state);
	}
	
	public BufferedImage getScoreStick() {
		return scoreStick;
	}

	public BufferedImage getScoreFive() {
		return scoreFive;
	}

	public List<Ammo> getAmmos() {
		return ammos;
	}

	public List<Integer> getAmmoStorage() {
		return ammoStorage;
	}

	public BufferedImage[] getNumbers() {
		return numbers;
	}

	public ScoreBoard getScoreBoard() {
		return scoreBoard;
	}
	
	public PauseBoard getPauseBoard() {
		return pauseBoard;
	}

	public GameProgress getGameProgress() {
		return gameProgress;
	}
	
	public LevelNumber[] getLvlNo() {
		return lvlNo;
	}

	public LevelNumber[] getBestScore() {
		return bestScore;
	}

	public int[][] getDuckSpawns() {
		return duckSpawns;
	}
	
	public int getTotalDucks(int currentLevel){
		int duckSpawner = 0;

		for (int i = 0; i < 5; i++) {
			duckSpawner += duckSpawns[currentLevel-1][i];
		}

		return duckSpawner;
	}

	public static ImageLoader getImageLoader(){
		//created objects will fetch the current loaded images
		return imgLoader;
	}//end of getImageLoader()
}
