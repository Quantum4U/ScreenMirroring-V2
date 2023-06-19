package engine.app.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import engine.app.adshandler.AHandler;
import engine.app.fcm.GCMPreferences;
import engine.app.fcm.MapperUtils;


/**
 * Created by Meenu Singh on 13-12-2017.
 */
public class MapperActivity extends Activity {
    private String splashNameMain, dashboardNameMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        GCMPreferences gcmPreferences = new GCMPreferences(this);
        splashNameMain = gcmPreferences.getSplashName();
        dashboardNameMain = gcmPreferences.getDashboardName();

        Intent intent = getIntent();
        String type = intent.getStringExtra(MapperUtils.keyType);
        String value = intent.getStringExtra(MapperUtils.keyValue);
        System.out.println("0643 key value" + " " + value);

        String packageName = intent.getStringExtra("PackageName");
        if (type != null && value != null) {
            if (!value.equalsIgnoreCase(MapperUtils.gcmAppLaunch)) {
                launchAppWithMapper(type, value, packageName);
            } else {
                if (dashboardNameMain != null) {
                    AHandler.getInstance().v2CallOnBGLaunch(this);

                    Intent intent1 = new Intent();
                    intent1.setClassName(this, dashboardNameMain);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.putExtra("PackageName", packageName);
                    try {
                        startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            this.finish();
        }
    }

    private void launchAppWithMapper(String type, String value, String packageName) {
        if (splashNameMain != null) {
            Intent intent = new Intent();
            intent.setClassName(this, splashNameMain);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(MapperUtils.keyType, type);
            intent.putExtra(MapperUtils.keyValue, value);
            intent.putExtra("PackageName", packageName);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        }


        finish();
    }
}
