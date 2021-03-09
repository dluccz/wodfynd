package word.game.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;


public class RemoveAdsDialog extends BaseDialog{

    private boolean buyClicked;

    public RemoveAdsDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);

        content.setSize(width * 0.7f, height * 0.6f);

        setContentBackground();
        setTitleLabel(LanguageManager.get("remove_ads"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });

        String font = UIConfig.DIALOG_BODY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle textStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_BODY_TEXT_COLOR);
        Label label = new Label(LanguageManager.get("remove_ads_desc"), textStyle);
        label.setAlignment(Align.center);
        label.setWrap(true);
        label.setWidth(content.getWidth() * 0.8f);
        if(label.getWidth() > content.getWidth() * 0.8f){
            label.setFontScale(content.getWidth() * 0.8f / label.getWidth());
        }


        label.setX((content.getWidth() - label.getWidth() ) * 0.5f);
        label.setY(content.getHeight() * 0.6f);
        content.addActor(label);

        TextButton.TextButtonStyle okayStyle = new TextButton.TextButtonStyle();
        String font2 = UIConfig.REMOVE_ADS_DIALOG_BUY_BUTTON_TEXT_USE_SHODOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        okayStyle.font = screen.wordConnectGame.resourceManager.get(font2, BitmapFont.class);
        okayStyle.fontColor = UIConfig.REMOVE_ADS_DIALOG_BUY_BUTTON_TEXT_COLOR;
        okayStyle.up = new NinePatchDrawable(NinePatches.btn_dialog_up);
        okayStyle.down = new NinePatchDrawable(NinePatches.btn_dialog_down);

        TextButton buy = new TextButton(LanguageManager.get("buy"), okayStyle);
        buy.getLabel().setFontScale(UIConfig.REMOVE_ADS_DIALOG_BUY_BUTTON_FONT_SCALE);
        buy.setWidth(content.getWidth() * UIConfig.REMOVE_ADS_DIALOG_BUY_BUTTON_WIDTH_COEF);
        buy.setX((content.getWidth() - buy.getWidth()) * 0.5f);
        buy.setY(getHeight() * 0.03f);
        content.addActor(buy);

        buy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buyClicked = true;
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });


        Image costbg = new Image(AtlasRegions.remove_ads2);
        Label.LabelStyle costStyle = new Label.LabelStyle();
        costStyle.fontColor = UIConfig.REMOVE_ADS_DIALOG_COST_TEXT_COLOR;
        costStyle.font = screen.wordConnectGame.resourceManager.get(UIConfig.REMOVE_ADS_DIALOG_COST_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold, BitmapFont.class);
        Label cost = new Label(screen.wordConnectGame.shoppingProcessor.getRemoveAdsPrice(), costStyle);
        cost.setAlignment(Align.center);

        if(cost.getPrefWidth() > costbg.getWidth() * 0.52f){
            cost.setFontScale(costbg.getWidth() * 0.52f / cost.getPrefWidth());
        }

        content.addActor(cost);

        Group costContainer = new Group();
        costContainer.setSize(costbg.getWidth(), costbg.getHeight());
        costContainer.setOrigin(Align.center);
        costContainer.setX((content.getWidth() - costContainer.getWidth()) * 0.5f);
        costContainer.setY(content.getHeight() * 0.3f);
        content.addActor(costContainer);
        costContainer.addActor(costbg);
        costContainer.addActor(cost);

        float coinArea = costbg.getWidth() * 0.298f;
        float textArea = costbg.getWidth() - coinArea;
        cost.setX(coinArea + (textArea - cost.getWidth()) * 0.4f);
        cost.setY((costbg.getHeight() - cost.getHeight()) * 0.5f);
    }




    @Override
    public void show() {
        super.show();
        buyClicked = false;
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
        if(buyClicked) screen.iapDialogOpener.changed(null, null);
    }
}
