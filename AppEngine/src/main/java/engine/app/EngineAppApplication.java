package engine.app;


import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.AudienceNetworkAds;

import engine.app.listener.AppForegroundStateListener;


public class EngineAppApplication extends MultiDexApplication {
    private EngineActivityCallback engineActivityCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        // AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CALLBACK_MODE);
        AudienceNetworkAds.initialize(this);

        //Applovin Ads mediation
        AppLovinSdk.getInstance(this).initializeSdk();

        // HMS Initialize the Ads SDK.
      //  HwAds.init(this);


        //This will help to know your activity lifecycle.
        if (engineActivityCallback == null) {
            engineActivityCallback = new EngineActivityCallback();
            registerActivityLifecycleCallbacks(engineActivityCallback);
            ProcessLifecycleOwner.get()
                    .getLifecycle()
                    .addObserver(engineActivityCallback);
        }

    }


    public boolean isAppInForeground() {
        return engineActivityCallback.isAppInForeground();
    }

    public void addAppForegroundStateListener(AppForegroundStateListener appForegroundStateListener) {
        if (engineActivityCallback != null) {
            engineActivityCallback.addAppForegroundStateListener(appForegroundStateListener);
        }
    }
}
