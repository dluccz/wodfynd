package word.game.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;


import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;

public class Toast extends Group {


    private Label label;
    private AlphaAction alphaAction;
    private DelayAction delayAction;
    private ScaleToAction scaleToAction;
    private RunnableAction runnableAction;
    private SequenceAction sequenceAction;


    public Toast(ResourceManager resourceManager, float width){

        Image bg = new Image(NinePatches.rect);
        bg.setColor(UIConfig.TOAST_BACKGROUND_COLOR);
        addActor(bg);

        String font = UIConfig.TOAST_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle labelStyle = new Label.LabelStyle(resourceManager.get(font, BitmapFont.class), UIConfig.TOAST_TEXT_COLOR);

        label = new Label(" ", labelStyle);
        addActor(label);

        bg.setWidth(width);
        bg.setHeight(label.getHeight() * 3f);
        setSize(bg.getWidth(), bg.getHeight());
        setOrigin(Align.center);

        setTouchable(Touchable.disabled);

    }



    public void show(String msg){
        getColor().a = 0;
        setScale(1);

        label.setText(msg);

        GlyphLayout layout = Pools.obtain(GlyphLayout.class);
        layout.setText(label.getStyle().font, msg);

        float maxWidth = 0.9f;

        if(layout.width > getWidth() * maxWidth){
            label.setFontScale(getWidth() * maxWidth / layout.width);
        }
        label.setX((getWidth() - layout.width * label.getFontScaleX()) * 0.5f);
        label.setY((getHeight() - layout.height * label.getFontScaleY()) * 0.5f);
        Pools.free(layout);

        if(alphaAction == null)
            alphaAction = new AlphaAction();
        else
            alphaAction.reset();

        alphaAction.setAlpha(1f);
        alphaAction.setDuration(0.3f);

        addAction(alphaAction);
        hide();
    }





    private void hide(){
        if(delayAction == null)
            delayAction = new DelayAction();
        else
            delayAction.reset();

        delayAction.setDuration(UIConfig.TOAST_RUN_TIME);

        if(scaleToAction == null)
            scaleToAction = new ScaleToAction();
        else
            scaleToAction.reset();

        scaleToAction.setScale(1f, 0f);
        scaleToAction.setDuration(0.5f);

        if(runnableAction == null)
            runnableAction = new RunnableAction();
        else
            runnableAction.reset();

        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                remove();
                setVisible(false);
            }
        });

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(scaleToAction);
        sequenceAction.addAction(runnableAction);
        addAction(sequenceAction);
    }
}
