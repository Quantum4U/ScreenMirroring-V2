package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

//import com.ironsource.mediationsdk.ISBannerSize;
//import com.ironsource.mediationsdk.IronSource;
//import com.ironsource.mediationsdk.IronSourceBannerLayout;
//import com.ironsource.mediationsdk.logger.IronSourceError;
//import com.ironsource.mediationsdk.sdk.BannerListener;
//import com.ironsource.mediationsdk.sdk.InterstitialListener;

import app.pnd.adshandler.R;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;


/**
 * Created by Meenu Singh on 22/09/2021.
 */
public class IronSourceUtils {
    private final String mTag = "IronSourceUtils";
    private static IronSourceUtils ironSourceUtils;
//    private IronSourceBannerLayout mIronSourceBannerLayout = null;

    private IronSourceUtils(Activity activity) {
        //IntegrationHelper.validateIntegration(activity);

//        String advertisingId = IronSource.getAdvertiserId(activity);
   //     Log.d(mTag, "IronSourceUtils: " + advertisingId);

//        IronSource.setUserId(advertisingId);
//        IronSource.init(activity, activity.getResources().getString(R.string.app_id_iron_source));
    }


    public static IronSourceUtils getInstance(Activity context) {
        if (ironSourceUtils == null) {
            synchronized (IronSourceUtils.class) {
                if (ironSourceUtils == null) {
                    ironSourceUtils = new IronSourceUtils(context);
                }
            }
        }
        return ironSourceUtils;
    }


    public void getIronSourceAdsBanner(final Activity ctx, String placeId, AppAdsListener listener) {
//        if (placeId != null && !placeId.equals("")) {
//            placeId = placeId.trim();
//            if (mIronSourceBannerLayout != null) {
//                IronSource.destroyBanner(mIronSourceBannerLayout);
//                mIronSourceBannerLayout = null;
//                listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "Banner static view null");
//            }
//
//            mIronSourceBannerLayout = IronSource.createBanner(ctx, ISBannerSize.BANNER);
//            mIronSourceBannerLayout.setBannerListener(new IronSourceBannerListener(mIronSourceBannerLayout, listener));
//            IronSource.loadBanner(mIronSourceBannerLayout);
//
//        } else {
//            listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "Banner Id null");
//        }
        listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, " not used");
    }

    public void getIronSourceAdsBannerLarge(final Activity ctx, String placeId, AppAdsListener listener) {
//        if (placeId != null && !placeId.equals("")) {
//            placeId = placeId.trim();
//            if (mIronSourceBannerLayout != null) {
//                IronSource.destroyBanner(mIronSourceBannerLayout);
//                mIronSourceBannerLayout = null;
//                listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "Banner static view null");
//            }
//            mIronSourceBannerLayout = IronSource.createBanner(ctx, ISBannerSize.LARGE);
//            mIronSourceBannerLayout.setBannerListener(new IronSourceBannerListener(mIronSourceBannerLayout, listener));
//            IronSource.loadBanner(mIronSourceBannerLayout);
//        } else {
//            listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "BannerLarge Id null");
//        }
        listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "not used");
    }

    public void getIronSourceAdsBannerRect(final Activity ctx, String placeId, AppAdsListener listener) {
//        if (placeId != null && !placeId.equals("")) {
//            placeId = placeId.trim();
//            Log.d(mTag, "getIronSourceAdsBannerRect: " + placeId);
//            if (mIronSourceBannerLayout != null) {
//                IronSource.destroyBanner(mIronSourceBannerLayout);
//                mIronSourceBannerLayout = null;
//                listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "Banner static view null");
//            }
//            mIronSourceBannerLayout = IronSource.createBanner(ctx, ISBannerSize.RECTANGLE);
//            mIronSourceBannerLayout.setBannerListener(new IronSourceBannerListener(mIronSourceBannerLayout, listener));
//            IronSource.loadBanner(mIronSourceBannerLayout);
//        } else {
//            listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "BannerRect Id null");
//        }
       listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, "Not Used");

    }

