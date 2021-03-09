package word.game.model;

import com.badlogic.gdx.utils.Pool;

public class Word implements Pool.Poolable{

    public int id;
    public boolean isSolved;
    public boolean hasRocket;
    public Direction direction;
    public String answer;
    public int x, y;
    public int reward;
    public boolean triggered;
    public boolean error;



    @Override
    public int hashCode(){
        return id;
    }


    public boolean equals(Object other){
        return id == ((Word) other).id;
    }


    @Override
    public void reset() {
        answer = null;
        isSolved = false;
        hasRocket = false;
        triggered = false;
        reward = 0;
        error = false;
        id = 0;
    }


    @Override
    public String toString() {
        return "word.answer: " + answer + ", word.id: " + id;
    }


}
