package word.game.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;

import word.game.model.Constants;
import word.game.ui.dial.Dial;
import word.game.ui.dial.DialButton;

public class DialButtonPool extends Pool<DialButton> {

    public DialButtonPool(){
            super(5, Constants.MAX_LETTERS, true);
    }


    @Override
    protected DialButton newObject() {
        return new DialButton();
    }
}
