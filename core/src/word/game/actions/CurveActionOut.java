package word.game.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

import word.game.ui.dial.Dial;
import word.game.ui.dial.DialButton;


public class CurveActionOut extends FloatAction {



    private DialButton dialButton;

    private float center;
    private float radius;
    private float temp;



    public void init(DialButton actor, Dial dial){
        this.dialButton = actor;

        center = dial.getWidth() * 0.5f;
        radius = dial.calculateRadius();
        temp = radius;
        radius = 0;
    }



    @Override
    protected void update(float percent) {

        if(percent > 1.0f)
            percent = 1.0f;

        float angle = MathUtils.lerp(dialButton.angle, dialButton.angle - CurveActionIn.motionAngle, percent);

        radius =  temp * percent;

        float x =  MathUtils.cos(angle) * radius;
        float y =  MathUtils.sin(angle) * radius;

        dialButton.setX(center + x - dialButton.getOriginX());
        dialButton.setY(center + y - dialButton.getOriginY());

        dialButton.setRotation(-380 * percent);

    }

}
