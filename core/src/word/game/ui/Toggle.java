package word.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import word.game.actions.Interpolation;
import word.game.graphics.AtlasRegions;


public class Toggle extends Group {


    private Image bg_swt_green;
    private Image bg_swt_red;
    private ImageButton swt_knob;
    private EventListener listener;

    public Toggle(){
        setName("toggle");
        Image swt_border = new Image(AtlasRegions.ui_switch_border);
        swt_border.setName("toggle");
        addActor(swt_border);
        swt_border.setTouchable(Touchable.disabled);
        setSize(swt_border.getWidth(), swt_border.getHeight());

        bg_swt_green = new Image(AtlasRegions.bg_green);
        bg_swt_green.setName("toggle");
        bg_swt_green.setX((getWidth() - bg_swt_green.getWidth()) * 0.5f);
        bg_swt_green.setY((getHeight() - bg_swt_green.getHeight()) * 0.5f);
        bg_swt_green.setTouchable(Touchable.disabled);
        addActor(bg_swt_green);

        bg_swt_red = new Image(AtlasRegions.bg_red);
        bg_swt_red.setName("toggle");
        bg_swt_red.setX((getWidth() - bg_swt_red.getWidth()) * 0.5f);
        bg_swt_red.setY((getHeight() - bg_swt_red.getHeight()) * 0.5f);
        bg_swt_red.setColor(Color.RED);
        bg_swt_red.setTouchable(Touchable.disabled);
        addActor(bg_swt_red);

        swt_knob = new ImageButton(new TextureRegionDrawable(AtlasRegions.knob_up), new TextureRegionDrawable(AtlasRegions.knob_down));
        swt_knob.setName("toggle");
        swt_knob.setY((getHeight() - swt_knob.getHeight()) * 0.45f);
        addActor(swt_knob);

        setEnabled(true);

        swt_knob.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggle();
            }
        });

    }




    public void setEnabled(boolean flag){
        if(flag){
            swt_knob.setX(getWidth() * 0.05f);
            bg_swt_red.getColor().a = 0;
            bg_swt_green.getColor().a = 1;
        }else{
            swt_knob.setX(getWidth() * 0.95f - swt_knob.getWidth());
            bg_swt_green.getColor().a = 0;
            bg_swt_red.getColor().a = 1;
        }
    }




    public boolean isEnabled(){
        return bg_swt_green.getColor().a == 1f;
    }




    private void toggle(){
        getStage().getRoot().setTouchable(Touchable.disabled);
        float duration = 0.25f;
        Action moveTo;

        if(bg_swt_green.getColor().a == 1f){
            bg_swt_green.addAction(Actions.fadeOut(duration));
            bg_swt_red.addAction(Actions.fadeIn(duration));
            moveTo = Actions.moveTo(getWidth() * 0.95f - swt_knob.getWidth(), swt_knob.getY(), duration, Interpolation.cubicInOut);
        }else{
            bg_swt_red.addAction(Actions.fadeOut(duration));
            bg_swt_green.addAction(Actions.fadeIn(duration));
            moveTo = Actions.moveTo(getWidth() * 0.05f, swt_knob.getY(), duration, Interpolation.cubicInOut);
        }

        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                if(listener != null)
                    ((ChangeListener)listener).changed(new ChangeListener.ChangeEvent(), Toggle.this);
                getStage().getRoot().setTouchable(Touchable.enabled);
            }
        });

        swt_knob.addAction(new SequenceAction(moveTo, runnableAction));
    }




    @Override
    public boolean addListener(EventListener listener) {
        this.listener = listener;
        return true;
    }
}
