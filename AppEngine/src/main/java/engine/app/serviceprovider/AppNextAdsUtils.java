package engine.app.serviceprovider;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appnext.ads.interstitial.Interstitial;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerListener;
import com.appnext.banners.BannerSize;
import com.appnext.banners.BannerView;
import com.appnext.base.Appnext;
import com.appnext.core.AppnextAdCreativeType;
import com.appnext.core.AppnextError;
import com.appnext.core.callbacks.OnAdClicked;
import com.appnext.core.callbacks.OnAdClosed;
import com.appnext.core.callbacks.OnAdError;
import com.appnext.core.callbacks.OnAdLoaded;
import com.appnext.nativeads.MediaView;
import com.appnext.nativeads.NativeAd;
import com.appnext.nativeads.NativeAdListener;
import com.appnext.nativeads.NativeAdRequest;
import com.appnext.nativeads.NativeAdView;
import com.appnext.nativeads.PrivacyIcon;
import com.appnext.nativeads.designed_native_ads.AppnextDesignedNativeAdData;
import com.appnext.nativeads.designed_native_ads.interfaces.AppnextDesignedNativeAdViewCallbacks;
import com.appnext.nativeads.designed_native_ads.views.AppnextDesignedNativeAdView;

import app.pnd.adshandler.R;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;

/**
 * Created by Meenu Singh on 27/07/20
 */
public class AppNextAdsUtils {

    private static AppNextAdsUtils appNextAdsUtils;

    private Interstitial mInterstitial;
    private NativeAd nativeAd;


    private AppNextAdsUtils(Context context) {
        Appnext.init(context);
    }

    public static AppNextAdsUtils getAppNextObj(Context context) {
        if (appNextAdsUtils == null) {
            synchronized (AppNextAdsUtils.class) {
                if (appNextAdsUtils == null) {
                    appNextAdsUtils = new AppNextAdsUtils(context);
                }
            }
        }
        return appNextAdsUtils;
    }

    public void getAppNextBannerAds(Context context, String id, final AppAdsListener listener) {
       if (id != null && !id.equals("")) {
            id = id.trim();
            System.out.println("AppNextAdsUtils.getAppNextAdsBanner " + id);
            BannerView bannerView = new BannerView(context);
            bannerView.setPlacementId(id);

            bannerView.setBannerSize(BannerSize.BANNER);

            try {
                bannerView.setBannerListener(new AppNextAdsAdsListener(bannerView, listener));
                // All sizes example
                BannerAdRequest banner_request = new BannerAdRequest();
                banner_request
                        .setCategories("category1, category2")
                        .setPostback("Postback string");

                bannerView.loadAd(banner_request);
                //bannerView.loadAd(new BannerAdRequest());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, e.getMessage());
                e.printStackTrace();
            }
        } else {
            listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Banner Id null");
        }/*  if (id != null && !id.equals("")) {
            id = id.trim();
            System.out.println("AppNextAdsUtils.getAppNextBannerLargeAds " + id);
            BannerView bannerView = new BannerView(context);
            bannerView.setPlacementId(id);

            bannerView.setBannerSize(BannerSize.LARGE_BANNER);

            try {
                bannerView.setBannerListener(new AppNextAdsAdsListener(bannerView, listener));
                // All sizes example
                BannerAdRequest banner_request = new BannerAdRequest();
                banner_request
                        .setCategories("category1, category2")
                        .setPostback("Postback string");

                bannerView.loadAd(banner_request);
                //bannerView.loadAd(new BannerAdRequest());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, e.getMessage());
                e.printStackTrace();
            }
        } else {
            listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Banner Id null");
        }
*/
        //listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");

    }

    public void getAppNextBannerLargeAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            id = id.trim();
            System.out.println("AppNextAdsUtils.getAppNextBannerLargeAds " + id);
            BannerView bannerView = new BannerView(context);
            bannerView.setPlacementId(id);

            bannerView.setBannerSize(BannerSize.LARGE_BANNER);

