package word.game.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;

import word.game.model.Word;

public class WordPool extends Pool<Word> {

    public WordPool(){
        super(5, 15, true);
    }

    @Override
    protected Word newObject() {
        return new Word();
    }
}
