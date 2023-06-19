package engine.app.campaign;


import java.util.ArrayList;

import engine.app.campaign.response.AdsIcon;
import engine.app.campaign.response.Redirection;


/**
 * Created by hp on 7/20/2017.
 */
public interface ICampaignLoad {
    void onLargeCampaignLoad(ArrayList<Redirection> l);

    void onSmallCampaignLoad(ArrayList<Redirection> s);

    void onCPIconLoad(ArrayList<AdsIcon> i);
//    void onCampaignLoad();
}
