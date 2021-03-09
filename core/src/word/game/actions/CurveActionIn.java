package word.game.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

import word.game.ui.dial.Dial;
import word.game.ui.dial.DialButton;

import static com.badlogic.gdx.math.MathUtils.lerpAngle;


public class CurveActionIn extends FloatAction {


    public static float motionAngle = 0;

    private DialButton dialButton;

    private float center;
    private float radius;
    private float temp;



    public void init(DialButton actor, Dial dial){
        this.dialButton = actor;

        center = dial.getOriginX();
        radius = dial.calculateRadius();
        temp = radius;
    }



    @Override
    protected void update(float percent) {

        float angle = MathUtils.lerp(dialButton.angle, dialButton.angle - 3.141592f * 2, percent);

        radius = temp * (1.0f - percent);
        float x =  MathUtils.cos(angle) * radius;
        float y =  MathUtils.sin(angle) * radius;

        dialButton.setX(center + x - dialButton.getOriginX());
        dialButton.setY(center + y - dialButton.getOriginY());

        dialButton.setRotation(-360 * percent);

    }


}
