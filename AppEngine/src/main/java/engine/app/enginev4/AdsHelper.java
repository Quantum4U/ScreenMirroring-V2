package engine.app.enginev4;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;
import engine.app.listener.OnCacheFullAdLoaded;
import engine.app.listener.OnRewardedEarnedItem;
import engine.app.server.v2.DataHubConstant;
import engine.app.server.v2.Slave;
import engine.app.server.v4.AdsProviders;
import engine.app.serviceprovider.AdMobAdaptive;
import engine.app.serviceprovider.AdMobAds;
import engine.app.serviceprovider.AdMobMediation;
import engine.app.serviceprovider.AdMobOpenAds;
import engine.app.serviceprovider.AdMobRewardedAds;
import engine.app.serviceprovider.AdmobMediationNativeAdvanced;
import engine.app.serviceprovider.AdmobNativeAdvanced;
import engine.app.serviceprovider.AppLovinAdsProvider;
import engine.app.serviceprovider.AppNextAdsUtils;
import engine.app.serviceprovider.FbAdsProvider;
import engine.app.serviceprovider.InHouseAds;
import engine.app.serviceprovider.IronSourceUtils;
import engine.app.serviceprovider.StartupAdsProvider;
import engine.app.serviceprovider.UnityAdsUtils;
import engine.app.serviceprovider.Utils;
import engine.app.serviceprovider.VungleAdsUtils;
import engine.app.socket.EngineClient;

import static engine.app.serviceprovider.Utils.getFullAdsCount;
import static engine.app.serviceprovider.Utils.isNetworkConnected;
import static engine.app.serviceprovider.Utils.setFullAdsCount;


/**
 * Created by Meenu Singh on 05/06/19.
 */
public class AdsHelper {
    private static final String TAG = "AdsHelper ";

    /**
     * here adMobSplashCache and fbSplashCache boolean are manage for not requesting multiple ads.
     */
    //private boolean adMobSplashCache = false, fbSplashCache = false;

    private static AdsHelper instance;

    private AdsHelper() {

    }

    public static AdsHelper getInstance() {
        if (instance == null) {
            synchronized (AdsHelper.class) {
                if (instance == null) {
                    instance = new AdsHelper();
                }
            }
        }
        return instance;
    }

