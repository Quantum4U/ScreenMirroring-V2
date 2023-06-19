package com.example.projectorcasting.engine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.projectorcasting.ui.activities.MainActivity;
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R;

import engine.app.adshandler.AHandler;
import engine.app.fcm.MapperUtils;
import engine.app.listener.AppFullAdsCloseListner;
import engine.app.serviceprovider.Utils;

public class TransLaunchFullAdsActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private String FullAdsType;
    private String CallinActivity;
    private boolean isForce,isNotification;
    private AppMapperConstant appMapperConstant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_full_ads_activity);
        appMapperConstant = AppMapperConstant.getInstance();


        Intent intent = getIntent();
        if (intent != null) {
            isNotification=intent.getBooleanExtra("isNotification",false);
            FullAdsType = intent.getStringExtra(appMapperConstant.FULLADSTYPE);
            if (FullAdsType.equalsIgnoreCase(appMapperConstant.Navigation_FullAds)) {
                CallinActivity = intent.getStringExtra(appMapperConstant.ACTIVITY_AFTER_FULLADS);
                isForce = intent.getBooleanExtra(appMapperConstant.IsForce, false);
            }
        }
        progressBar = findViewById(R.id.progress_bar);

        //if internet is not connected
        if (!Utils.isNetworkConnected(this)) {
            isNetworkDisConnectedClass();
        } else {
            System.out.println("TransLaunchFullAdsActivity.onCreate..." + FullAdsType);
            //if internet is connected, request for launch, exitand navigtion full ads
            if (FullAdsType.equalsIgnoreCase(appMapperConstant.Launch_FullAds)) {
                AHandler.getInstance().handle_launch_For_FullAds(this, new AppFullAdsCloseListner() {

                    @Override
                    public void onFullAdClosed() {
                        TransLaunchFullAdsActivity.this.finish();
                        startDashboard(MainActivity.class);

                    }
                });
            } else if (FullAdsType.equalsIgnoreCase(appMapperConstant.Exit_FullAds)) {

//                AHandler.getInstance().handle_exit_fullads(this, new AppFullAdsCloseListner() {
//                    @Override
//                    public void onFullAdClosed() {
//                        Log.d("AHandler", "TransLaunchFullAdsActivity " +
//                                "onClosedFullAds adclosed on exit >>>>>> 00223..");
//                        TransLaunchFullAdsActivity.this.finish();
//                        AHandler.getInstance().showExitPrompt(TransLaunchFullAdsActivity.this);
//                    }
//                });

            } else if (FullAdsType.equalsIgnoreCase(appMapperConstant.Navigation_FullAds)) {
//                AHandler.getInstance().showFullAdsNew(this, isForce, new AppFullAdsCloseListner() {
//                    @Override
//                    public void onFullAdClosed() {
//                        TransLaunchFullAdsActivity.this.finish();
//                        onStartNavigationActivity();
//
//                    }
//                });

            }

        }

        // finish();
    }

    private void startDashboard(Class<?> cls) {
        Intent intent = getIntent();
        String type = intent.getStringExtra(MapperUtils.keyType);
        String value = intent.getStringExtra(MapperUtils.keyValue);
        Log.e("Splash@@","key value 3" + getIntent().getStringExtra(MapperUtils.keyValue));
        try {
            if (type != null && value != null) {
                launchAppWithMapper(cls, type, value);
            } else {
                startActivity(new Intent(this, cls).putExtra("isNotification",isNotification));
            }
        } catch (Exception e) {
        }
    }


    private void launchAppWithMapper(Class<?> cls, String type, String value) {
        startActivity(new Intent(this, cls)
                .putExtra(MapperUtils.keyType, type)
                .putExtra(MapperUtils.keyValue, value)

        );
    }

    private void isNetworkDisConnectedClass() {

        System.out.println("TransLaunchFullAdsActivity.isNetworkConnected.." + FullAdsType + "  ");
        if (FullAdsType.equalsIgnoreCase(appMapperConstant.Launch_FullAds)) {
            startDashboard(MainActivity.class);
        }
//        else if (FullAdsType.equalsIgnoreCase(appMapperConstant.Exit_FullAds)) {
//            AHandler.getInstance().showExitPrompt(TransLaunchFullAdsActivity.this);
//
//        }
        else if (FullAdsType.equalsIgnoreCase(appMapperConstant.Navigation_FullAds)) {
            onStartNavigationActivity();
        }
        finish();
    }


    private void onStartNavigationActivity() {

    }

    @Override
    public void onBackPressed() {
        isNetworkDisConnectedClass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("TransLaunchFullAdsActivityOn Destroy called");
    }
}
