package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

//import com.huawei.hms.ads.AdListener;
//import com.huawei.hms.ads.AdParam;
//import com.huawei.hms.ads.BannerAdSize;
//import com.huawei.hms.ads.HwAds;
//import com.huawei.hms.ads.InterstitialAd;
//import com.huawei.hms.ads.VideoConfiguration;
//import com.huawei.hms.ads.banner.BannerView;
//import com.huawei.hms.ads.nativead.DislikeAdListener;
//import com.huawei.hms.ads.nativead.NativeAd;
//import com.huawei.hms.ads.nativead.NativeAdConfiguration;
//import com.huawei.hms.ads.nativead.NativeAdLoader;

import java.util.ArrayList;
import java.util.List;

import app.pnd.adshandler.BuildConfig;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;
import engine.app.utils.NativeViewFactory;

public class HmsAdsProvider  {
    private static HmsAdsProvider instance;
 //   private BannerView bannerView;
    private ScrollView adScrollView;
    //private NativeAd globalNativeAd;

   // private InterstitialAd mInterstitial;


    private HmsAdsProvider(Context context){
        // Initialize the HUAWEI Ads SDK.
       // HwAds.init(context);
    }

    public static HmsAdsProvider getInstance(Context context){
        synchronized (HmsAdsProvider.class){
            if(instance==null){
                instance=new HmsAdsProvider(context);
            }
        }
        return instance;
    }

    //working
    public void hcm_GetBannerAds(Context context, String id, final AppAdsListener listener) {
      /*  if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "testw6vs28auh3".trim();
            }
            id = id.trim();
            System.out.println("hcm_GetBannerAds " + id);

            // Call new BannerView(Context context) to create a BannerView class.
            bannerView = new BannerView(context);

            // Set an ad slot ID.
            bannerView.setAdId(id);

            // Set the background color and size based on user selection.
            bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);

            try {
                bannerView.setAdListener(new HmsAdsListener(bannerView, listener));
                bannerView.loadAd(new AdParam.Builder().build());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_HCM, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_HCM, "Banner Id null");
        }*/

        listener.onAdFailed(AdsEnum.ADS_HCM, "Ads not Used");
    }

    public void hcm_GetBannerRectangleAds(Context context, String id, final AppAdsListener listener) {
      /*  if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "testw6vs28auh3".trim();
            }
            id = id.trim();
            System.out.println("hcm_GetBannerAds ractangle " + id);

            // Call new BannerView(Context context) to create a BannerView class.
            bannerView = new BannerView(context);
            // Set an ad slot ID.
            bannerView.setAdId(id);

            // Set the background color and size based on user selection.
            bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_300_250);

            try {
                bannerView.setAdListener(new HmsAdsListener(bannerView, listener));
                bannerView.loadAd(new AdParam.Builder().build());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_HCM, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_HCM, "Banner Id null");
        }*/

        listener.onAdFailed(AdsEnum.ADS_HCM, "Ads not Used");

    }

    public void hcm_GetBannerLargeAds(Context context, String id, final AppAdsListener listener) {
      /*  if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "testw6vs28auh3".trim();
            }
            id = id.trim();
            System.out.println("hcm_GetBannerAds ractangle " + id);


            // Call new BannerView(Context context) to create a BannerView class.
            bannerView = new BannerView(context);

            // Set an ad slot ID.
            bannerView.setAdId(id);

            // Set the background color and size based on user selection.
            bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_100);

//            int color = getBannerViewBackground(colorRadioGroup.getCheckedRadioButtonId());
//            bannerView.setBackgroundColor(color);

//            adFrameLayout.addView(bannerView);
            try {
                bannerView.setAdListener(new HmsAdsListener(bannerView, listener));
                bannerView.loadAd(new AdParam.Builder().build());
            } catch (Exception e) {
                listener.onAdFailed(AdsEnum.ADS_HCM, e.getMessage());
                e.printStackTrace();
            }

        } else {
            listener.onAdFailed(AdsEnum.ADS_HCM, "Banner Id null");
        }*/

        listener.onAdFailed(AdsEnum.ADS_HCM, "Ads not Used");

    }

