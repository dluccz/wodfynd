package word.game.pool;

import com.badlogic.gdx.utils.Pool;

import word.game.ui.board.CellViewParticle;

public class CellViewParticlePool extends Pool<CellViewParticle> {


    public CellViewParticlePool(){
        super(24, 64, true);
    }



    @Override
    protected CellViewParticle newObject() {
        return new CellViewParticle();
    }
}