            try {
                bannerView.setBannerListener(new AppNextAdsAdsListener(bannerView, listener));
                // All sizes example
                BannerAdRequest banner_request = new BannerAdRequest();
                banner_request
                        .setCategories("category1, category2")
                        .setPostback("Postback string");

                bannerView.loadAd(banner_request);
                //bannerView.loadAd(new BannerAdRequest());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, e.getMessage());
                e.printStackTrace();
            }
        } else {
            listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Banner Id null");
        }

        //listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");

    }


    public void getAppNextBannerRectangleAds(Context context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            id = id.trim();
            System.out.println("AppNextAdsUtils.getAppNextBannerRectangleAds " + id);
            BannerView bannerView = new BannerView(context);
            bannerView.setPlacementId(id);

            bannerView.setBannerSize(BannerSize.MEDIUM_RECTANGLE);

            try {
                bannerView.setBannerListener(new AppNextAdsAdsListener(bannerView, listener));
                // All sizes example
                BannerAdRequest banner_request = new BannerAdRequest();
                banner_request
                        .setCategories("category1, category2")
                        .setPostback("Postback string");

                bannerView.loadAd(banner_request);
                //bannerView.loadAd(new BannerAdRequest());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, e.getMessage());
                e.printStackTrace();
            }
        } else {
            listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Banner Id null");
        }

        //listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");

    }

    /**
     * Listeners for banners...
     */

    private static class AppNextAdsAdsListener extends BannerListener {

        private final BannerView bannerView;
        private final AppAdsListener mAppAdListener;

        AppNextAdsAdsListener(BannerView view, AppAdsListener mOnAppAdListener) throws Exception {
            this.bannerView = view;
            this.mAppAdListener = mOnAppAdListener;

            if (view == null || mOnAppAdListener == null) {
                throw new Exception("AdView and AppAdsListener cannot be null ");
            }
        }

        @Override
        public void onError(AppnextError error) {
            super.onError(error);
            System.out.println("AppNextAdsAdsListener.onError " + error.getErrorMessage());
            mAppAdListener.onAdFailed(AdsEnum.ADS_APPNEXT, error.getErrorMessage());
        }

        @Override
        public void onAdLoaded(String s, AppnextAdCreativeType creativeType) {
            super.onAdLoaded(s, creativeType);
            mAppAdListener.onAdLoaded(bannerView);
        }

        @Override
        public void adImpression() {
            super.adImpression();
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
        }


    }

    private void setNativeLargeViews(Context context, NativeAd nativeAd, NativeAdView adView) {
        NativeAdView nativeAdView = adView.findViewById(R.id.na_view);
        ImageView imageView = adView.findViewById(R.id.na_icon);
        TextView textView = adView.findViewById(R.id.na_title);
        MediaView mediaView = adView.findViewById(R.id.na_media);
        //Button button = adView.findViewById(R.id.install);
        TextView rating = adView.findViewById(R.id.rating);
        TextView description = adView.findViewById(R.id.description);
        mediaView.setMute(true);
        //The ad Icon
        nativeAd.downloadAndDisplayImage(context,imageView, nativeAd.getIconURL());

        //The ad title
        textView.setText(nativeAd.getAdTitle());

        //Setting up the Appnext MediaView
        nativeAd.setMediaView(mediaView);

        //The ad rating
        rating.setText(nativeAd.getStoreRating());

        //The ad description
        description.setText(nativeAd.getAdDescription());

        //Registering the clickable areas - see the array object in `setViews()` function
        nativeAd.registerClickableViews(nativeAdView);

        //Setting up the entire native ad view
        nativeAd.setNativeAdView(nativeAdView);

    }

    private void setNativeMediumViews(Context context,NativeAd nativeAd, NativeAdView adView) {
        NativeAdView nativeAdView = adView.findViewById(R.id.na_view);
        ImageView imageView = adView.findViewById(R.id.na_icon);
        TextView textView = adView.findViewById(R.id.na_title);
        MediaView mediaView = adView.findViewById(R.id.na_media);
        //Button button = adView.findViewById(R.id.install);
        TextView rating = adView.findViewById(R.id.rating);
        TextView description = adView.findViewById(R.id.description);

        mediaView.setMute(true);
        //The ad Icon
        nativeAd.downloadAndDisplayImage(context,imageView, nativeAd.getIconURL());

        //The ad title
        textView.setText(nativeAd.getAdTitle());

        //Setting up the Appnext MediaView
        nativeAd.setMediaView(mediaView);

        //The ad rating
        rating.setText(nativeAd.getStoreRating());

        //The ad description
        description.setText(nativeAd.getAdDescription());

        //Registering the clickable areas - see the array object in `setViews()` function
        nativeAd.registerClickableViews(nativeAdView);

        //Setting up the entire native ad view
        nativeAd.setNativeAdView(nativeAdView);


    }


    public void showNativeMediumAds(final Activity context, String id, final AppAdsListener listener) {
        if (nativeAd == null) {
            getNativeAdvancedAdsMedium(context, id, listener);
        } else {
            System.out.println("AppNextAdsUtils.showNativeMediumAds " + nativeAd.getAdTitle());
            if (nativeAd.getAdTitle() != null) {
                final LinearLayout linearLayout = new LinearLayout(context);
                NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_appnext_native_medium,
                        linearLayout, false);
                setNativeMediumViews(context,nativeAd, adView);
                linearLayout.addView(adView);
                listener.onAdLoaded(linearLayout);
            } else {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Tittle null");
            }

            getNativeAdvancedAdsMedium(context, id, null);
        }

        //listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");

    }

    public void showNativeLargeAds(final Activity context, String id, final AppAdsListener listener) {
        if (nativeAd == null) {
            getNativeAdvancedAdsLarge(context, id, listener);
        } else {
            System.out.println("AppNextAdsUtils.showNativeLargeAds " + nativeAd.getAdTitle());
            if (nativeAd.getAdTitle() != null) {
                final LinearLayout linearLayout = new LinearLayout(context);
                NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_appnext_native_large,
                        linearLayout, false);
                setNativeLargeViews(context,nativeAd, adView);
                linearLayout.addView(adView);
                if (listener != null) {
                    listener.onAdLoaded(linearLayout);
                }
            } else {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Tittle null");
            }

            getNativeAdvancedAdsLarge(context, id, null);
        }

