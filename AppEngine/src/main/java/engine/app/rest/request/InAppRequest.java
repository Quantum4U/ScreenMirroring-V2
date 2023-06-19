package engine.app.rest.request;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;

/**
 * Created by Meenu Singh on 09/08/2021.
 */
public class InAppRequest {

    @SerializedName("app_id")
    public String appID = DataHubConstant.APP_ID;

    @SerializedName("product_id")
    public String product_id;

    @SerializedName("version")
    public String version;

    @SerializedName("os")
    public String os;

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("launchcount")
    public String launchCount;

    @SerializedName("country")
    public String country;


    public InAppRequest(Context context, String productID) {
        product_id = productID;
        version = RestUtils.getVersion(context);
        unique_id = new GCMPreferences(context).getUniqueId();
        launchCount = RestUtils.getAppLaunchCount();
        country = RestUtils.getCountryCode(context);

        // for android it will be 1
        os = "1";
    }

}
