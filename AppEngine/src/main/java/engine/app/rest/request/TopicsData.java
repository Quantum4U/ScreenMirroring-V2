package engine.app.rest.request;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import engine.app.fcm.GCMPreferences;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;

public class TopicsData {

    @SerializedName("app_id")
    public String appID;

    @SerializedName("version")
    public String version;

    @SerializedName("os")
    public String os;

    @SerializedName("launchcount")
    public String launchCount;

    @SerializedName("app_topics")
    @Expose
    public ArrayList<TopicsRequest> app_topics = new ArrayList<>();

    @SerializedName("unique_id")
    public String unique_id;

    @SerializedName("country")
    public String country;

    public TopicsData(Context context, ArrayList<String> list) {
        appID = DataHubConstant.APP_ID;
        version = RestUtils.getVersion(context);
        launchCount = RestUtils.getAppLaunchCount();
        country = RestUtils.getCountryCode(context);

        // for android it will be 1
        os = "1";
        unique_id = new GCMPreferences(context).getUniqueId();

        for (int i = 0; i < list.size(); i++) {
            TopicsRequest qr = new TopicsRequest(list.get(i));
            app_topics.add(qr);
        }
    }
}
