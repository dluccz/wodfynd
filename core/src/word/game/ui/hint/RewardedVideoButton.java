package word.game.ui.hint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import word.game.config.GameConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.screens.BaseScreen;


public class RewardedVideoButton extends Group {

    public ImageButton button;
    private Image glowImg;
    private long lastRewardedAdTime;
    private Label timeLabel;
    private boolean timer;
    private long millisToWait;
    private float counter;

    public RewardedVideoButton(BaseScreen screen){
        button = new ImageButton(new TextureRegionDrawable(AtlasRegions.rewarded_video_up), new TextureRegionDrawable(AtlasRegions.rewarded_video_down));
        addActor(button);

        setWidth(button.getWidth());
        setHeight(button.getHeight());

        if(screen.wordConnectGame.adManager.getIntervalBetweenRewardedAds() > 0){
            Label.LabelStyle style = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(ResourceManager.fontSemiBoldShadow, BitmapFont.class), Color.WHITE);
            timeLabel = new Label("00:00", style);
            timeLabel.setOrigin(Align.bottomLeft);
            timeLabel.setFontScale(0.6f);
            timeLabel.setX((getWidth() - timeLabel.getWidth() * timeLabel.getFontScaleX()) * 0.5f);
            timeLabel.setY(getHeight());
            timeLabel.setVisible(false);
            addActor(timeLabel);
            millisToWait = screen.wordConnectGame.adManager.getIntervalBetweenRewardedAds() * 1000;
        }

        glowImg = new Image(AtlasRegions.rewarded_video_glow);
        glowImg.setX(button.getX() + (button.getWidth() - glowImg.getWidth()) * 0.5f);
        glowImg.setY(button.getY() + (button.getHeight() - glowImg.getHeight()) * 0.5f);
        glowImg.setTouchable(Touchable.disabled);
        glowImg.getColor().a = 0;
        addActor(glowImg);
    }


    public void startRewardedGlow(){
        glowImg.addAction(Actions.forever(
                new SequenceAction(
                        Actions.delay(GameConfig.REWARDED_VIDEO_BUTTON_GLOW_INTERVAL),
                        Actions.fadeIn(0.5f),
                        Actions.fadeOut(0.5f)
                    )
                )
        );
    }






    public void stopRewardedGlow(){
        glowImg.clearActions();
        glowImg.getColor().a = 0f;
    }






    public void startTimer(long rewardTime){
        lastRewardedAdTime = rewardTime;
        GameData.saveLastRewardedAdTime(lastRewardedAdTime);
        counter = 0;
        timer = true;
    }



    public boolean timerRunning(){
        return timer;
    }


    public void stop(){
        timer = false;
    }





    public void flashText(){
        if(timeLabel == null) return;

        timeLabel.clearActions();

        SequenceAction sequenceAction = Actions.sequence(
                Actions.fadeOut(0.1f),
                Actions.fadeIn(0.1f)
        );

        timeLabel.addAction(Actions.repeat(3, sequenceAction));

    }




    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(timer && timeLabel != null){
            counter += Gdx.graphics.getDeltaTime();
            if(counter < 1f) return;

            long elapsed = TimeUtils.millis() - lastRewardedAdTime;
            long diff = millisToWait - elapsed;


            if(diff > 0) timeLabel.setVisible(true);

            diff = Math.max(0, diff);
            formatTime(diff);
            timeLabel.setX((getWidth() - timeLabel.getWidth() * timeLabel.getFontScaleX()) * 0.5f);

            if(diff <= 0){
                timer = false;
                timeLabel.setVisible(false);
            }
            counter = 0;
        }

    }





    private void formatTime(long time){
        int seconds = (int)((time / 1000) % 60);
        int minutes = (int)((time / (60 * 1000)) % 60);
        timeLabel.setText((minutes <= 9 ? "0" + minutes : minutes) + ":" + (seconds <= 9 ? "0" + seconds : seconds));
    }


}
