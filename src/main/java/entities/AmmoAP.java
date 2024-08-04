package entities;

import java.awt.Rectangle;

public class AmmoAP extends Ammo{

	public AmmoAP() {
		setX(600);
		setY(550);
		setRectangleHeight(50);
		setRectangleWidth(50);
		create("displayAP");
	}

	public AmmoAP(int x, int y){
		super(x, y);
	}

	@Override
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(), getY(), getRectangleWidth(), getRectangleHeight());
		return r;
	}
}
