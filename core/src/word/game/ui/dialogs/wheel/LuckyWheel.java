package word.game.ui.dialogs.wheel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;



import java.util.HashMap;
import java.util.Map;

import word.game.actions.Interpolation;
import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;


import static word.game.ui.dialogs.wheel.RewardRevealType.COINS;
import static word.game.ui.dialogs.wheel.RewardRevealType.FINGER_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.MULTI_RANDOM_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.ROCKET_REVEAL;
import static word.game.ui.dialogs.wheel.RewardRevealType.SINGLE_RANDOM_REVEAL;


public class LuckyWheel extends Group {

    public Slice selectedReward;

    private Array<Integer> randomWeights;
    private Array<Integer> sectorAngles;
    private final int TOTAL_SLICES = 8;

    private Image[] leds;

    private Map<RewardRevealType, Drawable> rewardTypeToIconMappingSmall;
    public Map<RewardRevealType, Drawable> rewardTypeToIconMappingBig;

    private Group background;
    private ResourceManager resourceManager;
    private Image arrow;
    private Runnable spinFinished;

    public LuckyWheel(Runnable spinFinished, ResourceManager resourceManager){

        this.spinFinished = spinFinished;
        this.resourceManager = resourceManager;

        rewardTypeToIconMappingSmall = new HashMap<>();
        rewardTypeToIconMappingSmall.put(COINS, new TextureRegionDrawable(AtlasRegions.coin_small));
        rewardTypeToIconMappingSmall.put(SINGLE_RANDOM_REVEAL, new TextureRegionDrawable(AtlasRegions.random_hint_small));
        rewardTypeToIconMappingSmall.put(FINGER_REVEAL, new TextureRegionDrawable(AtlasRegions.finger_hint_small));
        rewardTypeToIconMappingSmall.put(MULTI_RANDOM_REVEAL, new TextureRegionDrawable(AtlasRegions.multi_random_hint_small));
        rewardTypeToIconMappingSmall.put(ROCKET_REVEAL, new TextureRegionDrawable(AtlasRegions.rocket_small));

        rewardTypeToIconMappingBig = new HashMap<>();
        rewardTypeToIconMappingBig.put(COINS, new TextureRegionDrawable(AtlasRegions.coin_big));
        rewardTypeToIconMappingBig.put(SINGLE_RANDOM_REVEAL, new TextureRegionDrawable(AtlasRegions.random_hint_big));
        rewardTypeToIconMappingBig.put(FINGER_REVEAL, new TextureRegionDrawable(AtlasRegions.finger_hint_big));
        rewardTypeToIconMappingBig.put(MULTI_RANDOM_REVEAL, new TextureRegionDrawable(AtlasRegions.multi_random_hint_big));
        rewardTypeToIconMappingBig.put(ROCKET_REVEAL, new TextureRegionDrawable(AtlasRegions.rocket_big));


        background = new Group();

        Image wheel = new Image(AtlasRegions.lucky_wheel);
        wheel.setOrigin(Align.center);
        background.addActor(wheel);
        background.setSize(wheel.getWidth(), wheel.getHeight());
        background.setOrigin(Align.center);
        addActor(background);

        setSize(background.getWidth(), background.getHeight());
        setOrigin(Align.center);

        leds = new Image[TOTAL_SLICES];

        Drawable ledDrawable = new TextureRegionDrawable(AtlasRegions.discParticle);
        createSlices(ledDrawable);

        setupArrow();
    }




    private void setupArrow(){
        arrow = new Image(AtlasRegions.wheel_arrow);
        arrow.setOrigin(arrow.getWidth() * 0.5f, arrow.getHeight() * 0.704980f);
        arrow.setX((getWidth() - arrow.getWidth()) * 0.5f);
        arrow.setY(getHeight() - arrow.getHeight() * 0.35f);
        addActor(arrow);
    }




    private void createSlices(Drawable ledDrawable){
        randomWeights = new Array<>();
        sectorAngles = new Array<>();

        for(int i = 0; i < GameConfig.slices.length; i++){
            populateSlice(i, GameConfig.slices[i], ledDrawable);

            for(int j = 0; j < GameConfig.slices[i].probability; j++){
                randomWeights.add(i);
            }
        }

    }