    public void getNewBannerFooter(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.BOTTOM_BANNER_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.BOTTOM_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerFooter " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner:
                // AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, providers.ad_id, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptive(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Admob_Mediation_Banner:
                AdMobMediation.getAdMobMediationObj(context).admob_GetBannerMediation(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Banner:
                FbAdsProvider.getFbObject().getFBBanner(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Inhouse_Banner:
                new InHouseAds().getBannerFooter(context, InHouseAds.TYPE_BANNER_FOOTER, listener);

                break;
            case Slave.Provider_Startapp_Banner:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).getBannerAds(context, listener);

                break;
            case Slave.Provider_Unity_Banner:
                UnityAdsUtils.getUnityObj(context).getUnityAdsBanner(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Banner:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBanner(context, listener);
                // HmsAdsProvider.getInstance(context).hcm_GetBannerAds(context,providers.ad_id,listener);
                break;
            case Slave.Provider_AppNext_Banner:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerAds(context, providers.ad_id, listener);

                break;

            case Slave.Provider_InMobi_Banner:
                IronSourceUtils.getInstance(context).getIronSourceAdsBanner(context, providers.ad_id, listener);

                break;

            default:
                //AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, Slave.ADMOB_BANNER_ID_STATIC, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptive(context, Slave.ADMOB_BANNER_ID_STATIC, listener);
                break;
        }

    }

    public void getNewBannerHeaderAppLock(final Context context, final int position, AppAdsListener listener, int adsW) {
        if (position >= Slave.TOP_BANNER_providers.size()) {
            return;
        }

        AdsProviders providers = Slave.TOP_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerHeader " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

//        Toast.makeText(context, "Lock Screen Ads Calling SLave Provider "
//                +providers.provider_id+" Ads ID  "+ providers.ad_id,Toast.LENGTH_LONG).show();

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner:
                //AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, providers.ad_id, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptiveAppLock(context, providers.ad_id, listener,adsW);

                break;
            case Slave.Provider_Admob_Mediation_Banner:
                AdMobMediation.getAdMobMediationObj(context).admob_GetBannerMediationAppLock(context, providers.ad_id, listener, adsW);

                break;
           case Slave.Provider_Facebook_Banner:
                FbAdsProvider.getFbObject().getFBBanner(context, providers.ad_id, listener);
                break;
            case Slave.Provider_Inhouse_Banner:
                new InHouseAds().getBannerHeader(context, InHouseAds.TYPE_BANNER_HEADER, listener);

                break;
//            case Slave.Provider_Startapp_Banner:
//                StartupAdsProvider.getStartappObj(context, providers.ad_id).getBannerAds(context, listener);

      //          break;
//            case Slave.Provider_Unity_Banner:
//                UnityAdsUtils.getUnityObj(context).getUnityAdsBanner(context, providers.ad_id, listener);
//
//                break;
            case Slave.Provider_Applovin_Banner:

                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBanner(context, listener);
                //HmsAdsProvider.getInstance(context).hcm_GetBannerAds(context,providers.ad_id,listener);

                break;
            case Slave.Provider_AppNext_Banner:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerAds(context, providers.ad_id, listener);

                break;
//            case Slave.Provider_InMobi_Banner:
//                IronSourceUtils.getInstance(context).getIronSourceAdsBanner(context, providers.ad_id, listener);
//                break;
            default:
                //AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, Slave.ADMOB_BANNER_ID_STATIC, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptiveAppLock(context, Slave.ADMOB_BANNER_ID_STATIC, listener, adsW);
                break;
        }

    }


    public void getNewBannerHeader(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.TOP_BANNER_providers.size()) {
            return;
        }

        AdsProviders providers = Slave.TOP_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerHeader " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner:
                //AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, providers.ad_id, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptive(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Admob_Mediation_Banner:
                AdMobMediation.getAdMobMediationObj(context).admob_GetBannerMediation(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Banner:
                FbAdsProvider.getFbObject().getFBBanner(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Inhouse_Banner:
                new InHouseAds().getBannerHeader(context, InHouseAds.TYPE_BANNER_HEADER, listener);

                break;
            case Slave.Provider_Startapp_Banner:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).getBannerAds(context, listener);

                break;
            case Slave.Provider_Unity_Banner:
                UnityAdsUtils.getUnityObj(context).getUnityAdsBanner(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Banner:

                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBanner(context, listener);
                //HmsAdsProvider.getInstance(context).hcm_GetBannerAds(context,providers.ad_id,listener);

                break;
            case Slave.Provider_AppNext_Banner:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_InMobi_Banner:
                IronSourceUtils.getInstance(context).getIronSourceAdsBanner(context, providers.ad_id, listener);

                break;
            default:
                //AdMobAds.getAdmobOBJ(context).admob_GetBannerAds(context, Slave.ADMOB_BANNER_ID_STATIC, listener);
                AdMobAdaptive.getAdmobAdaptiveObj(context).admob_GetBannerAdaptive(context, Slave.ADMOB_BANNER_ID_STATIC, listener);
                break;
        }

    }

