package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.List;
import java.util.Map;

import app.pnd.adshandler.BuildConfig;
import app.pnd.adshandler.R;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.utils.EngineConstant;


/**
 * Created by Meenu Singh on 11/06/19.
 */

public class AdmobMediationNativeAdvanced {
    private final String mTag = "AdmobMediationNative";
    private NativeAd mainNativeAd;
    private static AdmobMediationNativeAdvanced instance;
    private final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";

    private AdmobMediationNativeAdvanced(Context context) {

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d(mTag, String.format(
                            "\"NewEngine getNewNative Mediation Banner adapter Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }
            }
        });

    }

    public static AdmobMediationNativeAdvanced getInstance(Context context) {
        if (instance == null) {
            synchronized (AdmobMediationNativeAdvanced.class) {
                if (instance == null) {
                    instance = new AdmobMediationNativeAdvanced(context);
                }
            }
        }
        return instance;
    }

    private void populateUnifiedNativeAdForLarge(NativeAd nativeAd,
                                                 NativeAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));
        adView.setMediaView(adView.findViewById(R.id.appinstall_media));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        if (nativeAd.getIcon() != null) {
            adView.getIconView().setVisibility(View.VISIBLE);
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
        } else {
            adView.getIconView().setVisibility(View.GONE);
        }

//        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
//        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);


        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
