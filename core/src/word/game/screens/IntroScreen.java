package word.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;

import word.game.WordConnectGame;
import word.game.config.GameConfig;
import word.game.config.UIConfig;

import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;


import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.Constants;
import word.game.model.GameData;

import word.game.ui.dialogs.ConfirmDialog;
import word.game.ui.tutorial.Tutorial;

import word.game.util.UiUtil;


public class IntroScreen extends BaseScreen{


    private Image logo;
    public TextButton playButton;


    public IntroScreen(WordConnectGame wordConnectGame) {
        super(wordConnectGame);
    }


    @Override
    public void show() {

        LanguageManager.updateSelectedLanguage();

        super.show();


        if(GameConfig.DEBUG_LEVEL_INDEX > -1) {
            GameData.updateFirstIncompleteLevelIndex(GameConfig.DEBUG_LEVEL_INDEX);
            GameData.clearTileStates();
            GameData.clearSavedSolvedWordsJson();
            GameData.clearWordsWithRocket();
            GameData.clearExtraWords();
            GameData.saveComboCount(0);
            GameData.saveComboReward(0);
        }



        int firstIncompleteLevel = GameData.findFirstIncompleteLevel();

        if(GameConfig.SKIP_INTRO){
            GameData.saveTutorialStep(Constants.TUTORIAL_PLAY_BUTTON);
            wordConnectGame.setScreen(new GameScreen(wordConnectGame));
            return;
        }




        setBackground(UIConfig.INTRO_SCREEN_BACKGROUND_COLOR, UIConfig.getIntroScreenBackgroundImage(wordConnectGame));

        setTopPanel();
        topPanel.setY(stage.getHeight());

        logo = new Image(AtlasRegions.splash_logo);
        logo.setOrigin(Align.center);
        logo.setScale(0);
        logo.setX((stage.getWidth() - logo.getWidth()) * 0.5f);
        logo.setY(stage.getHeight() * 0.6f);
        stage.addActor(logo);


        if(LanguageManager.locale.LevelCount > 0) {
            createPlayButton(firstIncompleteLevel);
        }else {
            Gdx.app.log("game.log", "You haven't generated any levels!");
            return;
        }
        animateIn();
    }