    public void getNewBannerLargeApplock(final Context context, final int position, AppAdsListener listener) {
        if (position >= Slave.LARGE_BANNER_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.LARGE_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerLarge " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner_Large:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerLargeAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Banner_Large:
                FbAdsProvider.getFbObject().getFBBannerLarge(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Banner_Large:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBannerLarge(context, listener);
                //HmsAdsProvider.getInstance(context).hcm_GetBannerLargeAds(context,providers.ad_id,listener);
                break;
            case Slave.Provider_Inhouse_Banner_Large:
                new InHouseAds().getBannerLarge(context, InHouseAds.TYPE_BANNER_LARGE, listener);

                break;
            case Slave.Provider_AppNext_Banner_Large:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerLargeAds(context, providers.ad_id, listener);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerLargeAds(context, Slave.ADMOB_BANNER_ID_LARGE_STATIC, listener);
                break;
        }

    }

    public void getNewBannerLarge(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.LARGE_BANNER_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.LARGE_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerLarge " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner_Large:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerLargeAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Banner_Large:
                FbAdsProvider.getFbObject().getFBBannerLarge(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Banner_Large:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBannerLarge(context, listener);
                //HmsAdsProvider.getInstance(context).hcm_GetBannerLargeAds(context,providers.ad_id,listener);
                break;
            case Slave.Provider_Inhouse_Banner_Large:
                new InHouseAds().getBannerLarge(context, InHouseAds.TYPE_BANNER_LARGE, listener);

                break;
            case Slave.Provider_AppNext_Banner_Large:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerLargeAds(context, providers.ad_id, listener);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerLargeAds(context, Slave.ADMOB_BANNER_ID_LARGE_STATIC, listener);
                break;
        }

    }

    public void getNewBannerRectangle(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.RECTANGLE_BANNER_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.RECTANGLE_BANNER_providers.get(position);
        Log.d(TAG, "NewEngine getNewBannerRectangle " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Banner_Rectangle:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerRectangleAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Admob_Mediation_Banner_Rect:
                AdMobMediation.getAdMobMediationObj(context).admob_GetBannerRectangleAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Banner_Rect:
                FbAdsProvider.getFbObject().getFBBannerRectangle(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Vungle_Banner_Rect:
                VungleAdsUtils.getVungleObject().getVungleNativeAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Banner_Rectangle:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).getAppLovinBannerRectangle(context, listener);
                //  HmsAdsProvider.getInstance(context).hcm_GetBannerRectangleAds(context,providers.ad_id,listener);

                break;
            case Slave.Provider_Inhouse_Banner_Rect:
                new InHouseAds().getBannerRectangle(context, InHouseAds.TYPE_BANNER_RECTANGLE, listener);

                break;
            case Slave.Provider_AppNext_Banner_Rectangle:
                AppNextAdsUtils.getAppNextObj(context).getAppNextBannerRectangleAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_InMobi_Banner_Rect:
                IronSourceUtils.getInstance(context).getIronSourceAdsBannerRect(context, providers.ad_id, listener);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_GetBannerRectangleAds(context, Slave.ADMOB_BANNER_ID_RECTANGLE_STATIC, listener);
                break;
        }

    }

    public void getNewNativeMedium(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.NATIVE_MEDIUM_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.NATIVE_MEDIUM_providers.get(position);
        Log.d(TAG, "NewEngine getNewNativeMedium " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);
        switch (providers.provider_id) {
            case Slave.Provider_Admob_Native_Medium:
                //new AdmobNativeAdvanced().getNativeAdvancedAds(context, providers.ad_id, false, listener);
                AdmobNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, providers.ad_id, false, listener);

                break;
            case Slave.Provider_Admob_Mediation_Native_Mid:
                AdmobMediationNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, providers.ad_id, false, listener);

                break;
            case Slave.Provider_Facebook_Native_Medium:
                FbAdsProvider.getFbObject().getNativeAds(context, false, providers.ad_id, listener);

                break;
            case Slave.Provider_Inhouse_Medium:
                new InHouseAds().showNativeMedium(context, InHouseAds.TYPE_NATIVE_MEDIUM, listener);

                break;
            case Slave.Provider_Startapp_Native_Medium:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showNativeMedium(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Native_Medium:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).showAppLovinNativeMedium(context, listener);
               // HmsAdsProvider.getInstance(context).showHcmNativeAds(context, providers.ad_id,false,listener);
                break;
            case Slave.Provider_AppNext_Native_Medium:
                AppNextAdsUtils.getAppNextObj(context).showNativeMediumAds(context, providers.ad_id, listener);

                break;
            default:
                AdmobNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, Slave.ADMOB_NATIVE_MEDIUM_ID_STATIC, false, listener);

