package engine.app.listener;

import com.android.billingclient.api.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meenu Singh on 23/02/2021.
 */
public interface InAppBillingListener {

    void onPurchaseSuccess(ArrayList<Purchase> purchase);

    void onPurchaseFailed(List<String> ProductId);

}
