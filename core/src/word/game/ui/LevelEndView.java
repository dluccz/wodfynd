package word.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.game.config.UIConfig;
import word.game.events.ShowDictionaryEvent;
import word.game.graphics.AtlasRegions;
import word.game.graphics.NinePatches;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.screens.BaseScreen;
import word.game.ui.board.CellViewParticle;
import word.game.ui.dial.Particle;
import word.game.ui.dialogs.DictionaryDialog;
import word.game.util.UiUtil;


public class LevelEndView extends Group {



    protected TextButton nextLevel;
    private ShowDictionaryEvent dictionaryEventListener;
    protected BaseScreen screen;
    protected float coinViewX, coinViewY;

    protected ImageButton dictButton;
    protected Label rewardLabel;
    protected Group rewardLabelContainer;
    protected Runnable nextLevelCallback;

    private Group cupContainer;
    private LevelEndView.ParticleContainer particleContainer;
    private Image cup;

    private Array<CellViewParticle> particles = new Array<>();
    private Group endedContainer;


    public LevelEndView(BaseScreen screen, float width, float height){
        setSize(width, height);
        this.screen = screen;

        setBackground();
        setDictButton();
        setButton(LanguageManager.get("next_level"), new NinePatchDrawable(NinePatches.play_r_up), new NinePatchDrawable(NinePatches.play_r_down));

        nextLevel.addListener(changeListener);
    }






