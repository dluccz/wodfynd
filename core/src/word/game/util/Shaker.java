package word.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;



public class Shaker extends SequenceAction {

    private MoveToAction mt1, mt2, mt3, mt4, mt5, mt6, mt7;
    private AlphaAction a;
    private RunnableAction runnableAction;




    public void shake(Actor actor, boolean horizontal, float amount, Runnable callback){
        float x = actor.getX();
        float y = actor.getY();


        if(mt1 == null) mt1 = new MoveToAction(); else mt1.reset();
        if(mt2 == null) mt2 = new MoveToAction(); else mt2.reset();
        if(mt3 == null) mt3 = new MoveToAction(); else mt3.reset();
        if(mt4 == null) mt4 = new MoveToAction(); else mt4.reset();
        if(mt5 == null) mt5 = new MoveToAction(); else mt5.reset();
        if(mt6 == null) mt6 = new MoveToAction(); else mt6.reset();
        if(mt7 == null) mt7 = new MoveToAction(); else mt7.reset();
        if(a == null) a = new AlphaAction(); else a.reset();

        if(horizontal)
            mt1.setPosition(x - amount * 0.5f, y);
        else
            mt1.setPosition(x, y - amount * 0.5f);


        mt1.setDuration(0.1f);
        addAction(mt1);


        if(horizontal)
            mt2.setPosition(x + amount * 0.5f, y);
        else
            mt2.setPosition(x, y + amount * 0.5f);

        mt2.setDuration(0.1f);
        addAction(mt2);


        if(horizontal)
            mt3.setPosition(x - amount * 0.33f, y);
        else
            mt3.setPosition(x, y - amount * 0.33f);

        mt3.setDuration(0.07f);
        addAction(mt3);


        if(horizontal)
            mt4.setPosition(x + amount * 0.33f, y);
        else
            mt4.setPosition(x, y + amount * 0.33f);

        mt4.setDuration(0.07f);
        addAction(mt4);


        if(horizontal)
            mt5.setPosition(x - amount * 0.25f, y);
        else
            mt5.setPosition(x, y - amount * 0.25f);

        mt5.setDuration(0.04f);
        addAction(mt5);


        if(horizontal)
            mt6.setPosition(x + amount * 0.25f, y);
        else
            mt6.setPosition(x, y + amount * 0.25f);

        mt6.setDuration(0.04f);
        addAction(mt6);


        mt7.setPosition(x, y);
        mt7.setDuration(0.02f);
        addAction(mt7);


        if(callback != null){
            if(runnableAction == null) runnableAction = new RunnableAction(); else runnableAction.reset();
            runnableAction.setRunnable(callback);
            addAction(runnableAction);
        }
    }

}
