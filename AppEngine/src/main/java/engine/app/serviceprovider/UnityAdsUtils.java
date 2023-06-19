package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

//import com.unity3d.ads.IUnityAdsInitializationListener;
//import com.unity3d.ads.IUnityAdsLoadListener;
//import com.unity3d.ads.IUnityAdsShowListener;
//import com.unity3d.ads.UnityAds;
//import com.unity3d.services.banners.BannerErrorInfo;
//import com.unity3d.services.banners.BannerView;
//import com.unity3d.services.banners.UnityBannerSize;

import app.pnd.adshandler.R;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;

/**
 * Created by Meenu Singh on 03/06/19.
 */
public class UnityAdsUtils {
    private final String mTag = "UnityAdsUtils";
    private static UnityAdsUtils unityAdsUtils = null;

    private UnityAdsUtils(Context context) {

        // Initialize the SDK:
//        UnityAds.initialize(context, context.getResources().getString(R.string.app_id_unity),true, new UnityAdsListener());
    }

    public static UnityAdsUtils getUnityObj(Context context) {
        if (unityAdsUtils == null) {
            synchronized (UnityAdsUtils.class) {
                if (unityAdsUtils == null) {
                    unityAdsUtils = new UnityAdsUtils(context);
                }
            }
        }
        return unityAdsUtils;
    }

    public void getUnityAdsBanner(final Activity ctx, String placeId, AppAdsListener listener) {
//        if (placeId != null && !placeId.equals("")) {
//            placeId = placeId.trim();
//
//            Log.d(mTag, "getUnityAdsBanner: " + placeId);
//            // Create the top banner view object:
//            BannerView topBanner = new BannerView(ctx, placeId, new UnityBannerSize(320, 50));
//            // Set the listener for banner lifcycle events:
//            topBanner.setListener(new UnityBannerListener(listener));
//            // Request a banner ad:
//            topBanner.load();
//
//        } else {
//            listener.onAdFailed(AdsEnum.ADS_UNITY, "Banner Id null");
//        }

        listener.onAdFailed(AdsEnum.ADS_UNITY, "Ads not Used");

    }


    // Implement listener methods:
/*
    private static class UnityBannerListener implements BannerView.IListener {
        private final AppAdsListener listener;

        UnityBannerListener(AppAdsListener appAdsListener) {
            listener = appAdsListener;
        }

        @Override
        public void onBannerLoaded(BannerView bannerAdView) {
            // Called when the banner is loaded.
            listener.onAdLoaded(bannerAdView);
        }

        @Override
        public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
            // Note that the BannerErrorInfo object can indicate a no fill (see API documentation).
            listener.onAdFailed(AdsEnum.ADS_UNITY, errorInfo.errorMessage);
        }

        @Override
        public void onBannerClick(BannerView bannerAdView) {
            // Called when a banner is clicked.
        }

        @Override
        public void onBannerLeftApplication(BannerView bannerAdView) {
            // Called when the banner links out of the application.
        }
    }
*/

    public void loadUnityFullAds(final Activity ctx, String placeId, final AppFullAdsListener listener, final boolean isFromCache) {
      /*  if (placeId != null && !placeId.equals("")) {
            placeId = placeId.trim();

            Log.d(mTag, "loadUnityFullAds: " + placeId + " " + ctx);

            final String finalPlaceId = placeId;
            UnityAds.load(placeId, new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    if (finalPlaceId.equals(placementId)) {
                        if (isFromCache) {
                            listener.onFullAdLoaded();
                        }
                    }
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
                    if (finalPlaceId.equals(placementId)) {
                        if (isFromCache) {
                            listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, error.name());
                        }
                    }

                }
            });
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, " Id null");
        }*/

        listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, "Ads not Used");
    }

    public void showUnityFullAds(Activity ctx, String placeId, final AppFullAdsListener listener) {
       /* if (placeId != null && !placeId.equals("")) {
            placeId = placeId.trim();


            //  if (UnityAds.isReady(placeId)) {
            //String finalPlaceId = placeId;
            UnityAds.show(ctx, placeId, new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, unityAdsShowError.name());
                    //loadUnityFullAds(ctx, finalPlaceId, listener, false);
                }

                @Override
                public void onUnityAdsShowStart(String s) {

                }

                @Override
                public void onUnityAdsShowClick(String s) {

                }

                @Override
                public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                }
            });
//            } else {
//                //loadUnityFullAds(ctx, placeId, listener, false);
//                listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, "Ads is not ready");
//            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, " Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, "Ads Not used");
    }

    // Implement the IUnityAdsListener interface methods:
 /*   public static class UnityAdsListener implements IUnityAdsInitializationListener {

        @Override
        public void onInitializationComplete() {

        }

        @Override
        public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
            Log.d("UnityAdsUtils", "NewEngine Unity Ads initialization failed: [" + error + "] " + message);

        }
    }*/
}
