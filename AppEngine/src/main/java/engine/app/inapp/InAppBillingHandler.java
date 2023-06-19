package engine.app.inapp;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.ArrayList;
import java.util.List;

import engine.app.fcm.GCMPreferences;
import engine.app.listener.InAppBillingListener;
import engine.app.server.v2.Billing;
import engine.app.server.v2.BillingResponseHandler;
import engine.app.server.v2.Slave;

/**
 * Created by Meenu Singh on 08/04/2021.
 */
public class InAppBillingHandler implements InAppBillingListener {
    private final Context context;
    private final BillingPreference mPreference;
    private final InAppBillingListener listener;
    private final String TAG = "InAppBillingHandler";

    public InAppBillingHandler(Context context, InAppBillingListener listener) {
        this.context = context;
        mPreference = new BillingPreference(context);
        this.listener = listener;
        //setAllValueFalse();

    }

    public void initializeBilling() {
        InAppBillingManager inAppBillingManager;
        ArrayList<Billing> mBillingList = BillingResponseHandler.getInstance().getBillingResponse();
        List<String> productIDPRO = new ArrayList<>();
        List<String> productISSUS = new ArrayList<>();
        if (mBillingList != null && mBillingList.size() > 0) {
            for (int i = 0; i < mBillingList.size(); i++) {
                Log.d(TAG, "initializeBilling: " + mBillingList.get(i).product_id + " " + mBillingList.get(i).billing_type);

                if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_Pro)) {
                    productIDPRO.add(mBillingList.get(i).product_id);

                } else {
                    productISSUS.add(mBillingList.get(i).product_id);
/*                    if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_Weekly)) {
                        inAppBillingManager = new InAppBillingManager(context, this);
                        inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, mBillingList.get(i).product_id, true);

                    }
                    if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_Monthly)) {

                        inAppBillingManager = new InAppBillingManager(context, this);
                        inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, mBillingList.get(i).product_id, true);

                    }
                    if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_Quarterly)) {

                        inAppBillingManager = new InAppBillingManager(context, this);
                        inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, mBillingList.get(i).product_id, true);

                    }
                    if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_HalfYear)) {

                        inAppBillingManager = new InAppBillingManager(context, this);
                        inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, mBillingList.get(i).product_id, true);

                    }
                    if (mBillingList.get(i).billing_type.equalsIgnoreCase(Slave.Billing_Yearly)) {

                        inAppBillingManager = new InAppBillingManager(context, this);
                        inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, mBillingList.get(i).product_id, true);

                    }*/
                }
            }

            if (productIDPRO.size() > 0) {
                inAppBillingManager = new InAppBillingManager(context, this);
                inAppBillingManager.initBilling(BillingClient.SkuType.INAPP, productIDPRO, true);

            }

