package word.game.pool;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Pool;

import word.game.model.CellModel;

public class CellModelPool extends Pool<CellModel> {

    public CellModelPool(){
        super(9, 50, true);
    }


    @Override
    protected CellModel newObject() {
        return new CellModel();
    }
}
