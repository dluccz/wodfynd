package word.game.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Align;

public class BezierToAction extends TemporalAction {


    private float startX, startY;

    private float pointAx, pointAy;
    private float pointBx, pointBy;

    private float endX, endY;

    protected float x, y;


    public void setStartPosition(float x, float y){
        startX = x;
        startY = y;
    }


    public void setPointA(float x, float y){
        pointAx = x;
        pointAy = y;
    }


    public void setPointB(float x, float y){
        pointBx = x;
        pointBy = y;
    }


    public void setEndPosition(float x, float y){
        endX = x;
        endY = y;
    }



    @Override
    protected void update(float percent) {



        if (percent == 0) {
            x = startX;
            y = startY;
        } else if (percent == 1) {
            x = endX;
            y = endY;
        } else {

            float u = 1 - percent;
            float tt = percent * percent;
            float uu = u*u;
            float uuu = uu * u;
            float ttt = tt * percent;

            x = startX * uuu;
            x += 3 * uu * percent * pointAx;
            x += 3 * u * tt * pointBx;
            x += ttt * endX;

            y = startY * uuu;
            y += 3 * uu * percent * pointAy;
            y += 3 * u * tt * pointBy;
            y += ttt * endY;

        }

        target.setPosition(x, y, Align.bottomLeft);

    }

}
