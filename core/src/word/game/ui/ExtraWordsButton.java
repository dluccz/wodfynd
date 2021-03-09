package word.game.ui;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.graphics.AtlasRegions;


public class ExtraWordsButton extends ImageButton {


    public boolean animating;


    public ExtraWordsButton() {
        super(new TextureRegionDrawable(AtlasRegions.extra_words_up), new TextureRegionDrawable(AtlasRegions.extra_words_down));
        setOrigin(Align.center);
    }





    public void growAndShrink(Runnable callback){
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.scaleTo(1.2f, 1.2f, 0.03f));
        sequenceAction.addAction(Actions.scaleTo(1, 1, 0.07f));

        if(callback != null){
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(callback);
            sequenceAction.addAction(runnableAction);
        }

        addAction(sequenceAction);
    }

}
