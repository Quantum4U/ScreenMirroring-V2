package engine.app.campaign.request;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;

public class CampaignData {

    @SerializedName("app_id")
    public String app_id = DataHubConstant.APP_ID;

    @SerializedName("version")
    public String version;

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("os")
    public String os;

    @SerializedName("launchcount")
    public String launchCount;

    public CampaignData(Context context) {
        version = RestUtils.getVersion(context);
        unique_id = new GCMPreferences(context).getUniqueId();
        launchCount = RestUtils.getAppLaunchCount();
        // for android it will be 1
        os = "1";
    }
}
