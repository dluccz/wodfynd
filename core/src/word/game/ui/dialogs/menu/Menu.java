package word.game.ui.dialogs.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import word.game.config.ConfigProcessor;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.GameData;
import word.game.screens.BaseScreen;

import word.game.ui.Toggle;
import word.game.ui.dialogs.BaseDialog;


public class Menu extends BaseDialog  {


    private Label.LabelStyle menuItemLabelStyle;

    private Runnable langSelectionCallback;
    private LanguageDialog languageDialog;
    private float rowHeight;
    private float hmargin = 0.05f;
    private float y;
    private float vSpace;
    private Label version;
    private float widthCoef = 0.8f;
    float innerMarginCoef = 0.7f;
    private TextureAtlas atlas4;
    private float pressDarkenCoef = 0.96f;
    private final String LANGUAGE = "1";
    private final String SOUND = "2";
    private final String RATE_US = "3";
    private final String GDPR = "4";
    private final String CONTACT_US = "5";
    private Array<Actor> rows = new Array<>();




    public Menu(float width, float height, BaseScreen screen, Runnable langSelectionCallback) {
        super(width, height, screen);

        this.langSelectionCallback = langSelectionCallback;
        String font = UIConfig.MENU_ITEM_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        menuItemLabelStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.MENU_ITEM_TEXT_COLOR);

        float contentHeight = calculateContentHeight();

        rowHeight = calculateRowHeight();
        vSpace = calculateVSpace();
        boolean inEu = screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isUserInEU();
        contentHeight += (ConfigProcessor.findTotalEnabledMenuRows(inEu, GameConfig.availableLanguages.size() > 1)) * vSpace;

        version = createVersionText(screen.wordConnectGame.version);

        y = version.getHeight() * 2f;
        content.setSize(width * widthCoef, contentHeight + y);

        setContentBackground();

        atlas4 = screen.wordConnectGame.resourceManager.get(ResourceManager.ATLAS_4, TextureAtlas.class);

        setTitleLabel(LanguageManager.get("menu"));

        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        hmargin *= content.getWidth();

