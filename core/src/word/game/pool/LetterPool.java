package word.game.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;

import word.game.model.Constants;
import word.game.ui.preview.Letter;

public class LetterPool extends Pool<Letter>{


    public LetterPool(){
        super(Constants.MAX_LETTERS, Constants.MAX_LETTERS * 4);
    }


    @Override
    protected Letter newObject() {
        return new Letter();
    }
}
