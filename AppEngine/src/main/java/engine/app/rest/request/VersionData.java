package engine.app.rest.request;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;

/**
 * Created by quantum4u1 on 24/04/18.
 */

public class VersionData {

    @SerializedName("app_id")
    public String appID = DataHubConstant.APP_ID;

    @SerializedName("version")
    public String version;

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("os")
    public String os;

    @SerializedName("launchcount")
    public String launchCount;

    @SerializedName("country")
    public String country;

    public VersionData(Context context) {
        version = RestUtils.getVersion(context);
        unique_id = new GCMPreferences(context).getUniqueId();
        country = RestUtils.getCountryCode(context);

        launchCount = RestUtils.getAppLaunchCount();
        // for android it will be 1
        os = "1";

    }

}
