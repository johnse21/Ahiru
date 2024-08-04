package userinterface;

import java.awt.Rectangle;

public class MainMenu extends UserInterface{

	public MainMenu(){
		create("play");
		setX(250);
		setY(370);
	}
	
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(),getY()+5,165,50);
		
		return r;
	}
}
