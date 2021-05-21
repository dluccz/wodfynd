package word.game.ui.dialogs.iap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import word.game.config.UIConfig;
import word.game.graphics.NinePatches;
import word.game.graphics.AtlasRegions;
import word.game.managers.LanguageManager;
import word.game.managers.ResourceManager;
import word.game.model.Constants;
import word.game.screens.BaseScreen;
import word.game.screens.GameScreen;

import word.game.ui.LoadingAnim;
import word.game.ui.Modal;
import word.game.ui.dialogs.AlertDialog;
import word.game.ui.top_panel.TopPanel;
import word.game.util.UiUtil;
import word.game.util.BackNavigator;

public class ShoppingDialog extends Group implements BackNavigator {


    public static Map<String, ItemContent> mapping = new HashMap<>();

    private LoadingAnim loadingAnim;
    private Label.LabelStyle titleTextStyle;
    private Label.LabelStyle ribbonTextStyle;
    private Label.LabelStyle cardTitlelabelStyle;
    private Label.LabelStyle countLabelStyle;
    private Label.LabelStyle costStyle;
    private Label title;
    private ResourceManager resourceManager;
    private Modal modal;
    private Group content;
    private Group titleBar;
    private TopPanel topPanel;
    private float coinViewX, coinViewY;
    private float margin;
    private TextButton.TextButtonStyle purchaseButtonsStyle;
    private Group box0, box1, box2, box3, box4, box5, ribbonCoins, box6, box7, box8, box9, ribbonBundles, box10, ribbonOneTime;
    private Array<Group> contents;
    private Runnable closeCallback;
    private Runnable openCallback;
    public boolean madeAPurchase;
    private BaseScreen screen;
    private float padding;
    private float maxButtonWidth = 0.75f;

    public ShoppingDialog(float width, float height, BaseScreen screen, TopPanel topPanel, Runnable openCallback, Runnable closeCallback) {


        if(screen instanceof GameScreen){
            GameScreen s = (GameScreen)screen;
            s.stopIdleTimer();
        }


        setSize(width, height);

        this.screen = screen;
        this.resourceManager = screen.wordConnectGame.resourceManager;
        this.topPanel = topPanel;
        this.openCallback = openCallback;
        this.closeCallback = closeCallback;

        notifyNavigationController(screen);

        modal = new Modal(getWidth(), getHeight());
        modal.getColor().a = 0;
        addActor(modal);

        content = new Group();

        Action fadeIn = Actions.fadeIn(0.2f);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(loadingAnimStarter);
        modal.addAction(new SequenceAction(fadeIn, runnableAction));

    }






    private Runnable loadingAnimStarter = new Runnable() {
        @Override
        public void run() {
            loadingAnim = new LoadingAnim();
            loadingAnim.setX((getWidth() - loadingAnim.getWidth()) * 0.5f);
            loadingAnim.setY((getHeight() - loadingAnim.getHeight()) * 0.5f);
            loadingAnim.start(UIConfig.IAP_DIALOG_LOADING_CIRCLE_COLOR);
            addActor(loadingAnim);
            openCallback.run();
        }
    };






