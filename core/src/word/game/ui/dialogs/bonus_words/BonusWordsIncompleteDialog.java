package word.game.ui.dialogs.bonus_words;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.StringBuilder;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.graphics.NinePatches;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;
import word.game.ui.ProgressBar;
import word.game.ui.dialogs.BaseDialog;
import word.game.ui.tutorial.Tutorial;
import word.game.ui.tutorial.TutorialBooster;


public class BonusWordsIncompleteDialog extends BaseDialog{


    private ProgressBar progressBar;
    private Label countLabel;
    private Label wordsLabel;
    private GameScreen gameScreen;
    private TutorialBooster tutorialBooster;

    public BonusWordsIncompleteDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);
        gameScreen = (GameScreen)screen;
        content.setSize(width * 0.8f, height * 0.8f);

        setContentBackground();
        String font = UIConfig.DIALOG_BODY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle bodyTextStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_BODY_TEXT_COLOR);

        setTitleLabel(LanguageManager.get("extra_words_incomplete"));

        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        Image boxClosed = new Image(AtlasRegions.giftbox_closed);
        boxClosed.setOrigin(Align.center);
        boxClosed.setX((content.getWidth() - boxClosed.getWidth()) * 0.5f);
        boxClosed.setY(titleContainer.getY() - boxClosed.getHeight() * 1.3f);
        content.addActor(boxClosed);

        Image badge = new Image(AtlasRegions.wave_badge);
        Group badgeGroup = new Group();
        badgeGroup.setSize(badge.getWidth(), badge.getHeight());
        badgeGroup.setOrigin(Align.center);
        badgeGroup.addActor(badge);
        badgeGroup.setX((boxClosed.getX() - badgeGroup.getWidth()) * 0.5f);
        badgeGroup.setY(boxClosed.getY() + (boxClosed.getHeight() - badgeGroup.getHeight()) * 0.5f);

        Label.LabelStyle badgeStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(ResourceManager.fontSemiBoldShadow, BitmapFont.class), UIConfig.BWD_BADGE_TEXT_COLOR);
        Label gain = new Label("+" + Integer.toString(GameConfig.NUMBER_OF_COINS_AWARDED_FOR_BONUS_WORDS_REWARD), badgeStyle);
        gain.setAlignment(Align.center);

        gain.setX((badgeGroup.getWidth() - gain.getWidth()) * 0.5f);
        gain.setY(badgeGroup.getHeight() * 0.15f);
        badgeGroup.addActor(gain);

        badgeGroup.setRotation(20);
        content.addActor(badgeGroup);

        progressBar = new ProgressBar(AtlasRegions.bonus_bar_bg, AtlasRegions.bonus_words_bar_track);
        progressBar.setOrigin(Align.center);
        progressBar.setX((content.getWidth() - progressBar.getWidth()) * 0.5f);
        progressBar.setY(boxClosed.getY() - progressBar.getHeight() * 1.5f);
        content.addActor(progressBar);

        countLabel = new Label(LanguageManager.get("extra_words_collected"), bodyTextStyle);
        countLabel.setFontScale(progressBar.getWidth() / countLabel.getWidth());
        countLabel.setY((progressBar.getY() - countLabel.getHeight() * 1.3f));
        content.addActor(countLabel);

        Image wordsGroupBg = new Image(NinePatches.iap_card2);
        wordsGroupBg.setColor(UIConfig.BWD_WORDS_BG_COLOR);

        Group wordsGroup = new Group();
        wordsGroup.addActor(wordsGroupBg);
        wordsGroup.setWidth(progressBar.getWidth());
        wordsGroup.setHeight(countLabel.getY() * 0.85f);
        wordsGroup.setOrigin(Align.center);

        wordsGroup.setX((content.getWidth() - wordsGroup.getWidth()) * 0.5f);
        wordsGroup.setY((countLabel.getY() - wordsGroup.getHeight()) * 0.5f);
        content.addActor(wordsGroup);


        wordsGroupBg.setSize(wordsGroup.getWidth(), wordsGroup.getHeight());


        Label.LabelStyle wordsTitleStyle = new Label.LabelStyle();
        wordsTitleStyle.font = screen.wordConnectGame.resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class);
        wordsTitleStyle.fontColor = UIConfig.BWD_WORDS_TITLE_COLOR;

        Label thisLabel = new Label(LanguageManager.get("extra_words_in_this_level"), wordsTitleStyle);
        thisLabel.setFontScale(0.8f);
        thisLabel.setAlignment(Align.center);
        float maxLabelWidth = wordsGroup.getWidth() * 0.9f;
        thisLabel.setWidth(maxLabelWidth);
        thisLabel.setWrap(true);



        Table labelTable = new Table();
        labelTable.setWidth(maxLabelWidth);
        labelTable.add(thisLabel).width(maxLabelWidth);
        labelTable.pack();



        labelTable.setX((wordsGroup.getWidth() - labelTable.getWidth()) * 0.5f);
        labelTable.setY(wordsGroup.getHeight() - labelTable.getHeight() * 1.05f);
        wordsGroup.addActor(labelTable);

        Label.LabelStyle wordsStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), UIConfig.BWD_WORDS_TEXT_COLOR);
        wordsLabel = new Label(" ", wordsStyle);
        wordsLabel.setAlignment(Align.center);
        wordsLabel.setWrap(true);
        if(wordsLabel.getWidth() > wordsGroup.getWidth())
            wordsLabel.setFontScale(wordsGroup.getWidth() * 0.9f);

        ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle();
        paneStyle.vScrollKnob = new TextureRegionDrawable(AtlasRegions.rect);

        ScrollPane pane = new ScrollPane(wordsLabel, paneStyle);
        pane.setScrollbarsVisible(false);
        pane.setSize(wordsGroup.getWidth(), labelTable.getY() * 0.9f);
        pane.setY(wordsGroup.getHeight() * 0.03f);
        pane.setupFadeScrollBars(0,0);

        wordsGroup.addActor(pane);
    }







    @Override
    public void show() {
        super.show();

        int current = GameData.getExtraWordsCount();
        int target = GameConfig.NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD;
        updateViewWithData((float)current / (float)target, current, target);
    }






    public void checkTutorial() {

        if(GameData.isExtraWordsTutorialDisplayed1() && !GameData.isExtraWordsTutorialDisplayed2()){
            gameScreen.tutorial = new TutorialBooster(screen);
            screen.stage.addActor(gameScreen.tutorial);
            gameScreen.tutorial.paddingX = 1.05f;
            gameScreen.tutorial.paddingY = 1.4f;

            Actor dummy = new Actor();
            dummy.setSize(progressBar.getWidth(), progressBar.getHeight());
            dummy.setPosition(progressBar.getX(), progressBar.getY());
            content.addActor(dummy);

            gameScreen.tutorial.highlightActor(dummy, Tutorial.Shape.RECT);
            gameScreen.tutorial.showText(LanguageManager.format("bonus_word_tutorial_2", GameConfig.NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD));

            Vector2 pos = progressBar.localToActorCoordinates(gameScreen.tutorial, new Vector2());
            gameScreen.tutorial.textContainer.setY(pos.y - gameScreen.tutorial.textContainer.getHeight() - progressBar.getHeight());
            tutorialBooster = (TutorialBooster)gameScreen.tutorial;
            tutorialBooster.setGotIt(LanguageManager.get("got_it"));
            gameScreen.tutorial.fadeIn(null);
            tutorialBooster.tutorialSaver = new Tutorial.TutorialSaver() {
                @Override
                public void save() {
                    tutorialBooster.gotit.setDisabled(true);
                    GameData.setExtraWordsTutorialComplete2();
                }
            };
            tutorialBooster.setGotItListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tutorialBooster.gotit.setDisabled(true);
                    GameData.setExtraWordsTutorialComplete2();
                    gameScreen.tutorial.fadeOut(gameScreen.tutorialRemover, true);
                }
            });
        }
    }





    private String getWordList(){
        Array<String> words = GameData.getExtraWords();
        StringBuilder sb = new StringBuilder();
        String lineBreak = "";

        for(int i = 0; i < words.size; i++){
            sb.append(lineBreak);
            sb.append(words.get(i));
            if(lineBreak.isEmpty())
                lineBreak = "\n";
        }
        return sb.toString();
    }





    public void updateViewWithData(float percent, int current, int target){
        progressBar.setPercent(percent);
        String text = LanguageManager.format("extra_words_collected", current, target);
        countLabel.setText(text);
        GlyphLayout countLayout = Pools.obtain(GlyphLayout.class);
        countLayout.setText(countLabel.getStyle().font, text);
        countLabel.setFontScale(progressBar.getWidth() / countLabel.getWidth());
        Pools.free(countLayout);

        countLabel.setX((content.getWidth() - countLabel.getWidth() * countLabel.getFontScaleX()) * 0.5f);

        if(wordsLabel != null)
            wordsLabel.setText(getWordList());
    }






    @Override
    protected void openAnimFinished() {
        super.openAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
        checkTutorial();
    }




    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();
    }




}
