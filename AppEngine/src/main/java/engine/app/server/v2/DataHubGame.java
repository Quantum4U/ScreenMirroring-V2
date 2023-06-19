package engine.app.server.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hp on 9/20/2017.
 */
public class DataHubGame {

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("sub_title")
    @Expose
    public String sub_title;

    @SerializedName("icon")
    @Expose
    public String icon;

    @SerializedName("Link")
    @Expose
    public String Link;

    @SerializedName("show_status")
    @Expose
    public String show_status;

}
