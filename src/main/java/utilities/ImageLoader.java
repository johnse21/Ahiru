package utilities;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import main.AhiruPanel;

public class ImageLoader {
	
	private ArrayList<BufferedImage> imgList;
	private ArrayList<String> imgNames;
	
	public ImageLoader(String fnm){
		imgList = new ArrayList<BufferedImage>(); 
		imgNames = new ArrayList<String>();
		
		loadImagesFile(fnm);
	}
	
	public BufferedImage getImage(String name) {
		BufferedImage singleImage = null;
		String namePNG = name+".png";
		//System.out.println(namePNG);
		for (int i = 0; i < imgNames.size(); i++) {
			
			if(namePNG.equals(imgNames.get(i))){
				singleImage = imgList.get(i);
			}
		}
		return singleImage;
	}
	
	private void loadImagesFile(String fileName){
		try{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			
			char ch;
			
			while((line = br.readLine()) != null){
				if(line.length() == 0)//blank line
					continue;
				if(line.startsWith("//"))//comments
					continue;
				ch = Character.toLowerCase(line.charAt(0));
				if(ch == 'o')
					getFileNameImage(line);
				else if(ch == 'n')
					getNumberedImages(line);
				else
					JOptionPane.showMessageDialog(null, "Do not recognize:"+line);
				
				AhiruPanel.setLoadingProgress(1);
			}
			in.close();
			br.close();
		}catch(IOException ie){
			JOptionPane.showMessageDialog(null, "error reading file, application will exit", "", 1);
			System.exit(1);
		}
	}
	
	private void getFileNameImage(String line){
		StringTokenizer tokens = new StringTokenizer(line);
		
		if(tokens.countTokens() != 2)
			JOptionPane.showMessageDialog(null, "Wrong no. of arguments for "+line);
		else{
			tokens.nextToken();
			loadSingleImage(tokens.nextToken());
		}
	}
	
	private void getNumberedImages(String line){
		StringTokenizer tokens = new StringTokenizer(line);
		
		if(tokens.countTokens() != 3)
			JOptionPane.showMessageDialog(null, "Wrong no. of arguments for "+line);
		else{
			tokens.nextToken();
			String baseFileName = tokens.nextToken();
			int numberOfBase = Integer.parseInt(tokens.nextToken());
			
			loadMultipleImages(baseFileName, numberOfBase);
		}
	}
	
	public void loadSingleImage(String fnm){

		BufferedImage bi = loadImage(fnm);
		
		if(bi != null){
			imgList.add(bi);
			imgNames.add(fnm);
		}
	
	}
	
	public void loadMultipleImages(String fnm, int numberOfSequence){
		
		for (int i = 1; i <= numberOfSequence; i++) {
			
			String newFileName = "";
			String n = "";
			n = "" + i;

			newFileName = fnm.replace('*', n.charAt(0));
			
			BufferedImage bi = loadImage(newFileName);
			
			if(bi != null){
				imgList.add(bi);
				imgNames.add(newFileName);
			}
		}
	
	}
	
	public BufferedImage loadImage(String fnm){
		//System.out.println(fnm);
		try{
			BufferedImage im = ImageIO.read(getClass().getResource("/gfx/"+fnm));
			return im;
		}catch(IOException e){
			return null;
		}
	}
}
