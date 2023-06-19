package engine.app.listener;

import com.google.android.gms.ads.rewarded.RewardItem;

/**
 * Created by Meenu Singh on 08/06/2021.
 */
public interface OnRewardedEarnedItem {

    void onRewardedLoaded();

    void onRewardedFailed(String msg);

    void onUserEarnedReward(RewardItem reward);

}
