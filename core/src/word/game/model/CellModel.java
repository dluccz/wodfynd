package word.game.model;

import com.badlogic.gdx.utils.Pool;

public class CellModel implements Pool.Poolable {


    private char letter;
    private int x, y;
    private int state;

    public Word acrossWord;
    public Word downWord;




    public void init(char letter, int x, int y, Word downWord, Word acrossWord, int state){
        this.letter = letter;
        this.x = x;
        this.y = y;
        this.downWord = downWord;
        this.acrossWord = acrossWord;
        this.state = state;
    }




    public void setDownWord(Word downWord){
        this.downWord = downWord;
    }






    public char getLetter(){
        return letter;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }






    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }



    @Override
    public void reset() {
        acrossWord = null;
        downWord = null;
        state = Constants.TILE_STATE_DEFAULT;
    }
}
