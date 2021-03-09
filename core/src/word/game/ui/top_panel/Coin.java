package word.game.ui.top_panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


import word.game.actions.AngledBezierToAction;
import word.game.config.ConfigProcessor;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.ResourceManager;
import word.game.pool.Pools;
import word.game.ui.dial.Particle;


public class Coin extends Image implements Pool.Poolable {


    private DelayAction coinDelay1, coinDelay2;
    private AngledBezierToAction coinBezier;
    private ScaleToAction coinScale;
    private RunnableAction coinRunnable;
    private SequenceAction sequenceAction1, sequenceAction2;
    private CoinView coinView;
    private boolean last;
    private int count;
    private float delay;


    public Coin(){
        super(AtlasRegions.coin_small);
        setOrigin(Align.center);
    }




    public void animateForCoinView(final CoinView coinView, float delayy, boolean last, int count){
        this.coinView = coinView;
        this.delay = delayy;
        this.last = last;
        this.count = count;

        if(coinDelay1 == null) coinDelay1 = new DelayAction();
        else coinDelay1.reset();
        coinDelay1.setDuration(delayy);

        if(coinDelay2 == null) coinDelay2 = new DelayAction();
        else coinDelay2.reset();
        coinDelay2.setDuration(0.4f + coinDelay1.getDuration());

        if(coinScale == null) coinScale = new ScaleToAction();
        else coinScale.reset();
        coinScale.setScale(0.5f);
        coinScale.setDuration(0.1f);

        if(sequenceAction2 == null) sequenceAction2 = new SequenceAction();
        else sequenceAction2.reset();
        sequenceAction2.addAction(coinDelay2);
        sequenceAction2.addAction(coinScale);
        addAction(sequenceAction2);

        Actor coin = coinView.coin;

        if(coinBezier == null) coinBezier = new AngledBezierToAction();
        else coinBezier.reset();
        coinBezier.setStartPosition(getX(), getY());
        coinBezier.setPointA(getX() + (coin.getX() - getX()) * 0.5f, getY());
        coinBezier.setPointB(coin.getX(), getY() + (coin.getY() - getY()) * 0.5f);
        coinBezier.setEndPosition(coin.getX(), coin.getY());
        coinBezier.setDuration(0.3f);
        coinBezier.setInterpolation(Interpolation.slowFast);

        if(coinRunnable == null) coinRunnable = new RunnableAction();
        coinRunnable.reset();
        coinRunnable.setRunnable(end);

        if(sequenceAction1 == null) sequenceAction1 = new SequenceAction();
        sequenceAction1.reset();

        sequenceAction1.addAction(coinDelay1);
        sequenceAction1.addAction(coinBezier);
        sequenceAction1.addAction(coinRunnable);
        addAction(sequenceAction1);
    }




    public void cancel(){
        if(getParent() != null) {
            clearActions();
            remove();
            reset();
            Pools.coinPool.free(Coin.this);
        }
    }




    private Runnable end = new Runnable() {
        @Override
        public void run() {
            coinView.coinPulseAnimation(last ? coinView.incrementCoinsWithAnimation(count) : null);
            remove();
            reset();
            Pools.coinPool.free(Coin.this);
            if(!ConfigProcessor.muted) {
                Sound sound = coinView.screen.wordConnectGame.resourceManager.get(ResourceManager.SFX_BONUS_WORD, Sound.class);
                sound.play(SoundConfig.SFX_BONUS_VOLUME);
            }
        }
    };



    @Override
    public void reset() {
        setScale(0.7f);
        elapsed = 0f;
        tail.clear();
    }




    /*******************************************/

    private float elapsed;
    private Array<Particle> tail = new Array<>();



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        elapsed += Gdx.graphics.getDeltaTime();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        int count = 10;

        if(elapsed > delay && tail.size < count){
            Particle sparkle = new Particle(AtlasRegions.sparkle);
            sparkle.setColor(UIConfig.COIN_ANIMATION_SPARKLE_COLOR);
            tail.add(sparkle);
        }

        for(int i = 0; i < tail.size; i++){
            Particle sparkle = tail.get(i);

            Color c = sparkle.getColor();
            sparkle.setColor(c.g, c.g, c.b, 0.8f * color.a);

            float centerX = getWidth() * 0.5f - sparkle.getWidth() * 0.5f;
            float centerY = getHeight() * 0.5f - sparkle.getHeight() * 0.5f;

            float random = MathUtils.random(-getWidth() * 0.1f, getWidth() * 0.1f);
            centerX += random;

            random = MathUtils.random(-getHeight() * 0.1f, getHeight() * 0.1f);
            centerY += random;

            float x = getX() - MathUtils.cos((getRotation()) * MathUtils.degreesToRadians) * getWidth() * 0.5f + centerX;
            float y = getY() - MathUtils.sin((getRotation()) * MathUtils.degreesToRadians) * getHeight() * 0.5f + centerY;

            if(sparkle.getScaleX() == 1.0f)
                sparkle.setPosition(x, y);


            sparkle.draw(batch);
            sparkle.setScale(sparkle.getScaleX() - 0.1f);

            if(sparkle.getScaleX() <= 0){
                sparkle.setScale(1);
            }
        }

        batch.setColor(color.r, color.g, color.b, 1f);
    }
}
