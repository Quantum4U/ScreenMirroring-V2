package engine.app.listener;

import java.io.Serializable;

import engine.app.enginev4.AdsEnum;

/**
 * Created by Meenu Singh on 12/06/19.
 */
public interface AppFullAdsListener extends Serializable {

    void onFullAdLoaded();

    void onFullAdFailed(AdsEnum adsEnum, String errorMsg);

    void onFullAdClosed();

}
