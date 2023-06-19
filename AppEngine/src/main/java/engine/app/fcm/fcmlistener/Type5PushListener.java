package engine.app.fcm.fcmlistener;

import android.content.Context;
import android.content.Intent;

import engine.app.PrintLog;
import engine.app.fcm.MapperUtils;
import engine.app.fcm.NotificationUIResponse;
import engine.app.server.v2.DataHubConstant;

public class Type5PushListener implements FCMType {

    @Override
    public void generatePush(Context c, NotificationUIResponse r) {
        try {
            Intent intent = new Intent(DataHubConstant.CUSTOM_ACTION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(MapperUtils.keyType, r.type);
            intent.putExtra(MapperUtils.keyValue, r.click_value);
            c.startActivity(intent);

        } catch (Exception e) {
            PrintLog.print("getNotificationValue.onPostExecute Exception" + e);

        }
    }
}
