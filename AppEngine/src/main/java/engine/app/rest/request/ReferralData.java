package engine.app.rest.request;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;

/**
 * Created by Meenu Singh on 21/05/18.
 */

public class ReferralData {

    @SerializedName("app_id")
    public String appID = DataHubConstant.APP_ID;

    @SerializedName("version")
    public String version;

    @SerializedName("referrer")
    public String referrer;

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("os")
    public String os;

    @SerializedName("launchcount")
    public String launchCount;

    @SerializedName("country")
    public String country;

    @SerializedName("screen")
    public String screen;

    @SerializedName("osversion")
    public String osVersion;

    @SerializedName("dversion")
    public String deviceVersion;


    public ReferralData(Context context, String refferalID) {
        version = RestUtils.getVersion(context);
        referrer = refferalID;
        unique_id = new GCMPreferences(context).getUniqueId();

        country = RestUtils.getCountryCode(context);
        screen = RestUtils.getScreenDimens(context);
        osVersion = RestUtils.getOSVersion(context);
        deviceVersion = RestUtils.getDeviceVersion(context);

        launchCount = RestUtils.getAppLaunchCount();
        // for android it will be 1
        os = "1";
    }
}
