//package engine.app.serviceprovider;
//
//import com.huawei.hms.ads.AdListener;
//import com.huawei.hms.ads.banner.BannerView;
//
//import engine.app.enginev4.AdsEnum;
//import engine.app.listener.AppAdsListener;
//
//class HmsAdsListener extends AdListener {
//    private final BannerView mAdView;
//    private final AppAdsListener mAppAdListener;
//
//    HmsAdsListener(BannerView mAdView, AppAdsListener mOnAppAdListener) throws Exception {
//        this.mAdView = mAdView;
//        this.mAppAdListener = mOnAppAdListener;
//
//        if (mAdView == null || mOnAppAdListener == null) {
//            throw new Exception("AdView and AppAdsListener cannot be null ");
//        }
//    }
//    @Override
//    public void onAdLoaded() {
//        super.onAdLoaded();
//        mAppAdListener.onAdLoaded(mAdView);
//    }
//
//    @Override
//    public void onAdClicked() {
//        super.onAdClicked();
//    }
//
//    @Override
//    public void onAdClosed() {
//        super.onAdClosed();
//    }
//
//    @Override
//    public void onAdFailed(int errorCode) {
//        super.onAdFailed(errorCode);
//        mAppAdListener.onAdFailed(AdsEnum.ADS_HCM, String.valueOf(errorCode));
//
//    }
//
//    @Override
//    public void onAdImpression() {
//        super.onAdImpression();
//    }
//
//    @Override
//    public void onAdOpened() {
//        super.onAdOpened();
//    }
//
//    @Override
//    public void onAdLeave() {
//        super.onAdLeave();
//    }
//}