            if (productISSUS.size() > 0) {
                inAppBillingManager = new InAppBillingManager(context, this);
                inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, productISSUS, true);
            }

        }
    }

    @Override
    public void onPurchaseSuccess(ArrayList<Purchase> purchase) {
        //Toast.makeText(context, "PurchaseSuccess", Toast.LENGTH_SHORT).show();
        //setPurchaseData(purchase.getSku());
        // setPurchaseData(purchase.getSkus());
        setPurchaseDataNew(purchase);
        // new GCMPreferences(context).savePurchaseJSON(purchase.getOriginalJson());
        if (listener != null) {
            listener.onPurchaseSuccess(purchase);
        }
    }

    @Override
    public void onPurchaseFailed(List<String> productID) {
        //Toast.makeText(context, "NonPurchased", Toast.LENGTH_SHORT).show();
        new GCMPreferences(context).savePurchaseJSON(null);
        setExpirePurchaseData(productID);
        if (listener != null) {
            listener.onPurchaseFailed(productID);
        }
    }

    private final List<String> tempUnPurchaseList = new ArrayList<>();
    private final List<String> tempPurchaseList = new ArrayList<>();

    private void setPurchaseDataNew(ArrayList<Purchase> purchaseList) {
        tempUnPurchaseList.clear();
        tempPurchaseList.clear();
        for (Purchase purchase : purchaseList) {
            setPurchaseData(purchase.getSkus());
        }
        Log.d(TAG, "setPurchaseData: type finalList " + tempPurchaseList + "  " + tempUnPurchaseList);
        if (tempUnPurchaseList.size() > 0) {
            for (String b : tempUnPurchaseList) {
                switch (b) {
                    case Slave.Billing_Pro:
                        mPreference.setPro(false);
                        Slave.IS_PRO = mPreference.isPro();
                        break;

                    case Slave.Billing_Weekly:
                        mPreference.setWeekly(false);
                        Slave.IS_WEEKLY = mPreference.isWeekly();
                        break;

                    case Slave.Billing_Monthly:
                        mPreference.setMonthly(false);
                        Slave.IS_MONTHLY = mPreference.isMonthly();
                        break;

                    case Slave.Billing_Quarterly:
                        mPreference.setQuarterly(false);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();
                        break;

                    case Slave.Billing_HalfYear:
                        mPreference.setHalfYearly(false);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();
                        break;

                    case Slave.Billing_Yearly:
                        mPreference.setYearly(false);
                        Slave.IS_YEARLY = mPreference.isYearly();
                        break;
                    default:
                        break;
                }
            }
        }

    }

    private void setPurchaseData(ArrayList<String> productId) {

        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            Log.d(TAG, "setPurchaseData: list " + productId);
            Log.d(TAG, "setPurchaseData: type " + b.billing_type + " " + productId + " " + b.product_id + " " + productId.contains(b.product_id) + "  " + tempUnPurchaseList);

            if (productId.contains(b.product_id)) {
                Log.d(TAG, "setPurchaseData: type 111 " + b.billing_type + " " + tempUnPurchaseList + " " + b.product_id + " " + tempUnPurchaseList.contains(b.billing_type));

                tempPurchaseList.add(b.billing_type);
                if (tempUnPurchaseList.contains(b.billing_type)) {
                    tempUnPurchaseList.remove(tempUnPurchaseList.indexOf(b.billing_type));
                }

                switch (b.billing_type) {
                    case Slave.Billing_Pro:
                        mPreference.setPro(true);
                        Slave.IS_PRO = mPreference.isPro();
                        break;

                    case Slave.Billing_Weekly:
                        mPreference.setWeekly(true);
                        Slave.IS_WEEKLY = mPreference.isWeekly();
                        break;

                    case Slave.Billing_Monthly:
                        mPreference.setMonthly(true);
                        Slave.IS_MONTHLY = mPreference.isMonthly();
                        break;

                    case Slave.Billing_Quarterly:
                        mPreference.setQuarterly(true);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();
                        break;

                    case Slave.Billing_HalfYear:
                        mPreference.setHalfYearly(true);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();
                        break;

                    case Slave.Billing_Yearly:
                        mPreference.setYearly(true);
                        Slave.IS_YEARLY = mPreference.isYearly();
                        break;

                    default:
                        break;
                }
            } else {
                if (!tempPurchaseList.contains(b.billing_type) && !tempUnPurchaseList.contains(b.billing_type))
                    tempUnPurchaseList.add(b.billing_type);
            }
        }

    }

    private void setExpirePurchaseData(List<String> productID) {
        System.out.println("setPurchaseData: InAppBillingHandler.setPurchaseData setExpirePurchaseData 1111 ");
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            System.out.println("setPurchaseData: InAppBillingHandler.setPurchaseData setExpirePurchaseData " + productID.contains(b.product_id) + "  " + productID + "  " +
                    b.billing_type + " " + b.product_id + " " + BillingResponseHandler.getInstance().getBillingResponse());

            if (productID.contains(b.product_id)) {
                switch (b.billing_type) {
                    case Slave.Billing_Pro:
                        mPreference.setPro(false);
                        Slave.IS_PRO = mPreference.isPro();
                        break;

                    case Slave.Billing_Weekly:
                        mPreference.setWeekly(false);
                        Slave.IS_WEEKLY = mPreference.isWeekly();

                        break;

                    case Slave.Billing_Monthly:
                        mPreference.setMonthly(false);
                        Slave.IS_MONTHLY = mPreference.isMonthly();
                        break;

                    case Slave.Billing_Quarterly:
                        mPreference.setQuarterly(false);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();

                        break;

                    case Slave.Billing_HalfYear:
                        mPreference.setHalfYearly(false);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();

                        break;

                    case Slave.Billing_Yearly:
                        mPreference.setYearly(false);
                        Slave.IS_YEARLY = mPreference.isYearly();

                        break;

                    default:
                        break;
                }
            }
        }
    }

    /*private void setAllValueFalse() {
        Slave.IS_PRO = false;
        Slave.IS_WEEKLY = false;
        Slave.IS_MONTHLY = false;
        Slave.IS_QUARTERLY = false;
        Slave.IS_HALFYEARLY = false;
        Slave.IS_YEARLY = false;

        mPreference.setPro(false);
        mPreference.setWeekly(false);
        mPreference.setMonthly(false);
        mPreference.setQuarterly(false);
        mPreference.setHalfYearly(false);
        mPreference.setYearly(false);
    }*/
}
