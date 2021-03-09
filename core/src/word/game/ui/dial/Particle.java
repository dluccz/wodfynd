package word.game.ui.dial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Particle extends Sprite  {


    public enum ParticleType{
        DISC,
        STAR,
        STRIPE
    }


    public float x, y               = 0;
    public float angle              = 0;
    public float rotation           = 0;
    public float vx, vy             = 0;
    public float radius             = 1;
    public float speed              = 1;
    public float friction           = 1;
    public float opacity            = 1;
    public float gravity            = 1;
    public float dst                = 1;
    public float startX, startY     = 0;
    public Color color;
    public ParticleType type;
    public int stage;
    public boolean finished;
    public float time;


    public Particle(){

    }


    public Particle(TextureRegion region){
        super(region);
    }


    @Override
    public void setRegion(TextureRegion region) {
        super.setRegion(region);
        setBounds(0, 0, region.getRegionWidth(), region.getRegionHeight());
    }


}

