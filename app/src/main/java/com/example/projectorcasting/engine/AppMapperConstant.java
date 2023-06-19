package com.example.projectorcasting.engine;

import engine.app.adshandler.AHandler;

public class AppMapperConstant {
    private static AppMapperConstant instance;

    public final String FULLADSTYPE = "full_ads_type";
    public final String ACTIVITY_AFTER_FULLADS = "activity_after_fullads";
    public final String Launch_FullAds = "Launch";
    public final String Exit_FullAds = "Exit";
    public final String Navigation_FullAds = "navigation";
    public final String IsForce = "is_Force";

    public static AppMapperConstant getInstance() {
        if (instance == null) {
            synchronized (AHandler.class) {
                if (instance == null) {
                    instance = new AppMapperConstant();
                }
            }
        }
        return instance;
    }


}
