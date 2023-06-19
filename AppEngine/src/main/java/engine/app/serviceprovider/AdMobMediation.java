package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;

import com.applovin.mediation.AppLovinExtras;
import com.applovin.mediation.ApplovinAdapter;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Map;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;
import engine.app.utils.EngineConstant;


/**
 * Created by Meenu Singh on 2019-08-26.
 */
public class AdMobMediation {

    private final String mTag = "AdMobMediation";

    private static AdMobMediation instance;
    private InterstitialAd mInterstitial;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712";

    private AdMobMediation(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d(mTag, String.format(
                            "\"NewEngine getNewBannerHeader Mediation Banner adapter Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }
            }
        });
    }

    public static AdMobMediation getAdMobMediationObj(Context context) {
        if (instance == null) {
            synchronized (AdMobMediation.class) {
                if (instance == null) {
                    instance = new AdMobMediation(context);
                }
            }
        }
        return instance;
    }

    public void admob_GetBannerMediationAppLock(Context context, String id, final AppAdsListener listener,int adsW) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            AdView adView = new AdView(context);
            adView.setAdUnitId(id);
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adsW);
            adView.setAdSize(adSize);


            //for applovin
            Bundle extras = new AppLovinExtras.Builder()
                    .setMuteAudio(true)
                    .build();

            // for collapse ads view of admob
//            Bundle extras1 = new Bundle();
//            extras1.putString("collapsible", "bottom");

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(ApplovinAdapter.class, extras)
                    //.addNetworkExtrasBundle(AdMobAdapter.class, extras1)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsMediationListener(adView, listener));
                adView.loadAd(adRequest);

            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Id null");
        }

    }


    public void admob_GetBannerMediation(Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            AdView adView = new AdView(context);
            adView.setAdUnitId(id);
            AdSize adSize = getAdSize(context);
            adView.setAdSize(adSize);


            //for applovin
            Bundle extras = new AppLovinExtras.Builder()
                    .setMuteAudio(true)
                    .build();

            // for collapse ads view of admob
//            Bundle extras1 = new Bundle();
//            extras1.putString("collapsible", "bottom");

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(ApplovinAdapter.class, extras)
                    //.addNetworkExtrasBundle(AdMobAdapter.class, extras1)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsMediationListener(adView, listener));
                adView.loadAd(adRequest);

            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Id null");
        }

    }

    private AdSize getAdSize(Activity activity) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    //working
    public void admob_GetBannerLargeAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            id = id.trim();
            Log.d(mTag, "admob_GetBannerLargeAds: " + id);

            AdView adView = new AdView(context);
            adView.setAdUnitId(id);

            adView.setAdSize(AdSize.LARGE_BANNER);


            //for applovin
            Bundle extras = new AppLovinExtras.Builder()
                    .setMuteAudio(true)
                    .build();

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(ApplovinAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsMediationListener(adView, listener));
                adView.loadAd(adRequest);
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Large Id null");
        }
    }

    public void admob_GetBannerRectangleAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            id = id.trim();
            Log.d(mTag, "admob_GetBannerRectangleAds: " + id);

            AdView adView = new AdView(context);
            adView.setAdUnitId(id);

            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);

            //for applovin
            Bundle extras = new AppLovinExtras.Builder()
                    .setMuteAudio(true)
                    .build();

            AdRequest adRequest = new AdRequest.Builder() .addNetworkExtrasBundle(ApplovinAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adView.setAdListener(new AdMobAdsMediationListener(adView, listener));
                adView.loadAd(adRequest);
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_ADMOB, "Banner Rectangle Id null");
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

            //for applovin
            Bundle extras = new AppLovinExtras.Builder()
                    .setMuteAudio(true)
                    .build();

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(ApplovinAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            InterstitialAd.load(context, id, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    mInterstitial = interstitialAd;
                    if (isFromCache) {
                        listener.onFullAdLoaded();
                    }
                    Log.d(mTag, "NewEngine showFullAds Mediation InitFullAds onAdLoaded: "+mInterstitial.getResponseInfo().getMediationAdapterClassName());

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.d(mTag, "NewEngine showFullAds Mediation InitFullAds onAdFailedToLoad: "+loadAdError.getMessage());
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
                Log.d(mTag, "NewEngine showFullAds Mediation: " +
                        mInterstitial.getResponseInfo().getMediationAdapterClassName()
                        + " " + id+"  "+mInterstitial.getResponseInfo().getAdapterResponses());
                final String finalId = id;
                mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        // Called when fullscreen content failed to show.
                        Log.d(mTag, "NewEngine showFullAds Mediation onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_ADMOB, adError.getMessage());
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitial = null;

                        Log.d(mTag, "NewEngine showFullAds Mediation onAdShowedFullScreenContent: "+ mInterstitial.getResponseInfo().getMediationAdapterClassName());
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        // Called when fullscreen content is dismissed.
                        Log.d(mTag, "NewEngine showFullAds Mediation onAdDismissedFullScreenContent: ");
                        if(!isFromSplash){
                            admob_InitFullAds(context, finalId, listener, false);
                        }
                        listener.onFullAdClosed();


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
