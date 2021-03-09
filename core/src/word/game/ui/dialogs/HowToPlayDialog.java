package word.game.ui.dialogs;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;


public class HowToPlayDialog extends BaseDialog{

    private Label.LabelStyle bodyTextStyle;
    private float itemHeight;


    public HowToPlayDialog(float width, float height, BaseScreen screen) {
        super(width, height, screen);

        itemHeight = AtlasRegions.howtoplay_1.getRegionHeight() * 1.4f;

        content.setSize(width * 0.8f, itemHeight * 4f);
        setContentBackground();

        String font = UIConfig.DIALOG_BODY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        bodyTextStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_BODY_TEXT_COLOR);

        setTitleLabel(LanguageManager.get("how_to_play"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });
    }





    public void setContent(String desc1, String desc2, String desc3, TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2, TextureAtlas.AtlasRegion region3){

        float top = titleContainer.getY() - closeButton.getHeight();

        float sliceHeight = (content.getHeight() - (content.getHeight() - top)) / 3f;
        float bottom = itemHeight * 0.15f;

        Group g3 = createSlice(sliceHeight, region3, desc3);
        g3.setY(bottom);
        content.addActor(g3);

        Group g2 = createSlice(sliceHeight, region2, desc2);
        g2.setY(sliceHeight + bottom);
        content.addActor(g2);

        Group g1 = createSlice(sliceHeight, region1, desc1);
        g1.setY(sliceHeight * 2f + bottom);
        content.addActor(g1);
    }





    private Group createSlice(float height, TextureAtlas.AtlasRegion icon, String text){
        Group group = new Group();

        group.setSize(content.getWidth(), height);

        float margin = content.getWidth() * 0.05f;

        Image img = new Image(icon);
        img.setX(margin);
        img.setY((height - img.getHeight()) * 0.5f);
        group.addActor(img);

        Label descLabel = new Label(text, bodyTextStyle);
        descLabel.setWrap(true);
        descLabel.setWidth(group.getWidth() - img.getWidth() - margin * 4);
        descLabel.setAlignment(Align.bottomLeft);

        descLabel.setX(img.getX() + img.getWidth() + margin);
        descLabel.setY((group.getHeight() - descLabel.getPrefHeight()) * 0.5f);
        group.addActor(descLabel);
        group.setOrigin(Align.center);

        return group;
    }









    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();
        getStage().getRoot().setTouchable(Touchable.enabled);
        remove();
        setVisible(false);
    }
}
