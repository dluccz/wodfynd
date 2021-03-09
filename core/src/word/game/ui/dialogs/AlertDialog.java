package word.game.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;
import word.game.util.UiUtil;

public class AlertDialog extends BaseDialog{

    protected Runnable callback;
    private TextButton okay;


    public AlertDialog(float width, float height, BaseScreen screen, String titleText, String msgText, String yesText, final Runnable okCallback) {
        super(width, height, screen);
        callback = okCallback;

        boolean isWide = UiUtil.isScreenWide();

        content.setWidth(isWide ? width * 0.7f : width * 0.8f);

        String font = UIConfig.DIALOG_BODY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle msgTextStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_BODY_TEXT_COLOR);
        Label desc = new Label(msgText, msgTextStyle);
        desc.setWrap(true);
        desc.setWidth(content.getWidth() * 0.8f);
        desc.setAlignment(Align.center);

        content.setHeight(desc.getHeight() + NinePatches.btn_dialog_up.getTotalHeight() * 4.5f);

        setContentBackground();

        setTitleLabel(titleText);

        desc.setX((content.getWidth() - desc.getWidth()) * 0.5f);
        desc.setY(content.getHeight() * 0.5f);
        content.addActor(desc);

        TextButton.TextButtonStyle okayStyle = new TextButton.TextButtonStyle();
        String fontName = UIConfig.ALERT_DIALOG_BUTTON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        okayStyle.font = screen.wordConnectGame.resourceManager.get(fontName, BitmapFont.class);
        okayStyle.up = new NinePatchDrawable(NinePatches.btn_dialog_up);
        okayStyle.down = new NinePatchDrawable(NinePatches.btn_dialog_down);

        okay = new TextButton(yesText, okayStyle);
        okay.getLabel().setFontScale(UIConfig.ALERT_DIALOG_BUTTON_FONT_SCALE);
        okay.setWidth(content.getWidth() * UIConfig.ALERT_DIALOG_BUTTON_WIDTH_COEF);
        okay.setX((content.getWidth() - okay.getWidth()) * 0.5f);
        okay.setY(getHeight() * 0.03f);
        content.addActor(okay);

        okay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);

                hide();
            }
        });
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

        if(callback != null) callback.run();
        screen.nullifyDialog(getDialogId());
    }
}
