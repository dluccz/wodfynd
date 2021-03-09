package word.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import word.game.graphics.AtlasRegions;


public class LoadingAnim extends Actor {


    private boolean started;

    public void start(Color color){
        setSize(AtlasRegions.loadingRegion.getRegionWidth(), AtlasRegions.loadingRegion.getRegionHeight());
        setOrigin(Align.center);

        if(color != null) setColor(color);
        started = true;
    }







    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!started)return;

        batch.setColor(getColor());

        setRotation(getRotation() + 4);

        batch.draw(
                AtlasRegions.loadingRegion,
                getX() - getWidth() * 0.5f,
                getY() - getHeight() * 0.5f,
                getOriginX(),
                getOriginY(),
                getWidth(),
                getHeight(),
                getScaleX(),
                getScaleY(),
                getRotation());

    }




}
