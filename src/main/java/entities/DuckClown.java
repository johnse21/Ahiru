package entities;

import static utilities.GameConstants.CLOWN_DUCK;

public class DuckClown extends Duck{

	public DuckClown(int xPos, int yPos) {
		super(xPos, yPos);
		setLife(1);
		setSpeed(3);
		this.setImgSrcName(CLOWN_DUCK);
	}

}
