package utilities;

import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundLoader implements LineListener{
	
	private HashMap<String, Clip> soundMap;

	Clip clip = null;
	public SoundLoader(){
		soundMap = new HashMap<String, Clip>();
	}
	
	public void playFX(String name){
		try{
			AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/"+name+".wav"));
			AudioFormat format = stream.getFormat();
			
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			
			clip = (Clip)AudioSystem.getLine(info);
			clip.addLineListener(new LineListener() {
				
				@Override
				public void update(LineEvent arg0) {
				}
			});
			
			clip.open(stream);
			soundMap.remove(name);
			soundMap.put(name, clip);
			clip.start();
			
			stream.close();
		
		}catch(Exception e){}
	}

	@Override
	public void update(LineEvent arg0) {

		if(soundMap.get("crowd").getFramePosition() >= soundMap.get("crowd").getFrameLength()){
			soundMap.get("crowd").stop();
			soundMap.get("crowd").setFramePosition(10000);
			soundMap.get("crowd").start();
		}
	}
	
}
