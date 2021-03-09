package word.game.pool;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Pool;

import word.game.ui.board.CellView;

public class CellViewPool extends Pool<CellView> {

    public CellViewPool(){
        super(9, 50, true);
    }


    @Override
    protected CellView newObject() {
        return new CellView();
    }

}
