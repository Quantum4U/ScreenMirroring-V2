package engine.app.adshandler;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import app.pnd.adshandler.R;
import engine.app.EngineAppApplication;
import engine.app.PrintLog;
import engine.app.analytics.EngineAnalyticsConstant;
import engine.app.enginev4.AdsEnum;
import engine.app.enginev4.AdsHelper;
import engine.app.enginev4.LoadAdData;
import engine.app.exitapp.ExitAdsActivity;
import engine.app.exitapp.ExitAdsType2Activity;
import engine.app.fcm.GCMPreferences;
import engine.app.fcm.MapperUtils;
import engine.app.inapp.BillingListActivityNew;
import engine.app.inapp.InAppBillingHandler;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsCloseListner;
import engine.app.listener.AppFullAdsListener;
import engine.app.listener.OnBannerAdsIdLoaded;
import engine.app.listener.OnCacheFullAdLoaded;
import engine.app.listener.OnRewardedEarnedItem;
import engine.app.listener.onParseDefaultValueListener;
import engine.app.server.v2.DataHubConstant;
import engine.app.server.v2.DataHubPreference;
import engine.app.server.v2.GameProvidersResponce;
import engine.app.server.v2.GameServiceV2ResponseHandler;
import engine.app.server.v2.MoreFeature;
import engine.app.server.v2.MoreFeatureResponseHandler;
import engine.app.server.v2.Slave;
import engine.app.serviceprovider.Utils;
import engine.app.socket.EngineClient;


public class AHandler {
    private static AHandler instance;
    private PromptHander promptHander;
    private int mMinBannerHeight = -1;
    private int mMinBannerLargeHeight = -1;
    //private int mMinBannerRectHeight = -1;
    private FrameLayout appAdContainer;
    //private boolean loadtofailedLaunchAds;
    //private Animation zoomin, zoomout;
    private static int isFirstExit = 0;
    private long exitTime = 0;
    private BannerRactangleCaching bannerRactangleCaching;
    public static String ShowBillingPage ="1";
    public static int SplashBillingpageCount  =6;
    private AHandler() {
        promptHander = new PromptHander();
    }

    public static AHandler getInstance() {
        if (instance == null) {
            synchronized (AHandler.class) {
                if (instance == null) {
                    instance = new AHandler();
                    isFirstExit=0;
                }
            }
        }
        return instance;
    }


