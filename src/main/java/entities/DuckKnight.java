package entities;

import static utilities.GameConstants.KNIGHT_DUCK;

public class DuckKnight extends Duck{

	public DuckKnight(int xPos, int yPos) {
		super(xPos, yPos);
		setLife(3);
		setSpeed(1);
		this.setImgSrcName(KNIGHT_DUCK);
	}

}
