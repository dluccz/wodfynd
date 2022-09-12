package word.game.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import word.game.R;
import word.game.ui.dialogs.iap.ShoppingItem;
import word.game.ui.dialogs.iap.ShoppingCallback;
import word.game.ui.dialogs.iap.ShoppingProcessor;

public class IAPActivity extends AdActivity implements PurchasesUpdatedListener, BillingClientStateListener{


    private BillingClient billingClient;
    protected AndroidShoppingProcessor androidShoppingProcessor;
    private List<ProductDetails> skuDetailsList;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getBoolean(R.bool.IAP_ENABLED)) {
            androidShoppingProcessor = new AndroidShoppingProcessor();
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(this);
        }
    }




    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            queryPurchases();
            findRemoveAdsCost();
            Log.d("iap", "billing service disconnected");
        }else{
            Log.d("iap", "error in billing service connection, error code:"+billingResult.getResponseCode());
        }

    }




    @Override
    public void onBillingServiceDisconnected() {
        Log.d("iap", "billing service disconnected");
    }




    void queryPurchases(){
        if(billingClient == null)
            return;

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                new PurchasesResponseListener() {
                    public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> list) {
                        if(list != null) handlePurchases(list);
                    }
                }
        );
    }




    void handlePurchases(List<Purchase> purchases) {
        for(Purchase purchase : purchases){
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                handlePurchase(purchase);
            }
        }
    }






    void handlePurchase(Purchase purchase){
        Log.d("iap", "Found purchase:"+purchase.getProducts().get(0)+", purchase state:"+purchase.getPurchaseState());
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){

            Log.d("iap", "purchase.isAcknowledged: " + purchase.isAcknowledged());

            //ConsumeParams cp = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
            //billingClient.consumeAsync(cp, consumeResponseListener);

            if(!purchase.isAcknowledged()) {
                if (purchase.getProducts().get(0).equals(getString(R.string.IAP_ITEM_remove_ads))) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                } else {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                }
                androidShoppingProcessor.hasMadeAPurchase(purchase.getProducts().get(0), true);
            }else{
                if (purchase.getProducts().get(0).equals(getString(R.string.IAP_ITEM_remove_ads))) {
                    androidShoppingProcessor.hasMadeAPurchase(purchase.getProducts().get(0), false);
                }
            }
        }
    }





    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("iap", "IAP operation cancelled");
        } else {
            androidShoppingProcessor.reportTransactionError(billingResult.getResponseCode());
            Log.d("iap", "IAP error on purchase, error code:"+billingResult.getResponseCode());
        }
    }






    public void startPurchase(String productId){
        Log.d("iap", "START PURCHASE, product id to purchase:."+productId);

        if(billingClient != null){
            for(ProductDetails skuDetails : skuDetailsList){

                if(skuDetails.getProductId().equals(productId)){
                    List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
                    productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(skuDetails).build());
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build();
                    BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
                    if(billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK)
                        Log.d("iap", "Failed to start purchase, error code:"+billingResult.getResponseCode()+", "+billingResult.getDebugMessage());

                    break;
                }
            }
        }

    }



    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            Log.d("iap", "non-consumable purchase acknowledged, result:" + billingResult.getResponseCode()+", "+billingResult.getDebugMessage());
        }
    };



    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            Log.d("iap", "consumable purchase consumed, result:" + billingResult.getResponseCode()+", "+billingResult.getDebugMessage());
        }
    };




    private void findRemoveAdsCost(){
        List<QueryProductDetailsParams.Product> skuList = new ArrayList<>();
        skuList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(getString(R.string.IAP_ITEM_remove_ads))
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams.Builder params = QueryProductDetailsParams.newBuilder();
        params.setProductList(skuList).build();

        billingClient.queryProductDetailsAsync(params.build(), new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {

                if(list == null){
                    androidShoppingProcessor.removeAdsPrice = "";
                    return;
                }

                for(ProductDetails skuDetails : list){
                    if(skuDetails.getProductId().equals(getString(R.string.IAP_ITEM_remove_ads))){
                        androidShoppingProcessor.removeAdsPrice = skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
                        break;
                    }
                }
            }
        });
    }



    private void showProducts(){
        List<String> skuList = new ArrayList<>();
        skuList.add(getString(R.string.IAP_ITEM_remove_ads));
        skuList.add(getString(R.string.IAP_ITEM_pack_jumbo));
        skuList.add(getString(R.string.IAP_ITEM_pack_large));
        skuList.add(getString(R.string.IAP_ITEM_pack_medium));
        skuList.add(getString(R.string.IAP_ITEM_pack_mini));
        skuList.add(getString(R.string.IAP_ITEM_coin_13440));
        skuList.add(getString(R.string.IAP_ITEM_coin_6240));
        skuList.add(getString(R.string.IAP_ITEM_coin_2940));
        skuList.add(getString(R.string.IAP_ITEM_coin_1340));
        skuList.add(getString(R.string.IAP_ITEM_coin_760));
        skuList.add(getString(R.string.IAP_ITEM_coin_240));

        ArrayList<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        for(String sku : skuList) {
            productList.add(QueryProductDetailsParams.Product.newBuilder().setProductId(sku).setProductType(BillingClient.ProductType.INAPP).build());
        }

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder().setProductList(productList).build();
        billingClient.queryProductDetailsAsync(params, productDetailsResponseListener);
    }




    ProductDetailsResponseListener productDetailsResponseListener = new ProductDetailsResponseListener() {
        @Override
        public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
            if(list == null){
                androidShoppingProcessor.reportItemRetrivalError(-100);
                return;
            }

            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                androidShoppingProcessor.returnShoppingItems(list);
            }else {
                androidShoppingProcessor.reportItemRetrivalError(billingResult.getResponseCode());
            }
        }
    };




    @Override
    protected void onDestroy() {

        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
        }
        super.onDestroy();
    }



    /**************************************************************************************************************************************************************************/




    class AndroidShoppingProcessor implements ShoppingProcessor {


        public boolean purchasedRemovedAds;
        private String removeAdsPrice = "";
        private ShoppingCallback shoppingCallback;

        @Override
        public boolean isIAPEnabled() {
            return getResources().getBoolean(R.bool.IAP_ENABLED);
        }

        @Override
        public void queryShoppingItems(ShoppingCallback callback) {
            shoppingCallback = callback;
            showProducts();

        }

        @Override
        public void reportItemRetrivalError(int code) {
            shoppingCallback.onShoppingItemsError(code);
        }


        @Override
        public void reportTransactionError(int code) {
            shoppingCallback.onTransactionError(code);
        }


        public void returnShoppingItems(List<ProductDetails> list){

            if(list == null) {
                shoppingCallback.onShoppingItemsError(-1);
                return;
            }

            skuDetailsList = list;

            final List<ShoppingItem> items = new ArrayList<>();

            Collections.reverse(list);

            for(ProductDetails skuDetails : list){
                items.add(new ShoppingItem(skuDetails.getProductId(), skuDetails.getOneTimePurchaseOfferDetails().getFormattedPrice(), skuDetails.getTitle()));
            }

            shoppingCallback.onShoppingItemsReady(items);

        }




        public void makeAPurchase(final String sku){
            Gdx.app.log("iap", "purchase this:" + sku);
            startPurchase(sku);
        }



        public void hasMadeAPurchase(String sku, boolean newPurchase){

            if(sku.equals(getString(R.string.IAP_ITEM_remove_ads))) {
                Gdx.app.log("purchase", "yes");
                purchasedRemovedAds = true;
                isInterstitialEnabled = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(IAPActivity.this);
                preferences.edit().putBoolean(keyRemoveAdsPurchased, true).apply();
            }else{
                Gdx.app.log("purchase", "no");
            }

            if(newPurchase && shoppingCallback != null) {
                shoppingCallback.onPurchase(sku);
                Log.d("iap", "has made a purchase:" + sku + ", new: " + newPurchase);
            }
        }




        @Override
        public boolean isRemoveAdsPurchased() {
            return purchasedRemovedAds;
        }



        @Override
        public String getRemoveAdsPrice() {
            return removeAdsPrice;
        }


    };
}
