package word.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import word.game.WordConnectGame;
import word.game.config.ConfigProcessor;
import word.game.config.GameConfig;
import word.game.config.SoundConfig;
import word.game.config.UIConfig;
import word.game.managers.ConnectionManager;
import word.game.managers.HintManager;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.Constants;
import word.game.ui.Toast;
import word.game.ui.Tooltip;
import word.game.ui.dialogs.AlertDialog;
import word.game.ui.dialogs.BaseDialog;
import word.game.ui.dialogs.WatchAndEarnDialog;
import word.game.ui.dialogs.iap.ItemContent;
import word.game.ui.dialogs.iap.ShoppingDialog;
import word.game.ui.dialogs.iap.ShoppingItem;
import word.game.ui.dialogs.iap.ShoppingCallback;
import word.game.ui.dialogs.menu.Menu;
import word.game.ui.dialogs.wheel.WheelDialog;
import word.game.ui.hint.HintButton;
import word.game.ui.hint.RewardedAdAnimation;
import word.game.ui.hint.RewardedVideoButton;
import word.game.ui.top_panel.TopPanel;
import word.game.ui.tutorial.Tutorial;
import word.game.util.BackNavigator;
import word.game.util.RewardedVideoCloseCallback;
import word.game.util.Text;
import word.game.util.TextLoader;

public class BaseScreen extends ScreenAdapter {


    public WordConnectGame wordConnectGame;
    protected OrthographicCamera camera;
    public ScreenViewport viewport;
    public Stage stage;
    public TopPanel topPanel;
    public Toast toast;
    protected ShoppingDialog shoppingDialog;
    private WatchAndEarnDialog watchAndEarnDialog;
    public Stack<BackNavigator> backNavQueue = new Stack<>();

    protected int zIndexDialog = 500;
    protected float r, g, b;
    public Texture backgroundTexture;
    private Texture prevBackgroundTexture;
    private WheelDialog wheelDialog;
    public Tutorial tutorial;
    protected Tooltip tooltip;
    private Image bgImage;
    private RewardedAdAnimation rewardedAdAnimation;
    private Menu menu;
    protected RewardedVideoButton rewardedVideoButton;
    public Map<Integer, BaseDialog> dialogMap = new HashMap<>();

    public HintButton singleRandomHintBtn;
    public HintButton multiRandomHintBtn;
    public HintButton fingerHintBtn;
    public HintButton rocketHintBtn;


    public BaseScreen(WordConnectGame wordConnectGame){
        this.wordConnectGame = wordConnectGame;
        ResourceManager.init();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new ScreenViewport();
        viewport.setUnitsPerPixel(1 / ResourceManager.scaleFactor);
        viewport.apply();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);


