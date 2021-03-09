package word.game.ui.board;

import com.badlogic.gdx.utils.Pool;

import word.game.graphics.AtlasRegions;
import word.game.ui.dial.Particle;

public class CellViewParticle extends Particle implements Pool.Poolable{



    public CellViewParticle(){
        super(AtlasRegions.star_particle);
    }


    @Override
    public void reset() {
        x = 0;
        y = 0;
        angle = 0;
        rotation = 0;
        vx = 0;
        vy = 0;
        radius = 1;
        speed = 1;

        opacity = 1;
        gravity = 1;
        dst = 1;
        startX = 0;
        startY = 0;

        type = null;
        stage = 0;
        finished = false;
        time = 0;
        setAlpha(1f);
    }
}
