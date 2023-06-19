package engine.app.server.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExitPromptButtomResponce implements Serializable {

    @SerializedName("exit_buttom_banner_src")
    @Expose
    public String exit_buttom_banner_src;

    @SerializedName("exit_msz_text")
    @Expose
    public String exit_msz_text;

    @SerializedName("exit_pos_button_bg")
    @Expose
    public String exit_pos_button_bg;

    @SerializedName("exit_pos_button_text")
    @Expose
    public String exit_pos_button_text;

    @SerializedName("exit_pos_button_textcolor")
    @Expose
    public String exit_pos_button_textcolor;

    @SerializedName("exit_neg_button_bg")
    @Expose
    public String exit_neg_button_bg;

    @SerializedName("exit_neg_button_text")
    public String exit_neg_button_text;

    @SerializedName("exit_neg_button_textcolor")
    @Expose
    public String exit_neg_button_textcolor;

    @SerializedName("topbanner")
    @Expose
    public ExitType4TopBannerResponce exitType4TopBannerResponce;

    @SerializedName("top_banner_header")
    @Expose
    public String top_banner_header;
}
