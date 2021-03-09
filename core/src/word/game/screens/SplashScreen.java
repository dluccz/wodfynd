package word.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import word.game.WordConnectGame;
import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.ui.board.CellView;
import word.game.ui.dial.DialButton;
import word.game.ui.dialogs.menu.LanguageDialog;
import word.game.util.UiUtil;


public class SplashScreen extends BaseScreen{

    private boolean loading;
    private Image loadingImg;
    private Image loadingBgImg;
    private float maxBarWidth;
    private Texture loading_bg, loadingTex;
    private Group group = new Group();


    public SplashScreen(WordConnectGame wordConnectGame) {
        super(wordConnectGame);
        ShaderProgram.pedantic = false;
    }





    @Override
    public void show() {
        super.show();

        ResourceManager.introBackground = UIConfig.getIntroScreenBackgroundImage(wordConnectGame);
        if(ResourceManager.introBackground != null){
            wordConnectGame.resourceManager.load(ResourceManager.introBackground, Texture.class);
            wordConnectGame.resourceManager.finishLoading();
            setBackground(UIConfig.INTRO_SCREEN_BACKGROUND_COLOR, ResourceManager.introBackground);
        }

        loading_bg = new Texture(Gdx.files.internal("textures/loading_bg.png"));

        int w = loading_bg.getWidth() / 3;

        NinePatch bgPatch = new NinePatch(loading_bg, w, w, w, w);

        loadingBgImg = new Image(bgPatch);

        if(UiUtil.isScreenWide()) loadingBgImg.setWidth(stage.getWidth() * 0.3f);
        else loadingBgImg.setWidth(stage.getWidth() * 0.5f);

        loadingBgImg.setHeight( 35 * Gdx.graphics.getDensity() * ResourceManager.scaleFactor);

        group.addActor(loadingBgImg);
        group.setSize(loadingBgImg.getWidth(), loadingBgImg.getHeight());
        group.setOrigin(Align.center);
        group.setX((stage.getWidth() - group.getWidth()) * 0.5f);
        group.setY(stage.getHeight() * 0.2f);
        stage.addActor(group);

        loadingTex = new Texture(Gdx.files.internal("textures/loading.png"));
        w = loadingTex.getWidth() / 3;
        NinePatch loadingPatch = new NinePatch(loadingTex, w, w, w, w);
        loadingImg = new Image(loadingPatch);
        loadingImg.setColor(UIConfig.LOADING_BAR_COLOR);
        loadingImg.setWidth(0);
        loadingImg.setHeight(loadingBgImg.getHeight() * 0.8f);
        group.addActor(loadingImg);

        float margin = (loadingBgImg.getHeight() - loadingImg.getHeight()) * 0.52f;
        loadingImg.setPosition(margin, margin);
        maxBarWidth = group.getWidth() - margin * 2f;

        loadAssets();
    }








    private void loadAssets(){
        GameData.resourceManager = wordConnectGame.resourceManager;
        I18NBundle.setSimpleFormatter(true);
        String localeCode = LanguageManager.getSelectedLocaleCode();

        if(localeCode != null){
            setNewLanguage(localeCode);
        }


        ResourceManager.ATLAS_1 = ResourceManager.resolveResolutionAwarePath(ResourceManager.ATLAS_1);
        ResourceManager.ATLAS_2 = ResourceManager.resolveResolutionAwarePath(ResourceManager.ATLAS_2);
        ResourceManager.ATLAS_3 = ResourceManager.resolveResolutionAwarePath(ResourceManager.ATLAS_3);
        ResourceManager.ATLAS_4 = ResourceManager.resolveResolutionAwarePath(ResourceManager.ATLAS_4);



        wordConnectGame.resourceManager.load(ResourceManager.ATLAS_1, TextureAtlas.class);
        wordConnectGame.resourceManager.load(ResourceManager.ATLAS_2, TextureAtlas.class);
        wordConnectGame.resourceManager.load(ResourceManager.ATLAS_3, TextureAtlas.class);
        wordConnectGame.resourceManager.load(ResourceManager.ATLAS_4, TextureAtlas.class);




        wordConnectGame.resourceManager.load(ResourceManager.SHADER_LINE, ShaderProgram.class, new ShaderProgramLoader.ShaderProgramParameter(){
            { vertexFile = ResourceManager.SHADER_VERTEX; }
        });

        wordConnectGame.resourceManager.load(ResourceManager.SHADER_DIAL, ShaderProgram.class, new ShaderProgramLoader.ShaderProgramParameter(){
            { vertexFile = ResourceManager.SHADER_VERTEX; }
        });

        wordConnectGame.resourceManager.load(ResourceManager.SHADER_OVERLAY, ShaderProgram.class, new ShaderProgramLoader.ShaderProgramParameter(){
            {vertexFile = ResourceManager.SHADER_VERTEX;}
        });




        ResourceManager.fontSemiBold = ResourceManager.resolveResolutionAwarePath(ResourceManager.fontSemiBold);
        wordConnectGame.resourceManager.load(ResourceManager.fontSemiBold, BitmapFont.class);

        ResourceManager.fontSemiBoldShadow = ResourceManager.resolveResolutionAwarePath(ResourceManager.fontSemiBoldShadow);
        wordConnectGame.resourceManager.load(ResourceManager.fontSemiBoldShadow, BitmapFont.class);

        ResourceManager.fontBlack = ResourceManager.resolveResolutionAwarePath(ResourceManager.fontBlack);
        wordConnectGame.resourceManager.load(ResourceManager.fontBlack, BitmapFont.class);

        ResourceManager.fontBoardAndDialFont = ResourceManager.resolveResolutionAwarePath(ResourceManager.fontBoardAndDialFont);
        wordConnectGame.resourceManager.load(ResourceManager.fontBoardAndDialFont, BitmapFont.class);


        wordConnectGame.resourceManager.load(ResourceManager.SFX_BLAST, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_HINT, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_MONSTER_JUMP, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_LEVEL_END, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_ROCKET, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_1, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_2, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_3, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_4, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_5, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_6, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_7, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SELECT_8, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SPIN_CLICK, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SUCCESS, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_WHEEL_SPIN, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_FOUND_BEFORE, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_BONUS_WORD, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_WRONG, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_SHUFFLE, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_NOTIFICATION, Sound.class);
        wordConnectGame.resourceManager.load(ResourceManager.SFX_HIT_BOOSTER, Sound.class);



        ResourceManager.gameBackground = UIConfig.getGameScreenBackgroundImage(GameData.findFirstIncompleteLevel());
        if(ResourceManager.gameBackground != null){
            wordConnectGame.resourceManager.load(ResourceManager.gameBackground, Texture.class);
        }

        loading = true;
    }