                break;
        }
    }

    public void getNewNativeLarge(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.NATIVE_LARGE_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.NATIVE_LARGE_providers.get(position);
        Log.d(TAG, "NewEngine getNewNativeLarge " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Native_Large:
                AdmobNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, providers.ad_id, true, listener);

                break;
            case Slave.Provider_Admob_Mediation_Native_Large:
                AdmobMediationNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, providers.ad_id, true, listener);

                break;
            case Slave.Provider_Facebook_Native_Large:
                FbAdsProvider.getFbObject().getNativeAds(context, true, providers.ad_id, listener);

                break;
            case Slave.Provider_Inhouse_Large:
                new InHouseAds().showNativeLarge(context, InHouseAds.TYPE_NATIVE_LARGE, listener);

                break;
            case Slave.Provider_Startapp_Native_Large:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showNativeLarge(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Native_Large:
                //Replace applovin with Hms
                AppLovinAdsProvider.getAppLovinObject(context).showAppLovinNativeLarge(context, listener);
              //  HmsAdsProvider.getInstance(context).showHcmNativeAds(context,providers.ad_id,true,listener);

                break;
            case Slave.Provider_AppNext_Native_Large:
                AppNextAdsUtils.getAppNextObj(context).showNativeLargeAds(context, providers.ad_id, listener);

                break;
            default:
                AdmobNativeAdvanced.getInstance(context).showNativeAdvancedAds(context, Slave.ADMOB_BANNER_ID_LARGE_STATIC, true, listener);
                break;
        }

    }

    public void getNewNativeGrid(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.NATIVE_MEDIUM_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.NATIVE_MEDIUM_providers.get(position);
        Log.d(TAG, "NewEngine getNewNativeGrid " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Native_Medium:
                // new AdmobNativeAdvanced().getNativeAdvancedAds_GridView_Ads(context, providers.ad_id, moreLayout, listener);
                AdmobNativeAdvanced.getInstance(context).showNativeGridAds(context, providers.ad_id, listener);
                break;
            case Slave.Provider_Facebook_Native_Medium:
                FbAdsProvider.getFbObject().getNativeAds_Grid(providers.ad_id, context, listener);

                break;
            case Slave.Provider_Inhouse_Medium:
                new InHouseAds().loadGridViewNativeAdsView(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Startapp_Native_Medium:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showNativeMedium_Grid(context, providers.ad_id, listener);

                break;


            case Slave.Provider_Applovin_Native_Medium:
                   AppLovinAdsProvider.getAppLovinObject(context).showAppLovinNativeGrid(context, listener);
                break;
            default:
                // new AdmobNativeAdvanced().getNativeAdvancedAds_GridView_Ads(context, Slave.ADMOB_NATIVE_MEDIUM_ID_STATIC, moreLayout, listener);
                AdmobNativeAdvanced.getInstance(context).showNativeGridAds(context, Slave.ADMOB_NATIVE_MEDIUM_ID_STATIC, listener);
                break;
        }
    }

    public void getNewNativeRectangle(final Activity context, final int position, AppAdsListener listener) {
        if (position >= Slave.NATIVE_MEDIUM_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.NATIVE_MEDIUM_providers.get(position);
        Log.d(TAG, "NewEngine getNewNativeRectangle " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Native_Medium:
                AdmobNativeAdvanced.getInstance(context).showNativeRectangleAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Admob_Mediation_Native_Mid:
                AdmobMediationNativeAdvanced.getInstance(context).showNativeRectangleAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Facebook_Native_Medium:
                FbAdsProvider.getFbObject().getNativeAds(context, false, providers.ad_id, listener);

                break;
            case Slave.Provider_Inhouse_Medium:
                new InHouseAds().showNativeMedium(context, InHouseAds.TYPE_NATIVE_MEDIUM, listener);

                break;
            case Slave.Provider_Startapp_Native_Medium:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showNativeMedium(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Applovin_Native_Medium:
                 AppLovinAdsProvider.getAppLovinObject(context).showAppLovinNativeMedium(context, listener);
                break;
            case Slave.Provider_AppNext_Native_Medium:
                AppNextAdsUtils.getAppNextObj(context).showNativeMediumAds(context, providers.ad_id, listener);

                break;
            default:
                AdmobNativeAdvanced.getInstance(context).showNativeRectangleAds(context, Slave.ADMOB_NATIVE_MEDIUM_ID_STATIC, listener);

                break;
        }
    }

    public void getNewLaunchCacheFullPageAd(Activity context, final int position, AppFullAdsListener listener,final OnCacheFullAdLoaded adsLoadedlistener) {
        if (position >= Slave.LAUNCH_FULL_ADS_providers.size()) {
            adsLoadedlistener.onCacheFullAdFailed();
            return;
        }
        AdsProviders providers = Slave.LAUNCH_FULL_ADS_providers.get(position);
        Log.d(TAG, "NewEngine getNewLaunchCacheFullPageAd " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_FullAds:
                // adMobSplashCache = true;
                AdMobAds.getAdmobOBJ(context).admob_InitFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Admob_Mediation_Full_Ads:
                // adMobSplashCache = true;
                AdMobMediation.getAdMobMediationObj(context).admob_InitFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Facebook_Full_Page_Ads:
                // fbSplashCache = true;
                FbAdsProvider.getFbObject().loadFBFullAds(providers.ad_id, context, listener, true);

                break;
            case Slave.Provider_Vungle_Full_Page_Ads:
                VungleAdsUtils.getVungleObject().loadVungleFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Applovin_FullAds_Page_Ads:
                //Replace Applovin with hms ads
                AppLovinAdsProvider.getAppLovinObject(context).loadAppLovinFullAds(context, listener, true);
                //HmsAdsProvider.getInstance(context).hcm_InitFullAds(context,providers.ad_id,listener,true);
                break;
            case Slave.Provider_Startapp_FullAds:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).loadFullAds(listener, true);

                break;
            case Slave.Provider_Inhouse_FullAds:
                /*
                 * in Inhouse case no need to cache that's why we assume cache is loaded.
                 */
                if (isNetworkConnected(context)) {
                    listener.onFullAdLoaded();
                } else {
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "Internet issue");
                }
                break;
            case Slave.Provider_Unity_Full_Page_Ads:
                UnityAdsUtils.getUnityObj(context).loadUnityFullAds(context, providers.ad_id, listener, true);
                break;

            case Slave.Provider_AppNext_FullAds_Page_Ads:
                AppNextAdsUtils.getAppNextObj(context).initAppNextFullAds(context, providers.ad_id, listener, true, true);

                break;

            case Slave.Provider_InMobi_Full_Page_Ads:
                IronSourceUtils.getInstance(context).loadIronSourceFullAds(context, providers.ad_id, listener, true);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_InitFullAds(context, Slave.ADMOB_FULL_ID_STATIC, listener, true);
                break;
        }

    }


    public void getNewNavCacheFullPageAd(Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.FULL_ADS_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.FULL_ADS_providers.get(position);
        Log.d(TAG, "NewEngine getNewNavCacheFullPageAd " + position
                + " " + providers.provider_id
                + " " + providers.ad_id + " ");

        switch (providers.provider_id) {
            case Slave.Provider_Admob_FullAds:
                AdMobAds.getAdmobOBJ(context).admob_InitFullAds(context, providers.ad_id, listener, true);
                break;
            case Slave.Provider_Admob_Mediation_Full_Ads:
                AdMobMediation.getAdMobMediationObj(context).admob_InitFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Facebook_Full_Page_Ads:
                FbAdsProvider.getFbObject().loadFBFullAds(providers.ad_id, context, listener, true);

                break;
            case Slave.Provider_Unity_Full_Page_Ads:
                UnityAdsUtils.getUnityObj(context).loadUnityFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Vungle_Full_Page_Ads:
                VungleAdsUtils.getVungleObject().loadVungleFullAds(context, providers.ad_id, listener, true);

                break;
            case Slave.Provider_Applovin_FullAds_Page_Ads:
                //Replace Applovin with hms ads
                AppLovinAdsProvider.getAppLovinObject(context).loadAppLovinFullAds(context, listener, true);
                // HmsAdsProvider.getInstance(context).hcm_InitFullAds(context,providers.ad_id,listener,true);
                break;
            case Slave.Provider_Startapp_FullAds:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).loadFullAds(listener, true);

                break;
            case Slave.Provider_Inhouse_FullAds:
                /*
                 * in Inhouse case no need to cache that's why we assume cache is loaded.
                 */
                if (isNetworkConnected(context)) {
                    listener.onFullAdLoaded();
                } else {
                    listener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "Internet issue");
                }
                break;
            case Slave.Provider_AppNext_FullAds_Page_Ads:
                AppNextAdsUtils.getAppNextObj(context).initAppNextFullAds(context, providers.ad_id, listener, false, false);

                break;
            case Slave.Provider_InMobi_Full_Page_Ads:
                IronSourceUtils.getInstance(context).loadIronSourceFullAds(context, providers.ad_id, listener, true);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_InitFullAds(context, Slave.ADMOB_FULL_ID_STATIC, listener, true);
                break;
        }

    }

    public void showFullAdsOnLaunch(final Activity context, final int position, final AppFullAdsListener listener) {
        if (position >= Slave.LAUNCH_FULL_ADS_providers.size()) {
            return;
        }
        final AdsProviders providers = Slave.LAUNCH_FULL_ADS_providers.get(position);
        Log.d(TAG, "NewEngine showFullAdsOnLaunch " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);


        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.LAUNCH_FULL_ADS_start_date)
                && DataHubConstant.APP_LAUNCH_COUNT > Utils.getStringtoInt(Slave.LAUNCH_FULL_ADS_show_after)) {

            switch (providers.provider_id) {
                case Slave.Provider_Admob_FullAds:
                    AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, providers.ad_id, listener, true);

                    break;
                case Slave.Provider_Admob_Mediation_Full_Ads:
                    AdMobMediation.getAdMobMediationObj(context).admob_showFullAds(context, providers.ad_id, listener, true);

                    break;
                case Slave.Provider_Facebook_Full_Page_Ads:
                    FbAdsProvider.getFbObject().showFBFullAds(providers.ad_id, context, listener, true);
                    break;
                case Slave.Provider_Startapp_FullAds:
                    StartupAdsProvider.getStartappObj(context, providers.ad_id).showfullAds(context, Utils.getStringtoInt(providers.ad_id), listener, true);

                    break;
                case Slave.Provider_Inhouse_FullAds:
                    if (isNetworkConnected(context)) {
                        Slave.LAUNCH_FULL_ADS_src = providers.src;
                        Slave.LAUNCH_FULL_ADS_clicklink = providers.clicklink;
                        new InHouseAds().showFullAds(context, EngineClient.IH_LAUNCH_FULL, Slave.LAUNCH_FULL_ADS_src, Slave.LAUNCH_FULL_ADS_clicklink, listener);
                    }

                    break;
                case Slave.Provider_Unity_Full_Page_Ads:
                    UnityAdsUtils.getUnityObj(context).showUnityFullAds(context, providers.ad_id, listener);
                    break;

                case Slave.Provider_Vungle_Full_Page_Ads:
                    VungleAdsUtils.getVungleObject().showVungleFullAds(context, providers.ad_id, listener, true);

                    break;
                case Slave.Provider_Applovin_FullAds_Page_Ads:
                    //Replace Applovin with hms ads
                    AppLovinAdsProvider.getAppLovinObject(context).showAppLovinFullAds(context, listener, true);
                    //  HmsAdsProvider.getInstance(context).hcm_showFullAds(context,providers.ad_id,listener);

                    break;
                case Slave.Provider_AppNext_FullAds_Page_Ads:
                    AppNextAdsUtils.getAppNextObj(context).showAppNextFullAds(context, providers.ad_id, listener, true);

                    break;
                case Slave.Provider_InMobi_Full_Page_Ads:
                    IronSourceUtils.getInstance(context).showIronSourceFullAds(context, providers.ad_id, listener, true);

                    break;
                default:
                    AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, Slave.ADMOB_FULL_ID_STATIC, listener, true);
                    break;
            }
        } else {
            listener.onFullAdClosed();
        }
    }


    public void showForcedFullAds(final Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.FULL_ADS_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.FULL_ADS_providers.get(position);
        Log.d(TAG, "NewEngine showForcedFullAds " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_FullAds:
                AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_Admob_Mediation_Full_Ads:
                AdMobMediation.getAdMobMediationObj(context).admob_showFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_Facebook_Full_Page_Ads:
                FbAdsProvider.getFbObject().showFBFullAds(providers.ad_id, context, listener, false);

                break;
            case Slave.Provider_Startapp_FullAds:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showfullAds(context, Utils.getStringtoInt(providers.ad_id), listener, false);

                break;
            case Slave.Provider_Inhouse_FullAds:
                if (isNetworkConnected(context)) {
                    Slave.FULL_ADS_src = providers.src;
                    Slave.FULL_ADS_clicklink = providers.clicklink;
                    new InHouseAds().showFullAds(context, EngineClient.IH_FULL, Slave.FULL_ADS_src, Slave.FULL_ADS_clicklink, listener);
                }
                break;
            case Slave.Provider_Unity_Full_Page_Ads:
                UnityAdsUtils.getUnityObj(context).showUnityFullAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Vungle_Full_Page_Ads:
                VungleAdsUtils.getVungleObject().loadVungleFullAds(context, providers.ad_id, listener, true);
                VungleAdsUtils.getVungleObject().showVungleFullAds(context, providers.ad_id, listener, false);


                break;
            case Slave.Provider_Applovin_FullAds_Page_Ads:
                //Replace Applovin with hms ads
                // AppLovinAdsProvider.getAppLovinObject(context).loadAppLovinFullAds(context, listener, false);
                AppLovinAdsProvider.getAppLovinObject(context).showAppLovinFullAds(context, listener, false);

                // HmsAdsProvider.getInstance(context).hcm_showFullAds(context,providers.ad_id,listener);

                break;
            case Slave.Provider_AppNext_FullAds_Page_Ads:
                AppNextAdsUtils.getAppNextObj(context).showAppNextFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_InMobi_Full_Page_Ads:
                IronSourceUtils.getInstance(context).loadIronSourceFullAds(context, providers.ad_id, listener, false);
                IronSourceUtils.getInstance(context).showIronSourceFullAds(context, providers.ad_id, listener, false);

                break;
            default:
                if (getFullAdsCount(context) >= Utils.getStringtoInt(Slave.FULL_ADS_nevigation)) {
                    setFullAdsCount(context, 0);
                    AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, Slave.ADMOB_FULL_ID_STATIC, listener, false);
                }
                break;
        }
    }

    public void showFullAds(final Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.FULL_ADS_providers.size()) {
            return;
        }
        final AdsProviders providers = Slave.FULL_ADS_providers.get(position);
        Log.d(TAG, "NewEngine showFullAds  navigation " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_FullAds:
                AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_Admob_Mediation_Full_Ads:
                AdMobMediation.getAdMobMediationObj(context).admob_showFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_Facebook_Full_Page_Ads:
                FbAdsProvider.getFbObject().showFBFullAds(providers.ad_id, context, listener, false);

                break;
            case Slave.Provider_Startapp_FullAds:
                StartupAdsProvider.getStartappObj(context, providers.ad_id).showfullAds(context, Utils.getStringtoInt(providers.ad_id), listener, false);

                break;
            case Slave.Provider_Inhouse_FullAds:
                if (isNetworkConnected(context)) {
                    Slave.FULL_ADS_src = providers.src;
                    Slave.FULL_ADS_clicklink = providers.clicklink;
                    new InHouseAds().showFullAds(context, EngineClient.IH_FULL, Slave.FULL_ADS_src, Slave.FULL_ADS_clicklink, listener);
                }
                break;
            case Slave.Provider_Unity_Full_Page_Ads:
                UnityAdsUtils.getUnityObj(context).showUnityFullAds(context, providers.ad_id, listener);

                break;
            case Slave.Provider_Vungle_Full_Page_Ads:
                VungleAdsUtils.getVungleObject().showVungleFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_Applovin_FullAds_Page_Ads:
                //Replace Applovin with hms ads
                AppLovinAdsProvider.getAppLovinObject(context).showAppLovinFullAds(context, listener, false);
                //HmsAdsProvider.getInstance(context).hcm_showFullAds(context,providers.ad_id,listener);
                break;
            case Slave.Provider_AppNext_FullAds_Page_Ads:
                AppNextAdsUtils.getAppNextObj(context).showAppNextFullAds(context, providers.ad_id, listener, false);

                break;
            case Slave.Provider_InMobi_Full_Page_Ads:
                IronSourceUtils.getInstance(context).showIronSourceFullAds(context, providers.ad_id, listener, false);

                break;
            default:
                AdMobAds.getAdmobOBJ(context).admob_showFullAds(context, Slave.ADMOB_FULL_ID_STATIC, listener, false);

                break;
        }

    }


    public void getNewNavCacheRewardedAds(Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.REWARDED_VIDEO_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.REWARDED_VIDEO_providers.get(position);
        Log.d(TAG, "NewEngine getNewNavCacheRewardedAds " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Rewarded_Video:
                AdMobRewardedAds.getInstance(context).initAdMobRewardedVideo(context, providers.ad_id, listener, null);
                break;

        }

    }

    public void showRewardedAds(final Activity context, final int position, AppFullAdsListener listener, OnRewardedEarnedItem onRewardedEarnedItem) {
        if (position >= Slave.REWARDED_VIDEO_providers.size()) {
            return;
        }
        final AdsProviders providers = Slave.REWARDED_VIDEO_providers.get(position);
        Log.v("AdsHelper ", "NewEngine showRewardedAds  navigation " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_Rewarded_Video:
                AdMobRewardedAds.getInstance(context).showAdMobRewardedVideo(context, providers.ad_id, listener, onRewardedEarnedItem);
                break;

        }

    }


    public void getAppOpenAdsCache(Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.APP_OPEN_ADS_providers.size()) {
            return;
        }
        AdsProviders providers = Slave.APP_OPEN_ADS_providers.get(position);
        Log.d(TAG, "NewEngine getAppOpenAdsCache " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_OpenFullAds:
                AdMobOpenAds.getInstance(context).initAdMobOpenAds(context, providers.ad_id, listener, true);
                break;

        }

    }

    public void showAppOpenAds(final Activity context, final int position, AppFullAdsListener listener) {
        if (position >= Slave.APP_OPEN_ADS_providers.size()) {
            return;
        }
        final AdsProviders providers = Slave.APP_OPEN_ADS_providers.get(position);
        Log.v("AdsHelper ", "NewEngine showAppOpenAds navigation " + position
                + " " + providers.provider_id
                + " " + providers.ad_id);

        switch (providers.provider_id) {
            case Slave.Provider_Admob_OpenFullAds:
                AdMobOpenAds.getInstance(context).showAdMobOpenAds(context, providers.ad_id, listener);
                break;

        }

    }


    public void onAHandlerDestroy() {
        FbAdsProvider.getFbObject().FbAdsDestroy();
    }


}
