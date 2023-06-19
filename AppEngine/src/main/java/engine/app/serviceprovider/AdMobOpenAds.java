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
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppFullAdsListener;
import engine.app.utils.EngineConstant;

/**
 * Created by Meenu Singh on 21/10/2020.
 */
public class AdMobOpenAds {
    private static AdMobOpenAds instance;
    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
    private AppOpenAd appOpenAd = null;

    private AdMobOpenAds(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });


    }

    public static AdMobOpenAds getInstance(Context context) {
        if (instance == null) {
            synchronized (AdMobOpenAds.class) {
                if (instance == null) {
                    instance = new AdMobOpenAds(context);
                }
            }
        }
        return instance;
    }


    /**
     * Request an ad
     */
    public void initAdMobOpenAds(Activity context, String id, final AppFullAdsListener listener, final boolean isFromCache) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID;
            }

            id = id.trim();
            // We will implement this below.
            // Have unused ad, no need to fetch another.
            if (isAdAvailable()) {
                return;
            }
            /*
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            /*
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            // Handle the error.
            AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {

                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                @Override
                public void onAdLoaded(@NonNull AppOpenAd ad) {
                    super.onAdLoaded(ad);
                    appOpenAd = ad;
                    if (isFromCache)
                        listener.onFullAdLoaded();

                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    // Handle the error.
                    if (isFromCache)
                        listener.onFullAdFailed(AdsEnum.FULL_OPEN_ADS_ADMOB, loadAdError.getMessage());
                }

            };
            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            AppOpenAd.load(context, id, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);

        } else {
            listener.onFullAdFailed(AdsEnum.FULL_OPEN_ADS_ADMOB, "Id null");
        }
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    //private static boolean isShowingAd = false;

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdMobOpenAds(final Activity context, String id, final AppFullAdsListener listener) {
        if (context != null && id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "ca-app-pub-3940256099942544/1033173712".trim();
            }
            id = id.trim();
            // Only show ad if there is not already an app open ad currently showing
            // and an ad is available.
            if (isAdAvailable()) {
                final String finalId = id;
                FullScreenContentCallback fullScreenContentCallback =
                        new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Set the reference to null so isAdAvailable() returns false.
                                appOpenAd = null;
                                listener.onFullAdClosed();
                                initAdMobOpenAds(context, finalId, listener, false);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                listener.onFullAdFailed(AdsEnum.FULL_OPEN_ADS_ADMOB, adError.getMessage());
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {

                            }
                        };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(context);
                listener.onFullAdLoaded();
            } else {
                Log.d(LOG_TAG, "Can not show ad.");
                listener.onFullAdFailed(AdsEnum.FULL_OPEN_ADS_ADMOB, String.valueOf(isAdAvailable()));
                initAdMobOpenAds(context, id, listener, false);
            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_OPEN_ADS_ADMOB, "Id null");
        }

    }
}
