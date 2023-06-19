package engine.app.inapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.billingclient.api.Purchase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.pnd.adshandler.R;
import engine.app.adshandler.EngineHandler;
import engine.app.fcm.GCMPreferences;
import engine.app.listener.InAppBillingListener;
import engine.app.server.v2.Billing;
import engine.app.server.v2.BillingResponseHandler;
import engine.app.server.v2.DataHubPreference;
import engine.app.server.v2.Slave;

/**
 * Created by Meenu Singh on 20/05/2021.
 */
public class BillingDetailActivity extends Activity implements InAppBillingListener {
    private BillingPreference mPreference;

    private String productName;
    private InAppBillingManager inAppBillingManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_details_layout);

        mPreference = new BillingPreference(this);

        LinearLayout layoutPurchase = findViewById(R.id.layoutPurchase);
        TextView header = findViewById(R.id.header);

        Typeface headerTypeFace = Typeface.createFromAsset(getAssets(), "fonts/billing_regular.ttf");
        header.setTypeface(headerTypeFace);


        ImageView detail_src = findViewById(R.id.details_src);
        LinearLayout details_description = findViewById(R.id.details_description);

        TextView btn_text = findViewById(R.id.btn_text);
        TextView btn_subtext = findViewById(R.id.btn_subtext);

        inAppBillingManager = new InAppBillingManager(this, this);

        Intent mIntent = getIntent();
        final Billing b = (Billing) mIntent.getSerializableExtra("billing");
        if (b != null) {
            productName = b.product_offer_text;

            if (b.details_page_type.equalsIgnoreCase("description")) {
                details_description.setVisibility(View.VISIBLE);
                detail_src.setVisibility(View.GONE);

                String[] description = b.details_description.split("\n");
                for (String s : description) {
                    Typeface descriptionTypeFace = Typeface.createFromAsset(getAssets(), "fonts/billing_light.ttf");
                    TextView textView = new TextView(BillingDetailActivity.this);
                    textView.setTextColor(getResources().getColor(android.R.color.white));
                    textView.setTextSize(17);
                    textView.setTypeface(descriptionTypeFace);

                    if (!s.equalsIgnoreCase("")) {
                        textView.setCompoundDrawablePadding(20);
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.billing_check, 0, 0, 0);
                    }
                    textView.setText(s);
                    details_description.addView(textView);
                }


            } else if (b.details_page_type.equalsIgnoreCase("image")) {
                details_description.setVisibility(View.GONE);
                detail_src.setVisibility(View.VISIBLE);

                if (b.details_src != null && !b.details_src.equalsIgnoreCase("")) {
                    Picasso.get().load(b.details_src).into(detail_src);
                }

            }
            btn_text.setText(Html.fromHtml(b.button_text));
            btn_subtext.setText(Html.fromHtml(b.button_sub_text));
            header.setText(b.product_offer_text);

            if (b.billing_type.equalsIgnoreCase(Slave.Billing_Free)) {
                layoutPurchase.setVisibility(View.INVISIBLE);
            }

            layoutPurchase.setOnClickListener(view -> {

                if (b.billing_type.equalsIgnoreCase(Slave.Billing_Pro)) {
                 //   inAppBillingManager.initBilling(BillingClient.SkuType.INAPP, b.product_id, false);
                } else {
                   // inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, b.product_id, false);
                }

            });

        }
        findViewById(R.id.iv_dismiss).setOnClickListener(view -> finish());

        findViewById(R.id.tv_other_plans).setOnClickListener(view -> finish());
    }

    private void showPurchaseDialog() {
        final Dialog dialog = new Dialog(BillingDetailActivity.this, R.style.BaseTheme);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.purchase_ok);


        TextView header = dialog.findViewById(R.id.tv_header);
        TextView description = dialog.findViewById(R.id.tv_description);

        DataHubPreference dP = new DataHubPreference(BillingDetailActivity.this);
        String headerText = "<b>" + dP.getAppname() + "</b>";
        header.setText(Html.fromHtml(headerText + " User,"));

        String descriptionText = "You have been upgraded to " + "<b>" + productName + "</b>" + "subsciption successfully.";

        description.setText(Html.fromHtml(descriptionText));

        LinearLayout restartLater = dialog.findViewById(R.id.restartLater);
        LinearLayout restartNow = dialog.findViewById(R.id.restartNow);


        restartLater.setOnClickListener(view -> {
            dialog.cancel();
            finish();
        });

        restartNow.setOnClickListener(view -> restartApplication());
        dialog.show();
    }


    private void restartApplication() {
        GCMPreferences gcmPreferences = new GCMPreferences(this);
        gcmPreferences.setFirsttimeString("false");

        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            System.exit(0);
        }
    }

    @Override
    public void onPurchaseSuccess(ArrayList<Purchase> purchase) {
        for(Purchase purchase1 :purchase) {
            setPurchaseData(purchase1.getSkus());
            for (String productId : purchase1.getSkus()) {
                new EngineHandler(this).doInAppSuccessRequest(productId);
            }
        }

//        setPurchaseData(purchase.getSkus());
//        for (String productId : purchase.getSkus()) {
//            new EngineHandler(this).doInAppSuccessRequest(productId);
//        }
        //  new EngineHandler(this).doInAppSuccessRequest(purchase.getSku());
    }

    @Override
    public void onPurchaseFailed(List<String> productID) {
        setExpirePurchaseData();
        Log.d("InAppBillingManager", "onPurchasesUpdated: listener details 001 ");
    }

    private void setPurchaseData(ArrayList<String> productId) {
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            switch (b.billing_type) {
                case Slave.Billing_Pro:
                    if (productId.contains(b.product_id)) {
                        mPreference.setPro(true);
                        Slave.IS_PRO = mPreference.isPro();

                        showPurchaseDialog();
                        break;
                    }

                case Slave.Billing_Weekly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setWeekly(true);
                        Slave.IS_WEEKLY = mPreference.isWeekly();

                        showPurchaseDialog();
                        break;
                    }

                case Slave.Billing_Monthly:
                    if (productId.contains(b.product_id)) {

                        mPreference.setMonthly(true);
                        Slave.IS_MONTHLY = mPreference.isMonthly();

                        showPurchaseDialog();
                        break;
                    }

                case Slave.Billing_Quarterly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setQuarterly(true);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();

                        showPurchaseDialog();
                        break;
                    }

                case Slave.Billing_HalfYear:
                    if (productId.contains(b.product_id)) {
                        mPreference.setHalfYearly(true);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();

                        showPurchaseDialog();
                        break;
                    }

                case Slave.Billing_Yearly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setYearly(true);
                        Slave.IS_YEARLY = mPreference.isYearly();

                        showPurchaseDialog();
                        break;
                    }
                default:
                    break;
            }
        }
    }

    private void setExpirePurchaseData() {
        System.out.println("InAppBillingHandler.setPurchaseData setExpirePurchaseData 1111 ");
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
                System.out.println("InAppBillingHandler.setPurchaseData setExpirePurchaseData "
                        + b.billing_type + " " + b.product_id + " " + BillingResponseHandler.getInstance().getBillingResponse());

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
