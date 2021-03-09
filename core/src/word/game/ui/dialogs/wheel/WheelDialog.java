package word.game.ui.dialogs.wheel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.Constants;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;
import word.game.ui.Glitter;
import word.game.ui.confetti.Confetti;
import word.game.ui.dialogs.BaseDialog;
import word.game.ui.dialogs.iap.ItemContent;
import word.game.util.RewardedVideoCloseCallback;
import word.game.util.UiUtil;

import static word.game.ui.dialogs.wheel.RewardRevealType.COINS;
import static word.game.ui.dialogs.wheel.RewardRevealType.FINGER_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.MULTI_RANDOM_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.ROCKET_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.SINGLE_RANDOM_REVEAL;


public class WheelDialog extends BaseDialog {

    private TextButton spinButton;
    private LuckyWheel luckyWheel;
    private int actualSpinCount;
    private Group giftBoxContainer;
    private Group boxGroup;
    private Group tapGroup;
    private Confetti confetti;
    private Label rewardLabel;
    private Image rewardIcon;
    private Label tapToCollect;
    private Image gift_box_open, lid;
    float boxYCoef = 0.4f;
    private Array<Image> rays;
    private Array<Glitter> glitters;
    private Image gift_glow;
    private Label.LabelStyle rewardLabelStyle;
    private Image videoIcon;
    private Color backgroundColor;




