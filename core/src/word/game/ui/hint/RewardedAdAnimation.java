package word.game.ui.hint;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;


public class RewardedAdAnimation extends Group {

    private Group group = new Group();
    private Image bg;

    public RewardedAdAnimation(BaseScreen screen){

        setSize(screen.stage.getWidth(), screen.stage.getHeight());

        bg = new Image(NinePatches.rect);
        bg.setSize(getWidth(), getHeight());
        bg.setColor(0f, 0f, 0f, 1.0f);
        addActor(bg);

        addActor(group);

        Image coin = new Image(AtlasRegions.rocket_coin);
        coin.setScale(0.7f);
        group.addActor(coin);

        Label.LabelStyle style = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(ResourceManager.fontBlack, BitmapFont.class), UIConfig.COIN_ANIM_TEXT_COLOR);
        Label label = new Label(LanguageManager.format("rewarded_ad_amount", GameConfig.NUMBER_OF_COINS_EARNED_FOR_WATCHING_VIDEO), style);
        label.setAlignment(Align.left);
        label.setOrigin(Align.bottomLeft);

        group.addActor(label);
        group.setHeight(coin.getHeight() * coin.getScaleY());
        group.setWidth(coin.getWidth() + label.getWidth());

        label.setY((group.getHeight() - label.getPrefHeight()) * 0.57f);
        label.setX(coin.getX() + coin.getWidth() * 1.3f * coin.getScaleX());
    }


    private MoveToAction moveToAction;
    private DelayAction delayAction;
    private AlphaAction alphaAction;
    private RunnableAction runnableAction;
    private SequenceAction sequenceAction;

    public void show(){
        group.setX((getWidth() - group.getWidth()) * 0.5f);
        group.setY(getHeight() * 0.6f);

        if(moveToAction == null) moveToAction = new MoveToAction();
        else moveToAction.reset();

        moveToAction.setPosition(group.getX(), getHeight() * 0.75f);
        moveToAction.setDuration(UIConfig.COIN_ANIM_DURATION);
        moveToAction.setInterpolation(Interpolation.sineIn);

        if(delayAction == null) delayAction = new DelayAction();
        else delayAction.reset();
        delayAction.setDuration(UIConfig.COIN_ANIM_DURATION * 0.7f);

        if(alphaAction == null) alphaAction = new AlphaAction();
        else alphaAction.reset();
        alphaAction.setAlpha(0f);
        alphaAction.setDuration(UIConfig.COIN_ANIM_DURATION * 0.3f);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();
        runnableAction.setRunnable(animEnded);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(delayAction);
        sequenceAction.addAction(alphaAction);
        sequenceAction.addAction(runnableAction);

        bg.getColor().a = 0.5f;
        group.getColor().a = 1f;
        group.addAction(moveToAction);
        group.addAction(sequenceAction);
    }


    private AlphaAction modalAlphaAction;
    private RunnableAction modalRunnableAction;
    private SequenceAction modalSequenceAction;

    private Runnable animEnded = new Runnable() {
        @Override
        public void run() {
            if(modalAlphaAction == null) modalAlphaAction = new AlphaAction();
            else modalAlphaAction.reset();
            modalAlphaAction.setAlpha(0f);
            modalAlphaAction.setDuration(0.2f);

            if(modalRunnableAction == null) modalRunnableAction = new RunnableAction();
            else modalRunnableAction.reset();
            modalRunnableAction.setRunnable(modalAnimEnded);

            if(modalSequenceAction == null) modalSequenceAction = new SequenceAction();
            else modalSequenceAction.reset();
            modalSequenceAction.addAction(modalAlphaAction);
            modalSequenceAction.addAction(modalRunnableAction);

            bg.addAction(modalSequenceAction);
        }
    };


    private Runnable modalAnimEnded = new Runnable() {
        @Override
        public void run() {
            remove();
        }
    };

}
