package word.game.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

import word.game.R;
import word.game.managers.AdManager;
import word.game.model.GameData;
import word.game.util.RewardedVideoCloseCallback;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class AdActivity extends AndroidApplication implements AdManager {

    private boolean isRewardedVideoEnabledToEarnCoins;
    private boolean isRewardedVideoEnabledToEarnMoves;
    private boolean isRewardedVideoEnabledToSpinWheel;
    protected boolean isInterstitialEnabled;

    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private RewardedVideoCloseCallback rewardedAdFinishedCallback;
    private Runnable interstitialClosedCallback;
    private boolean rewardEarned;
    private ConsentInformation consentInformation;
    private boolean inEUCountry;
    public String keyRemoveAdsPurchased = "keyRemoveAdsPurchased";
    private boolean interstitialLoaded;
    private boolean rewardedLoaded;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isRewardedVideoEnabledToEarnCoins = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_EARN_COINS);
        isRewardedVideoEnabledToEarnMoves = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_EARN_MOVES);
        isRewardedVideoEnabledToSpinWheel = getResources().getBoolean(R.bool.ADMOB_IS_REWARDED_AD_ENABLED_TO_SPIN_LUCKY_WHEEL);

        boolean purchasedIAP = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(keyRemoveAdsPurchased, false);

        if(purchasedIAP) isInterstitialEnabled = false;
        else isInterstitialEnabled = getResources().getBoolean(R.bool.ADMOB_IS_INTERSTITIAL_AD_ENABLED);

        if(isInterstitialEnabled || isRewardedVideoEnabledToEarnCoins || isRewardedVideoEnabledToEarnMoves || isRewardedVideoEnabledToSpinWheel) checkGDPR();

    }





    private void checkGDPR(){

        boolean testingGDPR = getResources().getBoolean(R.bool.ADMOB_IS_TESTING_GDPR);

        ConsentRequestParameters params;

        if(testingGDPR){

            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(getString(R.string.ADMOB_REAL_DEVICE_HASH_ID_FOR_TESTING))
                    .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            params = new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build();
        }else{
            params = new ConsentRequestParameters.Builder().build();
        }



        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        if(testingGDPR) {
            consentInformation.reset();
            inEUCountry = false;
        }
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        inEUCountry = consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED
                                || consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED;



                        if (consentInformation.isConsentFormAvailable() && consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            loadForm();
                        }else{
                            setupAds();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        Log.d("gdpr", "onConsentInfoUpdateFailure, code:"+formError.getErrorCode()+", " + formError.getMessage());
                        // Handle the error.
                    }
                });

    }




    public void loadForm(){
        UserMessagingPlatform.loadConsentForm(
                this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        consentForm.show(AdActivity.this, new ConsentForm.OnConsentFormDismissedListener() {
                                    @Override
                                    public void onConsentFormDismissed(@Nullable FormError formError) {
                                        setupAds();
                                    }
                                });
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        Log.d("gdpr", "onConsentFormLoadFailure: "+formError.getErrorCode()+", " + formError.getMessage());
                    }
                }
        );
    }




