package engine.app.server.v2;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;

/**
 * Created by Anon on 31,August,2018
 */
public class InHouseData {
    @SerializedName("app_id")
    public String appID = DataHubConstant.APP_ID;

    @SerializedName("version")
    public String version;

    @SerializedName("type")
    public String type;

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("os")
    public String os;

    @SerializedName("launchcount")
    public String launchCount;

    public InHouseData(Context context, String type) {
        version = RestUtils.getVersion(context);
        this.type = type;
        unique_id = new GCMPreferences(context).getUniqueId();

        launchCount = RestUtils.getAppLaunchCount();
        // for android it will be 1
        os = "1";
    }
}
