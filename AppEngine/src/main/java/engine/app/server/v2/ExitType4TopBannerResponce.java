package engine.app.server.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExitType4TopBannerResponce implements Serializable {

    @SerializedName("banner_scr")
    @Expose
    public String banner_scr;

    @SerializedName("banner_click_type")
    @Expose
    public String banner_click_type;

    @SerializedName("banner_click_value")
    @Expose
    public String banner_click_value;

    @SerializedName("app_list")
    @Expose
    public List<ExitAppListResponse> app_list;
}
