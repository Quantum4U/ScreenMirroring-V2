package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;
import engine.app.utils.EngineConstant;

public class AdMobAds {
    private final String mTag = "AdMobAds";
    private static AdMobAds instance;
    private InterstitialAd mInterstitial;
    private static final String AD_UNIT_ID_BANNER = "ca-app-pub-3940256099942544/6300978111";
    private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";

    private AdMobAds(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        MobileAds.setAppMuted(true);
        MobileAds.setAppVolume(0);
    }


    public static AdMobAds getAdmobOBJ(Context context) {
        if (instance == null) {
            synchronized (AdMobAds.class) {
                if (instance == null) {
                    instance = new AdMobAds(context);
                }
            }
        }
        return instance;
    }

    //working
    public void admob_GetBannerAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID_BANNER.trim();
            }
            id = id.trim();
            Log.d(mTag, "admob_GetBannerAds: " + id);

            AdView adView = new AdView(context);
            adView.setAdUnitId(id);

            adView.setAdSize(AdSize.BANNER);

            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsListener(adView, listener));
                adView.loadAd(adRequest);
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Id null");
        }
    }

    //working
    public void admob_GetBannerRectangleAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID_BANNER.trim();
            }
            id = id.trim();
            Log.d(mTag, "admob_GetBannerRectangleAds: " + id);

            AdView adView = new AdView(context);
            adView.setAdUnitId(id);

            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);


            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsListener(adView, listener));
                adView.loadAd(adRequest);
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Rectangle Id null");
        }

    }

    //working
    public void admob_GetBannerLargeAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID_BANNER.trim();
            }
            id = id.trim();
            Log.d(mTag, "admob_GetBannerLargeAds: " + id);

            AdView adView = new AdView(context);
            adView.setAdUnitId(id);

            adView.setAdSize(AdSize.LARGE_BANNER);

            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsListener(adView, listener));
                adView.loadAd(adRequest);
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Large Id null");
        }
    }

    /**
     * admob_InitFullAds function call only splash for first time caching ads and after ads onAdClosed
     */
    public void admob_InitFullAds(final Context context, String id, final AppFullAdsListener listener, final boolean isFromCache) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID_INTERSTITIAL.trim();
            }

            id = id.trim();

            AdRequest adRequest = new AdRequest.Builder().build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            InterstitialAd.load(context, id, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitial = interstitialAd;
                    if (isFromCache) {
                        listener.onFullAdLoaded();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mInterstitial = null;
                    if (isFromCache) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, loadAdError.getMessage());
                    }
                }
            });
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, "Init FullAds Id null");
        }

    }

    public void admob_showFullAds(final Activity context, String id, final AppFullAdsListener listener,boolean isFromSplash) {

        if (context != null && id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID_INTERSTITIAL.trim();
            }
            id = id.trim();
            if (mInterstitial != null) {
                Log.d(mTag, "admob_showFullAds: " + mInterstitial + " " + id);
                final String finalId = id;
                mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        // Called when fullscreen content failed to show.
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, adError.getMessage());
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitial = null;
                        Log.d(mTag, "onAdShowedFullScreenContent: ");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        // Called when fullscreen content is dismissed.
                        listener.onFullAdClosed();

                        if(!isFromSplash){
                            admob_InitFullAds(context, finalId, listener, false);
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
                mInterstitial.show(context);


            } else {
                if(!isFromSplash) {
                    admob_InitFullAds(context, id, listener, false);
                }
                listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, "Admob Interstitial null");

            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, "FullAds Id null");
        }
    }
}
