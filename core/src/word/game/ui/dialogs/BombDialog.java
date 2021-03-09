package word.game.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;

public class BombDialog extends BaseDialog{

    private boolean watchSelected;
    private BombDecision bombDecision;
    private TextButton watchButton;

    public BombDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);

        content.setWidth(width * 0.8f);

        String font = UIConfig.DIALOG_BODY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle bodyTextStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_BODY_TEXT_COLOR);
        Label moves = new Label(LanguageManager.format("out_of_moves", GameConfig.EXTRA_BOMB_MOVES_FOR_WATCHING_AD), bodyTextStyle);
        moves.setAlignment(Align.center);
        moves.setWrap(true);
        moves.setWidth(content.getWidth() * 0.8f);

        content.setHeight(moves.getHeight() + NinePatches.play_r_up.getTotalHeight() * 4f);

        setContentBackground();
        setTitleLabel(LanguageManager.get("bomb_exploding"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                watchSelected = false;
                hide();
            }
        });

        moves.setX((content.getWidth() - moves.getWidth()) * 0.5f);
        moves.setY(content.getHeight() * 0.5f);
        content.addActor(moves);

        TextButton.TextButtonStyle watchStyle = new TextButton.TextButtonStyle();
        String fontName = UIConfig.ALERT_DIALOG_BUTTON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        watchStyle.font = screen.wordConnectGame.resourceManager.get(fontName, BitmapFont.class);
        NinePatch rUp = NinePatches.play_r_up;
        watchStyle.up = new NinePatchDrawable(rUp);
        watchStyle.down = new NinePatchDrawable(NinePatches.play_r_down);

        watchButton = new TextButton(LanguageManager.get("watch_video"), watchStyle);
        watchButton.getLabel().setFontScale(UIConfig.ALERT_DIALOG_BUTTON_FONT_SCALE);
        watchButton.setWidth(content.getWidth() * UIConfig.ALERT_DIALOG_BUTTON_WIDTH_COEF * 1.1f);
        watchButton.setX((content.getWidth() - watchButton.getWidth()) * 0.5f);
        watchButton.setY(content.getHeight() * 0.05f);
        content.addActor(watchButton);

        watchButton.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(BombDialog.this.screen.wordConnectGame.adManager != null && !BombDialog.this.screen.wordConnectGame.adManager.isRewardedAdLoaded()){
                    BombDialog.this.screen.showToast(LanguageManager.get("no_video"));
                    return;
                }

                getStage().getRoot().setTouchable(Touchable.disabled);
                watchSelected = true;
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
        remove();
        setVisible(false);
        if(bombDecision != null)
            bombDecision.bombAction(watchSelected);
    }



    public void setBombDecision(BombDecision bombDecision){
        this.bombDecision = bombDecision;
    }


    public interface BombDecision{
        void bombAction(boolean watch);
    }

}