    /**
     * calling from app launcher class
     */
    public void v2CallOnSplash(final Activity context, final OnCacheFullAdLoaded l) {
        new GCMPreferences(context).setSplashName(context.getClass().getName());
        DataHubPreference dP = new DataHubPreference(context);
        dP.setAppName(Utils.getAppName(context));
        DataHubConstant.APP_LAUNCH_COUNT = Integer.parseInt(dP.getAppLaunchCount());
        Log.d("hello test ads load", "Hello onparsingDefault navigation 001");

        EngineHandler engineHandler = new EngineHandler(context);
        engineHandler.initDefaultValue();
        Log.d("AHandler", "NewEngine Hello onparsingDefault navigation 002");

        engineHandler.initServices(false, new onParseDefaultValueListener() {
            @Override
            public void onParsingCompleted() {
                Log.d("DataHubHandler", "Test DataHubHandler old hjhjhjhjh");
                /*
                 * cache are working on navigation ..
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DataHubHandler", "NewEngine showFullAdsOnLaunch Test DataHubHandler old hjhjhjhjh111");

                        handleLaunchCache(context, l);
                    }
                }, 4000);
            }
        });

        engineHandler.initServices(true, new onParseDefaultValueListener() {
            @Override
            public void onParsingCompleted() {
            }
        });
        InAppBillingHandler handler = new InAppBillingHandler(context, null);
        handler.initializeBilling();

        initCacheOpenAds(context);
        initCacheBannerRect(context);
    }




    /**
     * calling from app dashboard
     */

    public void v2CallonAppLaunch(final Activity context) {
        new GCMPreferences(context).setDashboardName(context.getClass().getName());
        if (!Slave.hasPurchased(context)) {

            PrintLog.print("CHECK CHECK 1 PRO" + " " + Slave.IS_PRO);
            PrintLog.print("CHECK CHECK 2 WEEKLY" + " " + Slave.IS_WEEKLY);
            PrintLog.print("CHECK CHECK 3 MONTHLY" + " " + Slave.IS_MONTHLY);
            PrintLog.print("CHECK CHECK 4 QUARTERLY" + " " + Slave.IS_QUARTERLY);
            PrintLog.print("CHECK CHECK 5 HALF_YEARLY" + " " + Slave.IS_HALFYEARLY);
            PrintLog.print("CHECK CHECK 6 YEARLY" + " " + Slave.IS_YEARLY);
            PrintLog.print("here inside applaunch 02");

            handle_launch_prompt(context);
            cacheNavigationFullAd(context);
/*
            if (Slave.EXIT_SHOW_AD_ON_EXIT_PROMPT.equals("true")) {
                cacheExitFullAd(context);
            }
*/
        }


        if (promptHander == null) {
            promptHander = new PromptHander();
        }
        promptHander.checkForForceUpdate(context);
        promptHander.checkForNormalUpdate(context);

        EngineHandler engineHandler = new EngineHandler(context);
        engineHandler.doGCMRequest();
        engineHandler.doTopicsRequest();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callingForMapper(context);
            }
        }, 2000);

    }


    /**
     * @param activity call this function for only in Applock
     */
    public void v2CallOnBGLaunchBannerLarge(Context activity) {
        EngineHandler engineHandler = new EngineHandler(activity);
        engineHandler.initServices(false, new onParseDefaultValueListener() {
            @Override
            public void onParsingCompleted() {
            }
        });

    }



    /**
     * @param activity call this function when app launch from background
     */
    public void v2CallOnBGLaunch(Activity activity) {
        EngineHandler engineHandler = new EngineHandler(activity);
        engineHandler.initServices(false, new onParseDefaultValueListener() {
            @Override
            public void onParsingCompleted() {
            }
        });

        cacheLaunchFullAd(activity, new OnCacheFullAdLoaded() {
            @Override
            public void onCacheFullAd() {

            }

            @Override
            public void onCacheFullAdFailed() {

            }
        });
        Utils.setFullAdsCount(activity, Utils.getStringtoInt(Slave.FULL_ADS_nevigation));


    }

    /**
     * @param context exit prompt
     */
    public void v2ManageAppExit(Activity context, View view) {


        //&& Utils.isNetworkConnected(context)
        Log.d("Ahandler", "Test v2ManageAppExit.." + Slave.EXIT_TYPE);
        if (!Slave.hasPurchased(context)
                && Slave.EXIT_TYPE != null) {
            switch (Slave.EXIT_TYPE) {
                case Slave.EXIT_TYPE1:
                    exitFromApp(context, view);
                    break;
                case Slave.EXIT_TYPE2:
                case Slave.EXIT_TYPE3:
                case Slave.EXIT_TYPE4:
                case Slave.EXIT_TYPE5:
                case Slave.EXIT_TYPE6:
                    showExitPrompt(context, Slave.EXIT_TYPE);
                    break;
                default:
                    exitFromApp(context, view);
                    break;
            }
        } else {
            exitFromApp(context, view);
        }
    }

    private void exitFromApp(Activity context, View id) {
        Log.d("Ahandler", "Test v2ManageAppExit.." + Slave.EXIT_TYPE+"  "+isFirstExit);
        int DEFAULT_DELAY = 2000;
//        isFirstExit++;
//        if (isFirstExit >= 2) {
//            v2CallOnExitPrompt(context);
//            isFirstExit =0;
//        } else {
//            Toast.makeText(context, "Please click BACK again to exit", Toast.LENGTH_LONG).show();
//        }


        if ((System.currentTimeMillis() - exitTime) > DEFAULT_DELAY) {
            try {

                final Snackbar snackbar = Snackbar.make(id, context.getString(R.string.press_again_to_exit), Snackbar.LENGTH_LONG);
                final View sbView = snackbar.getView();
                final TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTypeface(Utils.getRegularFont(context));
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBar));
                snackbar.show();
            } catch (Exception e) {
                Utils.getErrors(e);
                Utils.showToast(context, context.getString(R.string.press_again_to_exit));
            }
            exitTime = System.currentTimeMillis();
        } else {
            v2CallOnExitPrompt(context);
        }
    }

    public void onDisplayCustomToast(Activity context, View id){
        try {
            final Snackbar snackbar = Snackbar.make(id, context.getString(R.string.check_internet_connections), Snackbar.LENGTH_LONG);
            final View sbView = snackbar.getView();
            final TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTypeface(Utils.getRegularFont(context));
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBar));
            snackbar.show();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    /*
     *
     *
     * */
    public void v2CallOnExitPrompt(Activity context) {

        context.finishAffinity();
        /*
         * full ads count will be 0 on exit..
         */
        Utils.setFullAdsCount(context, 0);

        //Open Ads count will be 0 on exit..
        Utils.setOpenAdsCount_start(context, 0);
    }

    /**
     * open about us page
     */
    public void showAboutUs(Activity mContext) {
        mContext.startActivity(new Intent(mContext, new DataHubConstant(mContext).showAboutUsPage()));

    }

    /**
     * call to show exit prompt
     */
    public void showExitPrompt(final Activity mContext, final String exitType) {
        if (exitType != null) {
            if (exitType.equals(Slave.EXIT_TYPE2) || exitType.equals(Slave.EXIT_TYPE3)) {
                mContext.startActivity(new Intent(mContext, ExitAdsType2Activity.class).putExtra(EngineAnalyticsConstant.Companion.getExitPageType(), exitType));
            } else {
                mContext.startActivity(new Intent(mContext, ExitAdsActivity.class).putExtra(EngineAnalyticsConstant.Companion.getExitPageType(), exitType));
            }
        }

    }

    /**
     * open billing activity
     */
    public void showRemoveAdsPrompt(Context mContext) {
        Intent intent = new Intent(mContext, BillingListActivityNew.class);
        intent.putExtra("fromSplash", "false");
        mContext.startActivity(intent);
    }

    /**
     * open FAQs customtab
     */
    public void showFAQs(Activity mContext) {
        Utils.showFAQs(mContext);
    }

    /**
     * show banner footer ads
     */
    public View getBannerFooter(Activity context, OnBannerAdsIdLoaded bannerAdsIdLoaded) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            bannerAdsIdLoaded.onBannerFailToLoad();
            return getDefaultAdView(context);
        }

        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.BOTTOM_BANNER_start_date)) {

            if (Slave.TYPE_BOTTOM_BANNER.equalsIgnoreCase(Slave.BOTTOM_BANNER_call_native)) {
                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinBannerHeight(context, R.dimen.banner_height));
                appAdContainer.setPadding(0, 10, 0, 0);
                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadBannerFooter(context, loadAdData, appAdContainer, bannerAdsIdLoaded);

                return appAdContainer;

            } else if (Slave.TYPE_BANNER_LARGE.equalsIgnoreCase(Slave.BOTTOM_BANNER_call_native)) {
                return getBannerLarge(context);

            }

        } else {
            bannerAdsIdLoaded.onBannerFailToLoad();
        }
        return getDefaultAdView(context);
    }

    private void loadBannerFooter(final Activity context, final LoadAdData loadAdData, final ViewGroup ll,
                                  final OnBannerAdsIdLoaded bannerAdsIdLoaded) {
        AdsHelper.getInstance().getNewBannerFooter(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerFooter onAdFailed " + pos + " provider name " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.BOTTOM_BANNER_providers.size()) {
                    bannerAdsIdLoaded.onBannerFailToLoad();
                    ll.setVisibility(View.GONE);
                }
                loadBannerFooter(context, loadAdData, ll, bannerAdsIdLoaded);
            }
        });

    }

    /**
     * show banner header ads
     */
    public View getBannerHeader(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }
        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.TOP_BANNER_start_date)) {
            if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.TOP_BANNER_call_native)) {
                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinBannerHeight(context, R.dimen.banner_height));
                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadBannerHeader(context, loadAdData, appAdContainer);

                return appAdContainer;
            }

            if (Slave.TYPE_BANNER_LARGE.equalsIgnoreCase(Slave.TOP_BANNER_call_native)) {
                return getDefaultAdView(context);
            }

        }
        return getDefaultAdView(context);

    }

    // only for App lock application window banner
    public View getBannerHeaderAppLock(Context context, int adsW) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }
        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.TOP_BANNER_start_date)) {
            if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.TOP_BANNER_call_native)) {
                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinBannerHeight(context, R.dimen.banner_height));
                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadBannerHeaderAppLock(context, loadAdData, appAdContainer, adsW);

                return appAdContainer;
            }
//
//            if (Slave.TYPE_BANNER_LARGE.equalsIgnoreCase(Slave.TOP_BANNER_call_native)) {
//                return getDefaultAdView(context);
//            }

        }
        return getDefaultAdView(context);

    }

    private void loadBannerHeaderAppLock(final Context context, final LoadAdData loadAdData, final ViewGroup ll, int adsW) {
        AdsHelper.getInstance().getNewBannerHeaderAppLock(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerHeader onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.TOP_BANNER_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadBannerHeaderAppLock(context, loadAdData, ll, adsW);
            }
        }, adsW);

    }


    private void loadBannerHeader(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewBannerHeader(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerHeader onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.TOP_BANNER_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadBannerHeader(context, loadAdData, ll);
            }
        });

    }

    /**
     * show banner large ads
     */
    public View getBannerLarge(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }

        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.LARGE_BANNER_start_date)) {
            if (Slave.BANNER_TYPE_LARGE.equalsIgnoreCase(Slave.LARGE_BANNER_call_native)) {
                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinBannerHeight(context, R.dimen.banner_large_height));
                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadBannerLarge(context, loadAdData, appAdContainer);

                return appAdContainer;

            } else if (Slave.BANNER_TYPE_HEADER.equalsIgnoreCase(Slave.LARGE_BANNER_call_native)) {
                return getDefaultAdView(context);

            }

        }
        return getDefaultAdView(context);
    }

    public View getBannerLargeApplock(Context context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }
