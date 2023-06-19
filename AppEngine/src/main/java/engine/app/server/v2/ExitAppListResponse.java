package engine.app.server.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExitAppListResponse implements Serializable {


    @SerializedName("app_list_redirect")
    @Expose
    public String app_list_redirect;

    @SerializedName("app_list_icon_src")
    @Expose
    public String app_list_icon_src;

    @SerializedName("app_list_src")
    @Expose
    public String app_list_src;

    @SerializedName("app_list_title")
    @Expose
    public String app_list_title;

    @SerializedName("app_list_subtitle")
    @Expose
    public String app_list_subtitle;

    @SerializedName("app_list_rate_count")
    @Expose
    public String app_list_rate_count;

    @SerializedName("app_list_button_bg")
    @Expose
    public String app_list_button_bg;

    @SerializedName("app_list_button_text")
    @Expose
    public String app_list_button_text;

    @SerializedName("app_list_button_text_color")
    @Expose
    public String app_list_button_text_color;

}
