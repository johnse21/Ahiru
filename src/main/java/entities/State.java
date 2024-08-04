package entities;

import main.WorldCreation;
import utilities.ImageLoader;

public class State {

    private ImageLoader imgLoader;
    
	public State(){
		imgLoader = WorldCreation.getImageLoader();
        imgLoader.getImage("easyLeftFallen1");
        imgLoader.getImage("easyLeftFallen2");
	}
}
