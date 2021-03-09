package word.game.pool;

import com.badlogic.gdx.utils.Pool;

import word.game.ui.top_panel.Coin;

public class CoinPool extends Pool<Coin> {


    @Override
    protected Coin newObject() {
        return new Coin();
    }
}