        InputProcessor backProcessor = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {

                if ((keycode == Input.Keys.BACK) || (keycode == Input.Keys.BACKSPACE)){
                    onBackPress();
                }
                return false;
            }

        };


        InputMultiplexer multiplexer = new InputMultiplexer(stage, backProcessor);
        Gdx.input.setInputProcessor(multiplexer);


        bgImage = new Image();
        stage.addActor(bgImage);
    }








    protected void showTooltip(int align, Actor actor, String text){
        if(tooltip == null) {
            tooltip = new Tooltip(wordConnectGame);
            tooltip.setWidth(stage.getWidth() * UIConfig.TOOLTIP_WIDTH_COEF);
        }
        float margin = stage.getWidth() * 0.01f;
        if(align == Align.right) {
            tooltip.setX(actor.getX() - tooltip.getWidth() - margin);
        }else{
            tooltip.setX(actor.getX() + actor.getWidth() + margin);
        }
        tooltip.setY(actor.getY() + actor.getHeight() * 0.5f - tooltip.getHeight() * 0.5f);

        if(tooltip.getParent() == null) {
            stage.addActor(tooltip);
        }

        tooltip.setText(align, text);
    }




    public Toast showToast(String msg){

        if(toast == null) {
            toast = new Toast(wordConnectGame.resourceManager, stage.getWidth());
            toast.setZIndex(1000);
        }else{
            toast.clearActions();
        }

        toast.setX((stage.getWidth() - toast.getWidth()) * 0.5f);
        toast.setY((stage.getHeight() - toast.getHeight()) * 0.6f);

        toast.setVisible(true);
        stage.addActor(toast);

        toast.show(msg);
        return toast;
    }






    protected boolean checkWheelDialogTiming(){

        if(GameConfig.ALLOWED_SPIN_COUNT > 0){
            Preferences preferences = Gdx.app.getPreferences(Constants.PREFS_NAME);
            long lastSpinTime = preferences.getLong(Constants.KEY_LAST_WHEEL_SPIN_TIME, 0);

            boolean spin = false;

            if(lastSpinTime == 0){
                spin = true;
            }else{
                final long millisInADay = 86400000;
                long elapsed = TimeUtils.timeSinceMillis(lastSpinTime);

                if(elapsed > millisInADay)
                    spin = true;
            }

            spin |= GameConfig.DEBUG_LUCKY_WHEEL;

            if(spin){
                if(wheelDialog == null) {
                    wheelDialog = new WheelDialog(stage.getWidth(), stage.getHeight(), this);
                    wheelDialog.setDialogId(Constants.WHEEL_DIALOG);
                }
                stage.addActor(wheelDialog);
                wheelDialog.setVisible(true);
                wheelDialog.show();

                return true;
            }


        }

        return false;
    }





    protected void setTopPanel(){
        float width = stage.getWidth() - stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN * 2;
        topPanel = new TopPanel(this, width);
        topPanel.setOrigin(Align.center);
        topPanel.setX(stage.getWidth() * UIConfig.LEFT_AND_RIGHT_MARGIN);
        topPanel.setY(stage.getHeight() - topPanel.getHeight());
        stage.addActor(topPanel);
        topPanel.coinView.setPlusListener(iapDialogOpener);
        topPanel.addMenuButtonListener(menuOpener);
    }





    private ChangeListener menuOpener = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {

            stage.getRoot().setTouchable(Touchable.disabled);
            if(menu == null) menu = new Menu(stage.getWidth(), stage.getHeight(), BaseScreen.this, languageSelectionComplete);

            stage.addActor(menu);
            menu.show();
        }
    };








    public ChangeListener iapDialogOpener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(!ConnectionManager.network.isConnected()){
                showToast(LanguageManager.get("no_connection"));
                return;
            }

            stage.getRoot().setTouchable(Touchable.disabled);

            shoppingDialog = new ShoppingDialog(stage.getWidth(), stage.getHeight(), BaseScreen.this, topPanel, iapDialogOpenFinished, iapDialogClosed);
            shoppingDialog.setVisible(true);
            stage.addActor(shoppingDialog);


        }
    };






    protected Runnable iapDialogOpenFinished = new Runnable() {
        @Override
        public void run() {
            ShoppingCallback callback = new ShoppingCallback() {

                @Override
                public void onShoppingItemsReady(List<ShoppingItem> items) {
                    if(shoppingDialog != null){
                        shoppingDialog.setShoppingItems(items);
                    }
                }

                @Override
                public void onShoppingItemsError(int code) {
                    if(shoppingDialog != null) {
                        shoppingDialog.remove();
                        shoppingDialog = null;
                    }
                    showErrorDialog(LanguageManager.get("iap_error"), LanguageManager.format("iap_error_text", code));
                }


                @Override
                public void onPurchase(String sku) {
                    savePurchase(sku);
                }


                @Override
                public void onTransactionError(int code) {

                    shoppingDialog.onTransactionError(code);
                }
            };


            wordConnectGame.shoppingProcessor.queryShoppingItems(callback);
        }
    };



    private void showErrorDialog(String title, String text){
        AlertDialog alertDialog = new AlertDialog(stage.getWidth(), stage.getHeight(), this, title, text, LanguageManager.get("okay"), iapDialogClosed);
        alertDialog.setDialogId(Constants.ALERT_DIALOG_IAP_ERROR1);
        stage.addActor(alertDialog);
        alertDialog.show();
        stage.getRoot().setTouchable(Touchable.enabled);
    }



    private void savePurchase(String sku){
        ItemContent content = ShoppingDialog.mapping.get(sku);

        if(shoppingDialog != null) {
            shoppingDialog.madeAPurchase = true;
            shoppingDialog.close();
        }
        if(!content.removeAds){
            updateCoinsAndHints(content);
        }

        if(!ConfigProcessor.muted) {
            Sound sound = wordConnectGame.resourceManager.get(ResourceManager.SFX_BONUS_WORD, Sound.class);
            sound.play(SoundConfig.SFX_BONUS_VOLUME);
        }
    }



    public void updateCoinsAndHints(ItemContent content){

        if(content.coins > 0) {
            int remaining = HintManager.getRemainingCoins();
            int count = remaining + content.coins;
            HintManager.setCoinCount(count);

            if (topPanel != null) {
                topPanel.coinView.update(count);
            }
        }

        if(content.singleRandomReveal > 0){
            int remaining = HintManager.getRemainingSingleRandomRevealCount();
            int count = remaining + content.singleRandomReveal;
            HintManager.setSingleRandomRevealCount(count);
            updateHintButtonQuantity(singleRandomHintBtn, count);
        }

        if(content.multiRandomReveal > 0){
            int remaining = HintManager.getRemainingMultiRandomRevealCount();
            int count = remaining + content.multiRandomReveal;
            HintManager.setMultiRandomRevealCount(count);
            updateHintButtonQuantity(multiRandomHintBtn, count);
        }

        if(content.fingerReveal > 0){
            int remaining = HintManager.getRemainingFingerRevealCount();
            int count = remaining + content.fingerReveal;
            HintManager.setFingerHintRevealCount(count);
            updateHintButtonQuantity(fingerHintBtn, count);
        }

        if(content.rocketReveal > 0){
            int remaining = HintManager.getRemainingRocketRevealCount();
            int count = remaining + content.rocketReveal;
            HintManager.setRocketRevealCount(count);
            updateHintButtonQuantity(rocketHintBtn, count);
        }
    }



    private void updateHintButtonQuantity(HintButton button, int quantity){
        if(this instanceof GameScreen && button != null) {
            button.update(quantity);
        }
    }



    protected Runnable iapDialogClosed = new Runnable() {
        @Override
        public void run() {
            topPanel.coinView.plus.setDisabled(false);
            if(shoppingDialog == null) return;

            boolean madeAPurchase = shoppingDialog.madeAPurchase;
            shoppingDialog.remove();
            shoppingDialog = null;
            if(!madeAPurchase){
                if(rewardedVideoButton != null && !rewardedVideoButton.timerRunning() && GameConfig.SHOW_WATCH_AD_AFTER_IAP && wordConnectGame.adManager != null && wordConnectGame.adManager.isRewardedAdEnabledToEarnCoins()) {
                    openWatchAndEarnDialog(true);
                }else{
                    stage.getRoot().setTouchable(Touchable.enabled);
                    if(BaseScreen.this instanceof GameScreen){
                        GameScreen gameScreen = (GameScreen)BaseScreen.this;
                        gameScreen.resumeIdleTimer();
                    }
                }
            }else{
                stage.getRoot().setTouchable(Touchable.enabled);
                if(BaseScreen.this instanceof GameScreen){
                    GameScreen gameScreen = (GameScreen)BaseScreen.this;
                    gameScreen.resumeIdleTimer();
                }
            }


        }
    };




    protected void openWatchAndEarnDialog(boolean delay){
        if(rewardedVideoButton != null && rewardedVideoButton.timerRunning()){
            stage.getRoot().setTouchable(Touchable.enabled);
            rewardedVideoButton.flashText();
            return;
        }

        if(delay) {
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    setWatchAndEarnDialog();
                }
            });

            stage.addAction(new SequenceAction(Actions.delay(0.5f), runnableAction));
        }else{
            setWatchAndEarnDialog();
        }

    }




    private void setWatchAndEarnDialog(){
        if (watchAndEarnDialog == null) {
            watchAndEarnDialog = new WatchAndEarnDialog(stage.getWidth(), stage.getHeight(), BaseScreen.this, watchAndEarnDialogClosed);
        }
        stage.addActor(watchAndEarnDialog);
        watchAndEarnDialog.show();
    }




    private Runnable watchAndEarnDialogClosed = new Runnable() {
        @Override
        public void run() {
            if(wordConnectGame.adManager.isRewardedAdLoaded())
                wordConnectGame.adManager.showRewardedAd(rewardVideoForCoinsHasFinishedGameScreen);
            else
                showToast(LanguageManager.get("no_video"));

        }
    };





    protected RewardedVideoCloseCallback rewardVideoForCoinsHasFinishedGameScreen = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            //rewardedVideoForCoinsHasFinished.closed(earnedReward);
            //if(earnedReward) rewardedVideoButton.startTimer(TimeUtils.millis());

            if(earnedReward) {
                int remaining = HintManager.getRemainingCoins();
                int newTotal = remaining + GameConfig.NUMBER_OF_COINS_EARNED_FOR_WATCHING_VIDEO;
                HintManager.setCoinCount(newTotal);
                topPanel.coinView.update(newTotal);
                if (rewardedAdAnimation == null)
                    rewardedAdAnimation = new RewardedAdAnimation(BaseScreen.this);
                stage.addActor(rewardedAdAnimation);
                rewardedAdAnimation.show();

                if(rewardedVideoButton != null && wordConnectGame.adManager.getIntervalBetweenRewardedAds() > 0) rewardedVideoButton.startTimer(TimeUtils.millis());
            }
        }
    };



    /*protected RewardedVideoCloseCallback rewardedVideoForCoinsHasFinished = new RewardedVideoCloseCallback() {
        @Override
        public void closed(boolean earnedReward) {
            if(earnedReward) {
                int remaining = HintManager.getRemainingCoins();
                int newTotal = remaining + GameConfig.NUMBER_OF_COINS_EARNED_FOR_WATCHING_VIDEO;
                HintManager.setCoinCount(newTotal);
                topPanel.coinView.update(newTotal);
                if (rewardedAdAnimation == null)
                    rewardedAdAnimation = new RewardedAdAnimation(BaseScreen.this);
                stage.addActor(rewardedAdAnimation);
                rewardedAdAnimation.show();
            }
        }
    };*/




    public void notificationReceived(int newAmount, String title, String text){

        if(topPanel != null && topPanel.coinView != null){
            topPanel.coinView.update(newAmount);
        }


        AlertDialog alertDialog = new AlertDialog(stage.getWidth(), stage.getHeight(), this, title, text, LanguageManager.get("okay"), null);
        alertDialog.setDialogId(Constants.ALERT_DIALOG_NOTIFICATION);
        stage.addActor(alertDialog);
        alertDialog.show();

        if(!ConfigProcessor.muted) {
            Sound sound = wordConnectGame.resourceManager.get(ResourceManager.SFX_NOTIFICATION, Sound.class);
            sound.play(SoundConfig.SFX_NOTIFICATION_VOLUME);
        }

    }





    protected boolean onBackPress(){

        if (!backNavQueue.empty()){
            BackNavigator backNavigator = backNavQueue.peek();

            if(backNavigator != null) {
                Actor actor = (Actor)backNavigator;
                if(actor.getStage() != null) {
                    return backNavigator.navigateBack();
                }
            }
            return false;
        }else{
            return false;
        }
    }




    public void nullifyDialog(int id){
        if(id == -1) return;
        BaseDialog baseDialog = dialogMap.get(id);

        if(baseDialog != null){
            dialogMap.remove(id);
            baseDialog.remove();
            baseDialog = null;
        }
    }




    public Runnable nullifyTutorial = new Runnable() {
        @Override
        public void run() {
            if(tutorial != null){
                tutorial.remove();
                tutorial = null;
            }
        }
    };






    protected Runnable languageSelectionComplete = new Runnable() {
        @Override
        public void run() {
            wordConnectGame.setScreen(new IntroScreen(wordConnectGame));
        }
    };



    protected void setBackground(Color bgColor, String path){
        r = bgColor.r;
        g = bgColor.g;
        b = bgColor.b;

        if(path != null){
            //don't load the same image again
            if(prevBackgroundTexture != null && prevBackgroundTexture.toString().equals(path)) return;


            if(wordConnectGame.resourceManager.contains(path)){
                backgroundTexture = wordConnectGame.resourceManager.get(path, Texture.class);
            }else{
                wordConnectGame.resourceManager.load(path, Texture.class);
                wordConnectGame.resourceManager.finishLoading();

                try {
                    backgroundTexture = wordConnectGame.resourceManager.get(path, Texture.class);
                }catch (GdxRuntimeException e){
                    backgroundTexture = new Texture(Gdx.files.internal(path));
                }
            }


            backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            bgImage.setDrawable(new TextureRegionDrawable(backgroundTexture));
            bgImage.setScaling(Scaling.fill);
            bgImage.setSize(stage.getWidth(), stage.getHeight());

            if(prevBackgroundTexture != null) {
                wordConnectGame.resourceManager.unload(prevBackgroundTexture.toString());
                prevBackgroundTexture.dispose();
            }
            prevBackgroundTexture = backgroundTexture;
        }

    }




    public void setNewLanguage(String code){

        LanguageManager.setLocale(code, wordConnectGame);

        ResourceManager.LOCALE_PROPERTIES_FILE = "data/" + code + "/strings";
        wordConnectGame.resourceManager.load(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);
        wordConnectGame.resourceManager.setLoader(Text.class, new TextLoader(new InternalFileHandleResolver()));

        wordConnectGame.resourceManager.load( "data/" + LanguageManager.locale.code + "/words.txt", Text.class, new TextLoader.TextParameter());
        wordConnectGame.resourceManager.load( "data/" + LanguageManager.locale.code + "/vulgar.txt", Text.class, new TextLoader.TextParameter());
        wordConnectGame.resourceManager.finishLoading();
        LanguageManager.bundle = wordConnectGame.resourceManager.get(ResourceManager.LOCALE_PROPERTIES_FILE, I18NBundle.class);

    }







    @Override
    public void dispose() {
        super.dispose();
        if(tutorial != null) tutorial.dispose();
    }





    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        camera.update();

        Gdx.gl.glClearColor(r,g,b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }


}