    protected ChangeListener changeListener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            screen.topPanel.coinView.cancel(true);
            putBackCoinView();
            getStage().getRoot().setTouchable(Touchable.disabled);
            DictionaryDialog.words = null;
            hide();
        }
    };





    protected void setDictButton(){
        if(dictButton == null && LanguageManager.wordMeaningProviderMap.containsKey(LanguageManager.locale.code)) {
            dictButton = new ImageButton(new TextureRegionDrawable(AtlasRegions.btn_dictionary_up), new TextureRegionDrawable(AtlasRegions.btn_dictionary_down));
            dictButton.setOrigin(Align.center);
            dictButton.setTransform(true);
            dictButton.setScale(0);
            dictButton.setX((getWidth() - dictButton.getWidth()) * 0.5f);
            dictButton.setY(getHeight() - dictButton.getHeight()  - screen.topPanel.coinView.getY());
            addActor(dictButton);

            dictButton.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (dictionaryEventListener != null){
                        dictionaryEventListener.showDictionary(null);
                    }
                }
            });
        }
    }




    public void setButton(String text, Drawable up, Drawable down){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        String font = UIConfig.LEVEL_END_VIEW_NEXT_LEVEL_BUTTON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        buttonStyle.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
        buttonStyle.up = up;
        buttonStyle.down = down;

        nextLevel = new TextButton(text, buttonStyle);
        nextLevel.getLabel().setFontScale(UIConfig.LEVEL_END_VIEW_NEXT_LEVEL_BUTTON_FONT_SCALE);
        nextLevel.setWidth(getWidth() * ((UiUtil.isScreenWide() ? 0.55f : 0.6f)) * UIConfig.LEVEL_END_VIEW_NEXT_LEVEL_BUTTON_WIDTH_COEF);
        nextLevel.setX((getWidth() - nextLevel.getWidth()) * 0.5f);
        nextLevel.setY(getHeight() * 0.1f);
        nextLevel.setTransform(true);
        nextLevel.setOrigin(Align.center);
        addActor(nextLevel);
    }



    protected void setCoinView(){
        coinViewX = screen.topPanel.coinView.getX();
        coinViewY = screen.topPanel.coinView.getY();

        screen.topPanel.coinView.remove();
        screen.topPanel.coinView.setOrigin(Align.center);

        screen.topPanel.coinView.setPosition(screen.topPanel.getX() + coinViewX, screen.topPanel.getY() + coinViewY - screen.topPanel.getHeight());
        if(screen.topPanel.coinView.plus != null) screen.topPanel.coinView.plus.setDisabled(true);
        addActor(screen.topPanel.coinView);
    }





    public void addNextLevelListener(Runnable listener){
        nextLevelCallback = listener;
    }




    public void addDictionaryShowListener(ShowDictionaryEvent dictionaryEventListener){
        this.dictionaryEventListener = dictionaryEventListener;
    }





    protected void setRewardText(String text){
        if(screen.topPanel.coinView.isCancelled()) return;
        if(rewardLabel == null) {
            String font = UIConfig.LEVEL_FINISHED_VIEW_COINS_EARNED_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            Label.LabelStyle wordTitlelabelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.LEVEL_FINISHED_VIEW_COINS_EARNED_TEXT_COLOR);

            rewardLabel = new Label(text, wordTitlelabelStyle);

            rewardLabelContainer = new Group();
            rewardLabelContainer.addActor(rewardLabel);
           addActor(rewardLabelContainer);
        }else{
            rewardLabel.setText(text);
            rewardLabelContainer.setVisible(true);
            rewardLabelContainer.getColor().a = 1f;
        }

        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(rewardLabel.getStyle().font, text);
        rewardLabelContainer.setSize(glyphLayout.width, glyphLayout.height);
        Pools.free(glyphLayout);
        rewardLabelContainer.setOrigin(Align.center);
        rewardLabelContainer.setScale(0);
        rewardLabelContainer.setX((getWidth() - rewardLabelContainer.getWidth()) * 0.5f);

        float buttonTop = nextLevel.getY() + nextLevel.getHeight();
        rewardLabelContainer.setY(buttonTop + (cupContainer.getY() - buttonTop) * 0.5f - rewardLabelContainer.getHeight() * 0.5f);
    }





    protected void setCoinRewardView(int count){
        Vector2 pos = rewardLabel.localToActorCoordinates(screen.topPanel.coinView, new Vector2(rewardLabel.getWidth() * 0.5f, rewardLabel.getHeight() * 1.3f));
        screen.topPanel.coinView.createCoinAnimation(count, pos.x, pos.y, coinAnimComplete);
    }





    private Runnable coinAnimComplete = new Runnable() {
        @Override
        public void run() {
            Action fadeOut = Actions.fadeOut(0.3f);
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    putBackCoinView();

                }
            });
            screen.topPanel.coinView.addAction(new SequenceAction(fadeOut, runnableAction));
            rewardLabelContainer.addAction(Actions.fadeOut(0.3f));
        }
    };




    protected void putBackCoinView(){
        if(screen.topPanel.coinView.getParent() == this){
            screen.topPanel.coinView.remove();
            screen.topPanel.coinView.setPosition(coinViewX, coinViewY);
            screen.topPanel.addActor(screen.topPanel.coinView);
            screen.topPanel.coinView.getColor().a = 1;
            rewardLabelContainer.setVisible(false);
        }
    }




    public void setBackground() {
        Image bg = new Image(NinePatches.rect);
        bg.setSize(getWidth(), getHeight());
        bg.setColor(UIConfig.LEVEL_FINISHED_VIEW_BG_COLOR);
        addActor(bg);
    }





    public void hide() {
        float time = 0f;
        if(dictButton != null) {
            time += 0.1f;
            UiUtil.actorAnimOut(dictButton, time, null);
        }

        time += 0.1f;
        UiUtil.actorAnimOut(cupContainer, time, null);

        if(rewardLabelContainer != null) {
            time += 0.1f;
            UiUtil.actorAnimOut(rewardLabelContainer, time, null);
        }

        time += 0.1f;
        UiUtil.actorAnimOut(nextLevel, time, fadeOut);

        if(endedContainer != null){
            time += 0.1f;
            UiUtil.actorAnimOut(endedContainer, time, null);
        }
    }



    private AlphaAction fadeOutAlphaAction;
    private RunnableAction fadeOutRunnableAction;
    private SequenceAction fadeOutSequenceAction;


    private Runnable fadeOut = new Runnable() {

        @Override
        public void run() {
            if(fadeOutAlphaAction == null) fadeOutAlphaAction = new AlphaAction();
            else fadeOutAlphaAction.reset();
            fadeOutAlphaAction.setAlpha(0f);
            fadeOutAlphaAction.setDuration(0.15f);

            if(fadeOutRunnableAction == null) fadeOutRunnableAction = new RunnableAction();
            else fadeOutRunnableAction.reset();
            fadeOutRunnableAction.setRunnable(nextLevelCallback);

            if(fadeOutSequenceAction == null) fadeOutSequenceAction = new SequenceAction();
            else fadeOutSequenceAction.reset();
            fadeOutSequenceAction.addAction(fadeOutAlphaAction);
            fadeOutSequenceAction.addAction(fadeOutRunnableAction);

            addAction(fadeOutSequenceAction);

            if(level_clear != null) level_clear.remove();
            cup_rays.clearActions();
            cup_rays2.clearActions();

            for(CellViewParticle cellViewParticle : particles){
                word.game.pool.Pools.cellViewParticlePool.free(cellViewParticle);
            }
            particles.clear();
        }
    };





    private void checkGameEnd(int nextLevelIndex){
        if(nextLevelIndex == LanguageManager.locale.LevelCount){
            nextLevel.setText(LanguageManager.get("back"));

            String font = UIConfig.GAME_COMPLETELY_FINISHED_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            Label.LabelStyle style = new Label.LabelStyle();
            style.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
            style.fontColor = UIConfig.GAME_COMPLETELY_FINISHED_TEXT_COLOR;

            Label label = new Label(LanguageManager.get("to_be_continued"), style);

            endedContainer = new Group();
            endedContainer.addActor(label);
            endedContainer.setWidth(label.getPrefWidth());
            endedContainer.setHeight(label.getPrefHeight());
            endedContainer.setOrigin(Align.center);
            endedContainer.setX((getWidth() - endedContainer.getWidth()) * 0.5f);
            endedContainer.setY(nextLevel.getY() - endedContainer.getHeight() * 1.5f);
            addActor(endedContainer);
        }
    }




    public void startComboAnimWithRewards(int coinCount, int nextLevelIndex){
        checkGameEnd(nextLevelIndex);

        String nextGameScreenBackground = UIConfig.getGameScreenBackgroundImage(GameData.findFirstIncompleteLevel());
        if(!screen.wordConnectGame.resourceManager.contains(nextGameScreenBackground)){
            screen.wordConnectGame.resourceManager.load(nextGameScreenBackground, Texture.class);
            screen.wordConnectGame.resourceManager.finishLoading();
        }

        createCup();

        if(coinCount > 0){
            setCoinView();
            setRewardText(LanguageManager.format("combo_earned", coinCount));
        }
        animateAllCombo(coinCount);
        getStage().getRoot().setTouchable(Touchable.enabled);
    }





    private void animateAllCombo(final int coinCount){
        if(dictButton != null) UiUtil.actorAnimIn(dictButton, 0f, null);
        if(screen.topPanel.coinView.getParent().equals(this)) UiUtil.actorAnimIn(screen.topPanel.coinView, 0f, null);

        float time = 0.1f;
        UiUtil.actorAnimIn(cupContainer, time, null);

        if(rewardLabelContainer != null) {
            time += 0.1f;
            UiUtil.actorAnimIn(rewardLabelContainer, time, null);
        }

        time += 0.1f;
        UiUtil.actorAnimIn(nextLevel, time, new Runnable() {
            @Override
            public void run() {
                if(coinCount > 0) setCoinRewardView(coinCount);
            }
        });

        if(endedContainer != null){
            time += 0.1f;
            UiUtil.actorAnimIn(endedContainer, time, null);
        }
    }





    private Image cup_rays, cup_rays2;
    private Image level_clear;

    private RotateByAction rotateByAction1, rotateByAction2;
    private RepeatAction repeatAction1, repeatAction2;


    private void createCup(){

        if(cup_rays == null) {
            cup_rays = new Image(AtlasRegions.cup_rays);
            cup_rays.setOrigin(Align.center);
            cup_rays.setScale(getWidth() * (UiUtil.isScreenWide() ? 0.95f : 1f) / cup_rays.getWidth());
        }

        if(cupContainer == null) {
            cupContainer = new Group();
            cupContainer.setSize(cup_rays.getWidth(), cup_rays.getHeight());
            cupContainer.setOrigin(Align.center);
            cupContainer.setX((getWidth() - cup_rays.getWidth()) * 0.5f);
            cupContainer.setY((getHeight() - cupContainer.getHeight()) * 0.6f);
            cupContainer.setScale(0);
            addActor(cupContainer);
            cupContainer.addActor(cup_rays);
            cupContainer.setTouchable(Touchable.disabled);
        }

        if(cup_rays2 == null) {
            cup_rays2 = new Image(AtlasRegions.cup_rays);
            cup_rays2.setOrigin(Align.center);
            cup_rays2.setScale(cup_rays.getScaleX() * 0.8f);
            cup_rays2.setX((cupContainer.getWidth() - cup_rays.getWidth()) * 0.5f);
            cup_rays2.setY((cupContainer.getHeight() - cup_rays2.getHeight()) * 0.5f);
            cupContainer.addActor(cup_rays2);
        }

        if(rotateByAction1 == null) rotateByAction1 = new RotateByAction();
        else rotateByAction1.reset();
        rotateByAction1.setAmount(360f);
        rotateByAction1.setDuration(20f);

        if(repeatAction1 == null) repeatAction1 = new RepeatAction();
        else repeatAction1.reset();

        repeatAction1.setCount(RepeatAction.FOREVER);
        repeatAction1.setAction(rotateByAction1);
        cup_rays.addAction(repeatAction1);


        if(rotateByAction2 == null) rotateByAction2 = new RotateByAction();
        else rotateByAction2.reset();
        rotateByAction2.setAmount(-360f);
        rotateByAction2.setDuration(20f);

        if(repeatAction2 == null) repeatAction2 = new RepeatAction();
        else repeatAction2.reset();

        repeatAction2.setCount(RepeatAction.FOREVER);
        repeatAction2.setAction(rotateByAction2);
        cup_rays2.addAction(repeatAction2);

        if(particleContainer == null) {
            particleContainer = new ParticleContainer();
            cupContainer.addActor(particleContainer);
        }

        if(cup == null) {
            cup = new Image(AtlasRegions.cup);
            cup.setScale(cup_rays.getScaleX());
            cup.setX((cupContainer.getWidth() - cup.getWidth() * cup.getScaleX()) * 0.5f);
            cup.setY((cupContainer.getHeight() - cup.getHeight() * cup.getScaleY()) * 0.5f);
            cupContainer.addActor(cup);
        }

        TextureAtlas atlas = screen.wordConnectGame.resourceManager.get(ResourceManager.ATLAS_4, TextureAtlas.class);
        level_clear = new Image(atlas.findRegion("level_clear_" + LanguageManager.locale.code));
        level_clear.setScale(cup.getScaleX());
        level_clear.setX((cupContainer.getWidth() - level_clear.getWidth() * level_clear.getScaleX()) * 0.5f);

        float ribbonHeight = cup.getHeight() * 0.180487f;

        level_clear.setY(cup.getY() + (ribbonHeight - level_clear.getHeight()) * 0.85f);
        cupContainer.addActor(level_clear);
    }



    class ParticleContainer extends Actor {

        private void reset(Particle particle){
            float TWO_PI = 6.283185f;
            float angle = MathUtils.random() * TWO_PI;
            float cos = MathUtils.cos(angle);
            float sin = MathUtils.sin(angle);

            particle.angle = angle * MathUtils.radiansToDegrees;
            particle.x = particle.startX = (cup.getX() + cup.getWidth() * 0.5f) ;
            particle.y = particle.startY = (cup.getY() + cup.getHeight() * 0.5f);
            particle.radius = MathUtils.random(0.6f, 1.2f);
            particle.rotation = MathUtils.random() * 360.0f;
            particle.friction = 0.98f;
            particle.dst = 0;

            float speed = MathUtils.random(getStage().getWidth() * 0.001f, getStage().getWidth() * 0.003f);
            particle.vx = cos * speed;
            particle.vy = sin * speed;
            particle.setColor(UIConfig.LEVEL_FINISHED_VIEW_STAR_PARTICLES_COLOR);
        }




        private void renderParticles(Batch batch){
            if(particles.size < UIConfig.LEVEL_FINISHED_VIEW_PARTICLE_COUNT){
                CellViewParticle particle = word.game.pool.Pools.cellViewParticlePool.obtain();
                particle.setColor(UIConfig.LEVEL_FINISHED_VIEW_STAR_PARTICLES_COLOR);
                reset(particle);
                particles.add(particle);
            }


            for(int i = 0; i < particles.size; i++){
                Particle p = particles.get(i);

                p.x += p.vx;
                p.y += p.vy;
                p.dst = (float)Math.sqrt((p.x - p.startX) * (p.x - p.startX) + (p.y - p.startY) * (p.y - p.startY));
                p.speed *= p.friction;
                p.radius *= 0.995f;
                p.setPosition(p.x, p.y);
                p.setScale(p.radius);
                p.setRotation(p.angle);
                p.setAlpha(0.7f);
                p.draw(batch);

                if(p.dst > getStage().getWidth() * 0.4f){
                    reset(p);
                }
            }
        }




        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if(cupContainer != null) renderParticles(batch);
        }
    }

}
