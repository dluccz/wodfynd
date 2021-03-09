package word.game.ui.dialogs.iap;

public interface ShoppingProcessor {

    boolean isIAPEnabled();

    void queryShoppingItems(ShoppingCallback callback);//SkuDetails

    void reportItemRetrivalError(int code);
    void reportTransactionError(int code);

    void makeAPurchase(String sku);

    void hasMadeAPurchase(String sku, boolean newPurchase);

    boolean isRemoveAdsPurchased();

    String getRemoveAdsPrice();
}
