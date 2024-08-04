package entities;

import java.awt.Rectangle;

public class AmmoIce extends Ammo{

	public AmmoIce() {
		setX(550);
		setY(550);
		setRectangleHeight(50);
		setRectangleWidth(50);
		create("displayIce");
	}

	public AmmoIce(int x, int y){
		super(x, y);
	}

	@Override
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(), getY(), getRectangleWidth(), getRectangleHeight());
		return r;
	}
}
