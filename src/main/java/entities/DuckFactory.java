package entities;

import static utilities.GameConstants.*;

public class DuckFactory {

    private DuckFactory(){}

    public static DuckFactory of(){
        return new DuckFactory();
    }

    public Duck getDuck(String duckType, int xPos, int lane){
        if (EASY_DUCK.equalsIgnoreCase(duckType)){
            return new DuckEasy(xPos, lane);
        }else if(HELM_DUCK.equalsIgnoreCase(duckType)){
            return new DuckHelm(xPos, lane);
        }else if(KNIGHT_DUCK.equalsIgnoreCase(duckType)){
            return new DuckKnight(xPos, lane);
        }else if(CLOWN_DUCK.equalsIgnoreCase(duckType)){
            return new DuckClown(xPos, lane);
        }else{
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