//        if (Slave.LARGE_BANNER_start_date != null && Slave.LARGE_BANNER_start_date.equals("")) {
//            v2CallOnBGLaunchBannerLarge(context);
//        }
        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.LARGE_BANNER_start_date)) {
            if (Slave.BANNER_TYPE_LARGE.equalsIgnoreCase(Slave.LARGE_BANNER_call_native)) {
                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinBannerHeight(context, R.dimen.banner_large_height));
                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadBannerLargeApplock(context, loadAdData, appAdContainer);

                return appAdContainer;

            } else if (Slave.BANNER_TYPE_HEADER.equalsIgnoreCase(Slave.LARGE_BANNER_call_native)) {
                return getDefaultAdView(context);

            }

        }
        return getDefaultAdView(context);
    }

    private void loadBannerLargeApplock(final Context context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewBannerLargeApplock(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerLarge onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.LARGE_BANNER_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadBannerLargeApplock(context, loadAdData, ll);
            }
        });

    }


    private void loadBannerLarge(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewBannerLarge(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerLarge onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.LARGE_BANNER_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadBannerLarge(context, loadAdData, ll);
            }
        });

    }

    /**
     * show banner rectangle ads
     */
    public View getBannerRectangle(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }
        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.RECTANGLE_BANNER_start_date)) {

            if (Slave.BANNER_TYPE_RECTANGLE.equalsIgnoreCase(Slave.RECTANGLE_BANNER_call_native)) {
                if (appAdContainer != null && bannerRactangleCaching != null) {

                    try {
//                        LinearLayout appAdContainer = new LinearLayout(context);
//                        appAdContainer.setGravity(Gravity.CENTER);
//                        appAdContainer.setMinimumHeight(getMinNativeHeight(context, R.dimen.banner_rectangle_height));

                        if(bannerRactangleCaching.getParent() != null) {
                            ((ViewGroup)bannerRactangleCaching.getParent()).removeView(bannerRactangleCaching); // <- fix
                        }
                        bannerRactangleLoading(context, true);
                        appAdContainer.addView(bannerRactangleCaching);
                        bannerRactangleCaching = null;
                        return appAdContainer;
                    }catch (Exception e){
                        return getDefaultAdView(context);
                    }

                }
                bannerRactangleLoading(context, false);


                return appAdContainer;


            } else if (Slave.NATIVE_TYPE_MEDIUM.equalsIgnoreCase(Slave.RECTANGLE_BANNER_call_native)) {
                Log.d("AHandler", "Test getBannerRectangle2222...");
                //return getNativeMedium(context);
                return getNativeRectangle(context);
            }
        }
        return getDefaultAdView(context);

    }

    private void loadBannerRectangle(final Activity context, final LoadAdData loadAdData, final ViewGroup ll, boolean
            isFromCaching) {
        AdsHelper.getInstance().getNewBannerRectangle(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    bannerRactangleCaching = new BannerRactangleCaching(context);
                    bannerRactangleCaching.addView(adsView);
                    if (!isFromCaching) {
                        ll.removeAllViews();
                        ll.addView(bannerRactangleCaching);
                    }
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerRectangle onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.RECTANGLE_BANNER_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadBannerRectangle(context, loadAdData, ll,isFromCaching);
            }
        });

    }

    /**
     * show native medium ads
     */
    public View getNativeRectangle(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }


        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.NATIVE_MEDIUM_start_date)) {

            if (Slave.NATIVE_TYPE_MEDIUM.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {

                FrameLayout nativeAdsContainer = (FrameLayout) LayoutInflater.from(context)
                        .inflate(R.layout.native_ads_progress_dialog_ads_loader, null, false);
                LinearLayout linearLayout = nativeAdsContainer.findViewById(R.id.ll_progress_layout);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_rect_height));

                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadNativeRectangle(context, loadAdData, nativeAdsContainer);

                return nativeAdsContainer;

            } else if (Slave.NATIVE_TYPE_LARGE.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getNativeLarge(context);

            } else if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getBannerHeader(context);

            }
        }
        return getDefaultAdView(context);

    }

    private void loadNativeRectangle(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewNativeRectangle(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                addAdViewInContainer(ll, adsView);
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler ", "NewEngine getNewNativeRectangle onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.NATIVE_MEDIUM_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadNativeRectangle(context, loadAdData, ll);
            }
        });

    }

    /**
     * show native large ads
     */
    public View getNativeLarge(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }
        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.NATIVE_LARGE_start_date)) {

            if (Slave.NATIVE_TYPE_LARGE.equalsIgnoreCase(Slave.NATIVE_LARGE_call_native)) {

                FrameLayout nativeAdsContainer = (FrameLayout) LayoutInflater.from(context)
                        .inflate(R.layout.native_ads_progress_dialog_ads_loader, null, false);
                LinearLayout linearLayout = nativeAdsContainer.findViewById(R.id.ll_progress_layout);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_large_height));

                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadNativeLarge(context, loadAdData, nativeAdsContainer);
                return nativeAdsContainer;

            } else if (Slave.NATIVE_TYPE_MEDIUM.equalsIgnoreCase(Slave.NATIVE_LARGE_call_native)) {
                return getNativeMedium(context);

            } else if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.NATIVE_LARGE_call_native)) {
                return getBannerHeader(context);
            }
        }
        return getDefaultAdView(context);
    }

    private void loadNativeLarge(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewNativeLarge(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                Log.d("AHandler ", "NewEngine getNewNativeLarge loadNativeLarge " + ll + "  " + adsView);

                addAdViewInContainer(ll, adsView);
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler ", "NewEngine getNewNativeLarge onAdFailed "
                        + pos + " " + providerName + " msg " + errorMsg + "providers list size  " + Slave.NATIVE_LARGE_providers.size());
                loadAdData.setPosition(pos);
                if (pos >= Slave.NATIVE_LARGE_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadNativeLarge(context, loadAdData, ll);
            }
        });

    }

    /**
     * show native grid ads and using native medium ads id
     */
    public View getNativeGrid(Activity context) {
        if (Slave.hasPurchased(context)) {
            return getDefaultAdView(context);
        }

        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.NATIVE_MEDIUM_start_date)) {
            if (Slave.NATIVE_TYPE_MEDIUM.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {

                LinearLayout appAdContainer = new LinearLayout(context);
                appAdContainer.setGravity(Gravity.CENTER);
                appAdContainer.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_grid_height));

                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadNativeGrid(context, loadAdData, appAdContainer);

                return appAdContainer;

            } else if (Slave.NATIVE_TYPE_LARGE.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getNativeLarge(context);

            } else if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getBannerHeader(context);

            }
        }

        return getDefaultAdView(context);
    }

    private void loadNativeGrid(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewNativeGrid(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                if (ll != null) {
                    ll.removeAllViews();
                    ll.addView(adsView);
                } else {
                    LinearLayout appAdContainer = new LinearLayout(context);
                    appAdContainer.setGravity(Gravity.CENTER);
                    appAdContainer.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_grid_height));
                    appAdContainer.removeAllViews();
                    appAdContainer.addView(adsView);
                }
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler ", "NewEngine getNewNativeGrid onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.NATIVE_MEDIUM_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadNativeGrid(context, loadAdData, ll);
            }
        });

    }

    /**
     * show native rectangle ads
     */
    public View getNativeMedium(Activity context) {
        if (Slave.hasPurchased(context) || !Utils.isNetworkConnected(context)) {
            return getDefaultAdView(context);
        }

        if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.NATIVE_MEDIUM_start_date)) {
            // Slave.NATIVE_MEDIUM_call_native = "native_large";
            if (Slave.NATIVE_TYPE_MEDIUM.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                FrameLayout nativeAdsContainer = (FrameLayout) LayoutInflater.from(context)
                        .inflate(R.layout.native_ads_progress_dialog_ads_loader, null, false);
                LinearLayout linearLayout = nativeAdsContainer.findViewById(R.id.ll_progress_layout);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_medium_height));

                LoadAdData loadAdData = new LoadAdData();
                loadAdData.setPosition(0);
                loadNativeMedium(context, loadAdData, nativeAdsContainer);

                return nativeAdsContainer;

            } else if (Slave.NATIVE_TYPE_LARGE.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getNativeLarge(context);

            } else if (Slave.TYPE_TOP_BANNER.equalsIgnoreCase(Slave.NATIVE_MEDIUM_call_native)) {
                return getBannerHeader(context);
            }
        }
        return getDefaultAdView(context);

    }

    private void loadNativeMedium(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewNativeMedium(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                addAdViewInContainer(ll, adsView);
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {

                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler ", "NewEngine getNewNativeMedium onAdFailed " + pos + " " + providerName + " msg " + errorMsg + "   " + Slave.NATIVE_MEDIUM_providers.size());
                loadAdData.setPosition(pos);
                if (pos >= Slave.NATIVE_MEDIUM_providers.size()) {
                    ll.setVisibility(View.GONE);
                }
                loadNativeMedium(context, loadAdData, ll);

            }
        });

    }


    /*
     * cache are working on launch full ads ..
     */
    private void handleLaunchCache(Activity context, OnCacheFullAdLoaded l) {
        try {
            int full_nonRepeat;
            PrintLog.print("cacheHandle >>1" + " " + DataHubConstant.APP_LAUNCH_COUNT);
            if (Slave.LAUNCH_NON_REPEAT_COUNT != null && Slave.LAUNCH_NON_REPEAT_COUNT.size() > 0) {
                for (int i = 0; i < Slave.LAUNCH_NON_REPEAT_COUNT.size(); i++) {
                    full_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_full);

                    PrintLog.print("cacheHandle >>2" + " launchCount = " + DataHubConstant.APP_LAUNCH_COUNT + " | launchAdsCount = " + full_nonRepeat);

                    if (DataHubConstant.APP_LAUNCH_COUNT == full_nonRepeat) {
                        PrintLog.print("cacheHandle >>3" + " " + full_nonRepeat);
                        cacheLaunchFullAd(context, l);
                        return;
                    }
                }
            }
            PrintLog.print("cacheHandle >>4" + " " + Slave.LAUNCH_REPEAT_FULL_ADS);
            if (Slave.LAUNCH_REPEAT_FULL_ADS != null && !Slave.LAUNCH_REPEAT_FULL_ADS.equalsIgnoreCase("")
                    && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_FULL_ADS) == 0) {
                PrintLog.print("cacheHandle >>5" + " " + Slave.LAUNCH_REPEAT_FULL_ADS);
                cacheLaunchFullAd(context, l);
            }

        } catch (Exception e) {
            PrintLog.print("cacheHandle excep ");
        }

    }

    /**
     * cache Launch full ads
     */
    private void cacheLaunchFullAd(Activity activity, OnCacheFullAdLoaded listener) {
        if (!Utils.isNetworkConnected(activity) || Slave.hasPurchased(activity)) {
            listener.onCacheFullAdFailed();
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        loadLaunchCacheFullAds(activity, loadAdData, listener);
    }

    private void loadLaunchCacheFullAds(final Activity context, final LoadAdData loadAdData, final OnCacheFullAdLoaded listener) {
        AdsHelper.getInstance().getNewLaunchCacheFullPageAd(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                // loadtofailedLaunchAds=false;
                if (listener != null) {
                    listener.onCacheFullAd();
                }
                System.out.println("NewEngine loadLaunchCacheFullAds onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {
                // loadtofailedLaunchAds=true;
                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadLaunchCacheFullAds(context, loadAdData, listener);
                Log.d("AHandler", "NewEngine loadLaunchCacheFullAds onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                System.out.println("NewEngine loadLaunchCacheFullAds onFullAdClosed");
            }
        }, listener);

    }

    /**
     * show full ads on launch
     */
    private void showFullAdsOnLaunch(Activity activity, AppFullAdsCloseListner listener) {

        if (Slave.hasPurchased(activity)) {
            listener.onFullAdClosed();
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        loadFullAdsOnLaunch(activity, loadAdData, listener);
    }

    private void loadFullAdsOnLaunch(final Activity context, final LoadAdData loadAdData, AppFullAdsCloseListner listener) {
        System.out.println("NewEngine loadFullAdsOnLaunch " + context.getLocalClassName());
        AdsHelper.getInstance().showFullAdsOnLaunch(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                System.out.println("NewEngine loadFullAdsOnLaunch onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {

                int pos = loadAdData.getPosition();
                Log.d("AHandler", "NewEngine loadFullAdsOnLaunch onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg + "   " +
                        Slave.LAUNCH_FULL_ADS_providers.size());

                pos++;
                loadAdData.setPosition(pos);
                if (pos >= Slave.LAUNCH_FULL_ADS_providers.size()) {
                    //  listener.onFullAdClosed();
                    onCloseFullAd(context, listener);
                } else {
                    loadFullAdsOnLaunch(context, loadAdData, listener);
                }

            }

            @Override
            public void onFullAdClosed() {
                Log.d("Listener Error", "NewEngine loadFullAdsOnLaunch onAdClosed. " + listener);
                // listener.onFullAdClosed();
                onCloseFullAd(context, listener);
                Log.d("Listener Error", "NewEngine loadFullAdsOnLaunch onAdClosed. 111");
            }
        });

    }



    /**
     * cache full ads
     */
    private void cacheNavigationFullAd(Activity activity) {
        System.out.println("BBB AHandler.onFullAdLoaded111..." + Slave.hasPurchased(activity));
        if (Slave.hasPurchased(activity)) {
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        loadNavigationCacheFullAds(activity, loadAdData);
    }

    private void loadNavigationCacheFullAds(final Activity context, final LoadAdData loadAdData) {

        //  System.out.println("BBB AHandler.onFullAdLoaded2222"+" "+ loadtofailedLaunchAds);

        AdsHelper.getInstance().getNewNavCacheFullPageAd(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                System.out.println("BBB AHandler.onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadNavigationCacheFullAds(context, loadAdData);
                Log.d("AHandler", "NewEngine loadNavigationCacheFullAds onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                System.out.println("BBB AHandler.onFullAdClosed");
            }
        });

    }

    /**
     * show full ads forcefully or not depend on isForced boolean
     */
    public void showFullAds(Activity activity, boolean isForced) {
        if (Slave.hasPurchased(activity)) {
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        Log.d("AHandler", " NewEngine showFullAds getFullAdsCount "
                + Utils.getFullAdsCount(activity) + " FULL_ADS_nevigation " + Utils.getStringtoInt(Slave.FULL_ADS_nevigation)
                + activity.getLocalClassName());

        if (Utils.getDaysDiff(activity) >= Utils.getStringtoInt(Slave.FULL_ADS_start_date)) {
            Utils.setFullAdsCount(activity, -1);
            System.out.println("Full Nav Adder setter >>> " + Utils.getFullAdsCount(activity));

            if (!isForced) {
                if (Utils.getFullAdsCount(activity) >= Utils.getStringtoInt(Slave.FULL_ADS_nevigation)) {
                    Utils.setFullAdsCount(activity, 0);
                    System.out.println("Full Nav Adder setter >>> 1 " + Utils.getFullAdsCount(activity));
                    loadFullAds(activity, loadAdData);
                }
            } else {
                loadForceFullAds(activity, loadAdData);
            }
        }
    }

    /**
     * load force full ads
     */
    private void loadForceFullAds(final Activity context, final LoadAdData loadAdData) {
        AdsHelper.getInstance().showForcedFullAds(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadForceFullAds(context, loadAdData);
                Log.d("AHandler", "NewEngine loadForceFullAds onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {

            }
        });

    }

    /**
     * load full ads
     */
    private void loadFullAds(final Activity context, final LoadAdData loadAdData) {
        AdsHelper.getInstance().showFullAds(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                Log.d("AHandler", "NewEngine  showFullAds onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {

                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadFullAds(context, loadAdData);
                Log.d("AHandler", "NewEngine  showFullAds onFullAdFailed " + loadAdData.getPosition() + " " + adsEnum.name() + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                Log.d("AHandler", "NewEngine  showFullAds onFullAdClosed");
            }
        });
    }


    /**
     * cache rewarded ads
     */
    private void cacheNavigationRewardedAds(Activity activity) {
        if (Slave.hasPurchased(activity)) {
            return;
        }
        if (Slave.REWARDED_VIDEO_status.equals("true")) {
            LoadAdData loadAdData = new LoadAdData();
            loadAdData.setPosition(0);
            loadNavigationCacheRewardedAds(activity, loadAdData);
        }
    }

    private void loadNavigationCacheRewardedAds(final Activity context,
                                                final LoadAdData loadAdData) {
        AdsHelper.getInstance().getNewNavCacheRewardedAds(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                System.out.println("AHandler.loadNavigationCacheRewardedAds");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadNavigationCacheRewardedAds(context, loadAdData);
                Log.d("AHandler", "NewEngine loadNavigationCacheRewardedAds onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                System.out.println("AHandler.onFullAdClosed");
            }
        });

    }

    /**
     * show rewarded video..
     */
    public void showRewardedVideoOrFullAds(Activity activity, boolean forceFull, OnRewardedEarnedItem onRewardedEarnedItem) {
        if (Slave.hasPurchased(activity)) {
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);

        Log.d("AHandler", " NewEngine showRewardedVideo getAdsCount " + Utils.getFullAdsCount(activity)
                + " REWARDED_VIDEO_nevigation " + Utils.getStringtoInt(Slave.REWARDED_VIDEO_nevigation));

        if (Slave.REWARDED_VIDEO_status.equals("true")
                && Utils.getDaysDiff(activity) >= Utils.getStringtoInt(Slave.REWARDED_VIDEO_start_date)) {
            Utils.setFullAdsCount(activity, -1);

            if (Utils.getFullAdsCount(activity) >= Utils.getStringtoInt(Slave.REWARDED_VIDEO_nevigation)) {
                Utils.setFullAdsCount(activity, 0);
                loadRewardedAds(activity, loadAdData, onRewardedEarnedItem);
            }

        } else {
            showFullAds(activity, forceFull);
        }
    }

    /**
     * load rewarded ads
     */
    private void loadRewardedAds(final Activity context, final LoadAdData loadAdData, OnRewardedEarnedItem onRewardedEarnedItem) {
        AdsHelper.getInstance().showRewardedAds(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                Log.d("AHandler", "NewEngine  loadRewardedAds onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {

                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadRewardedAds(context, loadAdData, onRewardedEarnedItem);
                Log.d("AHandler", "NewEngine  loadRewardedAds onFullAdFailed " + loadAdData.getPosition() + " " + adsEnum.name() + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                Log.d("AHandler", "NewEngine  loadRewardedAds onFullAdClosed");
            }
        }, onRewardedEarnedItem);
    }

    /**
     * show cp ads on start
     */
    private void showCPStart(Activity context) {
        if (!Slave.hasPurchased(context)) {
            PrintLog.print("ding check inside 3 cp start");
            if (Slave.CP_is_start.equals(Slave.CP_YES)) {
                if (Utils.isPackageInstalled(Slave.CP_package_name, context)) {
                    PrintLog.print("ding check inside 4 cp start" + Slave.CP_startday);
                    if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.CP_startday)) {
                        PrintLog.print("ding check inside 5 cp start");

                        if (Utils.isNetworkConnected(context)) {
                            PrintLog.print("ding check inside 6 cp start");
                            Intent intent = new Intent(context, FullPagePromo.class);
                            intent.putExtra("src", Slave.CP_camp_img);
                            intent.putExtra("type", EngineClient.IH_CP_START);
                            intent.putExtra("link", Slave.CP_camp_click_link);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    /**
     * show cp ads on exit
     */
    private void showCPExit(Activity context) {
        if (!Slave.hasPurchased(context)) {
            if (Slave.CP_is_exit.equals(Slave.CP_YES)) {
                if (Utils.isPackageInstalled(Slave.CP_package_name, context)) {
                    if (Utils.getDaysDiff(context) >= Utils.getStringtoInt(Slave.CP_startday)) {

                        if (Utils.isNetworkConnected(context)) {

                            Intent intent = new Intent(context, FullPagePromo.class);
                            intent.putExtra("type", EngineClient.IH_CP_EXIT);
                            intent.putExtra("src", Slave.CP_camp_img);
                            intent.putExtra("link", Slave.CP_camp_click_link);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    public void handle_launch_For_FullAds(Activity context, AppFullAdsCloseListner listener) {
        PrintLog.print("handle launch trans prompt full ads " + " " + DataHubConstant.APP_LAUNCH_COUNT + " " + Slave.LAUNCH_REPEAT_FULL_ADS);

        int full_nonRepeat;
        new GCMPreferences(context).setTransLaunchName(context.getClass().getName());
        if (Slave.LAUNCH_NON_REPEAT_COUNT != null && Slave.LAUNCH_NON_REPEAT_COUNT.size() > 0) {
            for (int i = 0; i < Slave.LAUNCH_NON_REPEAT_COUNT.size(); i++) {
                full_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_full);

                PrintLog.print("handle launch trans fullads " + " " + DataHubConstant.APP_LAUNCH_COUNT + " " + full_nonRepeat);
                if (DataHubConstant.APP_LAUNCH_COUNT == full_nonRepeat) {
                    PrintLog.print("handle launch trans fullads non repeat..");
                    showFullAdsOnLaunch(context, listener);
                    return;
                }
            }
        }
        PrintLog.print("handle launch trans prompt repease" + " " + DataHubConstant.APP_LAUNCH_COUNT + " " + Slave.LAUNCH_REPEAT_FULL_ADS);
        if (Slave.LAUNCH_REPEAT_FULL_ADS != null && !Slave.LAUNCH_REPEAT_FULL_ADS.equalsIgnoreCase("") && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_FULL_ADS) == 0) {
            PrintLog.print("handle launch trans fullads repeat..");
            showFullAdsOnLaunch(context, listener);
            return;
        }

        listener.onFullAdClosed();
    }

    private void handle_launch_prompt(Context context) {
        int rate_nonRepeat, cp_nonRepeat, full_nonRepeat, removeads_nonRepeat;


        if (Slave.LAUNCH_NON_REPEAT_COUNT != null && Slave.LAUNCH_NON_REPEAT_COUNT.size() > 0) {
            for (int i = 0; i < Slave.LAUNCH_NON_REPEAT_COUNT.size(); i++) {
                rate_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_rate);
                cp_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_exit);
                full_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_full);
                removeads_nonRepeat = Utils.getStringtoInt(Slave.LAUNCH_NON_REPEAT_COUNT.get(i).launch_removeads);

                PrintLog.print("handle launch count " + " " +
                        DataHubConstant.APP_LAUNCH_COUNT + " " + rate_nonRepeat + " " + cp_nonRepeat + " " +
                        full_nonRepeat + " " + removeads_nonRepeat);
                if (DataHubConstant.APP_LAUNCH_COUNT == rate_nonRepeat) {
                    PrintLog.print("handle launch prompt inside 1 rate");
                    if (promptHander == null) {
                        promptHander = new PromptHander();
                    }

                    promptHander.rateUsDialog(false, (Activity) context);

                    return;
                } else if (DataHubConstant.APP_LAUNCH_COUNT == cp_nonRepeat) {
                    PrintLog.print("handle launch prompt ding check inside 2 cp start");
                    showCPStart((Activity) context);
                    return;
                } /*else if (DataHubConstant.APP_LAUNCH_COUNT == full_nonRepeat) {
                    PrintLog.print("handle launch prompt inside 3 fullads");
                    //showFullAdsOnLaunch((Activity) context, false, "non repeat");
                    showFullAdsOnLaunch((Activity) context);
                    return;
                }*/ else if (DataHubConstant.APP_LAUNCH_COUNT == removeads_nonRepeat) {
                    PrintLog.print("handle launch prompt inside 4 removeads");
                    showRemoveAdsPrompt(context);
                    return;
                }
            }
        }
        PrintLog.print("handle launch prompt repease" + " " + DataHubConstant.APP_LAUNCH_COUNT + " "
                + Slave.LAUNCH_REPEAT_FULL_ADS + "  " + Slave.LAUNCH_REPEAT_EXIT + "  " + Slave.LAUNCH_REPEAT_RATE);
        /*if (Slave.LAUNCH_REPEAT_FULL_ADS != null && !Slave.LAUNCH_REPEAT_FULL_ADS.equalsIgnoreCase("") && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_FULL_ADS) == 0) {
            PrintLog.print("handle launch prompt inside 13 fullads");
            // showFullAdsOnLaunch((Activity) context, false, "repeat");
            showFullAdsOnLaunch((Activity) context);
        } else */
        if (Slave.LAUNCH_REPEAT_EXIT != null && !Slave.LAUNCH_REPEAT_EXIT.equalsIgnoreCase("") && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_EXIT) == 0) {
            PrintLog.print("handle launch prompt inside 12 cp exit");
            showCPStart((Activity) context);
        } else if (Slave.LAUNCH_REPEAT_RATE != null && !Slave.LAUNCH_REPEAT_RATE.equalsIgnoreCase("") && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_RATE) == 0) {
            PrintLog.print("handle launch prompt inside 11 rate");
            if (promptHander == null) {
                promptHander = new PromptHander();
            }

            promptHander.rateUsDialog(false, (Activity) context);

        } else if (Slave.LAUNCH_REPEAT_REMOVEADS != null && !Slave.LAUNCH_REPEAT_REMOVEADS.equalsIgnoreCase("") && DataHubConstant.APP_LAUNCH_COUNT % Utils.getStringtoInt(Slave.LAUNCH_REPEAT_REMOVEADS) == 0) {
            PrintLog.print("handle launch prompt inside 14 removeads");
            showRemoveAdsPrompt(context);
        }
    }



    /**
     * @param context mapper class handeling
     */
    private void callingForMapper(Activity context) {
        Intent intent = context.getIntent();
        String type = intent.getStringExtra(MapperUtils.keyType);
        String value = intent.getStringExtra(MapperUtils.keyValue);
        System.out.println("AHandler.callingForMapper " + type + " " + value);
        try {
            if (type != null && value != null) {
                if (type.equalsIgnoreCase("url")) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    builder.addDefaultShareMenuItem();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(context, Uri.parse(value));

                } else if (type.equalsIgnoreCase("deeplink")) {
                    switch (value) {
                        case MapperUtils.gcmMoreApp:
                            //Remember to add here your gcmMoreApp.
                            new Utils().moreApps(context);

                            break;
                        case MapperUtils.gcmRateApp:
                            //Remember to add here your RareAppClass.
                            new PromptHander().rateUsDialog(true, context);

                            break;
                        case MapperUtils.gcmRemoveAds:
                            //Remember to add here your RemoveAdClass.
                            showRemoveAdsPrompt(context);
                            break;
                        case MapperUtils.gcmFeedbackApp:
                            new Utils().showFeedbackPrompt(context, "Please share your valuable feedback.");
                            break;
                        case MapperUtils.gcmShareApp:
                            //Remember to add here your ShareAppClass.
                            new Utils().showSharePrompt(context, "Share this cool & fast performance app with friends & family");
                            break;
                        case MapperUtils.gcmForceAppUpdate:
                            new Utils().showAppUpdatePrompt(context);
                            break;

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("AHandler.callingForMapper excep " + e.getMessage());
        }
    }


    public void callingForDeeplinking(Activity context , String value){
        switch (value) {
            case MapperUtils.gcmMoreApp:
                //Remember to add here your gcmMoreApp.
                new Utils().moreApps(context);

                break;
            case MapperUtils.gcmRateApp:
                //Remember to add here your RareAppClass.
                new PromptHander().rateUsDialog(true, context);

                break;
            case MapperUtils.gcmRemoveAds:
                //Remember to add here your RemoveAdClass.
                showRemoveAdsPrompt(context);
                break;
            case MapperUtils.gcmFeedbackApp:
                new Utils().showFeedbackPrompt(context, "Please share your valuable feedback.");
                break;
            case MapperUtils.gcmShareApp:
                //Remember to add here your ShareAppClass.
                new Utils().showSharePrompt(context, "Share this cool & fast performance app with friends & family");
                break;
            case MapperUtils.gcmForceAppUpdate:
                new Utils().showAppUpdatePrompt(context);
                break;

        }
    }

    public ArrayList<MoreFeature> getMoreFeatures() {
        return MoreFeatureResponseHandler.getInstance().getMoreFeaturesListResponse();
    }

    public void onAHandlerDestroy() {
        AdsHelper.getInstance().onAHandlerDestroy();
    }

    private int getMinBannerHeight(Context context, int minBannerHight) {
        if (mMinBannerHeight == -1) {
            mMinBannerHeight = context.getResources().getDimensionPixelOffset(minBannerHight);
        }
        return mMinBannerHeight;
    }

    private int getMinNativeHeight(Context context, int minHight) {
        if (mMinBannerLargeHeight == -1) {
            mMinBannerLargeHeight = context.getResources().getDimensionPixelOffset(minHight);
        }
        return mMinBannerLargeHeight;
    }

    private View getDefaultAdView(Context context) {
        return new LinearLayout(context);
    }

    private void addAdViewInContainer(final ViewGroup ll, View adsView) {
        if (ll != null) {

            //Hiding the Progress
            LinearLayout ll_progress_layout = ll.findViewById(R.id.ll_progress_layout);
            if (ll_progress_layout != null) {
                ll_progress_layout.setVisibility(View.GONE);
            }

            //Adding the Native Ad in ads container
            LinearLayout ll_banner_native_layout = ll.findViewById(R.id.ll_banner_native_layout);
            if (ll_banner_native_layout != null) {
                ll_banner_native_layout.removeAllViews();
                ll_banner_native_layout.addView(adsView);
            }
        }
    }

    private void onCloseFullAd(Activity activity, AppFullAdsCloseListner appFullAdsCloseListner) {
        Log.d("Listener Error", "Error in onCloseFullAd 0000000000000000    ");
        Log.d("Listener Error", "Error in onCloseFullAd activity = " + activity + ", appFullAdsCloseListner = " + appFullAdsCloseListner);
        if (activity == null || appFullAdsCloseListner == null) {
            return;
        }

        EngineAppApplication engineAppApplication;
        if (activity.getApplication() instanceof EngineAppApplication) {
            engineAppApplication = (EngineAppApplication) activity.getApplication();
            engineAppApplication.addAppForegroundStateListener(() -> {
                if (appFullAdsCloseListner != null) {
                    appFullAdsCloseListner.onFullAdClosed();
                }
            });
        } else {
            if (appFullAdsCloseListner != null) {
                appFullAdsCloseListner.onFullAdClosed();
            }
        }
    }


    public boolean isGameShow() {
        return Slave.game_ads_responce_show_status != null && !Slave.game_ads_responce_show_status.equalsIgnoreCase("")
                && Slave.game_ads_responce_show_status.equalsIgnoreCase("true");
    }

    public boolean is_game_title() {
        return Slave.game_ads_responce_title != null && !Slave.game_ads_responce_title.equalsIgnoreCase("");
    }

    public boolean is_game_sub_title() {
        return Slave.game_ads_responce_sub_title != null && !Slave.game_ads_responce_sub_title.equalsIgnoreCase("");
    }

    public boolean is_game_icon() {
        return Slave.game_ads_responce_icon != null && !Slave.game_ads_responce_icon.equalsIgnoreCase("");
    }

    public boolean is_game_click_link() {
        return Slave.game_ads_responce_Link != null && !Slave.game_ads_responce_Link.equalsIgnoreCase("");
    }

    public boolean is_page_id(String pageid) {
        if (Slave.getGame_ads_responce_position_name != null && !Slave.getGame_ads_responce_position_name.equalsIgnoreCase("")) {

            return Slave.getGame_ads_responce_position_name.equalsIgnoreCase(pageid);

        }
        return false;
    }


    public boolean isViewTypeGame() {
        return Slave.getGame_ads_responce_view_type_game != null && !Slave.getGame_ads_responce_view_type_game.equalsIgnoreCase("");
    }


    public ArrayList<GameProvidersResponce> getGameServiceResponce() {
        return GameServiceV2ResponseHandler.getInstance().getGameV2FeaturesListResponse();
    }


    public void getGameServicesSlaveValue(String pageId) {
        if (getGameServiceResponce() != null && getGameServiceResponce().size() > 0) {
            for (int i = 0; i < getGameServiceResponce().size(); i++) {
                if (getGameServiceResponce().get(i).position_name.equalsIgnoreCase(pageId)) {
                    PrintLog.print("0555 checking Type Top Bannergameservices 0012 game provider ff");
                    //Slave.TOP_BANNER_provider_id = list.get(i).provider_id;
                    //Slave.TOP_BANNER_ad_id = list.get(i).ad_id;
                    Slave.game_ads_responce_show_status = getGameServiceResponce().get(i).show_status;
                    Slave.game_ads_responce_provider = getGameServiceResponce().get(i).provider;
                    Slave.getGame_ads_responce_position_name = getGameServiceResponce().get(i).position_name;
                    Slave.game_ads_responce_title = getGameServiceResponce().get(i).title;
                    Slave.game_ads_responce_sub_title = getGameServiceResponce().get(i).sub_title;
                    Slave.game_ads_responce_icon = getGameServiceResponce().get(i).icon;
                    Slave.game_ads_responce_Link = getGameServiceResponce().get(i).link;
                    Slave.game_ads_responce_button_text = getGameServiceResponce().get(i).button_text;
                    Slave.game_ads_responce_button_bg_color = getGameServiceResponce().get(i).button_bg_color;
                    Slave.game_ads_responce_button_text_color = getGameServiceResponce().get(i).button_text_color;
                    Slave.getGame_ads_responce_view_type_game = getGameServiceResponce().get(i).view_type_game;
                    Slave.getGame_ads_responce_page_id = getGameServiceResponce().get(i).pageid;


                }
            }
        }

    }


    /**
     * cache open ads
     */
    private void initCacheOpenAds(Activity activity) {
        if (Slave.hasPurchased(activity)) {
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        loadCacheOpenAds(activity, loadAdData);
    }

    private void loadCacheOpenAds(final Activity context,
                                  final LoadAdData loadAdData) {
        AdsHelper.getInstance().getAppOpenAdsCache(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                System.out.println("AHandler.loadNavigationCacheOpenAds");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadCacheOpenAds(context, loadAdData);
                Log.d("AHandler", "NewEngine loadNavigationCacheOpenAds onAdFailed " + pos + " " + adsEnum + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                System.out.println("AHandler.onFullAdClosed");
            }
        });

    }

    /**
     * show App Open Ads..
     */
    public void showAppOpenAds(Activity activity, AppFullAdsListener listener) {
        if (Slave.hasPurchased(activity)) {
            return;
        }
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);

        Log.d("AHandler", " NewEngine showAppOpenAds getAdsCount " + Utils.getOpenAdsCount_start(activity)
                + " APP_OPEN_ADS_nevigation " + Utils.getStringtoInt(Slave.APP_OPEN_ADS_nevigation));
        if (Utils.getDaysDiff(activity) >= Utils.getStringtoInt(Slave.APP_OPEN_ADS_start_date)) {
            Utils.setOpenAdsCount_start(activity, -1);

            if (Utils.getOpenAdsCount_start(activity) >= Utils.getStringtoInt(Slave.APP_OPEN_ADS_nevigation)) {
                Utils.setOpenAdsCount_start(activity, 0);
                loadAppOpenAds(activity, loadAdData, listener);
            }
        }
    }

    /**
     * load App Open Ads..
     */
    private void loadAppOpenAds(final Activity context, final LoadAdData loadAdData, AppFullAdsListener listener) {
        AdsHelper.getInstance().showAppOpenAds(context, loadAdData.getPosition(), new AppFullAdsListener() {
            @Override
            public void onFullAdLoaded() {
                if (listener != null) {
                    listener.onFullAdLoaded();
                }
                Log.d("AHandler", "NewEngine  loadAppOpenAds onFullAdLoaded");
            }

            @Override
            public void onFullAdFailed(AdsEnum adsEnum, String errorMsg) {

                int pos = loadAdData.getPosition();
                pos++;
                loadAdData.setPosition(pos);
                loadAppOpenAds(context, loadAdData, listener);
                if (listener != null) {
                    listener.onFullAdFailed(adsEnum, errorMsg);
                }
                Log.d("AHandler", "NewEngine  loadAppOpenAds onFullAdFailed " + loadAdData.getPosition() + " " + adsEnum.name() + " msg " + errorMsg);
            }

            @Override
            public void onFullAdClosed() {
                if (listener != null) {
                    listener.onFullAdClosed();
                }
                Log.d("AHandler", "NewEngine  loadAppOpenAds onFullAdClosed");
            }
        });
    }

    private void bannerRactangleLoading(Activity context, boolean isFromCaching) {
        appAdContainer = (FrameLayout) LayoutInflater.from(context)
                .inflate(R.layout.native_ads_progress_dialog_ads_loader, null, false);
        LinearLayout linearLayout = appAdContainer.findViewById(R.id.ll_progress_layout);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_rect_height));
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        loadBannerRectangle(context, loadAdData, appAdContainer, isFromCaching);
    }

    private void initCacheBannerRect(Activity context){
        appAdContainer = (FrameLayout) LayoutInflater.from(context)
                .inflate(R.layout.native_ads_progress_dialog_ads_loader, null, false);
        LinearLayout linearLayout = appAdContainer.findViewById(R.id.ll_progress_layout);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setMinimumHeight(getMinNativeHeight(context, R.dimen.native_rect_height));
        LoadAdData loadAdData = new LoadAdData();
        loadAdData.setPosition(0);
        cacheBannerRectangle(context, loadAdData, appAdContainer);
    }

    private void cacheBannerRectangle(final Activity context, final LoadAdData loadAdData, final ViewGroup ll) {
        AdsHelper.getInstance().getNewBannerRectangle(context, loadAdData.getPosition(), new AppAdsListener() {
            @Override
            public void onAdLoaded(View adsView) {
                bannerRactangleCaching = new BannerRactangleCaching(context);
                bannerRactangleCaching.removeAllViews();
                bannerRactangleCaching.addView(adsView);
                ll.removeAllViews();
                ll.addView(bannerRactangleCaching);
            }

            @Override
            public void onAdFailed(AdsEnum providerName, String errorMsg) {
                int pos = loadAdData.getPosition();
                pos++;
                Log.d("AHandler", "NewEngine getNewBannerRectangle onAdFailed " + pos + " " + providerName + " msg " + errorMsg);
                loadAdData.setPosition(pos);
                if (pos >= Slave.RECTANGLE_BANNER_providers.size()) {
                }
                cacheBannerRectangle(context, loadAdData, ll);
            }
        });
    }

    public void onCallFullAdsBindWithEtc(Activity activity){
        if(Slave.ETC_5.equals("1")){
            showFullAds(activity, true);
        }else {
            showFullAds(activity, false);
        }
    }
    public void parseValueOfETC_3() {
        try {
            if (Slave.ETC_3 != null && !Slave.ETC_3.equals("") && Slave.ETC_3.length() > 0) {
                if (Slave.ETC_3.contains("#")) {
                    String[] str = Slave.ETC_3.split("#");
                    if (str[0] != null) {
                        ShowBillingPage = str[0];
                    }
                    if (str[1] != null) {
                        SplashBillingpageCount = Integer.parseInt(str[1]);
                    }
                } else {
                    ShowBillingPage = "2";
                    SplashBillingpageCount = 0;
                }
            }else {
                ShowBillingPage = "2";
                SplashBillingpageCount = 0;
            }
        } catch (Exception e) {
            ShowBillingPage = "2";
            SplashBillingpageCount = 0;
            e.printStackTrace();
        }
        System.out.println("AppUtils.parseValueOfETC_3..."+Slave.ETC_3+"  "+SplashBillingpageCount + " " + ShowBillingPage);
    }
}