    public void setShoppingItems(final List<ShoppingItem> items){

        for(ShoppingItem item : items){
            item.title = LanguageManager.get(item.sku);
        }

        content.setWidth(UiUtil.isScreenWide() ? (getWidth() * 0.75f) : (getWidth() * 0.9f));
        margin = content.getWidth() * 0.03f;
        padding = content.getWidth() * 0.03f;

        String font = UIConfig.IAP_RIBBON_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        ribbonTextStyle = new Label.LabelStyle(resourceManager.get(font, BitmapFont.class), UIConfig.IAP_RIBBON_TEXT_COLOR);

        String font2 = UIConfig.IAP_CARD_TITLE_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        cardTitlelabelStyle = new Label.LabelStyle(resourceManager.get(font2, BitmapFont.class), UIConfig.IAP_CARD_TITLE_TEXT_COLOR);
        costStyle = new Label.LabelStyle(cardTitlelabelStyle);
        costStyle.background = new TextureRegionDrawable(AtlasRegions.reward_count_bg);

        String font3 = UIConfig.IAP_CARD_QUANTITY_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        countLabelStyle = new Label.LabelStyle(resourceManager.get(font3, BitmapFont.class), UIConfig.IAP_CARD_QUANTITY_TEXT_COLOR);

        titleBar = new Group();
        addActor(titleBar);
        titleBar.setY(getHeight());

        Image titleBarBg = new Image(NinePatches.rect);
        titleBarBg.setWidth(getWidth());
        titleBarBg.setHeight(topPanel.coinView.getHeight() * 1.5f);
        titleBarBg.setColor(UIConfig.IAP_DIALOG_TITLE_BG_COLOR);

        titleBar.setSize(titleBarBg.getWidth(), titleBarBg.getHeight());
        titleBar.addActor(titleBarBg);

        coinViewX = topPanel.coinView.getX();
        coinViewY = topPanel.coinView.getY();

        topPanel.coinView.remove();

        float margin = topPanel.coinView.getWidth() * 0.1f;
        topPanel.coinView.setX(margin);
        topPanel.coinView.setY((titleBar.getHeight() - topPanel.coinView.getHeight() * topPanel.coinView.getScaleY()) * 0.5f);
        titleBar.addActor(topPanel.coinView);
        topPanel.coinView.plus.setDisabled(true);


        ImageButton.ImageButtonStyle closeBtnStyle = new ImageButton.ImageButtonStyle();
        closeBtnStyle.up = new TextureRegionDrawable(AtlasRegions.close_button_up);
        closeBtnStyle.down = new TextureRegionDrawable(AtlasRegions.close_button_down);

        ImageButton closeBtn = new ImageButton(closeBtnStyle);
        closeBtn.setTransform(true);


        closeBtn.setX(titleBar.getWidth() - margin - closeBtn.getWidth() * closeBtn.getScaleX());
        closeBtn.setY((titleBar.getHeight() - closeBtn.getHeight() * closeBtn.getScaleY()) * 0.5f);
        titleBar.addActor(closeBtn);
        closeBtn.addListener(closeListener);


        titleTextStyle = new Label.LabelStyle(resourceManager.get(ResourceManager.fontSemiBoldShadow, BitmapFont.class), UIConfig.IAP_DIALOG_TITLE_TEXT_COLOR);

        title = new Label(LanguageManager.get("shop"), titleTextStyle);
        title.setFontScale(1.4f);

        if(title.getWidth() > titleBar.getWidth() * 0.4f) title.setFontScale(titleBar.getWidth() * 0.4f / title.getWidth());

        title.setAlignment(Align.center);
        title.setX((getWidth() - title.getWidth()) * 0.5f);

        titleBar.addActor(title);
        title.setY((titleBar.getHeight() - title.getHeight()) * 0.5f);

        purchaseButtonsStyle = new TextButton.TextButtonStyle();
        String font4 = UIConfig.IAP_BUY_BUTTON_TEXT_USE_SHADOW_FONT ? ResourceManager.fontSemiBoldShadow : ResourceManager.fontSemiBold;
        purchaseButtonsStyle.font = resourceManager.get(font4, BitmapFont.class);
        purchaseButtonsStyle.fontColor = UIConfig.IAP_BUY_BUTTON_TEXT_COLOR;
        purchaseButtonsStyle.up = new NinePatchDrawable(NinePatches.btn_dialog_up);
        purchaseButtonsStyle.down = new NinePatchDrawable(NinePatches.btn_dialog_down);

        Action titleBarDown = Actions.moveTo(titleBar.getX(), getHeight() - titleBar.getHeight(), 0.15f, Interpolation.fastSlow);
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                populate(items);
            }
        });

        titleBar.addAction(new SequenceAction(titleBarDown, runnableAction));
    }





    private void populate(List<ShoppingItem> items){

        contents = new Array<>();

        box0 = createCoinBox(items.get(2));
        box0.setX(0);
        box0.setY(margin);
        content.addActor(box0);

        float hmargin = (content.getWidth() - box0.getWidth() * 3) * 0.5f;

        box1 = createCoinBox(items.get(1));
        box1.setX(box0.getX() + box0.getWidth() + hmargin);
        box1.setY(margin);
        content.addActor(box1);

        box2 = createCoinBox(items.get(0));
        box2.setX(box1.getX() + box1.getWidth() + hmargin);
        box2.setY(margin);
        content.addActor(box2);

        //2.row
        box3 = createCoinBox(items.get(5));
        box3.setY(box2.getY() + box2.getHeight() + margin);
        content.addActor(box3);

        box4 = createCoinBox(items.get(4));
        box4.setX(box3.getX() + box3.getWidth() + hmargin);
        box4.setY(box3.getY());
        content.addActor(box4);

        box5 = createCoinBox(items.get(3));
        box5.setX(box4.getX() + box4.getWidth() + hmargin);
        box5.setY(box3.getY());
        content.addActor(box5);


        ribbonCoins = getRibbon(UIConfig.IAP_COINS_RIBBON_COLOR, LanguageManager.get("coins"));
        ribbonCoins.setX((content.getWidth() - ribbonCoins.getWidth()) * 0.5f);
        ribbonCoins.setY((box5.getY() + box5.getHeight() + margin * 2));
        content.addActor(ribbonCoins);


        //bundles
        box6 = createBundleBox(items.get(6));
        box6.setY(ribbonCoins.getY() + ribbonCoins.getHeight() + margin * 3);
        content.addActor(box6);

        box7 = createBundleBox(items.get(7));
        box7.setY(box6.getY() + box6.getHeight() + margin);
        content.addActor(box7);


        box8 = createBundleBox(items.get(8));
        box8.setY(box7.getY() + box7.getHeight() + margin);
        content.addActor(box8);

        box9 = createBundleBox(items.get(9));
        box9.setY(box8.getY() + box8.getHeight() + margin);
        content.addActor(box9);

        ribbonBundles = getRibbon(UIConfig.IAP_COMBO_PACK_RIBBON_COLOR, LanguageManager.get("combo_packs"));
        ribbonBundles.setX((content.getWidth() - ribbonBundles.getWidth()) * 0.5f);
        ribbonBundles.setY((box9.getY() + box9.getHeight() + margin * 2));
        content.addActor(ribbonBundles);


        if(!screen.wordConnectGame.shoppingProcessor.isRemoveAdsPurchased()) {
            box10 = createRemoveAdsBox(items.get(10));
            box10.setY(ribbonBundles.getY() + ribbonBundles.getHeight() + margin * 3);
            content.addActor(box10);

            ribbonOneTime = getRibbon(UIConfig.IAP_ONE_TIME_OFFER_RIBBON_COLOR, LanguageManager.get("one_time_packs"));
            ribbonOneTime.setX((content.getWidth() - ribbonOneTime.getWidth()) * 0.5f);
            ribbonOneTime.setY((box10.getY() + box10.getHeight() + margin * 2));
            content.addActor(ribbonOneTime);
        }


        if(ribbonOneTime != null)
            content.setHeight(ribbonOneTime.getY() + ribbonOneTime.getHeight());
        else
            content.setHeight(ribbonBundles.getY() + ribbonBundles.getHeight());

        ScrollPane pane = new ScrollPane(content, new ScrollPane.ScrollPaneStyle());
        pane.setScrollbarsVisible(false);
        pane.setSize(content.getWidth(), titleBar.getY() - margin);
        pane.setX((getWidth() - pane.getWidth()) * 0.5f);
        addActor(pane);


        for(int i = contents.size - 1; i >= 0; i--){
            animateRowIn(contents.get(i), (contents.size - 1 - i) * 0.1f);
        }


        if(loadingAnim != null){
            loadingAnim.remove();
        }


        getStage().getRoot().setTouchable(Touchable.enabled);
    }




    public void close(){
        closeListener.changed(null, null);
    }




    private ChangeListener closeListener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            getStage().getRoot().setTouchable(Touchable.disabled);

            if(ribbonOneTime != null) {
                animateRowOut(ribbonOneTime, 0, false);
                animateRowOut(box10, 0.1f, false);
            }

            animateRowOut(ribbonBundles, 0.1f, false);
            animateRowOut(box9, 0.2f, false);
            animateRowOut(box8, 0.3f, false);
            animateRowOut(box7, 0.4f, false);
            animateRowOut(box6, 0.5f, false);
            animateRowOut(ribbonCoins, 0.6f, false);

            animateRowOut(box5, 0.7f, false);
            animateRowOut(box4, 0.7f, false);
            animateRowOut(box3, 0.7f, false);
            animateRowOut(box2, 0.8f, false);

            animateRowOut(box1, 0.8f, false);
            animateRowOut(box0, 0.8f, true);
        }
    };





    public void onTransactionError(int code){
        showErrorDialog(LanguageManager.get("iap_error"), LanguageManager.format("iap_error_text", code));
    }




    private void showErrorDialog(String title, String text){
        AlertDialog alertDialog = new AlertDialog(getWidth(), getHeight(), screen, title, text, LanguageManager.get("okay"), null);
        alertDialog.setDialogId(Constants.ALERT_DIALOG_IAP_ERROR2);
        addActor(alertDialog);
        alertDialog.show();
        getStage().getRoot().setTouchable(Touchable.enabled);
    }



    private void animateRowOut(Group row, float time, boolean isLast){
        Action delay = Actions.delay(time);
        Action scale1 = Actions.scaleTo(1.05f, 1.05f, 0.08f, Interpolation.fastSlow);
        Action scale2 = Actions.scaleTo(0, 0, 0.16f, Interpolation.slowFast);

        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(delay);
        sequenceAction.addAction(scale1);
        sequenceAction.addAction(scale2);

        if(isLast){
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(closeDialog);
            sequenceAction.addAction(runnableAction);
        }

        //Update
        if(row != null) {
            row.addAction(new SequenceAction(Actions.delay(time), Actions.fadeOut(0.24f)));
            row.addAction(sequenceAction);
        }
    }





    private Runnable closeDialog = new Runnable() {

        @Override
        public void run() {
            titleBar.addAction(Actions.moveBy(0, titleBar.getHeight(), 0.2f));
            modal.addAction(Actions.fadeOut(0.2f));

            Action a = Actions.moveTo(topPanel.coinView.getX(), getHeight() + title.getHeight(), 0.2f);
            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(closer);
            topPanel.coinView.addAction(new SequenceAction(a, runnableAction));
        }
    };




    private Runnable closer = new Runnable() {

        @Override
        public void run() {
            topPanel.coinView.getColor().a = 0;
            topPanel.coinView.remove();
            topPanel.addActor(topPanel.coinView);
            topPanel.coinView.setPosition(coinViewX, coinViewY);
            topPanel.coinView.addAction(Actions.fadeIn(0.1f));
            closeCallback.run();
        }
    };




    private void animateRowIn(Group row, float time){
        row.getColor().a = 0;
        Action delay = Actions.delay(time);
        Action scale1 = Actions.scaleTo(1.05f, 1.05f, 0.2f, Interpolation.fastSlow);
        Action scale2 = Actions.scaleTo(1, 1, 0.1f, Interpolation.slowFast);

        Action sequenceAction = new SequenceAction(delay, scale1, scale2);
        row.addAction(sequenceAction);
        row.addAction(new SequenceAction(Actions.delay(time), Actions.fadeIn(0.2f)));
    }




    private Group createRemoveAdsBox(ShoppingItem item){
        Group row = new Group();
        contents.add(row);
        row.getColor().a = 0;
        row.setScale(0.5f);
        row.setWidth(content.getWidth());

        Image bg = new Image(NinePatches.iap_card1);
        bg.setWidth(row.getWidth());
        row.addActor(bg);
        bg.setColor(UIConfig.IAP_CARD_BG_COLOR);

        TextButton purchase = new TextButton(item.price, purchaseButtonsStyle);
        purchase.setName(item.sku);

        purchase.setWidth(bg.getWidth() * 0.4f);
        purchase.setTransform(true);
        purchase.setScale(0.8f);

        if(purchase.getLabel().getPrefWidth() > purchase.getWidth() * maxButtonWidth){
            purchase.getLabel().setFontScale(purchase.getWidth() * maxButtonWidth / purchase.getLabel().getPrefWidth());
        }

        purchase.setX((row.getWidth() - purchase.getWidth() * purchase.getScaleX()) * 0.5f);
        purchase.setY(padding);
        row.addActor(purchase);

        purchase.addListener(makePurchase);

        Image lightBg = new Image(NinePatches.iap_card2);
        lightBg.setWidth(row.getWidth() - padding * 2);
        lightBg.setColor(UIConfig.IAP_CARD_CENTER_BG_COLOR);

        lightBg.setX(padding);
        lightBg.setY(purchase.getY() + purchase.getHeight() * purchase.getScaleY() + padding);
        row.addActor(lightBg);

        ItemContent content = mapping.get(item.sku);

        Image icon = new Image(content.textureRegion);
        icon.setX((row.getWidth() - icon.getWidth()) * 0.5f);
        icon.setY(lightBg.getY() + padding * 2);
        row.addActor(icon);

        lightBg.setHeight(icon.getHeight() + padding * 4);

        Label label = new Label(item.title, cardTitlelabelStyle);
        label.setX((row.getWidth() - label.getWidth()) * 0.5f);
        label.setY(lightBg.getY() + lightBg.getHeight() + padding);
        row.addActor(label);


        bg.setHeight(label.getY() + label.getHeight() + padding);
        row.setHeight(bg.getHeight());
        row.setOrigin(Align.top);

        return row;
    }





    private ChangeListener makePurchase = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            screen.wordConnectGame.shoppingProcessor.makeAPurchase(actor.getName());
        }
    };




    private Group createCoinBox(ShoppingItem item){
        Group row = new Group();
        contents.add(row);
        row.getColor().a = 0;
        row.setScale(0.5f);

        Image bg = new Image(NinePatches.iap_card1);
        bg.setColor(UIConfig.IAP_CARD_BG_COLOR);
        row.addActor(bg);

        Image lightBg = new Image(NinePatches.iap_card2);
        lightBg.setColor(UIConfig.IAP_CARD_CENTER_BG_COLOR);
        row.addActor(lightBg);

        TextButton purchase = new TextButton(item.price, purchaseButtonsStyle);
        purchase.setName(item.sku);
        purchase.addListener(makePurchase);
        purchase.setWidth(content.getWidth() * 0.26f);
        purchase.setTransform(true);

        purchase.setScaleX(0.95f);
        purchase.setScaleY(0.8f);
        if(purchase.getLabel().getPrefWidth() > purchase.getWidth() * maxButtonWidth){
            purchase.getLabel().setFontScale(purchase.getWidth() * maxButtonWidth / purchase.getLabel().getPrefWidth());
        }

        row.addActor(purchase);

        ItemContent content = mapping.get(item.sku);

        Image icon = new Image(content.textureRegion);
        row.addActor(icon);

        Label label = new Label(content.coins+"", cardTitlelabelStyle);
        label.setAlignment(Align.center);
        if(label.getWidth() > purchase.getWidth())
            label.setFontScale(purchase.getWidth() * 0.9f / label.getWidth());
        row.addActor(label);


        row.setWidth(purchase.getWidth() + padding * 2);
        row.setHeight(purchase.getHeight() * purchase.getScaleY() * 4.5f);
        bg.setSize(row.getWidth(), row.getHeight());

        purchase.setX((row.getWidth() - purchase.getWidth() * purchase.getScaleX()) * 0.5f);
        purchase.setY(padding * 1.3f);

        label.setX((row.getWidth() - label.getWidth()) * 0.5f);
        label.setY(row.getHeight() - label.getHeight() * 1.2f);

        lightBg.setSize(purchase.getWidth(), label.getY() - label.getHeight() * 0.5f);
        lightBg.setX(padding);
        lightBg.setY(padding);

        icon.setX((row.getWidth() - icon.getWidth()) * 0.5f);
        icon.setY(lightBg.getY() + (lightBg.getHeight() * 0.45f));

        row.setOrigin(Align.top);

        return row;
    }





    private Group createBundleBox(ShoppingItem item){
        Group row = new Group();
        contents.add(row);
        row.setScale(0.5f);
        row.getColor().a = 0;
        row.setWidth(content.getWidth());

        Image bg = new Image(NinePatches.iap_card1);
        bg.setWidth(row.getWidth());
        bg.setColor(UIConfig.IAP_CARD_BG_COLOR);
        row.addActor(bg);

        TextButton purchase = new TextButton(item.price, purchaseButtonsStyle);
        purchase.setName(item.sku);
        purchase.addListener(makePurchase);
        purchase.setWidth(bg.getWidth() * 0.4f);
        purchase.setTransform(true);
        purchase.setScale(0.8f);
        if(purchase.getLabel().getPrefWidth() > purchase.getWidth() * maxButtonWidth){
            purchase.getLabel().setFontScale(purchase.getWidth() * maxButtonWidth / purchase.getLabel().getPrefWidth());
        }
        purchase.setX((row.getWidth() - purchase.getWidth() * purchase.getScaleX()) * 0.5f);
        purchase.setY(padding);
        row.addActor(purchase);

        Image lightBg = new Image(NinePatches.iap_card2);
        lightBg.setWidth(row.getWidth() - padding * 2);
        lightBg.setHeight(AtlasRegions.bundle4.getRegionHeight() * 1.2f + padding * 2);
        lightBg.setColor(UIConfig.IAP_CARD_CENTER_BG_COLOR);
        lightBg.setX(padding);
        lightBg.setY(purchase.getY() + purchase.getHeight() * purchase.getScaleY() + padding);
        row.addActor(lightBg);

        ItemContent content = mapping.get(item.sku);

        Image icon = new Image(content.textureRegion);
        icon.setX(row.getWidth() - icon.getWidth() - padding * 2);
        icon.setY(lightBg.getY() + padding);
        row.addActor(icon);


        Label label = new Label(item.title, cardTitlelabelStyle);
        label.setX((row.getWidth() - label.getWidth()) * 0.5f);
        label.setY(lightBg.getY() + lightBg.getHeight() + padding);
        row.addActor(label);

        bg.setHeight(label.getY() + label.getHeight() + padding);
        row.setHeight(bg.getHeight());

        Image coins = new Image(AtlasRegions.bundle_coins);
        coins.setX(lightBg.getX() + padding);
        coins.setY(lightBg.getY() + lightBg.getHeight() * 0.6f);
        row.addActor(coins);

        float costScale = 0.75f;
        float scaleAmount = 1f - costScale;
        Label coinCount = new Label("x" + content.coins, countLabelStyle);
        coinCount.setFontScale(costScale);
        coinCount.setX(coins.getX() + coins.getWidth() * 0.5f);
        coinCount.setY(coins.getY() - coinCount.getHeight() * scaleAmount);
        row.addActor(coinCount);

        Image bundle_random_hint = new Image(AtlasRegions.bundle_random_hint);
        bundle_random_hint.setX(coins.getX());
        bundle_random_hint.setY(lightBg.getY() + lightBg.getHeight() * 0.12f);
        row.addActor(bundle_random_hint);

        Label singleRandomCount = new Label("x" + content.singleRandomReveal, countLabelStyle);
        singleRandomCount.setFontScale(costScale);
        singleRandomCount.setX(bundle_random_hint.getX() + bundle_random_hint.getWidth() * 0.5f);
        singleRandomCount.setY(bundle_random_hint.getY() - singleRandomCount.getHeight() * scaleAmount);
        row.addActor(singleRandomCount);

        Image bundle_finger_hint = new Image(AtlasRegions.bundle_finger_hint);
        bundle_finger_hint.setX(bundle_random_hint.getX() + bundle_random_hint.getWidth() * 1.5f);
        bundle_finger_hint.setY(bundle_random_hint.getY());
        row.addActor(bundle_finger_hint);

        Label fingerCount = new Label("x" + content.fingerReveal, countLabelStyle);
        fingerCount.setFontScale(costScale);
        fingerCount.setX(bundle_finger_hint.getX() + bundle_finger_hint.getWidth() * 0.5f);
        fingerCount.setY(singleRandomCount.getY());
        row.addActor(fingerCount);

        Image bundle_multi_random_hint = new Image(AtlasRegions.bundle_multi_random_hint);
        bundle_multi_random_hint.setX(bundle_finger_hint.getX() + bundle_finger_hint.getWidth() * 2);
        bundle_multi_random_hint.setY(bundle_finger_hint.getY());
        row.addActor(bundle_multi_random_hint);

        Label multiCount = new Label("x" + content.multiRandomReveal, countLabelStyle);
        multiCount.setFontScale(costScale);
        multiCount.setX(bundle_multi_random_hint.getX() + bundle_multi_random_hint.getWidth() * 0.5f);
        multiCount.setY(fingerCount.getY());
        row.addActor(multiCount);

        Image bundle_rocket = new Image(AtlasRegions.bundle_rocket);
        bundle_rocket.setX(bundle_multi_random_hint.getX() + bundle_multi_random_hint.getWidth() * 2);
        bundle_rocket.setY(bundle_multi_random_hint.getY());
        row.addActor(bundle_rocket);

        Label rocketCount = new Label("x" + content.rocketReveal, countLabelStyle);
        rocketCount.setFontScale(costScale);
        rocketCount.setX(bundle_rocket.getX() + bundle_rocket.getWidth() * 0.5f);
        rocketCount.setY(multiCount.getY());
        row.addActor(rocketCount);

        row.setOrigin(Align.top);

        return row;
    }




    private Group getRibbon(Color ribbonColor, String text){
        NinePatch ribbon = NinePatches.ribbon;
        Image image = new Image(ribbon);
        image.setColor(ribbonColor);

        Group group = new Group();
        group.addActor(image);

        Label label = new Label(text, ribbonTextStyle);
        if(label.getWidth() > content.getWidth() * 0.6f) label.setFontScale(content.getWidth() * 0.6f / label.getWidth());

        image.setWidth(label.getWidth() * label.getFontScaleX() + (ribbon.getLeftWidth() + ribbon.getRightWidth()));
        group.setSize(image.getWidth(), image.getHeight());

        label.setX((group.getWidth() - label.getWidth() * label.getFontScaleX()) * 0.5f);
        label.setY((group.getHeight() - label.getHeight()) * 0.3f);
        group.addActor(label);

        group.getColor().a = 0;
        group.setOrigin(Align.top);
        group.setScale(0.5f);
        contents.add(group);

        return group;
    }




    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }



    @Override
    public boolean navigateBack() {
        closeListener.changed(null, null);
        return true;
    }


}
