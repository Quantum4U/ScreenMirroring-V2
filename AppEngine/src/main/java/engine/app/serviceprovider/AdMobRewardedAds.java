package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppFullAdsListener;
import engine.app.listener.OnRewardedEarnedItem;
import engine.app.utils.EngineConstant;

/**
 * Created by Meenu Singh on 17/06/20.
 */
public class AdMobRewardedAds {
    private final String TAG = "AdMobRewardedAds";
    private static AdMobRewardedAds instance;
    private RewardedAd rewardedAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

    private AdMobRewardedAds(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

    }

    public static AdMobRewardedAds getInstance(Context context) {
        if (instance == null) {
            synchronized (AdMobRewardedAds.class) {
                if (instance == null) {
                    instance = new AdMobRewardedAds(context);
                }
            }
        }
        return instance;
    }

    public void initAdMobRewardedVideo(final Context context, String id, final AppFullAdsListener listener, final OnRewardedEarnedItem onRewardedEarnedItem) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }

            id = id.trim();
            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());
            MobileAds.setAppMuted(true);

            try {
                RewardedAd.load(context, id, adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        rewardedAd = null;
                        listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, loadAdError.getMessage());
                        if (onRewardedEarnedItem != null) {
                            onRewardedEarnedItem.onRewardedFailed(loadAdError.getMessage());
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        if (onRewardedEarnedItem != null) {
                            onRewardedEarnedItem.onRewardedLoaded();
                        }
                    }
                });
            } catch (Exception e) {
                listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, e.getMessage());
            }

        } else {
            listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, "Rewarded Video Id null");
        }

    }

    public void showAdMobRewardedVideo(final Context context, String id, final AppFullAdsListener listener, final OnRewardedEarnedItem onRewardedEarnedItem) {
        if (context != null && id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            id = id.trim();
            final String finalId = id;
            if (rewardedAd != null) {
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad was shown.");
                        rewardedAd = null;
                        listener.onFullAdLoaded();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "Ad failed to show.");
                        listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, String.valueOf(adError.getCode()));
                        if (onRewardedEarnedItem != null) {
                            onRewardedEarnedItem.onRewardedFailed(adError.getMessage());
                        }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        Log.d(TAG, "Ad was dismissed.");
                        if (onRewardedEarnedItem != null)
                            initAdMobRewardedVideo(context, finalId, listener, onRewardedEarnedItem);
                    }
                });
                rewardedAd.show((Activity) context, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d(TAG, "The user earned the reward. " + rewardItem.getAmount());
                        if (onRewardedEarnedItem != null)
                            onRewardedEarnedItem.onUserEarnedReward(rewardItem);
                    }
                });
            } else {
                if (onRewardedEarnedItem != null)
                    initAdMobRewardedVideo(context, id, listener, onRewardedEarnedItem);
                listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, "Rewarded Video object null");
            }
        } else {
            listener.onFullAdFailed(AdsEnum.ADS_REWARDED_ADMOB, "Rewarded Video Id null");
        }
    }
}
