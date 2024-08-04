package utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class SaveAndLoad {

	static HashMap<Integer, Double> ratingMap = new HashMap<Integer, Double>();
	static HashMap<Integer, Integer> scoreMap = new HashMap<Integer, Integer>();
	static int OPENED_LEVELS = 1;
	
	public static void setLevels(int val){
		OPENED_LEVELS = val;
	}
	
	@SuppressWarnings("unchecked")
	public static void setData(int currentLevel, int score, double rating){
		
		HashMap<Integer, Double> tmpMap1 = new HashMap<Integer, Double>();
		
		ratingMap.put(currentLevel, rating);
		
		try{
			InputStream file1 = new FileInputStream("rating.ser");
			InputStream buffer1 = new BufferedInputStream(file1);
			ObjectInput input1 = new ObjectInputStream(buffer1);
			
			try{
				tmpMap1 = (HashMap<Integer, Double>) input1.readObject();
				
			} finally{
				input1.close();
			}
		}catch(IOException e){

			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			for (int i = 1; i <= tmpMap1.size(); i++) {
				
				if(tmpMap1.get(i) > ratingMap.get(i)){
					ratingMap.put(i, tmpMap1.get(i));
				}
			
			}
		}catch(NullPointerException npe){}
		
//////////////////////
		HashMap<Integer, Integer> tmpMap2 = new HashMap<Integer, Integer>();
		
		scoreMap.put(currentLevel, score);
		
		try{
			InputStream file2 = new FileInputStream("score.ser");
			InputStream buffer2 = new BufferedInputStream(file2);
			ObjectInput input2 = new ObjectInputStream(buffer2);
			
			try{
				tmpMap2 = (HashMap<Integer, Integer>) input2.readObject();
				
			} finally{
				input2.close();
			}
		}catch(IOException e){

			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			for (int i = 1; i <= tmpMap2.size(); i++) {
				
				if(tmpMap2.get(i) > scoreMap.get(i)){
					scoreMap.put(i, tmpMap2.get(i));
				}
			
			}
		}catch(NullPointerException npe){}
	}
	
	public static HashMap<Integer, Double> getRatingData(){
		return ratingMap;
	}
	
	public static HashMap<Integer, Integer> getScoreData(){
		return scoreMap;
	}	
	
	public static int getLevels(){
		return OPENED_LEVELS;
	}
	
	public static void save(){

		try{
			OutputStream file1 = new FileOutputStream("rating.ser");
			OutputStream buffer1 = new BufferedOutputStream(file1);
			ObjectOutput output1 = new ObjectOutputStream(buffer1);
			
			try{
				System.out.println("save:"+ratingMap);
				output1.writeObject(ratingMap);
			}finally{
				output1.close();
			}

			OutputStream file2 = new FileOutputStream("score.ser");
			OutputStream buffer2 = new BufferedOutputStream(file2);
			ObjectOutput output2 = new ObjectOutputStream(buffer2);
			
			try{				
				output2.writeObject(scoreMap);
			}finally{
				output2.close();
			}
			
			OutputStream file3 = new FileOutputStream("availLevels.ser");
			OutputStream buffer3 = new BufferedOutputStream(file3);
			ObjectOutput output3 = new ObjectOutputStream(buffer3);
			
			try{				
				output3.writeObject(OPENED_LEVELS);
			}finally{
				output3.close();
			}
		}catch(IOException e){

			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void load(){

		try{
			InputStream file1 = new FileInputStream("rating.ser");
			InputStream buffer1 = new BufferedInputStream(file1);
			ObjectInput input1 = new ObjectInputStream(buffer1);
			try{
				ratingMap = (HashMap<Integer, Double>) input1.readObject();
				System.out.println("load:"+ratingMap);
			} finally{
				input1.close();
			}
			InputStream file2 = new FileInputStream("score.ser");
			InputStream buffer2 = new BufferedInputStream(file2);
			ObjectInput input2 = new ObjectInputStream(buffer2);
			try{
				scoreMap = (HashMap<Integer, Integer>) input2.readObject();
			} finally{
				input2.close();
			}
			InputStream file3 = new FileInputStream("availLevels.ser");
			InputStream buffer3 = new BufferedInputStream(file3);
			ObjectInput input3 = new ObjectInputStream(buffer3);
			try{
				OPENED_LEVELS = Integer.parseInt(input3.readObject().toString());
			} finally{
				input3.close();
			}
			
		}catch(Exception e){
			save();
		}
	}
	
}
