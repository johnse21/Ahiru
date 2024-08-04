package settings;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.WorldCreation;

abstract class Settings {

	private int x;
	private int y;
	private BufferedImage image;
	
	public void create(String name) {
		image = WorldCreation.getImageLoader().getImage(name);
	}

	public void draw(Graphics g) {
		g.drawImage(image, getX(), getY(), null);
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setImage(String name){
		image = WorldCreation.getImageLoader().getImage(name);
	}

}
