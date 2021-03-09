package word.game.ui.top_panel;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import word.game.config.UIConfig;
import word.game.graphics.AtlasRegions;
import word.game.managers.HintManager;
import word.game.managers.ResourceManager;
import word.game.screens.BaseScreen;



public class CoinView extends Group {


    public Image coin;
    private Label label;
    private float maxWidth;
    public BaseScreen screen;
    private CoinCountInrementer coinCountInrementer;
    private SequenceAction sequenceAction;
    private RunnableAction runnableAction;
    public ImageButton plus;
    private Runnable coinAnimFinishedCompleted;
    private Array<Coin> coins;
    private boolean cancelled;



    public CoinView(BaseScreen screen){
        this.screen = screen;

        Image bg = new Image(AtlasRegions.coin_view_bg);
        addActor(bg);
        setSize(bg.getWidth(), bg.getHeight());

        coin = new Image(AtlasRegions.coin_view_coin);
        coin.setOrigin(Align.center);
        coin.setX(getWidth() * 0.03f);
        coin.setScale(0.72f);
        coin.setY((getHeight() - coin.getHeight()) * 0.75f);
        addActor(coin);

        if(screen.wordConnectGame.shoppingProcessor != null && screen.wordConnectGame.shoppingProcessor.isIAPEnabled()){
            plus = new ImageButton(new TextureRegionDrawable(AtlasRegions.coin_view_plus_up), new TextureRegionDrawable(AtlasRegions.coin_view_plus_down));
            plus.setX(getWidth() - plus.getWidth() * 1.18f);
            plus.setY((getHeight() - plus.getHeight()) * 0.6f);
            addActor(plus);
            maxWidth = getWidth() - plus.getWidth() - coin.getWidth();
        }else{
            maxWidth = getWidth() - coin.getWidth();
        }

        maxWidth *= 0.7f;
        String font = UIConfig.REMAINING_COINS_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        Label.LabelStyle style = new Label.LabelStyle(screen.wordConnectGame.resourceManager.get(font, BitmapFont.class), UIConfig.REMAINING_COINS_TEXT_COLOR);

        label = new Label("", style);
        label.setAlignment(Align.center);
        addActor(label);

        update(HintManager.getRemainingCoins());
    }






    public void setPlusListener(ChangeListener changeListener){
        if(plus != null)
            plus.addListener(changeListener);
    }




    public void update(int count){
        label.setText(count);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(label.getStyle().font, label.getText());

        if(glyphLayout.width > maxWidth){
            label.setFontScale(maxWidth / glyphLayout.width);
        }

        if(plus == null)
            label.setX((getWidth() - label.getWidth()) * 0.6f);
        else
            label.setX((getWidth() - label.getWidth()) * 0.51f);

        label.setY((getHeight() - label.getHeight()) * 0.5f);
        Pools.free(glyphLayout);
    }






    public void incrementCoinLabelWithAnimationAndDeleteCoinImages(int startFrom, int count, Runnable callback){
        if(coinCountInrementer == null) {
            coinCountInrementer = new CoinCountInrementer();
        }else{
            coinCountInrementer.reset();
        }

        coinCountInrementer.setInterpolation(Interpolation.sineOut);
        coinCountInrementer.setDuration(count * 0.05f);
        coinCountInrementer.setStart(startFrom);
        coinCountInrementer.setEnd(startFrom + count);

        if(callback == null){
            label.addAction(coinCountInrementer);
        }else{
            if(sequenceAction == null)
                sequenceAction = new SequenceAction();
            else
                sequenceAction.reset();

            if(runnableAction == null)
                runnableAction = new RunnableAction();
            else
                runnableAction.reset();

            runnableAction.setRunnable(callback);

            sequenceAction.addAction(coinCountInrementer);
            sequenceAction.addAction(runnableAction);
            label.addAction(sequenceAction);
        }
    }





    private class CoinCountInrementer extends IntAction{

        @Override
        protected void update(float percent) {
            super.update(percent);
            CoinView.this.update(getValue());
        }
    }




    public boolean isCancelled(){
        return cancelled;
    }

    public void cancel(boolean flag){
        cancelled = flag;

        if(cancelled && coins != null) {
            Sound sound = screen.wordConnectGame.resourceManager.get(ResourceManager.SFX_BONUS_WORD, Sound.class);
            sound.stop();

            for (Coin coins : coins) {
                coins.cancel();
            }
            coins.clear();
        }
    }






    public void createCoinAnimation(int count, float x, float y, Runnable coinAnimFinishedCompleted){
        this.coinAnimFinishedCompleted = coinAnimFinishedCompleted;
        if(coins == null) coins = new Array<>();
        else coins.clear();

        getColor().a = 1f;
        for(int i = 0; i < count; i++){
            Coin coin = word.game.pool.Pools.coinPool.obtain();
            coins.add(coin);
            coin.setPosition(x - coin.getWidth() * coin.getScaleX() * 0.5f, y - coin.getHeight() * coin.getScaleY() * 0.5f);
            addActor(coin);
            coin.animateForCoinView(this, i * 0.1f, i == count - 1, count);
        }
    }


    private ScaleToAction grow, shrink;
    private SequenceAction pulseSequence;
    private RunnableAction pulseRunnable;


    public void coinPulseAnimation(Runnable callback){
        if(grow == null) grow = new ScaleToAction();
        else grow.reset();
        grow.setScale(1.1f);
        grow.setDuration(0.05f);

        if(shrink == null) shrink = new ScaleToAction();
        shrink.reset();
        shrink.setScale(0.72f);
        shrink.setDuration(0.05f);

        if(pulseSequence == null) pulseSequence = new SequenceAction();
        else pulseSequence.reset();

        pulseSequence.addAction(grow);
        pulseSequence.addAction(shrink);

        if(callback != null) {
            if(pulseRunnable == null) pulseRunnable = new RunnableAction();
            else pulseRunnable.reset();
            pulseRunnable.setRunnable(callback);
            pulseSequence.addAction(pulseRunnable);
        }

        coin.addAction(pulseSequence);
    }





    public Runnable incrementCoinsWithAnimation(final int count){

        return new Runnable() {
            @Override
            public void run() {
                int totalCoins = HintManager.getRemainingCoins() - count;
                incrementCoinLabelWithAnimationAndDeleteCoinImages(totalCoins, count, coinAnimFinishedCompleted);
            }
        };
    }

}
