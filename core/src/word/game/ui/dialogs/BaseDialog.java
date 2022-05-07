package word.game.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;
import word.game.ui.Modal;
import word.game.util.BackNavigator;


public class BaseDialog extends Group implements BackNavigator {

    protected Modal modal;
    protected Group content;
    protected BaseScreen screen;
    protected float closeScale = 0.6f;
    protected Label titleLabel;
    private Label.LabelStyle titleStyle;
    protected Button closeButton;
    protected Image contentBackground;
    protected Group titleContainer;
    protected Image titleBackground;
    private int id = -1;


    public BaseDialog(float width, float height, BaseScreen screen){
        setSize(width, height);
        this.screen = screen;

        modal = new Modal(getWidth(), getHeight());
        addActor(modal);

        content = new Group();
        addActor(content);
    }


    public void setDialogId(int id){
        screen.dialogMap.put(id, this);
    }

    public int getDialogId(){
        return id;
    }



    protected void setTitleLabel(String text){
        if(titleLabel == null){

            titleBackground = new Image(AtlasRegions.dialog_title);

            titleContainer = new Group();
            titleContainer.setSize(titleBackground.getWidth(), titleBackground.getHeight());
            titleContainer.setOrigin(Align.center);
            //
            titleContainer.setX((content.getWidth() - titleContainer.getWidth()) * 0.5f);
            titleContainer.addActor(titleBackground);

            String font = UIConfig.DIALOG_TITLE_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
            titleStyle = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.DIALOG_TITLE_TEXT_COLOR);

            titleLabel = new Label(text, titleStyle);
            titleLabel.setFontScale(UIConfig.DIALOG_TITLE_FONT_SCALE);
            titleLabel.setAlignment(Align.center);
            titleLabel.setWidth(titleContainer.getWidth() * 0.8f);
            titleLabel.setWrap(true);

            if(titleLabel.getPrefHeight() > titleContainer.getHeight() * 0.65f)
                titleLabel.setFontScale(titleContainer.getHeight() * 0.65f / titleLabel.getPrefHeight());

            titleLabel.setX((titleContainer.getWidth() - titleLabel.getWidth() ) * 0.5f);
            titleLabel.setY(titleContainer.getHeight() * 0.37f);
            titleContainer.addActor(titleLabel);
            titleContainer.setY(content.getHeight() - titleContainer.getHeight() * 0.83f);
            content.addActor(titleContainer);
        }else{
            titleLabel.setText(text);
        }

        setTitleBackgroundColor(UIConfig.DIALOG_TITLE_BACKROUND_COLOR);
    }







    public void setTitleBackgroundColor(Color color){
        titleBackground.setColor(color);
    }



    public void setBodyTextFontScale(float value){

    }


    protected void setCloseButton(){
        ImageButton.ImageButtonStyle closeBtnStyle = new ImageButton.ImageButtonStyle();
        closeBtnStyle.up = new TextureRegionDrawable(AtlasRegions.close_button_up);
        closeBtnStyle.down = new TextureRegionDrawable(AtlasRegions.close_button_down);
        closeBtnStyle.disabled = new TextureRegionDrawable(AtlasRegions.close_button_disabled);

        closeButton = new ImageButton(closeBtnStyle);
        closeButton.setOrigin(Align.center);
        closeButton.setTransform(true);
        closeButton.setX(content.getWidth() - closeButton.getWidth() * 1.05f);
        closeButton.setY(content.getHeight() - closeButton.getHeight() * 1.05f);
        content.addActor(closeButton);
    }





    protected void setContentBackground(){
        contentBackground = new Image(new NinePatchDrawable(NinePatches.dialog_bg));
        contentBackground.setSize(content.getWidth(), content.getHeight());
        setContentBackgroundColor(UIConfig.DIALOG_BACKGROUND_COLOR);
        content.addActor(contentBackground);
    }


    public void setContentBackgroundColor(Color color){
        contentBackground.setColor(color);
    }



    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }




    @Override
    public boolean navigateBack() {
        hide();
        return true;
    }




    protected void setContentPosition(){
        content.setOrigin(Align.center);
        content.setX((getWidth() - content.getWidth()) * 0.5f);
        content.setY((getHeight() - content.getHeight()) * 0.5f);
    }




    public void show(){
        notifyNavigationController(screen);

        if(screen instanceof GameScreen) {
            ((GameScreen) screen).stopIdleTimer();
        }

        setVisible(true);
        setContentPosition();

        content.setScale(closeScale);
        modal.getColor().a = 1f;

        content.clearActions();
        openDialog();
    }






    protected void openAnimFinished(){

    }



    public void hide(){
        if(screen.backNavQueue != null && screen.backNavQueue.size() > 0) screen.backNavQueue.pop();
        content.clearActions();
        closeDialog();
    }



    protected void hideAnimFinished(){
        setVisible(false);
        if(screen instanceof GameScreen) {
            ((GameScreen) screen).resumeIdleTimer();
        }
    }




    private class ModalDismisser extends SequenceAction{

        private ModalDismisser(){
            addAction(Actions.fadeOut(0.1f));
            RunnableAction end = new RunnableAction();
            end.setRunnable(new Runnable() {
                @Override
                public void run() {
                    hideAnimFinished();
                }
            });
            addAction(end);
        }

    }





    protected void openDialog(){
        float time = UIConfig.DIALOG_OPEN_DURATION;
        Action run = Actions.run(new Runnable() {
            @Override
            public void run() {
                openAnimFinished();
            }
        });
        content.addAction(new SequenceAction(Actions.scaleTo(1,1, time, word.game.actions.Interpolation.backOut), run));
    }





    protected void closeDialog(){
        float time = UIConfig.DIALOG_CLOSE_DURATION;
        Action run = Actions.run(new Runnable() {
            @Override
            public void run() {
                modal.addAction(new ModalDismisser());
            }
        });
        content.addAction(new SequenceAction(Actions.scaleTo(0,0,time, word.game.actions.Interpolation.backIn), run));
    }



}
