package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.utils.EngineConstant;


/**
 * Created by Meenu Singh on 2019-08-26.
 */
public class AdMobAdaptive {
    private static AdMobAdaptive instance;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";

    private AdMobAdaptive(Context context) {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
    }

    public static AdMobAdaptive getAdmobAdaptiveObj(Context context) {
        if (instance == null) {
            synchronized (AdMobAdaptive.class) {
                if (instance == null) {
                    instance = new AdMobAdaptive(context);
                }
            }
        }
        return instance;
    }

    public void admob_GetBannerAdaptive(Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            AdView adView = new AdView(context);
            adView.setAdUnitId(id);
            AdSize adSize = getAdSize(context);
            adView.setAdSize(adSize);

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

    public void admob_GetBannerAdaptiveAppLock(Context context, String id, final AppAdsListener listener, int adsW) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            AdView adView = new AdView(context);
            adView.setAdUnitId(id);
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adsW);;
            adView.setAdSize(adSize);

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
}
