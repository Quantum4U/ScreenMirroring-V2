package engine.app.serviceprovider;

import android.util.Log;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinAdViewEventListener;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;

import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;

/**
 * Created by Meenu Singh on 19/06/19.
 */
public class AppLovinAdsListener implements AppLovinAdLoadListener,
        AppLovinAdDisplayListener , AppLovinAdClickListener {

    private final AppLovinAdView mAdView;
    private final AppAdsListener mAppAdListener;

    AppLovinAdsListener(AppLovinAdView mAdView, AppAdsListener mOnAppAdListener) throws Exception {
        this.mAdView = mAdView;
        this.mAppAdListener = mOnAppAdListener;
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 333" + mAdView+"  "+mOnAppAdListener);

        if (mAdView == null || mOnAppAdListener == null) {
            throw new Exception("AdView and AppAdsListener cannot be null ");
        }
    }

    @Override
    public void adReceived(AppLovinAd ad) {
        //LinearLayout linearLayout = getAdViewLayout();
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 444" + ad);

        mAppAdListener.onAdLoaded(mAdView);

    }

    @Override
    public void failedToReceiveAd(int errorCode) {
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 555" +  String.valueOf(errorCode));

        mAppAdListener.onAdFailed(AdsEnum.ADS_APPLOVIN, String.valueOf(errorCode));
    }

    @Override
    public void adDisplayed(AppLovinAd ad) {
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 7777" + ad);

    }

    @Override
    public void adHidden(AppLovinAd ad) {
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 8888" + ad);

    }

    @Override
    public void adClicked(AppLovinAd ad) {
        Log.d("TAG", "NewEngine getNewBannerHeader applovin 999" + ad);

    }
}
