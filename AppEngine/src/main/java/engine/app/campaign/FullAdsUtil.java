package engine.app.campaign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import engine.app.adshandler.AHandler;
import engine.app.campaign.utils.AdPromptActivity;
import engine.app.campaign.utils.CampaignAllActivity;
import engine.app.campaign.utils.FullAdsInfoActivity;


/**
 * Created by hp on 8/9/2017.
 */
public class FullAdsUtil {

    public static final String TAG_HEADER = "_header";
    public static final String TAG_FOOTER = "_footer";
    public static final String TAG_IS_ICON = "_is_icon";

    private static final String FULL_ADS_FULL = "fullads";
    private static final String FULL_ADS_FLOATING = "floatingads";
    private static final String FULL_ADS_INHOUSE = "inhouse_camp";
    private static final String FULL_ADS_DEFAULT = "default_no_ads";

    private AHandler aHandler;

    private CampaignHandler handler;

    public FullAdsUtil() {
        this.aHandler = AHandler.getInstance();
        this.handler = CampaignHandler.getInstance();
    }


    public void showFullAdsGeneric(Context mContext, boolean flag) {
        aHandler.showFullAds((Activity) mContext, flag);
    }

    public void showFullAdsCustom(Context mContext, String pageId, String title, String subTitle, boolean isShowIcon) {

        if (handler.loadPageConfigurations() != null && handler.loadPageConfigurations().size() > 0) {
            for (int i = 0; i < handler.loadPageConfigurations().size(); i++) {
                System.out.println("checking the key junk file value 03" + " " + handler.loadPageConfigurations().get(i).pageId);
                if (pageId.equalsIgnoreCase(handler.loadPageConfigurations().get(i).pageId)) {
                    String type = handler.loadPageConfigurations().get(i).adConfig;

                    System.out.println("FullAdsUtil.showFullAdsCustom" + " " + type);
                    // Toast.makeText(mContext, "type::" + " " + type, Toast.LENGTH_SHORT).show();
                    if (type.equalsIgnoreCase(FULL_ADS_FULL)) {

                        showMessageAlert(mContext, title, subTitle);
                        aHandler.showFullAds((Activity) mContext, true);

                    } else if (type.equalsIgnoreCase(FULL_ADS_FLOATING)) {
                        mContext.startActivity(new Intent(mContext, CampaignAllActivity.class)
                                .putExtra(TAG_HEADER, title).putExtra(TAG_FOOTER, subTitle).
                                        putExtra(TAG_IS_ICON, isShowIcon));
                    } else if (type.equalsIgnoreCase(FULL_ADS_INHOUSE)) {
                        mContext.startActivity(new Intent(mContext, AdPromptActivity.class)
                                .putExtra(TAG_HEADER, title).putExtra(TAG_FOOTER, subTitle).
                                        putExtra(TAG_IS_ICON, isShowIcon));
                    } else if (type.equalsIgnoreCase(FULL_ADS_DEFAULT)) {
                        showMessageAlert(mContext, title, subTitle);
                    } else {
                        showMessageAlert(mContext, title, subTitle);
                        aHandler.showFullAds((Activity) mContext, true);
                    }
                }
            }
        } else {
            showMessageAlert(mContext, title, subTitle);
        }
    }

    public void showCompleteDialog(Context mContext, String title, String subTitle) {
        showMessageAlert(mContext, title, subTitle);
    }

    private void showMessageAlert(Context mContext, String title, String subTitle) {
        System.out.println("here 533 " + " " + title + " " + subTitle);
        mContext.startActivity(new Intent(mContext, FullAdsInfoActivity.class).
                putExtra(TAG_HEADER, title).putExtra(TAG_FOOTER, subTitle));

    }
}
