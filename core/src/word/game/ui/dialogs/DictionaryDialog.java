package word.game.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;


import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.net.WordMeaningProvider;
import word.game.net.WordMeaningRequest;
import word.game.screens.BaseScreen;


public class DictionaryDialog extends BaseDialog {


    private ClippingGroup mask;
    private Group allWords = new Group();
    private int index = 0;
    private Button leftArrow;
    private Button rightArrow;
    private ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle();
    private Label.LabelStyle wordTitlelabelStyle;
    private Label.LabelStyle meaninglabelStyle;
    private Image background;
    public static String[] words;
    private Rectangle clipBounds = new Rectangle(0,0,0,0);



    public DictionaryDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);

        content.setSize(width * 0.8f, height * 0.8f);
        setContentBackground();

        String font1 = UIConfig.DICTIONARY_DIALOG_WORD_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        wordTitlelabelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font1, BitmapFont.class), UIConfig.DICTIONARY_DIALOG_WORD_TEXT_COLOR);

        String font2 = UIConfig.DICTIONARY_DIALOG_MEANING_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        meaninglabelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font2, BitmapFont.class), UIConfig.DICTIONARY_DIALOG_MEANING_TEXT_COLOR);

        setTitleLabel(LanguageManager.get("dictionary"));

        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        leftArrow = dirButton(true);
        leftClick(leftArrow);
        leftArrow.setOrigin(Align.center);
        leftArrow.setX(content.getWidth() * 0.02f);
        leftArrow.setY((content.getHeight() - leftArrow.getHeight()) * 0.5f);
        content.addActor(leftArrow);

        rightArrow = dirButton(false);
        rightClick(rightArrow);
        rightArrow.setX(content.getWidth() * (1 - 0.02f) - rightArrow.getWidth());
        rightArrow.setY(leftArrow.getY());
        content.addActor(rightArrow);

        float maskWidth = (rightArrow.getX() - leftArrow.getX() ) * 0.8f;

        mask = new ClippingGroup();
        mask.setSize(maskWidth, titleContainer.getY() - titleContainer.getHeight());
        mask.setX((content.getWidth() - mask.getWidth()) * 0.5f);
        mask.setY(titleContainer.getHeight() * 0.5f);


        if(background == null){
            background = new Image(NinePatches.round_rect_shadow);
            background.setSize(mask.getWidth(), mask.getHeight() );
            background.setColor(UIConfig.DICTIONARY_DIALOG_CENTER_BG_COLOR);
            background.setPosition(mask.getX(), mask.getY());
            content.addActor(background);
        }
        content.addActor(mask);

        leftArrow.setZIndex(mask.getZIndex() + 1);
        rightArrow.setZIndex(mask.getZIndex() + 2);

        clipBounds.x = mask.getX();
        clipBounds.y = mask.getY();
        clipBounds.width = mask.getWidth();
        clipBounds.height = mask.getHeight();
    }





    @Override
    public void show(){
        getStage().getRoot().setTouchable(Touchable.disabled);
        adjustButtons();

        Color cl = leftArrow.getColor();
        Color cr = rightArrow.getColor();

        if(words.length == 1){
            cl.a = 0.3f;
            cr.a = 0.3f;
            leftArrow.setDisabled(true);
            rightArrow.setDisabled(true);
        }

        leftArrow.setColor(cl);
        rightArrow.setColor(cr);

        super.show();
    }




    @Override
    protected void openAnimFinished() {
        super.openAnimFinished();
        getAllWordMeanings();
        getStage().getRoot().setTouchable(Touchable.enabled);
    }




    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();

        allWords.clearChildren();
        allWords.setX(0);
        index = 0;
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();
        setVisible(false);
    }





    private void leftClick(Button button){
        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(index == 0)
                    return;

                index--;
                pageMotion(mask.getWidth());
            }
        });
    }






    private void rightClick(Button button){
        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(index == words.length - 1)
                    return;

                index++;
                pageMotion(-mask.getWidth());
            }
        });
    }





    private void pageMotion(float x){
        getStage().getRoot().setTouchable(Touchable.disabled);
        adjustButtons();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getStage().getRoot().setTouchable(Touchable.enabled);
            }
        };
        RunnableAction end = new RunnableAction();
        end.setRunnable(r);

        SequenceAction sequenceAction = new SequenceAction(Actions.moveBy(x, 0, 0.4f, Interpolation.smooth2), end);
        allWords.addAction(sequenceAction);
    }





    private void adjustButtons(){
        Color cl = leftArrow.getColor();
        Color cr = rightArrow.getColor();

        if(index == 0){
            cl.a = 0.3f;
            cr.a = 1;
            leftArrow.setDisabled(true);
            rightArrow.setDisabled(false);
        }else if(index == words.length - 1){
            cl.a = 1;
            cr.a = 0.3f;
            leftArrow.setDisabled(false);
            rightArrow.setDisabled(true);
        }else{
            cl.a = 1;
            cr.a = 1;
            leftArrow.setDisabled(false);
            rightArrow.setDisabled(false);
        }
    }





    private Button dirButton(boolean left){
        TextureRegionDrawable bg = new TextureRegionDrawable(left ? AtlasRegions.arrow_left : AtlasRegions.arrow_right);
        ImageButton button = new ImageButton(bg);
        button.getImage().setColor(UIConfig.DICTIONARY_DIALOG_NAVIGATION_ARROW_COLOR);
        button.setOrigin(Align.center);
        return button;
    }





    private void getAllWordMeanings(){
        allWords.setSize(mask.getWidth() * words.length, mask.getHeight());

        float x = 0;
        float y = 0;

        for(int i = 0; i < words.length; i++){
            Group group = new Group();
            group.setWidth(mask.getWidth());
            group.setHeight(mask.getHeight());
            getWordContent(group, words[i]);
            group.setPosition(x, y);
            allWords.addActor(group);
            x += group.getWidth();
        }

        mask.addActor(allWords);
    }





    private void getWordContent(final Group group, final String word){
        Image loadingCircle = new Image(AtlasRegions.loadingRegion);
        loadingCircle.setX((group.getWidth() - loadingCircle.getWidth()) * 0.5f);
        loadingCircle.setY((group.getHeight() - loadingCircle.getHeight()) * 0.5f + (mask.getY()));
        loadingCircle.setOrigin(Align.center);
        loadingCircle.setColor(UIConfig.DICTIONARY_DIALOG_LOADING_CIRCLE_COLOR);
        loadingCircle.setName("s");
        group.addActor(loadingCircle);
        loadingCircle.addAction(Actions.forever(Actions.rotateBy(360, 2f)));

        String langCode = LanguageManager.locale.code;
        WordMeaningProvider provider = LanguageManager.wordMeaningProviderMap.get(langCode);
        WordMeaningRequest wordMeaningRequest = provider.get(langCode);

        wordMeaningRequest.request(word, new DictionaryCallback() {
            @Override
            public void onMeaning(String word, String meaning) {
                prepareWord(group, word, meaning);
            }

            @Override
            public void onError(String msg) {
                prepareWord(group, word, msg);
            }
        });
    }






    private void prepareWord(Group group, String word, String meaning){
        Label title = new Label(word, wordTitlelabelStyle);
        title.setFontScale(UIConfig.DICTIONARY_DIALOG_WORD_TEXT_SCALE);
        title.setAlignment(Align.center);
        title.setX((group.getWidth() - title.getWidth()) * 0.5f);
        group.addActor(title);


        float panelWidth = group.getWidth() * 0.9f;
        float paneHeight = group.getHeight() * 0.85f;

        if(meaning.equals("Your search did not return any results")){
            meaning = LanguageManager.get("word_not_found");
        }

        Label text = new Label(meaning, meaninglabelStyle);
        text.setFontScale(0.9f);
        text.setOrigin(Align.bottom);
        text.setAlignment(Align.bottomLeft);
        text.setWrap(true);

        Table table = new Table();
        table.align(Align.bottomLeft);
        table.add(text).width(panelWidth * 0.9f).left();
        table.pack();

        Group container = new Group();
        container.setSize(group.getWidth(), Math.max(table.getHeight(), paneHeight));
        table.setX((container.getWidth() - table.getWidth()) * 0.5f);
        table.setY(container.getHeight() - table.getHeight());
        container.addActor(table);


        ScrollPane pane = new ScrollPane(container, paneStyle);
        pane.setSize(group.getWidth(), paneHeight);
        pane.setY(group.getHeight() * 0.02f);
        group.addActor(pane);

        float paneTop = pane.getY() + paneHeight;

        title.setY(paneTop + ((group.getHeight() - paneTop) - title.getHeight()) * 0.5f);


        Actor shader = group.findActor("s");
        if(shader != null){
            shader.remove();
            shader.setVisible(false);
        }

        if(word.equals(words[words.length - 1]))
            getStage().getRoot().setTouchable(Touchable.enabled);
    }






    class ClippingGroup extends Group{

        private Rectangle scissors = new Rectangle();


        @Override
        public void draw(Batch batch, float parentAlpha) {

            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            getStage().calculateScissors(clipBounds, this.scissors);
            batch.flush();
            if (ScissorStack.pushScissors(this.scissors)) {
                super.draw(batch, parentAlpha);
                batch.flush();
                ScissorStack.popScissors();
            }

            batch.setColor(color.r, color.g, color.b, 1);
        }
    }





    public interface DictionaryCallback{
        void onMeaning(String word, String meaning);
        void onError(String msg);
    }


}
