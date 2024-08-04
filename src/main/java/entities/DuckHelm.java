package entities;

import static utilities.GameConstants.HELM_DUCK;

public class DuckHelm extends Duck{

	public DuckHelm(int xPos, int yPos) {
		super(xPos, yPos);
		setLife(2);
		setSpeed(1);
		this.setImgSrcName(HELM_DUCK);
	}

}