    private void populateSlice(int index, Slice slice, Drawable ledDrawable) {

        float angle = getAngleByIndex(index);
        sectorAngles.add(90 - (int)angle);

        Label.LabelStyle labelStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), index % 2 == 0 ? UIConfig.WHEEL_DIALOG_ITEM_QUANTITY_TEXT_COLOR_DARK: UIConfig.WHEEL_DIALOG_ITEM_QUANTITY_TEXT_COLOR_LIGHT);

        Label label = new Label(slice.text, labelStyle);

        Group group = new Group();
        group.addActor(label);
        group.setSize(label.getWidth(), label.getHeight());
        group.setOrigin(Align.center);
        group.setTransform(true);
        group.setRotation(angle - 90);

        float centerX = background.getWidth() * 0.5f;
        float centerY = background.getHeight() * 0.5f;

        float dx = centerX * MathUtils.cos(angle * MathUtils.degreesToRadians);
        float dy = centerY * MathUtils.sin(angle * MathUtils.degreesToRadians);

        float labelMargin = 0.7f;
        float x = centerX + dx * labelMargin - group.getWidth() * 0.5f;
        float y = centerY + dy * labelMargin - group.getHeight() * 0.5f;
        group.setPosition(x, y);
        background.addActor(group);

        ///////icon
        Image icon = new Image(rewardTypeToIconMappingSmall.get(slice.reward));
        icon.setOrigin(Align.center);

        float iconMargin = 0.45f;

        x = centerX + dx * iconMargin - icon.getWidth() * 0.5f;
        y = centerY + dy * iconMargin - icon.getHeight() * 0.5f;

        icon.setPosition(x, y);
        icon.setRotation(angle);
        background.addActor(icon);

        ///////Led

        Image led = new Image(ledDrawable);
        led.setColor(UIConfig.WHEEL_DIALOG_LIGHT_BULB_COLOR);

        angle -= 22.5;

        dx = centerX * MathUtils.cos(angle * MathUtils.degreesToRadians);
        dy = centerY * MathUtils.sin(angle * MathUtils.degreesToRadians);

        float ledMargin = 0.935f;
        x = centerX + dx * ledMargin - led.getWidth() * 0.5f;
        y = centerY + dy * ledMargin - led.getHeight() * 0.5f;
        led.setPosition(x, y);
        background.addActor(led);

        led.setVisible(index % 2 == 0);
        leds[index] = led;
    }




    private float getAngleByIndex(int index){
        return (float)index / TOTAL_SLICES * 360.0f;
    }



    public void spin(){
        spin1();
    }



    private void spin1() {
        Action rotateby = Actions.rotateBy(-25, 0.3f);

        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                spin2();
            }
        });

        background.addAction(new SequenceAction(rotateby, runnableAction));
    }




    private void spin2(){
        int fullCircles = 5;
        int rnd = MathUtils.random(0, randomWeights.size - 1);
        rnd = randomWeights.get(rnd);
        int randomFinalAngle = sectorAngles.get(rnd);
        float _finalAngle = (fullCircles * 360 + randomFinalAngle);

        selectedReward = GameConfig.slices[rnd];

	    float duration = MathUtils.random(3.0f, 6.0f);
        Action rotate = Actions.rotateTo(_finalAngle, duration + 1, Interpolation.cubicOut);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(spinFinished);
        SequenceAction sequenceAction = new SequenceAction(rotate, runnableAction);
        background.addAction(sequenceAction);
    }



    private SequenceAction sequenceAction;
    private RotateToAction rotateToAction1, rotateToAction2;
    private float time;




    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(leds.length == TOTAL_SLICES){
            time += Gdx.graphics.getDeltaTime();

            if(time > 0.5f){
                time = 0;

                for(int i = 0; i < leds.length; i++){
                    leds[i].setVisible(!leds[i].isVisible());
                }
            }
        }

        int sliceAngle = Math.round(360f / TOTAL_SLICES);

        if(arrow.getRotation() > -2.5f && Math.abs(background.getRotation() - sliceAngle * 2.5f) % sliceAngle <= 5){
            arrow.clearActions();
            if(rotateToAction1 == null) rotateToAction1 = new RotateToAction();
            else rotateToAction1.reset();
            rotateToAction1.setRotation(-30);
            rotateToAction1.setDuration(0.1f);

            if(rotateToAction2 == null) rotateToAction2 = new RotateToAction();
            else rotateToAction2.reset();
            rotateToAction2.setRotation(0);
            rotateToAction2.setDuration(0.05f);

            if(sequenceAction == null) sequenceAction = new SequenceAction();
            else sequenceAction.reset();
            sequenceAction.addAction(rotateToAction1);
            sequenceAction.addAction(rotateToAction2);
            arrow.addAction(sequenceAction);

            if(!ConfigProcessor.muted) {
                Sound click = resourceManager.get(ResourceManager.SFX_SPIN_CLICK, Sound.class);
                click.play(SoundConfig.SFX_WHEEL_SPIN_CLICK_VOLUME);
            }
        }
    }
}
