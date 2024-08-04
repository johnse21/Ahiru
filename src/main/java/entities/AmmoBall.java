package entities;

import java.awt.Rectangle;

public class AmmoBall extends Ammo{
	
	public AmmoBall() {
		setX(650);
		setY(550);
		setRectangleHeight(35);
		setRectangleWidth(35);
		create("displayBall");
	}

	public AmmoBall(int x, int y){
		super(x, y);
	}
	
	@Override
	public Rectangle getBounds(){
		Rectangle r;
		r = new Rectangle(getX(), getY(), getRectangleWidth(), getRectangleHeight());
		return r;
	}
	
}
