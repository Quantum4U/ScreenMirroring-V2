package engine.app.server.v2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GameProvidersResponce implements Serializable {

    @Expose(serialize = false)
    public static final String MOREFEAT_APP = "app";

    @Expose(serialize = false)
    public static final String MOREFEAT_WEB = "web";

    @SerializedName("show_status")
    @Expose
    public String show_status;

    @SerializedName("provider")
    @Expose
    public String provider;


    @SerializedName("position_name")
    @Expose
    public String position_name;


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
    public String link;


    @SerializedName("button_text")
    @Expose
    public String button_text;

    @SerializedName("banner")
    @Expose
    public String banner;


    @SerializedName("b_bg_color")
    @Expose
    public String button_bg_color;

    @SerializedName("button_text_color")
    @Expose
    public String button_text_color;


    @SerializedName("view_type_game")
    @Expose
    public String view_type_game;

    @SerializedName("pageid")
    @Expose
    public String pageid;






}
