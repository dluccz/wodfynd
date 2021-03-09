package word.game.ui.hint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;

import word.game.config.GameConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;


public class HintButton extends ImageButton {


    private Label quantityLabel;
    private Image icon;
    private float maxQuantityWidth;
    private Group costContainer;
    private Image padlock;
    private TextureAtlas.AtlasRegion bottomRegion;
    private BitmapFont font;
    private int cost;
    private RotateByAction rotateByAction1, rotateByAction2, rotateByAction3;
    private RotateToAction rotateToAction1;
    private DelayAction delayAction;


    public HintButton(ImageButtonStyle style, BitmapFont font, TextureAtlas.AtlasRegion bottomRegion) {
        super(style);
        init(bottomRegion, font);
    }



    public HintButton(TextureRegion up, TextureRegion down, BitmapFont font){
        super(new TextureRegionDrawable(up), new TextureRegionDrawable(down));
        init(AtlasRegions.hint_btn_cost_bg, font);
    }



    public HintButton(TextureAtlas.AtlasRegion up, TextureAtlas.AtlasRegion down, TextureAtlas.AtlasRegion icon, BitmapFont font){
        this(up, down, font);
        setIcon(icon);
    }



    private void setIcon(TextureAtlas.AtlasRegion ic){
        icon = new Image(ic);
        icon.setOrigin(Align.center);
        icon.setX((getWidth() - icon.getWidth()) * 0.5f);
        icon.setY((getHeight() - icon.getHeight()) * 0.5f);
        addActor(icon);
    }




    public void showUp(){
        icon.getActions().clear();
        float angle = 20;

        if(rotateByAction1 == null) rotateByAction1 = new RotateByAction();
        else rotateByAction1.reset();
        rotateByAction1.setAmount(-angle);
        rotateByAction1.setDuration(0.1f);

        if(rotateByAction2 == null) rotateByAction2 = new RotateByAction();
        else rotateByAction2.reset();
        rotateByAction2.setAmount(angle * 2);
        rotateByAction2.setDuration(0.2f);

        if(rotateByAction3 == null) rotateByAction3 = new RotateByAction();
        else rotateByAction3.reset();
        rotateByAction3.setAmount(-angle * 2);
        rotateByAction3.setDuration(0.2f);

        if(rotateToAction1 == null) rotateToAction1 = new RotateToAction();
        else rotateToAction1.reset();
        rotateToAction1.setRotation(0);
        rotateToAction1.setDuration(0.1f);

        if(delayAction == null) delayAction = new DelayAction();
        else delayAction.reset();
        delayAction.setDuration(GameConfig.INTERVAL_BETWEEN_HINT_INDICATIONS);

        icon.addAction(Actions.forever(new SequenceAction(rotateByAction1, rotateByAction2, rotateByAction3, rotateToAction1, delayAction)));
    }




    public void stopShowUp(){
        icon.getActions().clear();
        icon.setRotation(0f);
    }



    private void init(TextureAtlas.AtlasRegion bottomRegion, BitmapFont font){
        this.bottomRegion = bottomRegion;
        this.font = font;

        maxQuantityWidth = AtlasRegions.value_bg.getRegionWidth() * 0.3f;
    }



    private void createQuantity(){
        Label.LabelStyle quantityStyle = new Label.LabelStyle(font, UIConfig.HINT_BUTTON_REMANING_TEXT_COLOR);
        Sprite qSprite = new Sprite(AtlasRegions.value_bg);
        qSprite.setColor(UIConfig.HINT_BUTTON_REMANING_BG_COLOR);
        quantityStyle.background = new SpriteDrawable(qSprite);

        quantityLabel = new Label("3", quantityStyle);
        quantityLabel.setFontScale(0.6f);
        quantityLabel.setAlignment(Align.center, Align.center);
        quantityLabel.setX(getWidth() * 0.65f);
        quantityLabel.setY(-getHeight() * 0.05f);
        quantityLabel.getColor().a = 0;
        quantityLabel.setVisible(false);
        addActor(quantityLabel);
    }