//        listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");
    }

    private void getNativeAdvancedAdsLarge(final Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            id = id.trim();

            nativeAd = new NativeAd(context, id);
            nativeAd.setPrivacyPolicyColor(PrivacyIcon.PP_ICON_COLOR_LIGHT);
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onAdLoaded(NativeAd nativeAd, AppnextAdCreativeType appnextAdCreativeType) {
                    super.onAdLoaded(nativeAd, appnextAdCreativeType);
                    if (nativeAd != null && nativeAd.getAdTitle() != null) {
                        final LinearLayout linearLayout = new LinearLayout(context);
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_appnext_native_large,
                                linearLayout, false);
                        setNativeLargeViews(context,nativeAd, adView);
                        linearLayout.addView(adView);
                        if (listener != null) {
                            listener.onAdLoaded(linearLayout);
                        }
                    } else {
                        listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Tittle null");
                    }

                }

                //Ad clicked callback
                @Override
                public void onAdClicked(NativeAd nativeAd) {
                    super.onAdClicked(nativeAd);
                }

                //Ad error callback
                @Override
                public void onError(NativeAd nativeAd, AppnextError appnextError) {
                    super.onError(nativeAd, appnextError);
                    if (listener != null)
                        listener.onAdFailed(AdsEnum.ADS_APPNEXT, appnextError.getErrorMessage());

                }

                //Ad impression callback
                @Override
                public void adImpression(NativeAd nativeAd) {
                    super.adImpression(nativeAd);
                }
            });


            nativeAd.loadAd(new NativeAdRequest()
                    // optional - config your ad request:
                    .setCachingPolicy(NativeAdRequest.CachingPolicy.STATIC_ONLY)
                    .setCreativeType(NativeAdRequest.CreativeType.ALL)
                    .setVideoLength(NativeAdRequest.VideoLength.SHORT)
                    .setVideoQuality(NativeAdRequest.VideoQuality.LOW)
            );
        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, "NativeAdvancedAds Id null");
            }
        }


    //    listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");
    }

    private void getNativeAdvancedAdsMedium(final Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            id = id.trim();

            nativeAd = new NativeAd(context, id);
            nativeAd.setPrivacyPolicyColor(PrivacyIcon.PP_ICON_COLOR_LIGHT);
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onAdLoaded(NativeAd nativeAd, AppnextAdCreativeType appnextAdCreativeType) {
                    super.onAdLoaded(nativeAd, appnextAdCreativeType);
                    if (nativeAd != null && nativeAd.getAdTitle() != null) {
                        final LinearLayout linearLayout = new LinearLayout(context);
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_appnext_native_medium,
                                linearLayout, false);
                        setNativeMediumViews(context,nativeAd, adView);
                        linearLayout.addView(adView);
                        if (listener != null) {
                            listener.onAdLoaded(linearLayout);
                        }
                    } else {
                        listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Tittle null");
                    }
                }

                //Ad clicked callback
                @Override
                public void onAdClicked(NativeAd nativeAd) {
                    super.onAdClicked(nativeAd);
                }

                //Ad error callback
                @Override
                public void onError(NativeAd nativeAd, AppnextError appnextError) {
                    super.onError(nativeAd, appnextError);
                    if (listener != null)
                        listener.onAdFailed(AdsEnum.ADS_APPNEXT, appnextError.getErrorMessage());
                }
                //Ad impression callback
                @Override
                public void adImpression(NativeAd nativeAd) {
                    super.adImpression(nativeAd);
                }
            });

            nativeAd.loadAd(new NativeAdRequest()
                    // optional - config your ad request:
                    .setCachingPolicy(NativeAdRequest.CachingPolicy.STATIC_ONLY)
                    .setCreativeType(NativeAdRequest.CreativeType.ALL)
                    .setVideoLength(NativeAdRequest.VideoLength.SHORT)
                    .setVideoQuality(NativeAdRequest.VideoQuality.LOW)
            );
        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, "NativeAdvancedAds Id null");
            }
        }


     //   listener.onAdFailed(AdsEnum.ADS_APPNEXT, "Not Used");
    }

    public void initAppNextFullAds(final Context context, String id, final AppFullAdsListener listener, final boolean isFromCache,
                                   boolean isFromSplash) {
       if (id != null && !id.equals("")) {
            id = id.trim();

            mInterstitial = new Interstitial(context, id);
            mInterstitial.setCategories("category1,category2");
            mInterstitial.setPostback("postback");
            mInterstitial.setMute(true);
            mInterstitial.setAutoPlay(true);

            mInterstitial.setCreativeType(Interstitial.TYPE_MANAGED);
            mInterstitial.setOnAdClickedCallback(getOnAdClicked());
            mInterstitial.setOnAdClosedCallback(getOnAdClosed(context, id, listener,isFromSplash));
            mInterstitial.setOnAdErrorCallback(getOnAdError(isFromCache, listener));
            mInterstitial.setOnAdLoadedCallback(getOnAdLoaded(listener));

            try {
                mInterstitial.loadAd();
            } catch (Exception e) {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, e.getMessage());
            }

        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, "Init FullAds Id null");
        }

