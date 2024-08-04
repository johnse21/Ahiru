package entities;

import main.WorldCreation;

import java.awt.Graphics;
import java.awt.image.BufferedImage;


abstract class GameObjects {

	private int x;
	private int y;
	private BufferedImage image;
	private String imgSrcName;

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

	public void setImage(BufferedImage image){
		this.image = image;
	}

	public BufferedImage getImage(){
		return image;
	}

	public String getImgSrcName() {
		return imgSrcName;
	}

	public void setImgSrcName(String imgSrcName) {
		this.imgSrcName = imgSrcName;
	}
	
}
