package word.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import word.game.graphics.AtlasRegions;
import word.game.util.MathUtil;


public class Glitter extends Actor {


    private float x, y, width, height;
    public boolean running = true;
    private float delayCounter;
    private float time;
    private float delay;


    public Glitter(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setSize(AtlasRegions.spark.getRegionWidth(), AtlasRegions.spark.getRegionHeight());
        setOrigin(Align.center);
    }






    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(!running)
            return;

        if(delayCounter == 0.0f){
            delay = MathUtils.random() * 1.2f;
            setX(x + width * MathUtils.random() - getWidth() * 0.5f);
            setY(y + height * MathUtils.random() - getHeight() * 0.5f);
        }


        if(delayCounter >= delay){
            time += Gdx.graphics.getDeltaTime();

            float climax = 0.2f;

            if(time < climax)
                setScale(MathUtil.scaleNumber(time, 0.0f, climax, 0.0f, 1));
            else
                setScale(MathUtil.scaleNumber(time, climax, 1, 1.0f, 0));


            if(time >= 1) {
                time = 0;
                delayCounter = 0;
            }
        }
        else{
            delayCounter += Gdx.graphics.getDeltaTime();
        }


        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(
                AtlasRegions.spark,
                getX(),
                getY(),
                getOriginX(),
                getOriginY(),
                getWidth(),
                getHeight(),
                getScaleX(),
                getScaleY(),
                getRotation()
        );

        batch.setColor(color.r, color.g, color.b, 1);
    }



}
