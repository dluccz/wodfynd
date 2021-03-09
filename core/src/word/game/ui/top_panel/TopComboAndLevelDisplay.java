package word.game.ui.top_panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pools;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.GameScreen;
import word.game.ui.dialogs.HowToPlayDialog;


public class TopComboAndLevelDisplay extends Group {


    private Label comboLabel;
    private Label levelLabel;
    private Animation<TextureRegion> blastAnimation;
    private float time = 0;
    private boolean anim;
    private GameScreen gameScreen;
    private HowToPlayDialog howToPlayDialog;


    public TopComboAndLevelDisplay(ResourceManager resourceManager){
        String font1 = UIConfig.LEVEL_NUMBER_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle levelStyle = new Label.LabelStyle(resourceManager.get(font1, BitmapFont.class), UIConfig.LEVEL_NUMBER_TEXT_COLOR);
        levelLabel = new Label(" ", levelStyle);
        addActor(levelLabel);

        String font2 = UIConfig.COMBO_REWARD_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle comboStyle = new Label.LabelStyle(resourceManager.get(font2, BitmapFont.class), UIConfig.COMBO_REWARD_TEXT_COLOR);
        comboLabel = new Label(" ", comboStyle);
        addActor(comboLabel);
        levelLabel.setY(comboLabel.getHeight());

        setHeight(comboLabel == null ? levelLabel.getHeight() : levelLabel.getHeight()  + comboLabel.getHeight());

        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(gameScreen != null){
                    openHowToPlay();
                }
            }
        });

    }






    private void openHowToPlay(){
        if(howToPlayDialog == null) {
            howToPlayDialog = new HowToPlayDialog(gameScreen.stage.getWidth(), gameScreen.stage.getHeight(), gameScreen);
            howToPlayDialog.setContent(
                    LanguageManager.get("how_to_play_desc1"),
                    LanguageManager.get("how_to_play_desc2"),
                    LanguageManager.get("how_to_play_desc3"),
                    AtlasRegions.howtoplay_1,
                    AtlasRegions.howtoplay_2,
                    AtlasRegions.howtoplay_3
            );
        }else{
            howToPlayDialog.setVisible(true);
        }
        gameScreen.stage.addActor(howToPlayDialog);
        howToPlayDialog.show();
    }





    public void setLevelNumber(int n){
        levelLabel.setText(LanguageManager.format("level", n));
        GlyphLayout levelLayout = Pools.obtain(GlyphLayout.class);
        levelLayout.setText(levelLabel.getStyle().font, levelLabel.getText());
        if(levelLayout.width >= getWidth() * 0.9f){
            levelLabel.setFontScale(getWidth() * 0.9f / levelLayout.width);
        }
        levelLabel.setX((getWidth() - levelLayout.width * levelLabel.getFontScaleX()) * 0.5f);
        Pools.free(levelLayout);
    }





    public void setGameScreen(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    private Runnable animationCallback;

    public void setComboCount(int n, Runnable animationCallback){
        this.animationCallback = animationCallback;

        comboLabel.setText(LanguageManager.format("combo_top", Math.max(n, 0)));
        GlyphLayout comboLayout = Pools.obtain(GlyphLayout.class);
        comboLayout.setText(comboLabel.getStyle().font, comboLabel.getText());
        if(comboLayout.width >= getWidth() * 0.9f){
            comboLabel.setFontScale(getWidth() * 0.9f / comboLayout.width);
        }
        comboLabel.setX((getWidth() - comboLayout.width * comboLabel.getFontScaleX()) * 0.5f);
        Pools.free(comboLayout);

        if(n > 0) {
            time = 0;
            anim = true;
        }else{
            if(animationCallback != null) animationCallback.run();
        }
    }



    public void setComboCountWithoutAnim(int n){
        comboLabel.setText(LanguageManager.format("combo_top", Math.max(0, n)));
    }



    public void reset(){
        comboLabel.setText(LanguageManager.format("combo_top", 0));
        anim = false;
        time = 0;
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.setColor(1,1,1,0.7f);

        if(anim){
            time += Gdx.graphics.getDeltaTime();

            if(blastAnimation == null) blastAnimation = new Animation<TextureRegion>(0.016666f, AtlasRegions.comboAnimation, Animation.PlayMode.NORMAL);
            int index = blastAnimation.getKeyFrameIndex(time);

            TextureRegion currentFrame = blastAnimation.getKeyFrame(time, false);
            float x = (getWidth() - currentFrame.getRegionWidth()) * 0.5f;
            float y = -currentFrame.getRegionHeight() * 0.5f;

            batch.draw(currentFrame, getX() + x, getY() + y, currentFrame.getRegionWidth() * 0.5f, currentFrame.getRegionHeight() * 0.5f, currentFrame.getRegionWidth(), currentFrame.getRegionHeight(), 2.0f, 2.0f, getRotation());

            if(index == blastAnimation.getKeyFrames().length - 1){
                anim = false;
                time = 0;
                if(animationCallback != null){
                    animationCallback.run();
                }
            }
        }else{
            time = 0;
        }
    }
}