//        listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, "Not Used");
    }

    public void showAppNextFullAds(final Context context, String id, final AppFullAdsListener listener,boolean isFromSplash) {
        if (context != null && id != null && !id.equals("")) {
            id = id.trim();
            if (mInterstitial != null) {
                System.out.println("AppNextAdsUtils.showAppNextFullAds " + mInterstitial.isAdLoaded());
                mInterstitial.setOnAdClosedCallback(getOnAdClosed(context, id, listener,isFromSplash));
                if (mInterstitial.isAdLoaded()) {
                    try {
                        mInterstitial.showAd();
                        listener.onFullAdLoaded();
                    } catch (Exception e) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, e.getMessage());
                    }
                } else {
                    if(!isFromSplash) {
                        initAppNextFullAds(context, id, listener, false,isFromSplash);
                    }
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, String.valueOf(mInterstitial.isAdLoaded()));

                }
            } else {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, "AppNextF Interstitial null");
            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, "FullAds Id null");
        }

        //listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, "Not Used");
    }

    private OnAdClicked getOnAdClicked() {
        return new OnAdClicked() {
            @Override
            public void adClicked() {
                System.out.println("AppNextAdsUtils.getOnAdClicked " );

            }
        };
    }

    private OnAdClosed getOnAdClosed(final Context context, final String id, final AppFullAdsListener listener,boolean isFromSplash) {
        return new OnAdClosed() {
            @Override
            public void onAdClosed() {
                if(!isFromSplash) {
                    initAppNextFullAds(context, id, listener, false,isFromSplash);
                }

                System.out.println("AppNextAdsUtils.getOnAdClosed " + listener);
                listener.onFullAdClosed();
            }
        };
    }

    private OnAdLoaded getOnAdLoaded(final AppFullAdsListener listener) {
        return new OnAdLoaded() {
            @Override
            public void adLoaded(String s, AppnextAdCreativeType appnextAdCreativeType) {
                System.out.println("AppNextAdsUtils.getOnAdLoaded "+listener );
                listener.onFullAdLoaded();
            }
        };

    }

    private OnAdError getOnAdError(final boolean isFromCache, final AppFullAdsListener listener) {
        return new OnAdError() {
            @Override
            public void adError(String error) {
                System.out.println("AppNextAdsUtils.getOnAdError "+error );
                if (isFromCache) {
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_APPNEXT, error);
                }

            }
        };
    }


    public void showSuggestedAds(AppnextDesignedNativeAdView appnextDesignedNativeAdView, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            id = id.trim();

            appnextDesignedNativeAdView.load(id, new AppnextDesignedNativeAdViewCallbacks() {
                @Override
                public void onAppnextAdsLoadedSuccessfully() {
                    System.out.println("AppNextAdsUtils.onAppnextAdsLoadedSuccessfully");
                    listener.onAdLoaded(null);
                }

                @Override
                public void onAdClicked(AppnextDesignedNativeAdData appnextDesignedNativeAdData) {
                }

                @Override
                public void onAppnextAdsError(AppnextError error) {
                    System.out.println("AppNextAdsUtils.onAppnextAdsError " + error);
                    listener.onAdFailed(AdsEnum.ADS_APPNEXT, error.getErrorMessage());
                }
            });

        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_APPNEXT, "getSuggestedApps Id null");
            }
        }
    }

}