/*
    private static class IronSourceBannerListener implements BannerListener {
        private static final String mTag = "IronSourceBanner";
        private final AppAdsListener listener;
        private final IronSourceBannerLayout ironSourceBannerLayout;

        IronSourceBannerListener(IronSourceBannerLayout banner, AppAdsListener appAdsListener) {
            this.listener = appAdsListener;
            this.ironSourceBannerLayout = banner;
        }

        @Override
        public void onBannerAdLoaded() {
            listener.onAdLoaded(ironSourceBannerLayout);
        }

        @Override
        public void onBannerAdLoadFailed(IronSourceError ironSourceError) {
            listener.onAdFailed(AdsEnum.ADS_IRON_SOURCE, ironSourceError.getErrorMessage());
        }

        @Override
        public void onBannerAdClicked() {

        }

        @Override
        public void onBannerAdScreenPresented() {
        }

        @Override
        public void onBannerAdScreenDismissed() {
        }

        @Override
        public void onBannerAdLeftApplication() {
        }
    }
*/


    public void loadIronSourceFullAds(final Activity ctx, String placeId, final AppFullAdsListener listener, final boolean isFromCache) {
      /*  if (placeId != null && !placeId.equals("")) {
            placeId = placeId.trim();

            Log.d(mTag, "IronSourceUtils.loadIronSourceFullAds: " + placeId + " " + ctx.getLocalClassName()+"  "+IronSource.isInterstitialReady());

            if (IronSource.isInterstitialReady()) {
                if (isFromCache) {
                    listener.onFullAdLoaded();
                }
            } else {
                IronSource.setInterstitialListener(new InterstitialListener() {
                    @Override
                    public void onInterstitialAdReady() {
                        System.out.println("IronSourceUtils.onInterstitialAdReady ");

                        listener.onFullAdLoaded();
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                        System.out.println("IronSourceUtils.onInterstitialAdLoadFailed "+isFromCache);

                        if (isFromCache) {
                            listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, ironSourceError.getErrorMessage());
                        }
                    }

                    @Override
                    public void onInterstitialAdOpened() {
                        System.out.println("IronSourceUtils.onInterstitialAdOpened ");

                    }

                    @Override
                    public void onInterstitialAdClosed() {
                        System.out.println("IronSourceUtils.onInterstitialAdClosed ");

                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {
                        System.out.println("IronSourceUtils.onInterstitialAdShowSucceeded ");

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                        System.out.println("IronSourceUtils.onInterstitialAdShowFailed "+ironSourceError);

                        if (isFromCache) {
                            listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, ironSourceError.getErrorMessage());
                        }
                    }

                    @Override
                    public void onInterstitialAdClicked() {

                    }
                });
                IronSource.loadInterstitial();
            }

        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, " Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, " not used");
    }

    public void showIronSourceFullAds(Activity ctx, String placeId, final AppFullAdsListener listener,boolean isFromSplash) {
      /*  if (placeId != null && !placeId.equals("")) {
            placeId = placeId.trim();

            Log.d(mTag, "IronSourceUtils.showUnityFullAds: " + placeId+"  "+IronSource.isInterstitialReady());

            if (IronSource.isInterstitialReady()) {
                String finalPlaceId = placeId;
                IronSource.setInterstitialListener(new InterstitialListener() {
                    @Override
                    public void onInterstitialAdReady() {

                        System.out.println("IronSourceUtils.onInterstitialAdReady Show ads ");
                        listener.onFullAdClosed();
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                        System.out.println("IronSourceUtils.onInterstitialAdLoadFailed Show ads ");

                        listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, ironSourceError.getErrorMessage());
                    }

                    @Override
                    public void onInterstitialAdOpened() {
                        System.out.println("IronSourceUtils.onInterstitialAdOpened Show ads ");

                    }

                    @Override
                    public void onInterstitialAdClosed() {
                        System.out.println("IronSourceUtils.onInterstitialAdClosed Show ads ");

                        if(!isFromSplash) {
                            loadIronSourceFullAds(ctx, finalPlaceId, listener, false);
                        }
                        listener.onFullAdClosed();

                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                        System.out.println("IronSourceUtils.onInterstitialAdShowFailed Show ads "+ironSourceError);

                        listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, ironSourceError.getErrorMessage());
                    }

                    @Override
                    public void onInterstitialAdClicked() {

                    }
                });
                IronSource.showInterstitial();

            } else {
                if(!isFromSplash) {
                    loadIronSourceFullAds(ctx, placeId, listener, false);
                }
                listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, "Ads is not ready");

            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_IRON_SOURCE, " Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_UNITY, " not used");
    }


}
