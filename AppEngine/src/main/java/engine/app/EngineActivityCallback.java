package engine.app;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import com.ironsource.mediationsdk.IronSource;

import engine.app.adshandler.AHandler;
import engine.app.listener.AppForegroundStateListener;
import engine.app.openads.AppOpenAdsHandler;

/**
 * Created by Meenu Singh on 21/01/2021.
 */
public class EngineActivityCallback implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private boolean isAppInForeground = false;
    private boolean showAppOpenAds = false;
    private Activity currentActivity;
    private Handler appForegroundHandler;
    private Handler activityResumeHandler;
    private ActivityResumeRunnable mActivityResumeRunnable;
    private AppForegroundRunnable mAppForegroundRunnable;
    private List<AppForegroundStateListener> mAppForegroundStateListenerList;
    public static final String ACTION_CALL_PASSWORD_PAGE = "Action_Call_Password_Page";

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onForeground() {

        isAppInForeground = true;
        showAppOpenAds = true;
        Log.d("EngineActivityCallback", "Hello onForeground  onForground ");

        /**
         * This needs to be called before {@link AppForegroundRunnable}
         */
        notifyAppForegroundStateListener();

        mAppForegroundRunnable = new AppForegroundRunnable(new WeakReference(this), new WeakReference(currentActivity));
        appForegroundHandler = new Handler();
        appForegroundHandler.postDelayed(mAppForegroundRunnable, 200);

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onBackground() {
        isAppInForeground = false;
        Log.d("EngineActivityCallback", "Hello onForeground  Background ");
        try {
            Intent intent = new Intent("custom-event-meditation-app-is-in-background");
            LocalBroadcastManager.getInstance(currentActivity).sendBroadcast(intent);
        }catch (Exception e){}
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {


    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityPreResumed " + activity);
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityResumed " + activity + " showAppOpenAds - " + showAppOpenAds);
        currentActivity = activity;
        if (appForegroundHandler != null && mAppForegroundRunnable != null)
            appForegroundHandler.removeCallbacks(mAppForegroundRunnable);

        if (isAppInForeground && showAppOpenAds) {
            if (activityResumeHandler == null)
                activityResumeHandler = new Handler();

            mActivityResumeRunnable = new ActivityResumeRunnable(new WeakReference(this), new WeakReference(activity));
            activityResumeHandler.postDelayed(mActivityResumeRunnable, 300);
        }

        //for mediation
        IronSource.onResume(activity);

    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityPostResumed " + activity + " showAppOpenAds - " + showAppOpenAds);
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityPrePaused " + activity);

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityPaused " + activity);
//        showAppOpenAds = false;

        //for mediation
        IronSource.onPause(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("EngineActivityCallback", "Hello onActivityStopped " + activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        AHandler.getInstance().onAHandlerDestroy();
        Log.d("EngineActivityCallback", "Hello onActivityDestroyed " + activity);
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
    }

    public boolean isAppInForeground() {
        return isAppInForeground;
    }

    private void notifyAppForegroundStateListener() {
        if (mAppForegroundStateListenerList != null) {
            for (AppForegroundStateListener appForegroundStateListener : mAppForegroundStateListenerList) {
                if (appForegroundStateListener != null) {
                    appForegroundStateListener.onAppForeground();
                }
            }
            mAppForegroundStateListenerList.clear();
        }

        mAppForegroundStateListenerList = null;
    }

    public void addAppForegroundStateListener(AppForegroundStateListener appForegroundStateListener) {
        if (appForegroundStateListener == null)
            return;

        if (isAppInForeground()) {
            if (appForegroundStateListener != null) {
                appForegroundStateListener.onAppForeground();
            }
        } else {
            if (mAppForegroundStateListenerList == null) {
                mAppForegroundStateListenerList = new ArrayList<>();
            }

            mAppForegroundStateListenerList.add(appForegroundStateListener);
        }
    }

    private static class AppForegroundRunnable implements Runnable {


        private final WeakReference<EngineActivityCallback> engineAppApplicationWeakReference;
        private final WeakReference<Activity> mWeakReference;

        private AppForegroundRunnable(WeakReference<EngineActivityCallback> engineAppApplicationWeakReference, WeakReference<Activity> mWeakReference) {
            this.engineAppApplicationWeakReference = engineAppApplicationWeakReference;
            this.mWeakReference = mWeakReference;
        }


        @Override
        public void run() {
            Log.d("EngineActivityCallback", "Hello appForegroundRunnable - run ");
            engineAppApplicationWeakReference.get().showAppOpenAds = false;
        }
    }

    private static class ActivityResumeRunnable implements Runnable {


        private final WeakReference<EngineActivityCallback> engineAppApplicationWeakReference;
        private final WeakReference<Activity> mWeakReference;

        private ActivityResumeRunnable(WeakReference<EngineActivityCallback> engineAppApplicationWeakReference, WeakReference<Activity> mWeakReference) {
            Log.d("EngineActivityCallback", "Hello ActivityResumeRunnable - constructor - " + mWeakReference.get());
            this.engineAppApplicationWeakReference = engineAppApplicationWeakReference;
            this.mWeakReference = mWeakReference;
        }


        @Override
        public void run() {
            Log.d("EngineActivityCallback", "Hello ActivityResumeRunnable - run " + mWeakReference.get() + " current Activity - " + engineAppApplicationWeakReference.get().currentActivity);
            if (engineAppApplicationWeakReference.get().showAppOpenAds && (mWeakReference.get() == engineAppApplicationWeakReference.get().currentActivity)) {
                engineAppApplicationWeakReference.get().showAppOpenAds = false;
                AppOpenAdsHandler.INSTANCE.showAppOpenAds(mWeakReference.get());
            }
        }
    }
}