    public WheelDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);

        if(screen instanceof GameScreen) {
            ((GameScreen) screen).stopIdleTimer();
        }


        content.setSize(width * 0.8f, AtlasRegions.lucky_wheel.getRegionHeight() * 1.5f + NinePatches.btn_dialog_up.getTotalHeight());

        setContentBackground();

        setTitleLabel(LanguageManager.get("lucky_wheel_dialog_title"));

        setCloseButton();
        closeButton.setScale(1);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });


        luckyWheel = new LuckyWheel(createGiftBoxContainer, screen.wordConnectGame.resourceManager);
        luckyWheel.setX((content.getWidth() - luckyWheel.getWidth()) * 0.5f);
        luckyWheel.setY((content.getHeight() - luckyWheel.getHeight()) * 0.5f);
        luckyWheel.setOrigin(Align.center);
        content.addActor(luckyWheel);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        String font = UIConfig.WHEEL_DIALOG_SPIN_BUTTON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        style.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
        style.fontColor = UIConfig.WHEEL_DIALOG_SPIN_BUTTON_TEXT_COLOR;
        style.up = new NinePatchDrawable(NinePatches.btn_dialog_up);
        style.down = new NinePatchDrawable(NinePatches.btn_dialog_down);
        style.disabled = new NinePatchDrawable(NinePatches.btn_dialog_disabled);

        spinButton = new TextButton(LanguageManager.get("spin_btn_label"), style);
        spinButton.getLabel().setFontScale(UIConfig.WHEEL_DIALOG_SPIN_BUTTON_FONT_SCALE);
        spinButton.setWidth(luckyWheel.getWidth() * UIConfig.WHEEL_DIAL_SPIN_BUTTON_WIDTH_COEF);
        spinButton.setX((content.getWidth() - spinButton.getWidth()) * 0.5f);
        spinButton.setY(getHeight() * 0.03f);
        spinButton.setOrigin(Align.center);
        content.addActor(spinButton);
        actualSpinCount = GameConfig.ALLOWED_SPIN_COUNT;

        spinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                determineSpin();
            }
        });
    }




    private void changeSpinButton(){
        if (actualSpinCount > 0){
            confetti.reset();
            if(videoIcon == null && screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isRewardedAdEnabledToSpinWheel()) {
                videoIcon = new Image(AtlasRegions.ic_video);
                videoIcon.setScale(UIConfig.WHEEL_DIALOG_SPIN_BUTTON_FONT_SCALE);
                videoIcon.setX(spinButton.getWidth() * 0.1f);
                videoIcon.setY((spinButton.getHeight() - videoIcon.getHeight()) * 0.5f);
                spinButton.addActor(videoIcon);
            }
            spinButton.setText(" " + LanguageManager.format("spin_again", actualSpinCount));
            spinButton.setDisabled(false);
        }
    }




    @Override
    public void hide() {
        if(actualSpinCount == GameConfig.ALLOWED_SPIN_COUNT){//the user did not spin, save the time and let go.
            updateWheelLastSpinTime();
        }
        super.hide();
    }





    private void determineSpin(){
        if(actualSpinCount == GameConfig.ALLOWED_SPIN_COUNT){
            spin();
        }else{
            if(screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isRewardedAdEnabledToSpinWheel()){
                if(!screen.wordConnectGame.adManager.isRewardedAdLoaded()){
                        screen.showToast(LanguageManager.get("no_video"));
                        closeButton.setDisabled(false);
                }else{
                    screen.wordConnectGame.adManager.showRewardedAd(adFinished);
                }

            }else{
                spin();
            }
        }
    }




    private RewardedVideoCloseCallback adFinished = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward) spin();
        }
    };




    @Override
    protected void setContentBackground() {
        super.setContentBackground();
        contentBackground.setColor(UIConfig.WHEEL_DIALOG_BACKGROUND_COLOR);

        TextureAtlas atlas = screen.wordConnectGame.resourceManager.get(ResourceManager.ATLAS_4, TextureAtlas.class);
        Image rays = new Image(atlas.findRegion("rays"));
        rays.setColor(UIConfig.WHEEL_DIALOG_RAYS_COLOR);
        rays.setScaleX(content.getWidth() / rays.getWidth());
        rays.setScaleY(content.getHeight() / rays.getHeight());
        content.addActor(rays);
    }






    private void spin(){
        getStage().getRoot().setTouchable(Touchable.disabled);
        actualSpinCount--;

        updateWheelLastSpinTime();

        closeButton.setDisabled(true);
        spinButton.setDisabled(true);
        luckyWheel.spin();

    }


    @Override
    public boolean navigateBack() {
        if(closeButton.isDisabled()) return true;
        return super.navigateBack();
    }

    private void updateWheelLastSpinTime() {
        Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
        preferences.putLong(Constants.KEY_LAST_WHEEL_SPIN_TIME, TimeUtils.millis());
        preferences.flush();
    }






    private Runnable createGiftBoxContainer = new Runnable() {
        @Override
        public void run() {
            if(giftBoxContainer == null) {
                giftBoxContainer = new Group();
                giftBoxContainer.setSize(getWidth(), getHeight());
                giftBoxContainer.setScale(1.3f);
                giftBoxContainer.setX((getWidth() - giftBoxContainer.getWidth() * giftBoxContainer.getScaleX()) * 0.5f);
                giftBoxContainer.setY((getHeight() - giftBoxContainer.getHeight() * giftBoxContainer.getScaleY()) * 0.5f);
                giftBoxContainer.getColor().a = 0;
                addActor(giftBoxContainer);

                Image background = new Image(AtlasRegions.rect);
                background.setWidth(giftBoxContainer.getWidth());
                background.setHeight(giftBoxContainer.getHeight());

                backgroundColor = Pools.obtain(Color.class);
                backgroundColor.set(0f, 0f, 0f, 0.8f);
                background.setColor(backgroundColor);
                giftBoxContainer.addActor(background);
            }else{
                giftBoxContainer.removeListener(touchListener);
            }

            giftBoxContainer.addAction(new SequenceAction(
                    Actions.fadeIn(0.2f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            giftBoxAnim_1();

                        }
                    })
            ));

        }
    };







    private void confettiAnim(){
        if(confetti == null) {
            confetti = new Confetti(giftBoxContainer.getWidth(), giftBoxContainer.getHeight(), 100);
        }
        confetti.running = true;
    }





    private void giftBoxAnim_1(){
        if(boxGroup == null){
            boxGroup = new Group();
            giftBoxContainer.addActor(boxGroup);
        }else{
            giftBoxContainer.setVisible(true);
            giftBoxContainer.getColor().a = 1;
        }

        if(gift_box_open == null) {
            gift_box_open = new Image(AtlasRegions.gift_box_open);
            boxGroup.setSize(gift_box_open.getWidth(), gift_box_open.getHeight());
            boxGroup.setX((giftBoxContainer.getWidth() - boxGroup.getWidth()) * 0.5f);
            boxGroup.addActor(gift_box_open);
        }

        boxGroup.setY(giftBoxContainer.getHeight() * 0.2f);

        if(lid == null) {
            lid = new Image(AtlasRegions.gift_box_lid);
            lid.setX((gift_box_open.getWidth() - lid.getWidth()) * 0.5f);
            boxGroup.addActor(lid);
        }
        lid.setY(gift_box_open.getHeight() * boxYCoef);

        boxGroup.addAction(new SequenceAction(
                Actions.delay(0.3f),
                Actions.scaleTo(1f,0.7f, 0.2f),
                Actions.parallel(
                        Actions.moveTo(boxGroup.getX(), (giftBoxContainer.getHeight() - boxGroup.getHeight()) * 0.5f, 0.15f, Interpolation.sineIn),
                        Actions.scaleTo(1f, 1.5f, 0.15f, Interpolation.sineIn)
                ),
                Actions.delay(0.1f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        giftBoxAnim_2();
                        confettiAnim();
                    }
                })
        ));
    }






    private void giftBoxAnim_2(){
        lid.addAction(Actions.moveTo(lid.getX(), giftBoxContainer.getHeight(), 0.3f, Interpolation.sineIn));
        boxGroup.addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.sineIn));
        giftBoxAnim_3();

        if(!ConfigProcessor.muted) {
            Sound sound = screen.wordConnectGame.resourceManager.get(ResourceManager.SFX_WHEEL_SPIN, Sound.class);
            sound.play(SoundConfig.SFX_WHEEL_SPIN_FINISH_VOLUME);
        }
    }






    private void giftBoxAnim_3(){
        if(rays == null) {
            rays = new Array<>();
            Drawable drawable = new TextureRegionDrawable(AtlasRegions.gift_ray);
            for (int i = 0; i < 25; i++) {
                Image ray = new Image(drawable);
                ray.setOrigin(Align.bottom);
                ray.setX(gift_box_open.getX() + (gift_box_open.getWidth() - ray.getWidth()) * 0.5f);
                ray.setY(gift_box_open.getY() + gift_box_open.getHeight() * boxYCoef * 1.1f);
                boxGroup.addActor(ray);
                rays.add(ray);
            }
        }


        final float minAlpha = 0.4f;
        final float maxAlpha = 0.8f;

        for(int i = 0; i < rays.size; i++){
            final Image ray = rays.get(i);

            final float angleA = MathUtils.randomBoolean() ? 51 : -51;
            final float angleB = angleA * -1;
            ray.setRotation(angleA);
            ray.setScaleY(MathUtils.random(1, 1.8f));
            ray.getColor().a = 0;
            ray.setVisible(true);

            ray.addAction(new SequenceAction(
                    Actions.delay(i * 0.1f),
                    Actions.alpha(MathUtils.random(minAlpha, maxAlpha)),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            ray.addAction(Actions.forever(Actions.sequence(Actions.rotateTo(angleB, MathUtils.random(2.0f, 3.0f)), Actions.rotateTo(angleA, MathUtils.random(2.0f, 3.0f)))));
                            ray.addAction(Actions.forever(Actions.sequence(
                                    Actions.delay(MathUtils.random(0.5f, 1.5f)),
                                    Actions.fadeOut(0.5f),
                                    Actions.delay(MathUtils.random(0.5f, 1f)),
                                    Actions.alpha(MathUtils.random(minAlpha, maxAlpha), 0.5f)
                            )));
                        }
                    })
            ));
        }


        if(gift_glow == null) {
            gift_glow = new Image(AtlasRegions.gift_glow);
            boxGroup.addActor(gift_glow);
        }else{
            gift_glow.setVisible(true);
        }

        gift_glow.setY(gift_box_open.getY() + gift_box_open.getHeight() * boxYCoef);

        giftBoxAnim_4();

        if(glitters == null) {
            glitters = new Array<>();

            for (int i = 0; i < 10; i++) {
                Glitter glitter = new Glitter(0, gift_box_open.getY() , boxGroup.getWidth(), boxGroup.getHeight() * 2);
                glitter.setName("s");
                boxGroup.addActor(glitter);
                glitters.add(glitter);
            }
        }

        for(Glitter glitter : glitters){
            glitter.running = true;
            glitter.setVisible(true);
        }
    }






    private void giftBoxAnim_4(){
        if(rewardIcon == null) {
            rewardIcon = new Image(luckyWheel.rewardTypeToIconMappingBig.get(luckyWheel.selectedReward.reward));
            boxGroup.addActor(rewardIcon);
        }else{
            Drawable drawable = luckyWheel.rewardTypeToIconMappingBig.get(luckyWheel.selectedReward.reward);
            rewardIcon.setDrawable(drawable);
            rewardIcon.setSize(drawable.getMinWidth(), drawable.getMinHeight());
            rewardIcon.setVisible(true);
        }

        rewardIcon.setOrigin(Align.bottom);
        rewardIcon.setScale(0);
        rewardIcon.setX((boxGroup.getWidth() - rewardIcon.getWidth()) * 0.5f);
        rewardIcon.setY(boxGroup.getHeight() * boxYCoef);
        rewardIcon.addAction(new SequenceAction(
                Actions.delay(0.3f),
                Actions.parallel(
                        Actions.moveTo(rewardIcon.getX(), boxGroup.getHeight() * 0.8f),
                        Actions.scaleTo(1f, 1f, 0.5f, word.game.actions.Interpolation.backOut)
                )
        ));


        if(rewardLabel == null) {
            rewardLabelStyle = new Label.LabelStyle();
            String font = UIConfig.WHEEL_DIALOG_REWARD_COUNT_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            rewardLabelStyle.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);

            Sprite sprite = new Sprite(AtlasRegions.reward_count_bg);
            sprite.setColor(UIConfig.WHEEL_DIALOG_REWARD_COUNT_BACKGROUND_COLOR);
            rewardLabelStyle.background = new SpriteDrawable(sprite);
            rewardLabelStyle.fontColor = UIConfig.WHEEL_DIALOG_REWARD_COUNT_TEXT_COLOR;

            rewardLabel = new Label(" ", rewardLabelStyle);
            rewardLabel.setAlignment(Align.center);
            boxGroup.addActor(rewardLabel);
        }else{
            rewardLabel.setVisible(true);
        }


        rewardLabel.setX((boxGroup.getWidth() - rewardLabel.getWidth()) * 0.5f);
        rewardLabel.setY(rewardIcon.getY());
        rewardLabel.setText(luckyWheel.selectedReward.text);
        rewardLabel.setVisible(true);
        rewardLabel.addAction(Actions.moveTo(rewardLabel.getX(), -boxGroup.getHeight() * 0.8f, 1.0f, Interpolation.bounceOut));

        giftBoxAnim_5();
    }





    private void giftBoxAnim_5(){
        giftBoxContainer.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run(new Runnable() {

                    @Override
                    public void run() {
                        if(tapToCollect == null) {
                            Label.LabelStyle style = new Label.LabelStyle();
                            style.fontColor = UIConfig.WHEEL_DIALOG_TAP_TO_COLLECT_TEXT_COLOR;
                            String font = UIConfig.WHEEL_DIALOG_TAP_TO_COLLECT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
                            style.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);

                            tapToCollect = new Label(LanguageManager.get("tap_to_collect"), style);
                            tapGroup = new Group();
                            tapGroup.addActor(tapToCollect);
                            tapGroup.setSize(tapToCollect.getWidth(), tapToCollect.getHeight());
                            tapGroup.setOrigin(Align.center);
                            tapGroup.setX((giftBoxContainer.getWidth() - tapGroup.getWidth()) * 0.5f);
                            tapGroup.setY(giftBoxContainer.getHeight() * 0.25f);
                            giftBoxContainer.addActor(tapGroup);
                            UiUtil.pulsate(tapGroup);
                        }else{
                            tapToCollect.setVisible(true);
                        }
                        giftBoxContainer.addListener(touchListener);
                        getStage().getRoot().setTouchable(Touchable.enabled);
                    }
                })
        ));

    }




    InputListener touchListener = new InputListener() {

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            getStage().getRoot().setTouchable(Touchable.disabled);
            collectReward();
        }
    };





    private void collectReward() {
        Slice selection = luckyWheel.selectedReward;

        ItemContent content = new ItemContent();

        if (selection.reward == COINS){
            content.coins = selection.quantity;
        }else if (selection.reward == SINGLE_RANDOM_REVEAL){
            content.singleRandomReveal = selection.quantity;
        }else if (selection.reward == FINGER_REVEAL){
            content.fingerReveal = selection.quantity;
        }else if (selection.reward == MULTI_RANDOM_REVEAL){
            content.multiRandomReveal = selection.quantity;
        }else if(selection.reward == ROCKET_REVEAL){
            content.rocketReveal = selection.quantity;
        }

        screen.updateCoinsAndHints(content);

        closeGiftBox();
    }





    private void closeGiftBox(){
        giftBoxContainer.addAction(Actions.sequence(
                Actions.fadeOut(0.3f),
                Actions.run(new Runnable() {

                    @Override
                    public void run() {
                        confetti.running = false;
                        for(Glitter glitter : glitters){
                            glitter.running = false;
                            glitter.clearActions();
                            glitter.setVisible(false);
                        }
                        for(Image ray : rays){
                            ray.clearActions();
                            ray.setVisible(false);
                        }

                        if(tapToCollect != null) {
                            tapToCollect.clearActions();
                            tapToCollect.setVisible(false);
                        }

                        rewardIcon.setVisible(false);
                        rewardLabel.setVisible(false);
                        gift_glow.setVisible(false);
                        closeButton.setDisabled(false);
                        changeSpinButton();
                        if(actualSpinCount == 0)sayGoodbye();
                        giftBoxContainer.setVisible(false);
                        getStage().getRoot().setTouchable(Touchable.enabled);
                    }
                })
        ));

    }





    private void sayGoodbye(){
        luckyWheel.setVisible(false);
        spinButton.setVisible(false);

        rewardLabel.remove();
        rewardLabelStyle.background = null;
        content.addActor(rewardLabel);

        String font = UIConfig.WHEEL_DIALOG_COME_BACK_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;

        Label.LabelStyle style = new Label.LabelStyle();

        style.font = screen.wordConnectGame.resourceManager.get(font, BitmapFont.class);
        style.fontColor = UIConfig.WHEEL_DIALOG_COME_BACK_TEXT_COLOR;
        rewardLabel.setStyle(style);
        rewardLabel.setText(LanguageManager.get("spin_tomorrow"));
        rewardLabel.setWidth(content.getWidth() * 0.8f);
        rewardLabel.setWrap(true);
        rewardLabel.setAlignment(Align.center);
        rewardLabel.setX((content.getWidth() - rewardLabel.getWidth()) * 0.5f);
        rewardLabel.setY((content.getHeight() - rewardLabel.getHeight()) * 0.5f);
        rewardLabel.getColor().a = 0;
        rewardLabel.setVisible(true);
        rewardLabel.addAction(Actions.fadeIn(0.3f));
    }





    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        if(backgroundColor != null) Pools.free(backgroundColor);
        getStage().getRoot().setTouchable(Touchable.enabled);
        setVisible(false);
        if(screen instanceof GameScreen) {
            ((GameScreen) screen).resumeIdleTimer();
        }
        screen.nullifyDialog(getDialogId());
    }






    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color color = getColor();

        if(confetti != null && confetti.running) {
            if(giftBoxContainer != null)
                batch.setColor(color.r, color.g, color.b,  giftBoxContainer.getColor().a);

            confetti.render(batch);
        }

        batch.setColor(color.r, color.g, color.b, 1);
    }
}
