package settings;

public class Wave extends Settings{
	private boolean isIced = false;
	
	public Wave(int xPos, int yPos){
		setX(xPos);
		setY(yPos);
	}

	public boolean isIced() {
		return isIced;
	}

	public void setIced(boolean isIced) {
		this.isIced = isIced;
	}


}