    /**
     * hcm_InitFullAds function call only splash for first time caching ads and after ads onAdClosed
     */
    public void hcm_InitFullAds(final Context context, String id, final AppFullAdsListener listener, final boolean isFromCache) {
       /* if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                //for image teste9ih9j0rc3
                //for video testb4znbuh3n2
                id = "teste9ih9j0rc3".trim();
            }

            id = id.trim();

            mInterstitial = new InterstitialAd(context);
            mInterstitial.setAdId(id);
            String finalId1 = id;
            mInterstitial.setAdListener(new AdListener(){
                @Override
                public void onAdLoaded() {
                    // Display an interstitial ad.
                    if (isFromCache) {
                        listener.onFullAdLoaded();
                    }
                }

                @Override
                public void onAdFailed(int errorCode) {
                    if (isFromCache) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, String.valueOf(errorCode));
                    }
                }

                @Override
                public void onAdClosed() {
                    listener.onFullAdClosed();
                    hcm_InitFullAds(context, finalId1, listener, false);
                }

                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdOpened() {
                }
            });

            AdParam adParam = new AdParam.Builder().build();
            try {
                mInterstitial.loadAd(adParam);
            } catch (Exception e) {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, e.getMessage());
            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Init FullAds Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Ads not Used");
    }

    public void hcm_showFullAds(final Context context, String id, final AppFullAdsListener listener) {
/*
        if (context != null && id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "teste9ih9j0rc3".trim();
            }
            id = id.trim();

            if (mInterstitial != null) {

                final String finalId = id;
                mInterstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        Log.d("HmsAdsProvider", " NewEngine loadFullAdsOnLaunch hcm_showFullAds closed ");
                        super.onAdClosed();
                        listener.onFullAdClosed();
                        hcm_InitFullAds(context, finalId, listener, false);
                    }
                });


                if (mInterstitial.isLoaded()) {
                    try {
                        mInterstitial.show((Activity) context);
                        listener.onFullAdLoaded();
                    } catch (Exception e) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, e.getMessage());
                    }

                } else {
                    hcm_InitFullAds(context, id, listener, false);
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, String.valueOf(mInterstitial.isLoaded()));
                }
            } else {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "HCM Interstitial null");
            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "FullAds Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Ads not Used");

    }

    /**
     * admob_InitFullAds function call only splash for first time caching ads and after ads onAdClosed
     */
    public void hcm_InitFullAds_Exit(final Context context, String id, final AppFullAdsListener listener, final boolean isFromCache) {
        /*if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "teste9ih9j0rc3".trim();
            }

            id = id.trim();
            mInterstitial = new InterstitialAd(context);
            mInterstitial.setAdId(id);
            final String finalId = id;
            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.d("HmsAdsProvider", "NewEngine hcm_InitFullAds_Exit" + isFromCache);
                    if (isFromCache) {
                        listener.onFullAdLoaded();
                    }
                }

                @Override
                public void onAdFailed(int i) {
                    if (isFromCache) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, String.valueOf(i));
                    }
                }


                @Override
                public void onAdOpened() {

                }

                @Override
                public void onAdLeave() {
                }


                @Override
                public void onAdClosed() {
                    listener.onFullAdClosed();
                    hcm_InitFullAds_Exit(context, finalId, listener, false);
                }

            });


            AdParam adParam = new AdParam.Builder().build();
            try {
                mInterstitial.loadAd(adParam);
            } catch (Exception e) {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, e.getMessage());
            }

        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Init FullAds Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Ads not Used");

    }

    public void hcm_showFullAds_Exit(final Context context, String id, final AppFullAdsListener listener) {
       /* if (context != null && id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "teste9ih9j0rc3".trim();
            }
            id = id.trim();
            if (mInterstitial != null) {
                final String finalId = id;
                mInterstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        listener.onFullAdClosed();
                        hcm_InitFullAds_Exit(context, finalId, listener, false);
                    }
                });

                if (mInterstitial.isLoaded()) {
                    try {
                        mInterstitial.show((Activity) context);
                        listener.onFullAdLoaded();
                    } catch (Exception e) {
                        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, e.getMessage());
                    }

                } else {
                    hcm_InitFullAds_Exit(context, id, listener, false);
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, String.valueOf(mInterstitial.isLoaded()));
                }
            } else {
                listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "hcm Interstitial null");
            }
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "FullAds Id null");
        }*/
        listener.onFullAdFailed(AdsEnum.FULL_ADS_HCM, "Ads not Used");

    }

    public void showHcmNativeAds(final Activity context, String id,
                                 final boolean isNativeLarge, final AppAdsListener listener) {
      /*  if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                if(isNativeLarge) {
                    id = "testu7m3hc4gvm".trim();
                }else {
                    id = "testr6w14o0hqz".trim();
                }
            }
            id = id.trim();
            NativeAdLoader.Builder builder = new NativeAdLoader.Builder(context, id);
            builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    // Call this method when an ad is successfully loaded.

                    // Display native ad.
                    showNativeAd(context,nativeAd,listener);
                }
            }).setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdFailed(int errorCode) {
                    // Call this method when an ad fails to be loaded.
                }
            });

            VideoConfiguration videoConfiguration = new VideoConfiguration.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                    .setVideoConfiguration(videoConfiguration)
                    .setRequestMultiImages(true)
                    .build();

            NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
            nativeAdLoader.loadAd(new AdParam.Builder().build());

        } else {

            listener.onAdFailed(AdsEnum.ADS_HCM,"Native ads Id null ");
        }*/

        listener.onAdFailed(AdsEnum.ADS_HCM,"Ads not used ");

    }

    public void showHcmNativeRectangleAds(final Activity context, String id, final AppAdsListener listener) {
      /*  if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = "testy63txaom86".trim();
            }
            id = id.trim();
            NativeAdLoader.Builder builder = new NativeAdLoader.Builder(context, id);
            builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    // Call this method when an ad is successfully loaded.

                    // Display native ad.
                    showNativeAd(context,nativeAd,listener);
                }
            }).setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdFailed(int errorCode) {
                    // Call this method when an ad fails to be loaded.
                }
            });

            VideoConfiguration videoConfiguration = new VideoConfiguration.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT) // Set custom attributes.
                    .setVideoConfiguration(videoConfiguration)
                    .setRequestMultiImages(true)
                    .build();

            NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();
            nativeAdLoader.loadAd(new AdParam.Builder().build());

        } else {
            listener.onAdFailed(AdsEnum.ADS_HCM,"Native ads Id null ");
        }*/
        listener.onAdFailed(AdsEnum.ADS_HCM,"Ads not used ");

    }

    /**
     * Display native ad.
     *
     * @param nativeAd native ad object that contains ad materials.
     */
/*
    private void showNativeAd(Context context,NativeAd nativeAd,AppAdsListener listener) {
        // Destroy the original native ad.
        final LinearLayout linearLayout = new LinearLayout(context);
        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }
        globalNativeAd = nativeAd;

        final View nativeView = createNativeView(nativeAd, linearLayout);
        if (nativeView != null) {
            globalNativeAd.setDislikeAdListener(new DislikeAdListener() {
                @Override
                public void onAdDisliked() {
                    // Call this method when an ad is closed.
                    linearLayout.removeView(nativeView);
                }
            });

            // Add NativeView to the app UI.
            linearLayout.removeAllViews();
            linearLayout.addView(nativeView);
            if (listener != null) {
                listener.onAdLoaded(linearLayout);
            }
        }
    }
*/

    /**
     * Create a nativeView by creativeType and fill in ad material.
     *
     * @param nativeAd   native ad object that contains ad materials.
     * @param parentView parent view of nativeView.
     */
  /*  private View createNativeView(NativeAd nativeAd, ViewGroup parentView) {
        int createType = nativeAd.getCreativeType();
        Log.i("HmsAdsProvider", "Native ad createType is " + createType);
        if (createType == 2 || createType == 102) {
            // Large image
            return NativeViewFactory.createImageOnlyAdView(nativeAd, parentView);
        } else if (createType == 3 || createType == 6) {
            // Large image with text or video with text
            return NativeViewFactory.createMediumAdView(nativeAd, parentView);
        } else if (createType == 103 || createType == 106) {
            // Large image with text or Video with text, using AppDownloadButton template.
            return NativeViewFactory.createAppDownloadButtonAdView(nativeAd, parentView);
        } else if (createType == 7 || createType == 107) {
            // Small image with text-
            return NativeViewFactory.createSmallImageAdView(nativeAd, parentView);
        } else if (createType == 8 || createType == 108) {
            // Three small images with text
            return NativeViewFactory.createThreeImagesAdView(nativeAd, parentView);
        } else {
            // Undefined creative type
            return null;
        }
    }*/
}
