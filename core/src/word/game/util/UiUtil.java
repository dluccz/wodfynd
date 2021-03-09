package word.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import word.game.actions.Interpolation;


public class UiUtil {


    private static Shaker shaker;


    public static void shake(Actor actor, boolean horizontal, float amount, Runnable callback){
        if(shaker == null) shaker = new Shaker();

        shaker.shake(actor, horizontal, amount, callback);
        actor.addAction(shaker);
    }





    public static void pulsate(Actor actor){

        SequenceAction sequenceAction = new SequenceAction(
                Actions.scaleTo(0.95f, 0.95f, 1),
                Actions.scaleTo(1,1, 1)
        );

        actor.addAction(Actions.forever(sequenceAction));
    }





    public static void actorAnimIn(Actor actor, float delay, Runnable callback){
        if(actor != null && actor.isVisible()){
            actor.setScale(0);
            SequenceAction sequenceAction = new SequenceAction();
            sequenceAction.addAction(Actions.delay(delay));
            sequenceAction.addAction(Actions.scaleTo(1, 1, 0.3f, word.game.actions.Interpolation.backOut));
            if(callback != null) sequenceAction.addAction(Actions.run(callback));
            actor.addAction(sequenceAction);
        }
    }





    public static void actorAnimOut(Actor actor, float delay, Runnable callback){
        actorAnimOut(actor, delay, 0f, callback);
    }



    public static void actorAnimOut(Actor actor, float delay1, float delay2, Runnable callback){
        if(actor != null && actor.isVisible()){
            SequenceAction sequenceAction = new SequenceAction();
            sequenceAction.addAction(Actions.delay(delay1));
            sequenceAction.addAction(Actions.scaleTo(0, 0, 0.3f, Interpolation.backIn));
            if(delay2 > 0f) sequenceAction.addAction(Actions.delay(delay2));
            if(callback != null) sequenceAction.addAction(Actions.run(callback));
            actor.addAction(sequenceAction);
        }
    }






    public static boolean isScreenWide(){
        return (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth() < 1.43f;
    }
}
