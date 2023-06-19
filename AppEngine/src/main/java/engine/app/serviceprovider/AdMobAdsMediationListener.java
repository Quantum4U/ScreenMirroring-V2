package engine.app.serviceprovider;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.Objects;

import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;

public class AdMobAdsMediationListener extends AdListener {

    private final AdView mAdView;
    private final AppAdsListener mAppAdListener;

    AdMobAdsMediationListener(AdView mAdView, AppAdsListener mOnAppAdListener) throws Exception {
        this.mAdView = mAdView;
        this.mAppAdListener = mOnAppAdListener;

        if (mAdView == null || mOnAppAdListener == null) {
            throw new Exception("AdView and AppAdsListener cannot be null ");
        }
    }


    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        Log.d("AdMobMediation","NewEngine getNewBannerHeader Mediation  Banner adapter class name: " + Objects.requireNonNull(mAdView.getResponseInfo()).getMediationAdapterClassName());
        mAppAdListener.onAdLoaded(mAdView);
    }

    @Override
    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
        super.onAdFailedToLoad(loadAdError);
        mAppAdListener.onAdFailed(AdsEnum.ADS_ADMOB, loadAdError.getMessage());
    }

    @Override
    public void onAdImpression() {
        super.onAdImpression();
    }

    @Override
    public void onAdClicked() {
        super.onAdClicked();
        // Code to be executed when the user clicks on an ad.
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        // Code to be executed when the user is about to return
        // to the app after tapping on an ad.
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
    }
}
