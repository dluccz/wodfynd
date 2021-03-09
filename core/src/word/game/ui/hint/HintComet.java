package word.game.ui.hint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import word.game.graphics.AtlasRegions;
import word.game.ui.dial.Particle;


public class HintComet extends Group {



    private TextureRegion hintLightRegion;
    private TextureRegion sparkleRegion;
    private Array<Particle> tail = new Array<>();
    private int count = 50;
    private float elapsed;



    public HintComet(){
        hintLightRegion = AtlasRegions.hintLightRegion;
        sparkleRegion = AtlasRegions.sparkle;
        setSize(hintLightRegion.getRegionWidth(), hintLightRegion.getRegionHeight() );
        setOrigin(Align.center);
    }


    public void reset(){
        elapsed = 0;
    }



    public void destroy(){
        for(Particle particle : tail) particle = null;
        tail.clear();
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        elapsed += Gdx.graphics.getDeltaTime();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(hintLightRegion, getX(), getY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation() + 90,false);


        if(elapsed > 0.2f && tail.size < count){
            Particle sparkle = new Particle(sparkleRegion);
            tail.add(sparkle);
        }

        for(int i = 0; i < tail.size; i++){
            Particle sparkle = tail.get(i);

            Color c = sparkle.getColor();
            sparkle.setColor(c.g, c.g, c.b, 0.8f * color.a);

            float centerX = getWidth() * 0.5f - sparkle.getWidth() * 0.5f;
            float centerY = getHeight() * 0.5f - sparkle.getHeight() * 0.5f;

            float random = MathUtils.random(-getWidth() * 0.1f, getWidth() * 0.1f);
            centerX += random;

            random = MathUtils.random(-getHeight() * 0.1f, getHeight() * 0.1f);
            centerY += random;

            float x = getX() - MathUtils.cos((getRotation()) * MathUtils.degreesToRadians) * getWidth() * 0.5f + centerX;
            float y = getY() - MathUtils.sin((getRotation()) * MathUtils.degreesToRadians) * getHeight() * 0.5f + centerY;

            if(sparkle.getScaleX() == 1.0f)
            sparkle.setPosition(x, y);
            sparkle.draw(batch);
            sparkle.setScale(sparkle.getScaleX() - 0.033f);

            if(sparkle.getScaleX() <= 0){
                sparkle.setScale(1);
            }
        }

        batch.setColor(color.r, color.g, color.b, 1f);

    }



}
