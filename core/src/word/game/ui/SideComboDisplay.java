package word.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;


import word.game.actions.BezierToAction;
import word.game.graphics.NinePatches;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;

public class SideComboDisplay extends Group {


    private Label label;
    private Label bubbleLabel;
    private Group labelContainer;
    private Label.LabelStyle wordTitlelabelStyle;

    private MoveToAction slideB;
    private RunnableAction runnableAction;
    private SequenceAction sequenceAction;
    private AlphaAction alphaAction;
    private BezierToAction angledBezierToAction;

    private RunnableAction jumpRunnableAction;
    private SequenceAction jumpSequenceAction;
    private Vector2 labelContainerParentPos;

    private MoveByAction bubbleMoveByAction;
    private DelayAction bubbleDelayAction;
    private AlphaAction bubbleAlphaAction;
    private SequenceAction bubbleSequenceAction;
    private boolean firstRun = true;



    public SideComboDisplay(BaseScreen screen){
        Image bg = new Image(NinePatches.combo_bg);
        bg.setSize(NinePatches.combo_bg.getTotalWidth() * 5f, bg.getHeight());
        setSize(bg.getWidth(), bg.getHeight());
        addActor(bg);

        wordTitlelabelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.WHITE);

        Label combo = new Label(LanguageManager.get("combo_side"), wordTitlelabelStyle);
        combo.setFontScale(0.6f);
        combo.setX(getWidth() * 0.9f - combo.getWidth() * combo.getFontScaleX());
        combo.setY((getHeight() - combo.getHeight()) * 0.5f);
        addActor(combo);

        labelContainer = new Group();
        addActor(labelContainer);

        Label.LabelStyle numStyle = new Label.LabelStyle(wordTitlelabelStyle);
        label = new Label("20", numStyle);
        labelContainer.addActor(label);

