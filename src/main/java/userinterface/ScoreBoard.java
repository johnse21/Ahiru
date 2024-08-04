package userinterface;

import java.awt.Rectangle;

public class ScoreBoard extends UserInterface{

	public ScoreBoard(){
		setX(140);
		setY(150);
	}
	
	public Rectangle getBounds1(){
		Rectangle r;
		r = new Rectangle(getX()+100, getY()+135, 200, 40);
		return r;
	}

	public Rectangle getBounds2(){
		Rectangle r;
		r = new Rectangle(getX()+90, getY()+215, 200, 40);
		return r;
	}
	
}
