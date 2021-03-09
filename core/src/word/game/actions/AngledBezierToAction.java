package word.game.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class AngledBezierToAction extends BezierToAction {

    private float prevX, prevY;



    private boolean firstRun = true;


    @Override
    protected void update(float percent) {
        super.update(percent);

        if(firstRun){
            prevX = target.getX();
            prevY = target.getY();
        }

        float angle = MathUtils.atan2(y - prevY, x - prevX);

        target.setRotation(angle * MathUtils.radiansToDegrees);
        prevX = x;
        prevY = y;

        firstRun = false;
    }
}
