package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import word.game.graphics.AtlasRegions;


public class CoinRotateAnim extends Actor {


    private Animation<TextureAtlas.AtlasRegion> rotate;
    private float stateTime;
    private float delayTime;



    public CoinRotateAnim(){
        rotate = new Animation<>(0.07f, AtlasRegions.coinAnimation);
        setSize(AtlasRegions.coinAnimation.get(0).getRegionWidth(), AtlasRegions.coinAnimation.get(0).getRegionHeight());
    }






    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.setColor(1,1,1,1);

        TextureRegion frame = null;

        if(rotate.isAnimationFinished(stateTime)){
            frame = rotate.getKeyFrame(0);
            delayTime += Gdx.graphics.getDeltaTime();
            float interval = 5f;
            if(delayTime >= interval) {
                delayTime = 0;
                stateTime = 0;
            }
        }else{
            frame = rotate.getKeyFrame(stateTime);
        }

        stateTime += Gdx.graphics.getDeltaTime();

        if(frame != null) {
            float x = getX() + (getWidth() - frame.getRegionWidth()) * getScaleX() * 0.5f;
            float y = getY() + (getHeight() - frame.getRegionHeight()) * getScaleY()  * 0.5f;
            batch.draw(frame, x, y, getOriginX(), getOriginY(), frame.getRegionWidth(), frame.getRegionHeight(),getScaleX(),getScaleY(),getRotation());
        }

    }
}
