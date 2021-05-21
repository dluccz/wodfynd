package word.game.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraShaker {

    private boolean isShaking = false;
    private float originalShakeRadius;
    private float shakeRadius;
    private float randomAngle;

    private Vector2 currentPosition = new Vector2();

    private float originalX, originalY;
    private float offsetX, offsetY;






    private void init(float shakeRadius){
        //originalX = 0f;
        //originalY = 0f;
        originalShakeRadius = shakeRadius;
        offsetX = 0f;
        offsetY = 0f;
        currentPosition.x = 0f;
        currentPosition.y = 0f;
        reset();
    }



    public boolean isCameraShaking(){
        return isShaking;
    }

    public void startShaking(float shakeRadius){
        init(shakeRadius);
        isShaking = true;
    }



    private void seedRandomAngle(){
        randomAngle = MathUtils.random(6.283185f);
    }




    private void computeCameraOffset(){
        offsetX =  MathUtils.cos(randomAngle) * shakeRadius;
        offsetY =  MathUtils.sin(randomAngle) * shakeRadius;
    }




    private void computeCurrentPosition(){
        currentPosition.x = originalX + offsetX;
        currentPosition.y = originalY + offsetY;
    }




    private void diminishShake(){
        if( shakeRadius < 2.0 ){
            reset();
            return;
        }

        isShaking = true;
        shakeRadius *= .9f;
        randomAngle = MathUtils.random(1, 360);
    }




    public void reset(){
        shakeRadius = originalShakeRadius;
        isShaking = false;
        seedRandomAngle();
        currentPosition.x = originalX;
        currentPosition.y = originalY;
    }




    public Vector2 getNewShakePosition(){
        computeCameraOffset();
        computeCurrentPosition();
        diminishShake();
        return currentPosition;
    }
}
