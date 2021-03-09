package word.game.managers;

import word.game.util.RewardedVideoCloseCallback;

public interface AdManager {


    boolean isInterstitialAdEnabled();
    boolean isRewardedAdEnabledToEarnCoins();
    boolean isRewardedAdEnabledToEarnMoves();
    boolean isRewardedAdEnabledToSpinWheel();

    boolean isRewardedAdLoaded();
    boolean isInterstitialAdLoaded();

    void showInterstitialAd(Runnable closedCallback);
    void showRewardedAd(RewardedVideoCloseCallback finishedCallback);

    int getIntervalBetweenRewardedAds();

    void openGDPRForm();
    boolean isUserInEU();


}
