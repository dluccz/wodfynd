package word.game.ui.confetti;

import com.badlogic.gdx.graphics.g2d.Sprite;

import word.game.graphics.AtlasRegions;


public class Paper extends Sprite {

    public Face face;
    float dimension_x, dimension_y;
    float position_x, position_y;
    float rotation;
    float scale_x, scale_y;
    float velocity_x, velocity_y;
    boolean dead;

    public Paper(){
        super(AtlasRegions.rect);
    }

}
