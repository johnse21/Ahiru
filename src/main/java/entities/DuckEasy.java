package entities;

import static utilities.GameConstants.EASY_DUCK;

public class DuckEasy extends Duck{

	public DuckEasy(int xPos, int yPos) {
		super(xPos, yPos);
		setLife(1);
		setSpeed(2);
		this.setImgSrcName(EASY_DUCK);
	}

}