/**************************************************************************************************************************************************************************/



    private void enableTestAds(){
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        testDevices.add(getString(R.string.ADMOB_REAL_DEVICE_HASH_ID_FOR_TESTING));
        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDevices).build();
        MobileAds.setRequestConfiguration(configuration);
    }



    private void setupAds(){

        if(getResources().getBoolean(R.bool.ADMOB_IS_TESTING_ADS)) enableTestAds();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if(isRewardedVideoEnabledToEarnCoins || isRewardedVideoEnabledToEarnMoves || isRewardedVideoEnabledToSpinWheel) initRewardedAds();
                if(isInterstitialEnabled) loadInterstitialAds();
                MobileAds.setAppMuted(GameData.isGameMuted());
            }
        });


    }





    private void initRewardedAds(){
        rewardedAd = new RewardedAd(this, getString(R.string.ADMOB_REWARDED_AD_UNIT_ID));
        rewardedAd.loadAd(new AdRequest.Builder().build(), rewardedAdLoadCallback);
    }




    private void loadInterstitialAds(){
        Log.d("interstitial_ad", isInterstitialEnabled+"");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ADMOB_INTERSTITIAL_AD_UNIT_ID));
        interstitialAd.setAdListener(interstitialAdListener);
        interstitialAd.loadAd(new AdRequest.Builder().build());

    }




    private AdListener interstitialAdListener = new AdListener(){

        @Override
        public void onAdLoaded() {
            Log.d("interstitial_ad", "Interstitial ad loaded");
        }

        @Override
        public void onAdFailedToLoad(LoadAdError adError) {
            Log.d("interstitial_ad", "Interstitial ad failed to load: " + adError.toString());
            tryToLoadInterstitialAgain();
        }

        @Override
        public void onAdClosed() {
            if(interstitialClosedCallback != null) interstitialClosedCallback.run();
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    };




    private RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback(){

        @Override
        public void onRewardedAdLoaded() {
            Log.d("rewarded_ad", "Rewarded ad loaded");
        }

        @Override
        public void onRewardedAdFailedToLoad(LoadAdError adError) {
            Log.d("rewarded_ad", "Rewarded ad failed to load: " + adError.toString());
            tryToLoadRewardedAgain();
        }
    };




    private RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {

        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            rewardEarned = true;

        }


        @Override
        public void onRewardedAdClosed() {
            Log.d("rewarded_ad", "onRewardedAdClosed, earned:" + rewardEarned);
            initRewardedAds();
            rewardedAdFinishedCallback.closed(rewardEarned);

        }
    };




    private void tryToLoadRewardedAgain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRewardedAds();
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }




    private void tryToLoadInterstitialAgain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }



/**************************************************************************************************************************************************************************/

    @Override
    public boolean isInterstitialAdEnabled() {
        return isInterstitialEnabled;
    }




    @Override
    public boolean isRewardedAdEnabledToEarnCoins() {
        return isRewardedVideoEnabledToEarnCoins;
    }


    @Override
    public boolean isRewardedAdEnabledToEarnMoves(){
        return isRewardedVideoEnabledToEarnMoves;
    }


    @Override
    public boolean isRewardedAdEnabledToSpinWheel() {
        return isRewardedVideoEnabledToSpinWheel;
    }


    @Override
    public boolean isRewardedAdLoaded() {
        if(rewardedAd == null) return false;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                rewardedLoaded = rewardedAd.isLoaded();
                synchronized(this){
                    this.notify();
                }
            }
        };

        synchronized(r) {
            runOnUiThread(r);

            try {
                r.wait() ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return rewardedLoaded;
    }





    @Override
    public boolean isInterstitialAdLoaded() {

        if(interstitialAd == null) return false;


        Runnable r = new Runnable() {
            @Override
            public void run() {
                interstitialLoaded = interstitialAd.isLoaded();
                synchronized(this){
                    this.notify();
                }
            }
        };

        synchronized(r) {
            runOnUiThread(r);

            try {
                r.wait() ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return interstitialLoaded;
    }




    @Override
    public void showInterstitialAd(final Runnable closedCallback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialClosedCallback = closedCallback;
                if(interstitialAd != null && interstitialAd.isLoaded()) interstitialAd.show();
            }
        });

    }





    @Override
    public void showRewardedAd(final RewardedVideoCloseCallback finishedCallback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedAdFinishedCallback = finishedCallback;
                rewardEarned = false;
                if(rewardedAd != null  && rewardedAd.isLoaded()) rewardedAd.show(AdActivity.this, rewardedAdCallback);
            }
        });

    }




    @Override
    public int getIntervalBetweenRewardedAds(){
        return getResources().getInteger(R.integer.ADMOB_INTERVAL_BETWEEN_REWARDED_ADS_IN_SECONDS);
    }



    @Override
    public void openGDPRForm(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadForm();
            }
        });
    }


    @Override
    public boolean isUserInEU(){
        return inEUCountry;
    }

}