//        if (vc.hasVideoContent()) {
//            adView.setMediaView(mediaView);
//            mediaView.setVisibility(View.VISIBLE);
//            mainImageView.setVisibility(View.GONE);
//
//        } else {
//            adView.setImageView(mainImageView);
//            mediaView.setVisibility(View.GONE);
//            mainImageView.setVisibility(View.VISIBLE);
//
//            // At least one image is guaranteed.
//            List<NativeAd.Image> images = nativeAd.getImages();
//            if (images != null && images.size() > 0) {
//                mainImageView.setImageDrawable(images.get(0).getDrawable());
//            }
//        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getPrice() != null) {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        } else {
            adView.getPriceView().setVisibility(View.GONE);
        }

        if (nativeAd.getStore() != null) {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        } else {
            adView.getStoreView().setVisibility(View.GONE);
        }

        if (nativeAd.getStarRating() != null) {
            adView.getStarRatingView().setVisibility(View.VISIBLE);
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
        } else {
            adView.getStarRatingView().setVisibility(View.GONE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    /**
     * @param nativeContentAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdForMedium(NativeAd nativeContentAd,
                                                  NativeAdView adView) {

        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setMediaView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());
        ((MediaView) adView.getMediaView()).setMediaContent(nativeContentAd.getMediaContent());
        //List<NativeAd.Image> images = nativeContentAd.getImages();

        //if (images.size() > 0) {
        //    ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        //}

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getIcon();

        if (logoImage == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(logoImage.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    private void getNativeAdvancedAds(final Activity context, String id,
                                      final boolean isNativeLarge, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }

            id = id.trim();
            AdLoader.Builder builder = new AdLoader.Builder(context, id);
            if (isNativeLarge) {
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        mainNativeAd = nativeAd;
                        final LinearLayout linearLayout = new LinearLayout(context);
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_large, linearLayout, false);
                        populateUnifiedNativeAdForLarge(nativeAd, adView);
                        linearLayout.addView(adView);
                        if (listener != null) {
                            listener.onAdLoaded(linearLayout);
                        }

                    }
                });
            } else {
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        mainNativeAd = nativeAd;
                        final LinearLayout linearLayout = new LinearLayout(context);
                        NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_medium, linearLayout, false);
                        populateUnifiedNativeAdForMedium(nativeAd, adView);
                        linearLayout.addView(adView);
                        if (listener != null) {
                            listener.onAdLoaded(linearLayout);
                        }
                    }
                });
            }

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    System.out.println("NewEngine getNewNativeMedium Mediation getNativeAdvancedAds " + loadAdError.getMessage() + " " + isNativeLarge);
                    if (listener != null) {
                        listener.onAdFailed(AdsEnum.ADS_ADMOB, loadAdError.getMessage());
                    }
                }

            }).build();

            //for Facebook mediation
            Bundle extras = new FacebookExtras()
                    .setNativeBanner(true)
                    .build();

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adLoader.loadAd(adRequest);
            } catch (Exception e) {
                if (listener != null) {
                    listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                }
            }

        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, "NativeAdvancedAds Id null");
            }
        }

    }


    /**
     * for native ads grid view
     * Populates a {@link NativeAdView} object with data from a given
     * {@link NativeAd }.
     *
     * @param unifiedNativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateUnifiedNativeAdForGrid(NativeAd unifiedNativeAd,
                                                NativeAdView adView) {
//        mVideoStatus.setText("Video status: Ad does not contain a video asset.");
//        mRefresh.setEnabled(true);

      //  System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>004");
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every unifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(unifiedNativeAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());

        List<NativeAd.Image> images = unifiedNativeAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = unifiedNativeAd.getIcon();

        if (logoImage != null) {
            ((ImageView) adView.getIconView()).setImageDrawable(logoImage.getDrawable());
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            adView.getIconView().setVisibility(View.INVISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(unifiedNativeAd);
    }


    private void getNativeAdvancedAds_GridView_Ads(final Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
          //  System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>007");
            id = id.trim();
            AdLoader.Builder builder = new AdLoader.Builder(context, id);
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    mainNativeAd = nativeAd;
                    final LinearLayout linearLayout = new LinearLayout(context);
                    NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_grid, linearLayout, false);
                    populateUnifiedNativeAdForGrid(nativeAd, adView);
                    linearLayout.addView(adView);
                    if (listener != null) {
                     //   System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>008");
                        listener.onAdLoaded(linearLayout);

                    }
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                  //  System.out.println("AdmobNativeAdvanced.onAdFailedToLoad getNativeAdvancedAds_GridView_Ads" + loadAdError.getMessage());
                    if (listener != null) {
                        listener.onAdFailed(AdsEnum.ADS_ADMOB, loadAdError.getMessage());
                    }
                }
            }).build();
            //for Facebook mediation
            Bundle extras = new FacebookExtras()
                    .setNativeBanner(true)
                    .build();

            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
              //  System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>0010");
                adLoader.loadAd(adRequest);
            } catch (Exception e) {
               // System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>0011" + " " + e.getMessage());
                if (listener != null) {
                    listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                }
            }

        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, "NativeAdvancedAds_GridView_Ads Id null");
            }
        }

    }


    private void populateUnifiedNativeAdForRect(NativeAd unifiedNativeAd,
                                                NativeAdView adView) {

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(unifiedNativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());

        if (unifiedNativeAd.getIcon() != null) {
            adView.getIconView().setVisibility(View.VISIBLE);
            ((ImageView) adView.getIconView()).setImageDrawable(
                    unifiedNativeAd.getIcon().getDrawable());
        } else {
            adView.getIconView().setVisibility(View.GONE);
        }

        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);


        adView.setImageView(mainImageView);
        mainImageView.setVisibility(View.VISIBLE);

        // At least one image is guaranteed.
        List<NativeAd.Image> images = unifiedNativeAd.getImages();
        if (images != null && images.size() > 0) {
            mainImageView.setImageDrawable(images.get(0).getDrawable());
        }


        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (unifiedNativeAd.getPrice() != null) {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(unifiedNativeAd.getPrice());
        } else {
            adView.getPriceView().setVisibility(View.GONE);
        }

        if (unifiedNativeAd.getStore() != null) {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(unifiedNativeAd.getStore());
        } else {
            adView.getStoreView().setVisibility(View.GONE);
        }

        if (unifiedNativeAd.getStarRating() != null) {
            adView.getStarRatingView().setVisibility(View.VISIBLE);
            ((RatingBar) adView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
        } else {
            adView.getStarRatingView().setVisibility(View.GONE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(unifiedNativeAd);
    }


    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    private void getNativeRectangleAds(final Activity context, String id, final AppAdsListener listener) {
        if (id != null && !id.equals("")) {
            if (BuildConfig.DEBUG) {
                id = AD_UNIT_ID.trim();
            }
            id = id.trim();
            AdLoader.Builder builder = new AdLoader.Builder(context, id);

            builder.forNativeAd(nativeAd -> {
                mainNativeAd = nativeAd;
                final LinearLayout linearLayout = new LinearLayout(context);
                NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_rectangle, linearLayout, false);
                populateUnifiedNativeAdForRect(nativeAd, adView);
                linearLayout.addView(adView);
                if (listener != null) {
                    listener.onAdLoaded(linearLayout);
                }
            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    System.out.println("NewEngine getNewBannerRectangle Mediation getNativeRectangleAds " + loadAdError.getMessage());
                    if (listener != null) {
                        listener.onAdFailed(AdsEnum.ADS_ADMOB, loadAdError.getMessage());
                    }
                }
            }).build();

            //for Facebook mediation
            Bundle extras = new FacebookExtras()
                    .setNativeBanner(true)
                    .build();
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
            MobileAds.setRequestConfiguration(EngineConstant.addTestDeviceForAdMob());

            try {
                adLoader.loadAd(adRequest);
            } catch (Exception e) {
                if (listener != null) {
                    listener.onAdFailed(AdsEnum.ADS_ADMOB, e.getMessage());
                }
            }

        } else {
            if (listener != null) {
                listener.onAdFailed(AdsEnum.ADS_ADMOB, "NativeAdvancedAds Id null");
            }
        }

    }


    public void showNativeAdvancedAds(final Activity context, String id,
                                      final boolean isNativeLarge, final AppAdsListener listener) {
        if (mainNativeAd == null) {
            getNativeAdvancedAds(context, id, isNativeLarge, listener);
        } else {
            final LinearLayout linearLayout = new LinearLayout(context);
            if (isNativeLarge) {
                NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_large, linearLayout, false);
                populateUnifiedNativeAdForLarge(mainNativeAd, adView);
                linearLayout.addView(adView);
                listener.onAdLoaded(linearLayout);
            } else {
                NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_medium, linearLayout, false);
                populateUnifiedNativeAdForMedium(mainNativeAd, adView);
                linearLayout.addView(adView);
                listener.onAdLoaded(linearLayout);
            }
            getNativeAdvancedAds(context, id, isNativeLarge, null);
        }
    }

    public void showNativeGridAds(final Activity context, String id, final AppAdsListener listener) {
        if (mainNativeAd == null) {
           // System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>001");
            getNativeAdvancedAds_GridView_Ads(context, id, listener);

        } else {
          //  System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>002");
            final LinearLayout linearLayout = new LinearLayout(context);
            NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_grid, linearLayout, false);
            populateUnifiedNativeAdForGrid(mainNativeAd, adView);
            linearLayout.addView(adView);
            listener.onAdLoaded(linearLayout);
          //  System.out.println("AdmobNativeAdvanced.showNativeGridAds NewEngine getNewNativeGrid >>>006");
            getNativeAdvancedAds_GridView_Ads(context, id, null);
        }
    }

    public void showNativeRectangleAds(final Activity context, String id, final AppAdsListener listener) {
        if (mainNativeAd == null) {
            getNativeRectangleAds(context, id, listener);

        } else {
            final LinearLayout linearLayout = new LinearLayout(context);
            NativeAdView adView = (NativeAdView) context.getLayoutInflater().inflate(R.layout.ad_admob_native_rectangle, linearLayout, false);
            populateUnifiedNativeAdForRect(mainNativeAd, adView);
            linearLayout.addView(adView);
            listener.onAdLoaded(linearLayout);
            getNativeRectangleAds(context, id, null);
        }
    }
}
