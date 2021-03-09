package word.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;


public class ProgressBar extends Group {


    private Rectangle scissors = new Rectangle();
    private Rectangle clipBounds = new Rectangle(0,0,0,0);
    private TextureAtlas.AtlasRegion track;
    private TextureAtlas.AtlasRegion bar;
    private float percent;


    public ProgressBar(TextureAtlas.AtlasRegion track, TextureAtlas.AtlasRegion bar){

        this.track = track;
        this.bar = bar;
        setSize(track.getRegionWidth(), track.getRegionHeight());

        clipBounds.width = 0;
        clipBounds.height = getHeight();
    }




    @Override
    public void setX(float x) {
        super.setX(x);
        clipBounds.x = x;
    }



    @Override
    public void setY(float y) {
        super.setY(y);
        clipBounds.y = y;
    }




    public void setPercent(float percent){
        this.percent = percent;
        clipBounds.width = track.getRegionWidth() * percent;
    }




    public float getPercent(){
        return percent;
    }




    @Override
    public void draw(Batch batch, float parentAlpha) {

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(track, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.flush();

        getStage().calculateScissors(clipBounds, this.scissors);

        if (ScissorStack.pushScissors(this.scissors)) {
            batch.draw(bar, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

            batch.flush();
            ScissorStack.popScissors();
        }

        batch.setColor(color.r, color.g, color.b, 1);
    }
}
