package word.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;

public class Feedback extends Group {

    private Image bg;
    private List<String> feedbackList;
    private Label label;
    private int fbIndex;

    private Label.LabelStyle labelStyle;


    public Feedback(BaseScreen screen){
        setVisible(false);

        NinePatch ninePatch = NinePatches.ribbon2;

        bg = new Image(ninePatch);
        addActor(bg);

        setSize(bg.getWidth(), bg.getHeight());
        setOrigin(Align.center);
        setScale(0);

        String[] feedbacks = LanguageManager.get("feedback").split(",");
        feedbackList = Arrays.asList(feedbacks);
        Collections.shuffle(feedbackList);

        String font = UIConfig.FEEDBACK_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        labelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.FEEDBACK_TEXT_COLOR);

        label = new Label(feedbackList.get(0).trim(), labelStyle);
        label.setFontScale(1.2f);
        addActor(label);

        setTouchable(Touchable.disabled);
    }



    private ParallelAction parallelAction;
    private ScaleToAction scaleToAction1, scaleToAction2;
    private AlphaAction alphaAction1, alphaAction2;
    private DelayAction delayAction;
    private RunnableAction runnableAction;
    private SequenceAction sequenceAction;


    public void show(Color color, String text){
        bg.setColor(color);
        label.setText(text);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(labelStyle.font, label.getText());

        bg.setWidth(NinePatches.ribbon.getLeftWidth() + NinePatches.ribbon.getRightWidth() + glyphLayout.width * 1.1f);
        setWidth(bg.getWidth());
        setOrigin(Align.center);
        setX((getStage().getWidth() - getWidth()) * 0.5f);

        label.setX((getWidth() - glyphLayout.width * label.getFontScaleX()) * 0.5f);
        label.setY((getHeight() - glyphLayout.height * label.getFontScaleY()) * 0.5f);
        Pools.free(glyphLayout);

        setScale(0.5f);
        getColor().a = 0;

        if(scaleToAction1 == null) scaleToAction1 = new ScaleToAction();
        else scaleToAction1.reset();
        scaleToAction1.setScale(1.05f);
        scaleToAction1.setDuration(0.3f);
        scaleToAction1.setInterpolation(Interpolation.fastSlow);

        if(alphaAction1 == null) alphaAction1 = new AlphaAction();
        else alphaAction1.reset();
        alphaAction1.setAlpha(1f);
        alphaAction1.setDuration(0.3f);
        alphaAction1.setInterpolation(Interpolation.fastSlow);

        if(parallelAction == null) parallelAction = new ParallelAction();
        else parallelAction.reset();

        parallelAction.addAction(scaleToAction1);
        parallelAction.addAction(alphaAction1);

        if(scaleToAction2 == null) scaleToAction2 = new ScaleToAction();
        else scaleToAction2.reset();
        scaleToAction2.setScale(1f);
        scaleToAction2.setDuration(0.1f);
        scaleToAction2.setInterpolation(Interpolation.slowFast);

        if(alphaAction2 == null) alphaAction2 = new AlphaAction();
        else alphaAction2.reset();
        alphaAction2.setAlpha(0f);
        alphaAction2.setDuration(0.3f);

        if(delayAction == null) delayAction = new DelayAction();
        else delayAction.reset();
        delayAction.setDuration(UIConfig.FEEDBACK_SHOW_DURATION);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();
        runnableAction.setRunnable(end);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(parallelAction);
        sequenceAction.addAction(scaleToAction2);
        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(alphaAction2);
        sequenceAction.addAction(runnableAction);

        setVisible(true);
        addAction(sequenceAction);
    }



    private Runnable end = new Runnable() {

        @Override
        public void run() {
            setVisible(false);
            remove();
        }
    };




    public void show(Color color){
        show(color, feedbackList.get(fbIndex).trim());

        fbIndex++;
        fbIndex %= feedbackList.size();
    }


}
