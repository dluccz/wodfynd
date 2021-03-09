package word.game.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import word.game.WordConnectGame;
import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.managers.ResourceManager;

public class Tooltip extends Group {

    private Image tooltip;
    private WordConnectGame worderfulGame;
    private Label label;
    private AlphaAction alphaAction1;
    private AlphaAction alphaAction2;
    private DelayAction delayAction;



    public Tooltip(WordConnectGame worderfulGame){
        tooltip = new Image(NinePatches.tooltip);
        tooltip.setColor(UIConfig.TOOLTIP_BG_COLOR);
        addActor(tooltip);
        setHeight(tooltip.getHeight());
        this.worderfulGame = worderfulGame;
    }



    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        tooltip.setWidth(width);
        tooltip.setOrigin(Align.center);
    }



    public void setText(int align, String text){
        clearActions();
        getColor().a = 0;
        if(align == Align.right) tooltip.setRotation(0);
        else tooltip.setRotation(180);

        if(label == null) {
            Label.LabelStyle style = new Label.LabelStyle();
            String font = UIConfig.TOOLTIP_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            style.font = worderfulGame.resourceManager.get(font, BitmapFont.class);
            style.fontColor = UIConfig.TOOLTIP_TEXT_COLOR;

            label = new Label("", style);
            label.setWrap(true);
            label.setWidth(getWidth() * 0.7f);
            label.setFontScale(UIConfig.TOOLTIP_FONT_SCALE);
            label.setOrigin(Align.bottomLeft);
            addActor(label);
        }

        label.setText(text);
        label.setX(align == Align.right ? NinePatches.tooltip.getRightWidth() * 0.5f : NinePatches.tooltip.getRightWidth());
        label.setY((getHeight() - label.getHeight()) * 0.5f);

        fadeInAndOut();
    }





    private void fadeInAndOut(){
        if(alphaAction1 == null)
            alphaAction1 = new AlphaAction();
        else
            alphaAction1.reset();

        alphaAction1.setAlpha(1f);
        alphaAction1.setDuration(0.2f);

        if(delayAction == null)
            delayAction = new DelayAction();
        else
            delayAction.reset();

        delayAction.setDuration(UIConfig.TOOLTIP_SHOW_DURATION);

        if(alphaAction2 == null)
            alphaAction2 = new AlphaAction();
        else
            alphaAction2.reset();

        alphaAction2.setAlpha(0f);
        alphaAction2.setDuration(0.3f);

        addAction(Actions.sequence(alphaAction1, delayAction, alphaAction2));
    }

}