    private void checkLanguage(){

        if(GameConfig.availableLanguages == null || (GameConfig.availableLanguages.size() == 0)){
            Gdx.app.log("game.log", "No language has been configured in GameConfig.");
            return;
        }



        String localeCode = LanguageManager.getSelectedLocaleCode();
        if(localeCode == null){
            dispose();

            if(GameConfig.availableLanguages.size() > 1) {
                LanguageDialog languageDialog = new LanguageDialog(stage.getWidth(), stage.getHeight(), this, languageSelectionComplete);
                stage.addActor(languageDialog);
                languageDialog.show();
            }else{
                for(String code : GameConfig.availableLanguages.keySet()) {
                    setNewLanguage(code);
                    languageSelectionComplete.run();
                }
            }
        }else{
            LanguageManager.bundle = wordConnectGame.resourceManager.get(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);

            group.addAction(Actions.sequence(Actions.delay(0.1f), Actions.scaleTo(0f, 0f, 0.3f, Interpolation.sineIn), Actions.run(new Runnable() {
                @Override
                public void run() {
                    dispose();
                    wordConnectGame.setScreen(new IntroScreen(wordConnectGame));
                }
            })));

        }
    }





    @Override
    public void dispose() {
        super.dispose();

        loadingImg.remove();
        loadingBgImg.remove();

        loading_bg.dispose();
        loadingTex.dispose();
    }





    @Override
    public void render(float delta) {
        super.render(delta);

        if(loading){
            update();
        }
    }






    private void update() {
        wordConnectGame.resourceManager.update();
        float progress = wordConnectGame.resourceManager.getProgress();

        loadingImg.setWidth(maxBarWidth * progress);

        if(progress == 1f){
           loading = false;

            AtlasRegions.init(wordConnectGame.resourceManager);
            NinePatches.init(wordConnectGame.resourceManager);
            GameConfig.setUpIAPToItemMapping();

            BitmapFont font1 = wordConnectGame.resourceManager.get(ResourceManager.fontSemiBoldShadow, BitmapFont.class);
            font1.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            BitmapFont font2 = wordConnectGame.resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class);
            font2.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            BitmapFont gameFont = wordConnectGame.resourceManager.get(ResourceManager.fontBoardAndDialFont, BitmapFont.class);
            gameFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            ConfigProcessor.muted = GameData.isGameMuted();

            CellView.labelStyleSolved = new Label.LabelStyle(gameFont, UIConfig.GAME_GRID_LETTER_COLOR_SOLVED);
            CellView.labelStyleRevealed = new Label.LabelStyle(gameFont, UIConfig.GAME_GRID_LETTER_COLOR_REVEALED);

            DialButton.labelStyleUp = new Label.LabelStyle();
            DialButton.labelStyleUp.font = gameFont;
            DialButton.labelStyleDown = new Label.LabelStyle();
            DialButton.labelStyleDown.font = gameFont;

            checkLanguage();

        }


    }


}
