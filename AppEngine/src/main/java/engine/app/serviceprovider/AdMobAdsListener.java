package engine.app.serviceprovider;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;

/**
 * Created by Meenu Singh on 10/06/19.
 */
public class AdMobAdsListener extends AdListener {

    private final AdView mAdView;
    private final AppAdsListener mAppAdListener;

    AdMobAdsListener(AdView mAdView, AppAdsListener mOnAppAdListener) throws Exception {
        this.mAdView = mAdView;
        this.mAppAdListener = mOnAppAdListener;

        if (mAdView == null || mOnAppAdListener == null) {
            throw new Exception("AdView and AppAdsListener cannot be null ");
        }
    }


    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
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


    /* private LinearLayout getAdViewLayout() {
        final LinearLayout linLayout = new LinearLayout(mAdView.getContext());
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // creating LayoutParams
        ViewGroup.LayoutParams linLayoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linLayout.setGravity(Gravity.CENTER);
        linLayout.setLayoutParams(linLayoutParam);

        linLayout.addView(mAdView);
        return linLayout;
    }*/
}