        labelContainer.setHeight(label.getPrefHeight());
        setLabelContainerPosition();
    }



    public void setComboState(int n){
        label.setText(n);

        if(getX() + getWidth() <= 0){
            slideRight();
        }else{
            bubble(n);
        }
    }





    private void slideRight(){
        setVisible(true);
        label.getColor().a = 0;

        if(slideB == null) slideB = new MoveToAction();
        else slideB.reset();

        float localMargin = 0.1f;
        slideB.setPosition(-getWidth() * localMargin, getY());
        slideB.setDuration(0.4f);
        slideB.setInterpolation(word.game.actions.Interpolation.backOut);

        if(runnableAction == null) runnableAction = new RunnableAction();
        else runnableAction.reset();

        runnableAction.setRunnable(slideRightEnd);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(slideB);
        sequenceAction.addAction(runnableAction);
        addAction(sequenceAction);
    }




    private Runnable slideRightEnd = new Runnable() {

        @Override
        public void run() {
            if(alphaAction == null) alphaAction = new AlphaAction();
            else alphaAction.reset();
            alphaAction.setAlpha(1f);
            alphaAction.setDuration(0.2f);
            label.addAction(alphaAction);
        }
    };






    public void slideLeft(){
        if(slideB == null) slideB = new MoveToAction();
        else slideB.reset();

        slideB.setPosition(-getWidth(), getY());
        slideB.setDuration(0.3f);
        slideB.setInterpolation(word.game.actions.Interpolation.backIn);

        if(sequenceAction == null) sequenceAction = new SequenceAction();
        else sequenceAction.reset();

        sequenceAction.addAction(slideB);
        addAction(sequenceAction);
    }




    public void setComboStateSilent(int n){
        label.setText(n);
        slideRight();
    }




    private void bubble(int n){
        if(bubbleLabel == null) {
            bubbleLabel = new Label(n+"", wordTitlelabelStyle);
            addActor(bubbleLabel);
        }
        bubbleLabel.setText("+" + n);
        bubbleLabel.setX(labelContainer.getX());
        bubbleLabel.setY(getHeight() * 2f);
        bubbleLabel.getColor().a = 1;

        if(bubbleMoveByAction == null) bubbleMoveByAction = new MoveByAction();
        else bubbleMoveByAction.reset();
        bubbleMoveByAction.setAmount(0, getHeight() * 3);
        bubbleMoveByAction.setDuration(1.0f);
        bubbleMoveByAction.setInterpolation(Interpolation.slowFast);
        bubbleLabel.addAction(bubbleMoveByAction);

        if(bubbleDelayAction == null) bubbleDelayAction = new DelayAction();
        else bubbleDelayAction.reset();
        bubbleDelayAction.setDuration(0.7f);

        if(bubbleAlphaAction == null) bubbleAlphaAction = new AlphaAction();
        else bubbleAlphaAction.reset();
        bubbleAlphaAction.setAlpha(0);
        bubbleAlphaAction.setDuration(0.3f);

        if(bubbleSequenceAction == null) bubbleSequenceAction = new SequenceAction();
        else bubbleSequenceAction.reset();

        bubbleSequenceAction.addAction(bubbleDelayAction);
        bubbleSequenceAction.addAction(bubbleAlphaAction);

        bubbleLabel.addAction(bubbleSequenceAction);
    }


    private Runnable jumpCallback;

    public void jump(Runnable callback){
        jumpCallback = callback;

        if(getX() + getWidth() * 0.5f < 0){
            if(jumpCallback != null) {
                jumpCallback.run();
                jumpCallback = null;
            }
            return;
        }

        if(labelContainerParentPos == null) labelContainerParentPos = labelContainer.localToActorCoordinates(getParent(), new Vector2());

        labelContainer.remove();
        labelContainer.setPosition(labelContainerParentPos.x, labelContainerParentPos.y);
        getParent().addActor(labelContainer);

        if(angledBezierToAction == null){
            angledBezierToAction = new BezierToAction(){
                float prevX, prevY;

                @Override
                protected void update(float percent) {
                    super.update(percent);

                    if(firstRun){
                        prevX = target.getX();
                        prevY = target.getY();
                    }

                    float angle = MathUtils.atan2(y - prevY, x - prevX);
                    if(angle == 0f) angle = 1.4789445f;

                    target.setRotation(angle * MathUtils.radiansToDegrees - 90f);
                    prevX = x;
                    prevY = y;
                    firstRun = false;
                }
            };
        }else {
            angledBezierToAction.reset();
        }

        angledBezierToAction.setStartPosition(labelContainerParentPos.x, labelContainerParentPos.y);
        angledBezierToAction.setPointA(getStage().getWidth() * 0.05f, getStage().getHeight() * 0.5f);
        angledBezierToAction.setPointB(getStage().getWidth() * 0.8f, getY() + getWidth());
        angledBezierToAction.setEndPosition(getStage().getWidth() * 0.7f, -getY() - getHeight());
        angledBezierToAction.setDuration(0.6f);
        angledBezierToAction.setInterpolation(Interpolation.slowFast);

        if(jumpRunnableAction == null) jumpRunnableAction = new RunnableAction();
        else jumpRunnableAction.reset();

        jumpRunnableAction.setRunnable(jumpEnd);

        if(jumpSequenceAction == null) jumpSequenceAction = new SequenceAction();
        else jumpSequenceAction.reset();

        jumpSequenceAction.addAction(angledBezierToAction);
        jumpSequenceAction.addAction(jumpRunnableAction);

        labelContainer.addAction(jumpSequenceAction);

        slideLeft();
    }





    private Runnable jumpEnd = new Runnable() {

        @Override
        public void run() {
            labelContainer.remove();
            setLabelContainerPosition();
            addActor(labelContainer);

            labelContainer.setRotation(0);
            setVisible(false);
            firstRun = true;
            if(jumpCallback != null){
                jumpCallback.run();
                jumpCallback = null;
            }
        }
    };





    private void setLabelContainerPosition(){
        labelContainer.setX(getWidth() * 0.18f);
        labelContainer.setY((getHeight() - labelContainer.getHeight()) * 0.5f);
    }



}
