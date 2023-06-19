package engine.app.listener;


import android.view.View;

import engine.app.enginev4.AdsEnum;

public interface AppAdsListener {

    void onAdLoaded(View adsView);

    void onAdFailed(AdsEnum adsEnum, String errorMsg);

}
