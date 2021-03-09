package word.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import word.game.graphics.AtlasRegions;

public class Smoke extends Image implements Runnable{


    private ScaleToAction scaleToAction;
    private DelayAction delayAction;
    private AlphaAction alphaAction;
    private SequenceAction sequenceAction;
    private RunnableAction runnableAction;



    public Smoke(){
        super(AtlasRegions.smoke);
        setVisible(false);
        setOrigin(Align.center);
        setTouchable(Touchable.disabled);
    }



    public void blast(Color color){
        setColor(color);
        setRotation(MathUtils.random() * 360f);
        setScale(0.7f);
        setVisible(true);

        if(scaleToAction == null)
            scaleToAction = new ScaleToAction();
        else
            scaleToAction.reset();

        scaleToAction.setScale(1.9f);
        scaleToAction.setDuration(0.3f);
        scaleToAction.setInterpolation(Interpolation.fastSlow);

        if(delayAction == null)
            delayAction = new DelayAction();
        else
            delayAction.reset();

        delayAction.setDuration(0.2f);

        if(alphaAction == null)
            alphaAction = new AlphaAction();
        else
            alphaAction.reset();

        alphaAction.setAlpha(0);
        alphaAction.setDuration(0.5f);

        if(sequenceAction == null)
            sequenceAction = new SequenceAction();
        else
            sequenceAction.reset();

        if(runnableAction == null)
            runnableAction = new RunnableAction();
        else
            runnableAction.reset();

        runnableAction.setRunnable(this);

        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(alphaAction);
        sequenceAction.addAction(runnableAction);

        addAction(scaleToAction);
        addAction(sequenceAction);
    }




    @Override
    public void run() {
        setVisible(false);
    }
}