        init();
    }





    private float calculateContentHeight(){
        boolean inEu = screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isUserInEU();
        boolean hasManyLocale = GameConfig.availableLanguages.size() > 1;
        return (ConfigProcessor.findTotalEnabledMenuRows(inEu, hasManyLocale) + 1) * calculateRowHeight() * 1.05f;
    }





    private float calculateRowHeight(){
        return AtlasRegions.ic_rate_us.getRegionHeight() * UIConfig.MENU_ITEM_HEIGHT_COEF;
    }




    private float calculateVSpace(){
        return rowHeight * UIConfig.MENU_ITEM_SPACING_COEF;
    }




    private void init(){
        rows.add(version);
        version.setX((content.getWidth() - version.getWidth() ) * 0.5f);
        version.setY(version.getPrefHeight() * version.getFontScaleY());

        boolean inEu = screen.wordConnectGame.adManager != null && screen.wordConnectGame.adManager.isUserInEU();
        if(UIConfig.MENU_ITEM_GDPR_ENABLED && inEu)setGDPR();
        if(UIConfig.MENU_ITEM_RATE_US_ENABLED)setRateUs();
        if(UIConfig.MENU_ITEM_CONTACT_US_ENABLED)setContactUs();
        boolean hasManyLocale = GameConfig.availableLanguages.size() > 1;
        if(UIConfig.MENU_ITEM_LANGUAGE_ENABLED && hasManyLocale)setLanguage();
        if(UIConfig.MENU_ITEM_SOUND_ENABLED)setSound();
        content.addActor(version);
    }






    private void setGDPR(){
        Group group1 = getRow(AtlasRegions.gdpr, LanguageManager.get("gdpr"));
        group1.setName(GDPR);
        content.addActor(group1);
        group1.setY(y);
        y += group1.getHeight() + vSpace;
    }





    private void setRateUs(){
        Group group2 = getRow(AtlasRegions.ic_rate_us, LanguageManager.get("rate_us"));
        group2.setName(RATE_US);
        group2.setY(y);
        content.addActor(group2);
        y += group2.getHeight() + vSpace;
    }





    private void setContactUs(){
        Group group3 = getRow(AtlasRegions.ic_email, LanguageManager.get("contact_us"));
        group3.setName(CONTACT_US);
        group3.setY(y);
        content.addActor(group3);
        y += group3.getHeight() + vSpace;
    }





    private void setLanguage(){
        Group group4 = getRow(AtlasRegions.ic_language, LanguageManager.get("language"));
        group4.setName(LANGUAGE);
        group4.setY(y);
        content.addActor(group4);
        y += group4.getHeight() + vSpace;

        TextureAtlas.AtlasRegion langIcon = atlas4.findRegion(LanguageManager.locale.code);

        if(langIcon != null){
            group4.setOrigin(Align.center);
            Image image = new Image(langIcon);
            float size = group4.getHeight() * 0.5f;
            image.setSize(size, size);
            image.setX((group4.getWidth() - image.getWidth()) - hmargin * innerMarginCoef);
            image.setY((group4.getHeight() - image.getHeight()) * 0.5f);
            group4.addActor(image);
            image.setOrigin(Align.center);
        }
    }






    private void setSound(){
        Group group5 = getRow(AtlasRegions.ic_sound, LanguageManager.get("sounds"));
        group5.setName(SOUND);
        group5.setY(y);
        content.addActor(group5);

        final Toggle toggle = new Toggle();
        toggle.setEnabled(!ConfigProcessor.muted);
        toggle.setX(group5.getWidth() - toggle.getWidth() - hmargin * innerMarginCoef);
        toggle.setY((group5.getHeight() - toggle.getHeight()) * 0.5f);
        group5.addActor(toggle);
        toggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameData.setGameMute(!toggle.isEnabled());
            }
        });

        toggle.setOrigin(Align.center);
    }






    private Group getRow(TextureAtlas.AtlasRegion icon, String title){
        Group container = new Group();
        container.setName(title);
        container.setSize(content.getWidth() - hmargin * 2, rowHeight);

        Image bg = new Image(NinePatches.round_rect_shadow);
        bg.setName("bg");
        bg.setSize(container.getWidth(), container.getHeight());
        bg.setColor(UIConfig.MENU_ITEM_BG_COLOR);
        container.addActor(bg);

        Image ic = new Image(icon);
        ic.setOrigin(Align.center);
        ic.setX(hmargin * innerMarginCoef);
        ic.setY((container.getHeight() - ic.getHeight()) * 0.5f);
        container.addActor(ic);

        Label label = new Label(title, menuItemLabelStyle);
        label.setX(ic.getX() + ic.getWidth() + hmargin * innerMarginCoef);
        label.setY((container.getHeight() - label.getHeight()) * 0.5f);
        container.addActor(label);
        container.addListener(clickListener);
        container.setX(hmargin);

        rows.add(container);
        return container;
    }






    private Label createVersionText(String v){
        if(v == null) v = "";

        String font = UIConfig.MENU_DIALOG_VERSION_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle style = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.MENU_DIALOG_VERSION_TEXT_COLOR);
        Label label = new Label(LanguageManager.format("app_version", v), style);
        label.setAlignment(Align.center);
        label.setFontScale(0.8f);
        return label;
    }





    private InputListener clickListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if(event.getTarget().getParent().getName() != null){
                if(event.getTarget().getParent().getName().equals("toggle")){
                    return false;
                }

                if(event.getTarget().getParent().getName().equals(SOUND)){
                    return false;
                }
            }

            if(event.getTarget().getName() != null && event.getTarget().getName().equals("toggle"))return false;


            Color color = null;
            if(event.getTarget().getName() != null && event.getTarget().getName().equals("bg")) {
                color = event.getTarget().getColor();
            }else{
                if(event.getTarget().getParent().findActor("bg") != null)
                    color = event.getTarget().getParent().findActor("bg").getColor();
                else
                    color = event.getTarget().getParent().getParent().findActor("bg").getColor();
            }

            if(color != null){
                color.r *= pressDarkenCoef;
                color.g *= pressDarkenCoef;
                color.b *= pressDarkenCoef;
            }

            return true;
        }







        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            Color color = null;
            if(event.getTarget().getName() != null && event.getTarget().getName().equals("bg")) {
                color = event.getTarget().getColor();
            }else{
                if(event.getTarget().getParent().findActor("bg") != null)
                    color = event.getTarget().getParent().findActor("bg").getColor();
                else
                    color = event.getTarget().getParent().getParent().findActor("bg").getColor();
            }

            if(color != null){
                color.r *= 1 / pressDarkenCoef;
                color.g *= 1 / pressDarkenCoef;
                color.b *= 1 / pressDarkenCoef;
            }

            onMenuRowClick(event.getTarget());

            super.touchUp(event, x, y, pointer, button);
        }
    };






    private void onMenuRowClick(Actor target){
        String name = target.getParent().getName();

        if(name == null){
            name = target.getParent().getParent().getName();
        }

        if(name == null) return;


        switch (name){
            case LANGUAGE: {
                if(languageDialog == null) languageDialog = new LanguageDialog(getWidth(), getHeight(), screen, langSelectionCallback);
                addActor(languageDialog);
                languageDialog.show();
                break;
            }

            case GDPR:{
                screen.wordConnectGame.adManager.openGDPRForm();
                break;
            }

            case RATE_US:{
                if(screen.wordConnectGame.rateUsLauncher != null) screen.wordConnectGame.rateUsLauncher.launch();
                break;
            }

            case CONTACT_US:{
                if(screen.wordConnectGame.supportRequest != null) screen.wordConnectGame.supportRequest.sendSupportEmail();
                break;
            }

        }
    }


    @Override
    protected void openAnimFinished() {
        super.openAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
    }

    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();

    }


}
