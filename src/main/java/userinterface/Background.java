package userinterface;

import java.awt.Rectangle;

public class Background extends UserInterface{

	public Background(String backgroundintro){
		create(backgroundintro);
	}
	
	public Rectangle getBounds1(){
		Rectangle r;
		r = new Rectangle(10, 650-105, 50, 45);
		return r;
	}
	
	public Rectangle getBounds2(){
		Rectangle r;
		r = new Rectangle(260, 550, 220, 40);
		return r;
	}

}
