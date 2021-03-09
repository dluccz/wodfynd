package word.game.ui.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;


import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.model.GameData;

import word.game.screens.GameScreen;
import word.game.util.MathUtil;

public class Bomb extends Group {

    private GameScreen screen;
    private ResourceManager resourceManager;

    private Label label;

    private int count;
    private float smallScale;
    private float largeScale;
    private Image bomb;
    private Image fire;
    private Image waveImg;
    private float waveTime;

    private Animation<TextureRegion> explosion;
    private float explosionTime;
    boolean explode;
    private Runnable explodeCallback;
    public boolean hit;
    public CellView targetCell;



    public Bomb(GameScreen screen){
        this.screen = screen;
        this.resourceManager = screen.wordConnectGame.resourceManager;

        bomb = new Image(AtlasRegions.bomb);
        setSize(bomb.getWidth(), bomb.getHeight() * 0.72f);
        setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);

        addActor(bomb);

        Label.LabelStyle wordTitlelabelStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBold, BitmapFont.class), Color.WHITE);

        label = new Label("", wordTitlelabelStyle);
        label.setOrigin(Align.bottomLeft);
        label.setAlignment(Align.bottomLeft);
        addActor(label);
        setTouchable(Touchable.disabled);

        explosion = new Animation<TextureRegion>(0.033333f, AtlasRegions.explosion, Animation.PlayMode.NORMAL);

    }





    public void setCountText(int n){
        label.setText(n);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, Integer.toString(n));
        label.setX((getWidth() - glyphLayout.width) * 0.5f);
        label.setY(getHeight() * 0.25f);
        Pools.free(glyphLayout);
    }



    public void setSmallScale(float scale){
        smallScale = scale;
    }

    public void setLargeScale(float scale){
        largeScale = scale;
    }

    public int getCount() {
        return count;
    }


    public void setDefaultCount(int count){
        this.count = count;
        setCountText(count);
        setScale(smallScale);

        label.clearActions();
        label.setColor(Color.WHITE);

        GameData.setNumberOfBombMoves(count);

        one();
        zero();

        if(count > 0)
            startPulse();

        if(count > 1 && fire != null)fire.setVisible(false);

    }



    public void setCount(int count) {
        this.count = count;
        setCountText(count);
        GameData.setNumberOfBombMoves(count);
        wave();

        one();
        zero();
    }




    private void zero(){
        if(count == 0) {
            clearActions();
            label.clearActions();
            label.setColor(Color.WHITE);
            setScale(smallScale);
        }
    }


    private void one(){
        if(count == 1){
            setFire();
            indicateOnly1Left();
        }
    }




    public void startPulse(){
        addAction(Actions.forever(Actions.sequence(
                Actions.scaleTo(largeScale, smallScale, 1.3f, Interpolation.slowFast),
                Actions.scaleTo(smallScale, largeScale, 1.3f, Interpolation.slowFast)
        )));
    }




    private void wave(){
        if(waveImg == null) {
            waveImg = new Image(AtlasRegions.wave);
            waveImg.setOrigin(Align.center);
            addActor(waveImg);
        }
        waveImg.setX(getOriginX() - waveImg.getWidth() * 0.5f);
        waveImg.setY(getOriginY() - waveImg.getHeight() * 0.5f);
        waveImg.setVisible(true);
    }





    private void setFire(){
        if(fire == null) {
            fire = new Image(AtlasRegions.bomb_fire);
            fire.setX(bomb.getWidth() * bomb.getScaleX() * 0.7f);
            fire.setY(bomb.getHeight() * bomb.getScaleY() - fire.getHeight() * 0.5f);
            addActor(fire);
        }else{
            fire.setVisible(true);
        }
    }



    public void indicateOnly1Left(){
        Action red = Actions.color(Color.RED, 1);
        Action white = Actions.color(Color.WHITE, 1);
        label.addAction(Actions.forever(new SequenceAction(red, white)));
    }



    public void setDefused(){
        clearActions();

        if(fire != null)
            fire.setVisible(false);

        Action scale = Actions.scaleTo(0, 0, 0.3f, Interpolation.sineIn);
        Action run = Actions.run(screen.bombDeleter);
        addAction(new SequenceAction( scale, run));
    }





    public void explode(Runnable callback){
        explodeCallback = callback;
        bomb.setVisible(false);
        label.setVisible(false);
        if(fire != null)fire.setVisible(false);
        explosionTime = 0;
        explode = true;
    }





    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(waveImg != null && waveImg.isVisible()){
            waveTime += Gdx.graphics.getDeltaTime();
            float value = waveTime;

            waveImg.setScale(value * 4f * (1 / getScaleX()), value * 4f * (1 / getScaleY()));

            if(value > 0.499f) {
                waveImg.getColor().a = MathUtil.scaleNumber(value, 0.5f, 1f, 1f, 0f);
            }

            if(waveTime >= 1f){
                waveImg.setVisible(false);
                waveTime = 0;
            }

        }



        if(explode) {
            explosionTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = explosion.getKeyFrame(explosionTime, false);
            float scale = 3f;

            batch.draw(
                    currentFrame,
                    getX() - currentFrame.getRegionWidth() * scale * 0.5f + getWidth() * getScaleX() * 0.5f,
                    getY() - currentFrame.getRegionHeight() * scale * 0.5f + getHeight() * getScaleY() * 0.5f,
                    currentFrame.getRegionWidth() * scale,
                    currentFrame.getRegionHeight() * scale
            );

            if(explosion.isAnimationFinished(explosionTime)) {
                explode = false;
                explodeCallback.run();
            }
        }

    }
}
