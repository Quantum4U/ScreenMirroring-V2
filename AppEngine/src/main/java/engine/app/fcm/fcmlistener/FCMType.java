package engine.app.fcm.fcmlistener;

import android.content.Context;

import engine.app.fcm.NotificationUIResponse;

/**
 * Created by quantum4u1 on 27/04/18.
 */

public interface FCMType {

    void generatePush(Context c, NotificationUIResponse r);

}