    private void createCostContainer(){
        costContainer = new Group();
        costContainer.setVisible(false);

        Image costBg = new Image(bottomRegion);
        costContainer.addActor(costBg);
        costContainer.setSize(costBg.getWidth(), costBg.getHeight());

        float maxCostHeight = bottomRegion.getRegionHeight() * 0.5f;

        Label.LabelStyle bottomStyle = new Label.LabelStyle(font, UIConfig.HINT_BUTTON_COST_TEXT_COLOR);

        Label costLabel = new Label("1", bottomStyle);
        costLabel.setAlignment(Align.center);
        costLabel.setX(costContainer.getWidth() * 0.56f);

        costContainer.addActor(costLabel);

        costContainer.setX((getWidth() - costContainer.getWidth()) * 0.5f);
        costContainer.setY(-costContainer.getHeight() * 0.5f);
        addActor(costContainer);

        costLabel.setText(cost);
        GlyphLayout costLayout = Pools.obtain(GlyphLayout.class);
        costLayout.setText(costLabel.getStyle().font, costLabel.getText());
        if(costLayout.height >= maxCostHeight)
            costLabel.setFontScale(maxCostHeight / costLayout.height);

        costLabel.setY((-costLayout.height * (1-costLabel.getFontScaleY())) + (costContainer.getHeight() * 0.1f ));
        Pools.free(costLayout);
    }


    public void setCost(int cost){
        this.cost = cost;
    }



    public void update(int quantity){
        if(padlock != null) return;
        float time = 0.2f;
        if(quantity == 0){
            if(quantityLabel != null && quantityLabel.isVisible()){
                RunnableAction runnableAction = new RunnableAction();
                runnableAction.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        quantityLabel.setVisible(false);
                        quantityLabel.setText(0);
                        clampQuntityText();
                    }
                });

                quantityLabel.addAction(new SequenceAction(Actions.fadeOut(time), runnableAction));

            }

            if(costContainer == null) createCostContainer();
            costContainer.setVisible(true);
            costContainer.addAction(Actions.fadeIn(time));
        }else{
            if(quantityLabel == null) createQuantity();
            if(quantityLabel.isVisible()){
                quantityLabel.setText(quantity);
                clampQuntityText();
            }else{
                RunnableAction runnableAction = new RunnableAction();
                runnableAction.setRunnable(new Runnable() {
                    @Override
                    public void run() {

                        if(costContainer != null) costContainer.setVisible(false);
                    }
                });

                quantityLabel.setVisible(true);
                quantityLabel.setText(quantity);
                clampQuntityText();
                quantityLabel.addAction(new SequenceAction(Actions.fadeIn(time), runnableAction));
            }

            if(costContainer != null && costContainer.isVisible()){
                costContainer.addAction(Actions.fadeOut(time));
            }
        }
    }





    private void clampQuntityText(){
        GlyphLayout quantityLayout = Pools.obtain(GlyphLayout.class);
        quantityLayout.setText(quantityLabel.getStyle().font, quantityLabel.getText());
        if(quantityLayout.width >= maxQuantityWidth)
            quantityLabel.setFontScale(maxQuantityWidth / quantityLayout.width);
        else
            quantityLabel.setFontScale(0.6f);
        Pools.free(quantityLayout);
    }





    public void lock(boolean leftSize){

        if(quantityLabel != null) quantityLabel.setVisible(false);
        if(costContainer != null) costContainer.setVisible(false);

        if(padlock == null) {
            padlock = new Image(AtlasRegions.padlock);
            if(leftSize) padlock.setX(-padlock.getWidth() * 0.1f);
            else padlock.setX(getWidth() - padlock.getWidth());
            padlock.setY(-getHeight() * 0.05f);
            addActor(padlock);
        }
    }


    public void unlock(){
        if(padlock != null) {
            padlock.remove();
            padlock = null;
        }
        if(quantityLabel != null) quantityLabel.setVisible(true);
    }

}
