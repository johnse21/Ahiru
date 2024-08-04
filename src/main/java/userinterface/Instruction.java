package userinterface;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Instruction extends UserInterface{

	public Instruction(){

		create("inst"+getCurrentPage());
		setX(0);
		setY(0);
	}

	private int page = 1;
	@SuppressWarnings("unused")
	private BufferedImage image;
	
	public Rectangle getBounds1(){
		Rectangle r;
		r = new Rectangle(getX()+130, getY()+430, 55, 50);
		return r;
	}

	public Rectangle getBounds2(){
		Rectangle r;
		r = new Rectangle(getX()+350, getY()+465, 25, 25);
		return r;
	}
	public Rectangle getBounds3(){
		Rectangle r;
		r = new Rectangle(getX()+530, getY()+430, 55, 50);
		return r;
	}
	
	public void setCurrentPage(int val){
		page = val;
	}
	
	public int getCurrentPage(){
		return page;
	}
}