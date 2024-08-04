package entities;

import java.awt.Rectangle;
public class Duck extends GameObjects {
	
	public boolean alive;
	private int life = 0;
	private int speed = 0;
	
	public Duck(int xPos, int yPos){
		setX(xPos);
		setY(yPos);
		this.alive = true;
	}
	
	public void setAlive(boolean alive){
		this.alive = alive;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(), getY()+10, 100,70);
		return r;
	}
}
