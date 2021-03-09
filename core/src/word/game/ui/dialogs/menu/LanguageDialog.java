package word.game.ui.dialogs.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;



import java.util.Map;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.graphics.NinePatches;
import word.game.i18n.Locale;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;

import word.game.ui.dialogs.BaseDialog;

public class LanguageDialog extends BaseDialog {

    private Image glow;
    private Runnable callback;
    private Table table = new Table();
    private String currentLanguage;


    public LanguageDialog(float width, float height, BaseScreen screen, Runnable callback) {
        super(width, height, screen);
        this.callback = callback;
        content.setSize(width * 0.8f, height * 0.9f);


        TextureAtlas atlas4 = screen.wordConnectGame.resourceManager.get(ResourceManager.ATLAS_4, TextureAtlas.class);


        table.setWidth(content.getWidth());
        table.setHeight(content.getHeight());



        String selectedCode = null;

        if(LanguageManager.locale != null)
            selectedCode = LanguageManager.locale.code;

        glow = new Image(AtlasRegions.glow);
        glow.setColor(UIConfig.LANGUAGE_DIALOG_SELECTION_GLOW_COLOR);
        glow.setScaling(Scaling.none);

        int index = 0;


        for(Map.Entry<String, Locale> entry : GameConfig.availableLanguages.entrySet()){
            String code = entry.getKey();

            Image icon = new Image(atlas4.findRegion(code));
            icon.setOrigin(Align.center);
            icon.setName(code);

            Stack stack = new Stack();
            icon.setScaling(Scaling.none);
            stack.add(icon);
            icon.addListener(clickListener);

            if(code.equals(selectedCode)){
                stack.add(glow);
            }

            index++;

            Cell cell = table.add(stack).width(glow.getWidth()).height(glow.getHeight()).padBottom(Gdx.graphics.getWidth() * 0.05f).pad(glow.getWidth() * 0.1f);
            if(index % 3 == 0)table.row();


        }
        table.pack();

        content.setHeight(table.getHeight() +  AtlasRegions.dialog_title.getRegionHeight() * 1.2f);
        setContentBackground();
        contentBackground.setSize(content.getWidth(), content.getHeight());
        content.addActor(table);

        table.setX((content.getWidth() - table.getWidth()) * 0.5f);
        table.setY(content.getHeight() * 0.1f);
        table.setTransform(true);
        table.setOrigin(Align.center);


        setTitleLabel(LanguageManager.getSelectedLocaleCode() == null ? "Please Select a Language" : LanguageManager.get("language"));

        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        if(LanguageManager.locale == null)
            closeButton.setVisible(false);
    }




    @Override
    public void show() {
        super.show();
        if(LanguageManager.locale != null && LanguageManager.locale.code != null) currentLanguage = LanguageManager.locale.code;
    }
















    private InputListener clickListener = new InputListener(){
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            event.getTarget().setScale(0.9f);
            return true;
        }


        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Actor target = event.getTarget();
            target.setScale(1f);
            glow.remove();
            glow.getColor().a = 0;
            target.getParent().addActor(glow);
            glow.addAction(Actions.fadeIn(0.2f));

            if(target.getName() != null) {
                newCode = target.getName();
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
                //setNewLanguage(target.getName());
            }

            super.touchUp(event, x, y, pointer, button);
        }
    };

    String newCode;

    private void setNewLanguage(String code){

        if(currentLanguage != null && currentLanguage.equals(code)) return;
        screen.setNewLanguage(code);
        getStage().getRoot().setTouchable(Touchable.disabled);
        hide();
    }




    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();

        if(currentLanguage == null || (newCode != null && !currentLanguage.equals(newCode))){
            screen.setNewLanguage(newCode);
            if(callback != null)callback.run();
        }

    }


    @Override
    public boolean navigateBack() {
        if(currentLanguage == null) return false;
        return super.navigateBack();
    }
}
