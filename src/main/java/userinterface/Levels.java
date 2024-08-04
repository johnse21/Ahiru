package userinterface;

import java.awt.*;

public class Levels extends UserInterface{

	public Levels(){}

	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX()+10,getY()+10,120,135);
		
		return r;
	}

}
