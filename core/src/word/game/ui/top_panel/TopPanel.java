package word.game.ui.top_panel;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;
import word.game.util.UiUtil;


public class TopPanel extends Group {


    public CoinView coinView;
    public TopComboAndLevelDisplay topComboDisplay;
    public BaseScreen screen;
    public ImageButton btnMenu;
    public ImageButton backBtn;
    private float width;


    public TopPanel(BaseScreen screen, float width){
        this.screen = screen;
        this.width = width;
        ResourceManager resourceManager = screen.wordConnectGame.resourceManager;

        setWidth(width);
        setHeight(AtlasRegions.coin_view_bg.originalHeight * (UiUtil.isScreenWide() ? UIConfig.MARGIN_TOP_WIDE_SCREEN : UIConfig.MARGIN_TOP_NORMAL_SCREEN));

        if(screen instanceof GameScreen) {
            topComboDisplay = new TopComboAndLevelDisplay(resourceManager);
            topComboDisplay.setY((getHeight() - topComboDisplay.getHeight()) * 0.5f);
            addActor(topComboDisplay);

            if(!GameConfig.SKIP_INTRO) {
                backBtn = new ImageButton(new TextureRegionDrawable(AtlasRegions.back_up), new TextureRegionDrawable(AtlasRegions.back_down));
                addActor(backBtn);
                backBtn.setX(0);
                backBtn.setY((getHeight() - backBtn.getHeight()) * 0.5f);
                backBtn.addListener(((GameScreen) screen).gotoIntroScreen);
            }
        }

        coinView = new CoinView(screen);
        coinView.setX(width - coinView.getWidth());
        coinView.setY((getHeight() - coinView.getHeight()) * 0.5f);
        addActor(coinView);

        boolean inEu = screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isUserInEU();
        if(ConfigProcessor.isMenuEnabled(inEu, GameConfig.availableLanguages.size() > 1)) {
            btnMenu = new ImageButton(new TextureRegionDrawable(AtlasRegions.settings_up), new TextureRegionDrawable(AtlasRegions.settings_down));
            addActor(btnMenu);
            if(backBtn != null) btnMenu.setX(backBtn.getX() + backBtn.getWidth() * 1.2f);
            else btnMenu.setX(0);

            btnMenu.setY((getHeight() - btnMenu.getHeight()) * 0.5f);
        }

        if(topComboDisplay != null ) {
            float leftMost = coinView.getWidth();
            float centerWidth = coinView.getX() - leftMost;
            topComboDisplay.setWidth(centerWidth);
            topComboDisplay.setX(leftMost);
            topComboDisplay.setComboCount(0, null);
        }


    }





    public void addMenuButtonListener(ChangeListener changeListener){
        if(btnMenu != null) btnMenu.addListener(changeListener);
    }


}
