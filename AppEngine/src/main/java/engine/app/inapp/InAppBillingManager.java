package engine.app.inapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import engine.app.analytics.AppAnalyticsKt;
import engine.app.listener.InAppBillingListener;
import engine.app.server.v2.Slave;
import engine.util.Security;

/**
 * Created by Meenu Singh on 22/02/2021.
 */
public class InAppBillingManager {
    private final String TAG = "InAppBillingManager";

    private final Context context;
    private BillingClient billingClient;
    private List<String> PRODUCT_ID = new ArrayList<>();
    private String PRODUCT_TYPE = "";
    private final InAppBillingListener inAppBillingListener;
    //private final BillingPreference mPreference;

    public InAppBillingManager(Context context, InAppBillingListener listener) {
        this.context = context;
        this.inAppBillingListener = listener;
        // mPreference = new BillingPreference(context);
    }

    public void initBilling(String type, List<String> productID, boolean fromStart) {
        PRODUCT_TYPE = type;
        PRODUCT_ID = productID;
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        //  setAllValueFalse();
        startConnection(fromStart);

    }

    private void startConnection(boolean fromStart) {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.


                    List<String> skuList = new ArrayList<>(PRODUCT_ID);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(PRODUCT_TYPE);
                    billingClient.querySkuDetailsAsync(params.build(),
                            (billingResult1, skuDetailsList) -> {
                                // Process the result.
                                if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                    Log.d(TAG, "onSkuDetailsResponse: " + billingResult1.getResponseCode()
                                            + " " + skuDetailsList.size());
                                    if (fromStart) {
                                        queryAlreadyPurchasesResult();
                                    } else {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            Log.d(TAG, "onSkuDetailsResponse: " + skuDetails);
                                            launchBillingFlow(skuDetails);
                                        }
                                    }
                                }
                            });


                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    public void queryAlreadyPurchasesResult() {
        //if item already purchased then check and reflect changes
        billingClient.queryPurchasesAsync(PRODUCT_TYPE, (billingResult, list) -> {
            Log.d(TAG, "queryAlreadyPurchasesResult: " + PRODUCT_TYPE + "  " + PRODUCT_ID + "  " +
                    billingResult.getResponseCode() + " " + list.size() + "  " + list);
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list.size() > 0) {
                handlePurchases(list);
            } else {
                inAppBillingListener.onPurchaseFailed(PRODUCT_ID);
                disconnectConnection();
            }
        });
    }

    private void launchBillingFlow(SkuDetails skuDetails) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        int responseCode = billingClient.launchBillingFlow((Activity) context, billingFlowParams).getResponseCode();

        Log.d(TAG, "launchBillingFlow: " + responseCode);
    }


    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        Log.d(TAG, "onPurchasesUpdated: " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
        // To be implemented in a later section.
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            //if item newly purchased
            Log.d(TAG, "onPurchasesUpdated: user ok ");
            handlePurchases(purchases);

        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            //if item already purchased then check and reflect changes
            queryAlreadyPurchasesResult();

        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, "onPurchasesUpdated: user canceled " + billingResult.getResponseCode());
            //inAppBillingListener.onPurchaseFailed();
            disconnectConnection();
        } else {
            // Handle any other error codes.
            Log.d(TAG, "onPurchasesUpdated: error " + billingResult.getResponseCode());
            //inAppBillingListener.onPurchaseFailed();
            disconnectConnection();
        }
    };

    private void handlePurchases(List<Purchase> purchases) {
        ArrayList<Purchase> listPurchase = new ArrayList<>();

        for (Purchase purchase : purchases) {
            Log.d(TAG, "handlePurchases: item " + purchase);
            //if item is purchased
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Log.d(TAG, "handlePurchases: invalid purchase ");
                    return;
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            //if purchase is acknowledged
                            // Grant entitlement to the user. and restart activity
                            listPurchase.add(purchase);

                          /*  inAppBillingListener.onPurchaseSuccess(purchase);
                            disconnectConnection();*/
                        }
                    });
                }
                //else item is purchased and also acknowledged
                //else {
                // Grant entitlement to the user on item purchase
                // restart activity
                Log.d(TAG, "handlePurchases: save here to pref ");
                listPurchase.add(purchase);

               /* inAppBillingListener.onPurchaseSuccess(purchase);
                disconnectConnection();*/
                // }
            }

            // consume(purchase);

        }
        inAppBillingListener.onPurchaseSuccess(listPurchase);
        disconnectConnection();
    }

   /* private void consume(Purchase purchase) {
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
                Log.d(TAG, "consume: ");
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }*/


    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
        String base64Key = Slave.INAPP_PUBLIC_KEY;
        return Security.verifyPurchase(base64Key, signedData, signature);
    }

    private void disconnectConnection() {
        billingClient.endConnection();
    }

  /*  private void setAllValueFalse() {
        Slave.IS_PRO = false;
        Slave.IS_WEEKLY = false;
        Slave.IS_MONTHLY = false;
        Slave.IS_QUARTERLY = false;
        Slave.IS_HALFYEARLY = false;
        Slave.IS_YEARLY = false;
    }*/
}
