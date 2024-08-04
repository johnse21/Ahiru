package entities;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Ammo extends GameObjects {
	
	private int rWidth = 0;
	private int rHeight = 0;
	private int pathIterator = 0;
	private ArrayList<Point2D> path = null;
	private int power = 0;
	
	public Ammo(){}

	public Ammo(int x, int y){
		setX(x);
		setY(y);
	}
	
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(), getY(), 25,25);
		return r;
	}
	
	public void setRectangleWidth(int w){
		rWidth = w;
	}
	
	public int getRectangleWidth(){
		return rWidth;
	}

	public void setRectangleHeight(int h){
		rHeight = h;
	}
	
	public int getRectangleHeight(){
		return rHeight;
	}
	
	public void setPath(ArrayList<Point2D> path){
		this.path = path;
	}
	
	public ArrayList<Point2D> getPath(){
		return path;
	}

	public int getPathIterator() {
		return pathIterator;
	}

	public void setPathIterator(int pathIterator) {
		this.pathIterator = pathIterator;
	}

	public void setPower(int power) {
		this.power = power;
	}
	
	public int getPower() {
		return power;
	}

}