    private void createPlayButton(int firstIncompleteLevel){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        String font = UIConfig.INTRO_PLAY_BUTTON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        buttonStyle.font = wordConnectGame.resourceManager.get(font, BitmapFont.class);
        buttonStyle.fontColor = UIConfig.INTRO_PLAY_BUTTON_TEXT_COLOR;
        NinePatch rUp = NinePatches.play_r_up;
        buttonStyle.up = new NinePatchDrawable(rUp);
        buttonStyle.down = new NinePatchDrawable(NinePatches.play_r_down);

        String label = null;

        if(firstIncompleteLevel == LanguageManager.locale.LevelCount){
            label = LanguageManager.get("to_be_continued");
        } else {
            label = LanguageManager.format("play_label", firstIncompleteLevel + 1);
        }

        playButton = new TextButton(label, buttonStyle);
        playButton.getLabel().setFontScale(UIConfig.INTRO_PLAY_BUTTON_FONT_SCALE);
        playButton.setWidth(playButton.getLabel().getWidth() + rUp.getLeftWidth() * UIConfig.INTRO_PLAY_BUTTON_WIDTH_COEF);
        playButton.setTransform(true);

        playButton.setOrigin(Align.center);
        playButton.setScale(0);
        playButton.setX((stage.getWidth() - playButton.getWidth()) * 0.5f);
        playButton.setY(stage.getHeight() * 0.3f);
        if(label.equals(LanguageManager.get("to_be_continued"))) playButton.setDisabled(true);
        stage.addActor(playButton);


        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stage.getRoot().setTouchable(Touchable.disabled);
                playButton.clearActions();
                playButton.setScale(1f);

                if(tutorial != null) {
                    GameData.saveTutorialStep(Constants.TUTORIAL_PLAY_BUTTON);
                    tutorial.fadeOut(null, true);
                }

                animateOut(new Runnable() {
                    @Override
                    public void run() {
                        wordConnectGame.setScreen(new GameScreen(wordConnectGame));
                    }
                });

            }
        });
    }







    private void animateIn(){
        topPanel.addAction(Actions.moveBy(0, -topPanel.getHeight(), 0.2f, word.game.actions.Interpolation.backOut));

        UiUtil.actorAnimIn(logo, 0.2f, null);
        UiUtil.actorAnimIn(playButton, 0.3f, animateInFinished);
    }




    private Runnable animateInFinished = new Runnable() {
        @Override
        public void run() {

            if(UIConfig.INTERACTIVE_TUTORIAL_ENABLED && GameData.getTutorialStep() == 0){
                tutorialStep_1();
            }else{
                if(!checkWheelDialogTiming()){
                    checkRateStatus();
                }
            }
            if(UIConfig.INTRO_PLAY_BUTTON_PULSATE) UiUtil.pulsate(playButton);
        }
    };





    private void tutorialStep_1(){
        tutorial = new Tutorial(this);
        tutorial.paddingX = 1.0f;
        tutorial.paddingY = 1.2f;
        tutorial.step = Constants.TUTORIAL_PLAY_BUTTON;
        stage.addActor(tutorial);
        tutorial.getColor().a = 0f;
        tutorial.highlightActor(playButton, Tutorial.Shape.RECT);
        tutorial.addAction(Actions.fadeIn(.3f));
        tutorial.indicateActor(90);
    }




    private void animateOut(Runnable callback){
        topPanel.addAction(Actions.moveBy(0, topPanel.getHeight(), 0.2f, word.game.actions.Interpolation.backIn));
        UiUtil.actorAnimOut(logo, 0.1f, null);
        UiUtil.actorAnimOut(playButton, 0.2f, 0.08f, callback);
    }





    private void checkRateStatus(){
        if(GameConfig.SHOW_RATE_DIALOG && wordConnectGame.rateUsLauncher != null){

            if(GameConfig.DEBUG_RATE_US){
                showRateDialog();
                return;
            }

            Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
            if(prefs.getBoolean(Constants.KEY_DONT_SHOW_AGAIN, false)){
                return;
            }

            int launchCount = prefs.getInteger(Constants.KEY_APP_LAUNCH_COUNT, 0);
            launchCount++;
            prefs.putInteger(Constants.KEY_APP_LAUNCH_COUNT, launchCount);

            long firstLaunchDate = prefs.getLong(Constants.KEY_APP_FIRST_LAUNCH_DATE, 0);
            if(firstLaunchDate == 0){
                prefs.putLong(Constants.KEY_APP_FIRST_LAUNCH_DATE, TimeUtils.millis());
            }

            if(launchCount >= GameConfig.APP_LAUNCHES_BEFORE_RATE){
                if(TimeUtils.millis() >= firstLaunchDate + GameConfig.DAYS_TO_ELAPSE_BEFORE_RATE  * 24 * 60 * 60 * 1000){
                    showRateDialog();
                }
            }

            prefs.flush();
        }
    }





    private void showRateDialog(){
        ConfirmDialog confirmDialog = new ConfirmDialog(
                stage.getWidth(),
                stage.getHeight(),
                this,
                LanguageManager.get("rate_us_title"),
                LanguageManager.get("rate_us_text"),
                LanguageManager.get("rate_button_label"),
                LanguageManager.get("later_button_label")
        );

        confirmDialog.setDialogId(Constants.CONFIRM_DIALOG_RATE_US);
        stage.addActor(confirmDialog);
        confirmDialog.show();

        confirmDialog.setConfirmCallback(new ConfirmDialog.ConfirmCallback() {
            @Override
            public void confirmClicked(String buttonLabel) {
                if(buttonLabel != null) {
                    if (buttonLabel.equals(LanguageManager.get("rate_button_label"))){
                        wordConnectGame.rateUsLauncher.launch();
                    }

                    Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                    prefs.putBoolean(Constants.KEY_DONT_SHOW_AGAIN, true);
                    prefs.flush();
                }
            }
        });
    }




    @Override
    protected boolean onBackPress() {

        boolean hasDialog = super.onBackPress();



        if(!hasDialog) wordConnectGame.appExit.exitApp();
        return false;
    }
}
