package engine.app.fcm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import engine.app.PrintLog;
import engine.app.server.v2.DataHubConstant;
import engine.app.ui.MapperActivity;

/**
 * Created by rajeev on 10/04/18.
 */

public class NotificationActionReceiver extends BroadcastReceiver {
    Intent intent2;
    int TYPE_4;

    @Override
    public void onReceive(Context context, Intent intent) {
        PrintLog.print("NotificationActionReceiver.onReceive "+intent.getAction());
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase("sec_btn")) {
                TYPE_4 = intent.getIntExtra("TYPE_4", 0);
                String clickType2 = intent.getStringExtra("sec_btn_type");
                String clickValue2 = intent.getStringExtra("sec_btn_value");

                PrintLog.print("NotificationActionReceiver.onReceive 01 " + clickType2 + " " + clickValue2);

                intent2 = new Intent(context, MapperActivity.class);
//                intent2.addCategory(Intent.CATEGORY_DEFAULT);
                intent2.putExtra(MapperUtils.keyType, clickType2);
                intent2.putExtra(MapperUtils.keyValue, clickValue2);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent2);

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null)
                    notificationManager.cancel(TYPE_4);
            }
        }

    }
}
