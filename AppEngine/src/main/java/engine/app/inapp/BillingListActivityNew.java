package engine.app.inapp;

import static engine.app.server.v2.DataHubConstant.quantumList;
import static engine.app.utils.EngineConstant.m24apps;
import static engine.app.utils.EngineConstant.q4u;
import static engine.app.utils.EngineConstant.quantum;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.ArrayList;
import java.util.List;

import app.pnd.adshandler.R;
import engine.app.adapter.BillingListAdapterNew;
import engine.app.adshandler.AHandler;
import engine.app.adshandler.EngineHandler;
import engine.app.analytics.AppAnalyticsKt;
import engine.app.analytics.EngineAnalyticsConstant;
import engine.app.fcm.GCMPreferences;
import engine.app.listener.InAppBillingListener;
import engine.app.listener.RecyclerViewClickListener;
import engine.app.server.v2.Billing;
import engine.app.server.v2.BillingResponseHandler;
import engine.app.server.v2.DataHubPreference;
import engine.app.server.v2.Slave;
import engine.app.utils.EngineConstant;

/**
 * Created by Meenu Singh on 20/05/2021.
 */
public class BillingListActivityNew extends AppCompatActivity implements RecyclerViewClickListener, View.OnClickListener, InAppBillingListener {

    private ArrayList<Billing> mBillingList;
    private TextView buttonSubs;
    private TextView textViewWithADs;
    private ImageView backArrow, purchaseBillingIcon;
    private BillingListAdapterNew billingListAdapter;
    private TextView tv_description, description_text;
    private InAppBillingManager inAppBillingManager;
    private BillingPreference mPreference;
    private String productName;
    private GCMPreferences gcmPreference;
    private boolean isShowBackArrow;
    private boolean fromSplash = false;
    private static String mPackageName;
    private TextView textViewManageSubs,tv_premium_benefits;
    private NestedScrollView scroolView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.billing_list_layout_new);
         scroolView = findViewById(R.id.scroolView);
        tv_premium_benefits = findViewById(R.id.tv_premium_benefits);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scroolView.setSmoothScrollingEnabled(true);
                scroolView.smoothScrollTo(0, scroolView.getBottom());
            }
        }, 2000);


        mPackageName = getPackageName();
        if (getIntent() != null) {
            isShowBackArrow = getIntent().getBooleanExtra(EngineConstant.isShowBackArrow, false);
            fromSplash = getIntent().getBooleanExtra("fromSplash", false);
        }
        gcmPreference = new GCMPreferences(this);
        mPreference = new BillingPreference(this);
        System.out.println("InAppBillingHandler.setPurchaseData biling mlist " + BillingResponseHandler.getInstance().getBillingResponse());
        mBillingList = BillingResponseHandler.getInstance().getBillingResponse();
        tv_description = findViewById(R.id.tv_description);
        description_text = findViewById(R.id.description_text);
        backArrow = findViewById(R.id.iv_back);
//        closeBtn = findViewById(R.id.iv_cross);

        textViewManageSubs = findViewById(R.id.manange_subs);
//        billingIcon = findViewById(R.id.billing_icon);
        purchaseBillingIcon = findViewById(R.id.purchased_billing_icon);

        inAppBillingManager = new InAppBillingManager(this, this);

        buttonSubs = findViewById(R.id.subs_now);
        textViewWithADs = findViewById(R.id.conti_with_ads);
//        textViewWithADs.setPaintFlags(textViewWithADs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //onSetBackIconUiWithPackage();
        onSetProBillingIconUiWithPackage();
        onSetUiAfterPurchase();

        buttonSubs.setOnClickListener(this);
        textViewWithADs.setOnClickListener(this);
        backArrow.setOnClickListener(this);
//        closeBtn.setOnClickListener(this);

        textViewManageSubs.setOnClickListener(this);

        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (mBillingList != null && mBillingList.size() > 0) {
            setPremiumBenifit(mBillingList);
            //  Slave.IS_MONTHLY = true;
            billingListAdapter = new BillingListAdapterNew(this, mBillingList, this);
            mRecyclerView.setAdapter(billingListAdapter);
            if (billingListAdapter.getItem(0).button_sub_text != null)
                tv_description.setText(billingListAdapter.getItem(0).button_sub_text);

            setDescription_text(0);
            showDataAccordingToPurchase();
        }
        TextView tvTerms = findViewById(R.id.tvTerms);
//        TextView tvPrivacy = findViewById(R.id.tvPrivacy);

        tvTerms.setClickable(true);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        tvTerms.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_TERM_AND_COND)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

//        tvPrivacy.setClickable(true);
//        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
//        tvPrivacy.setOnClickListener(v -> {
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_PRIVACYPOLICY)));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        TextView dont_show = findViewById(R.id.dont_show);
//        TextView dont_showSpace = findViewById(R.id.dont_showSpace);

        if (fromSplash) {
//            closeBtn.setVisibility(View.VISIBLE);
//            backArrow.setVisibility(View.GONE);
            textViewWithADs.setVisibility(View.VISIBLE);
            try {
                if (AHandler.ShowBillingPage.equals("1")) {
                    tvTerms.setVisibility(View.GONE);
//                    tvPrivacy.setVisibility(View.GONE);
                    dont_show.setVisibility(View.VISIBLE);
//                dont_showSpace.setVisibility(View.INVISIBLE);
                    dont_show.setOnClickListener(view -> {
                        gcmPreference.setDoNotShow("true");
                        finishPage();
                    });
                } else {
                    dont_show.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                dont_show.setVisibility(View.GONE);
            }


        } else {
//            closeBtn.setVisibility(View.GONE);
//            backArrow.setVisibility(View.VISIBLE);
            textViewWithADs.setVisibility(View.GONE);
            tvTerms.setVisibility(View.INVISIBLE);
//            tvPrivacy.setVisibility(View.INVISIBLE);
            dont_show.setVisibility(View.GONE);
//            dont_showSpace.setVisibility(View.GONE);
        }
    }

    private void onSetBackIconUiWithPackage() {
        if (mPackageName != null && (mPackageName.contains(m24apps) || mPackageName.contains("m24") ||
                mPackageName.contains(q4u)
                || mPackageName.equalsIgnoreCase("com.pnd.shareall")
                || mPackageName.equalsIgnoreCase("app.phone2location")
                || mPackageName.equalsIgnoreCase("com.pnd.fourgspeed")
                || mPackageName.equalsIgnoreCase("com.q4u.qrscanner"))) {
            backArrow.setVisibility(View.GONE);
        } else if (mPackageName != null && getAccountQU()) {
            backArrow.setVisibility(View.VISIBLE);
        }
    }

    private boolean getAccountQU() {
        for (String s : quantumList) {
            if (mPackageName.contains(quantum)
                    || mPackageName.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private void onSetProBillingIconUiWithPackage() {
        if (mPackageName != null && (mPackageName.contains(m24apps) || mPackageName.contains("m24"))) {
            purchaseBillingIcon.setVisibility(View.VISIBLE);
        } else if (mPackageName != null && mPackageName.contains(q4u)
                || mPackageName.equalsIgnoreCase("com.pnd.shareall")
                || mPackageName.equalsIgnoreCase("app.phone2location")
                || mPackageName.equalsIgnoreCase("com.pnd.fourgspeed")
                || mPackageName.equalsIgnoreCase("com.q4u.qrscanner")) {
            purchaseBillingIcon.setVisibility(View.GONE);
        }
    }

    private void onSetProBillingIconAfterPuchase() {
        if (mPackageName != null && (mPackageName.contains(m24apps) || mPackageName.contains("m24") ||
                mPackageName.contains(q4u)
                || mPackageName.equalsIgnoreCase("com.pnd.shareall")
                || mPackageName.equalsIgnoreCase("app.phone2location")
                || mPackageName.equalsIgnoreCase("com.pnd.fourgspeed")
                || mPackageName.equalsIgnoreCase("com.q4u.qrscanner"))) {
            purchaseBillingIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_pro_icon_purchase));
            purchaseBillingIcon.setVisibility(View.VISIBLE);
        }
//        else if(mPackageName!=null  ){
//            purchaseBillingIcon.setVisibility(View.GONE);
//        }
    }

    private void onSetUiAfterPurchase() {
        if (Slave.hasPurchased(this)) {
            textViewWithADs.setText(getResources().getString(R.string.continuet));
            //textViewWithADs.setVisibility(View.INVISIBLE);
            backArrow.setVisibility(View.VISIBLE);
            //buttonSubs.setVisibility(View.GONE);
            onSetProBillingIconAfterPuchase();
        } else {
            textViewWithADs.setText(getResources().getString(R.string.continue_with_ads));
            //textViewWithADs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewClicked(View mView, int position) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                scroolView.setSmoothScrollingEnabled(true);
//                scroolView.smoothScrollTo(0, scroolView.getBottom());
//            }
//        }, 100);
        switch (mBillingList.get(position).billing_type) {
            case Slave.Billing_Free:
                if (!Slave.hasPurchased(this)) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;
            case Slave.Billing_Pro:
                if (!Slave.IS_PRO) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;

            case Slave.Billing_Weekly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY && !Slave.IS_MONTHLY) && !Slave.IS_WEEKLY) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;
            case Slave.Billing_Monthly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY) && !Slave.IS_MONTHLY) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;
            case Slave.Billing_Quarterly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY) && !Slave.IS_QUARTERLY) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;
            case Slave.Billing_HalfYear:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY) && !Slave.IS_HALFYEARLY) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;

            case Slave.Billing_Yearly:
                if (!Slave.IS_PRO && !Slave.IS_YEARLY) {
                    buttonSubs.setText(mBillingList.get(position).button_text);
                }
                break;
        }
        setDescription_text(position);
    }

    private void setPurchaseClick(int position) {
        Billing b = mBillingList.get(position);
        productName = b.product_offer_text;

        Log.d("BillingListActivityNew", "Test onViewClicked...." + b.billing_type);
        switch (b.billing_type) {
            case Slave.Billing_Free:
                if (!Slave.hasPurchased(this)) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }
                break;
            case Slave.Billing_Pro:
                if (!Slave.IS_PRO) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }
                break;

            case Slave.Billing_Weekly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY && !Slave.IS_MONTHLY) && !Slave.IS_WEEKLY) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }
                break;
            case Slave.Billing_Monthly:
                // Log.d("BillingListActivityNew", "Test onViewClicked...."+b.billing_type+"  "+Slave.IS_YEARLY+" "+Slave.IS_PRO+"  "+Slave.IS_HALFYEARLY+"  "+Slave.IS_QUARTERLY+"  "+Slave.IS_MONTHLY);
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY) && !Slave.IS_MONTHLY) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }

                break;
            case Slave.Billing_Quarterly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY) && !Slave.IS_QUARTERLY) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }

                break;
            case Slave.Billing_HalfYear:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY) && !Slave.IS_HALFYEARLY) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }

                break;

            case Slave.Billing_Yearly:
                if (!Slave.IS_PRO && !Slave.IS_YEARLY) {
                    onClickPurchase(b);
                } else {
                    showToast();
                }
                break;
        }
    }

    private void onClickPurchase(Billing b) {
        if (b.billing_type.equalsIgnoreCase(Slave.Billing_Pro)) {
            List<String> procductIDPRO = new ArrayList<>();
            procductIDPRO.add(b.product_id);
            inAppBillingManager.initBilling(BillingClient.SkuType.INAPP, procductIDPRO, false);
        } else {
            List<String> procductIDSUB = new ArrayList<>();
            procductIDSUB.add(b.product_id);
            inAppBillingManager.initBilling(BillingClient.SkuType.SUBS, procductIDSUB, false);
        }
    }

    @Override
    public void onListItemClicked(View mView, String reDirectUrl) {

    }

    private void openPlaystoreAccount() {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions"));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.d("BillingListActivityNew", "Test openPlaystoreAccount.." + e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.subs_now) {
            if (billingListAdapter != null) {
                //onViewClicked(view, billingListAdapter.getSelectedPos());
                setPurchaseClick(billingListAdapter.getSelectedPos());
            }
            //Toast.makeText(this, getResources().getString(R.string.tap_on_select_plans), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.conti_with_ads) {
            finishPage();
        } else if (id == R.id.manange_subs) {
            openPlaystoreAccount();
        } else if (id == R.id.iv_back) {
            finishPage();
        } else if (id == R.id.iv_cross) {
            finishPage();
        }
    }

    @Override
    public void onPurchaseSuccess(ArrayList<Purchase> purchase) {
        for (Purchase purchase1 : purchase) {
            setPurchaseData(purchase1.getSkus());
            for (String productId : purchase1.getSkus()) {
                new EngineHandler(this).doInAppSuccessRequest(productId);
            }
        }
    }

    @Override
    public void onPurchaseFailed(List<String> productID) {
        setExpirePurchaseData();
        Log.d("InAppBillingManager", "onPurchasesUpdated: listener ");
    }

    private void setPurchaseData(ArrayList<String> productId) {
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            switch (b.billing_type) {
                case Slave.Billing_Pro:
                    if (productId.contains(b.product_id)) {
                        mPreference.setPro(true);
                        Slave.IS_PRO = mPreference.isPro();

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Weekly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setWeekly(true);
                        Slave.IS_WEEKLY = mPreference.isWeekly();

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Monthly:
                    if (productId.contains(b.product_id)) {

                        mPreference.setMonthly(true);
                        Slave.IS_MONTHLY = mPreference.isMonthly();

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Quarterly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setQuarterly(true);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_HalfYear:
                    if (productId.contains(b.product_id)) {
                        mPreference.setHalfYearly(true);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Yearly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setYearly(true);
                        Slave.IS_YEARLY = mPreference.isYearly();

                        showPurchaseDialog(b.product_id);
                        break;
                    }
                default:
                    break;
            }
        }
    }


    private void setExpirePurchaseData() {
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
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

    private void showPurchaseDialog(String productID) {
        AppAnalyticsKt.logGAEvents(this, EngineAnalyticsConstant.Companion.getGA_IAP_SUCCESS()+productID);

        final Dialog dialog = new Dialog(this, R.style.BaseTheme);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.purchase_ok);


        TextView header = dialog.findViewById(R.id.tv_header);
        TextView description = dialog.findViewById(R.id.tv_description);

        DataHubPreference dP = new DataHubPreference(this);

        // for removing # from appname in smart switch
        String appName = dP.getAppname();
        if (appName.contains("#")) {
            appName = appName.replace("#", "");
        }

        String headerText = "<b>" + appName + "</b>";
        header.setText(Html.fromHtml(headerText + " User,"));

        String descriptionText = "You have been upgraded to " + "<b>" + productName + "</b>" + " successfully.";

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

        gcmPreference.setFirstTime(false);
        gcmPreference.setFirsttimeString("false");

        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            //System.exit(0);
        }
    }

    private void finishPage() {
        Intent intent = getIntent();
        //intent.putExtra("key", value);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        finishPage();
        super.onBackPressed();
    }

    private void showDataAccordingToPurchase() {
        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            switch (b.billing_type) {
                case Slave.Billing_Weekly:
                    if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY && !Slave.IS_MONTHLY) && Slave.IS_WEEKLY) {
                        buttonSubs.setEnabled(true);
                        buttonSubs.setText("Upgrade Subscription");
                    }
                    break;
                case Slave.Billing_Monthly:
                    if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY) && Slave.IS_MONTHLY) {
                        buttonSubs.setEnabled(true);
                        buttonSubs.setText("Upgrade Subscription");
                    }

                    break;
                case Slave.Billing_Quarterly:
                    if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY) && Slave.IS_QUARTERLY) {
                        buttonSubs.setEnabled(true);
                        buttonSubs.setText("Upgrade Subscription");
                    }

                    break;
                case Slave.Billing_HalfYear:
                    if ((!Slave.IS_PRO && !Slave.IS_YEARLY) && Slave.IS_HALFYEARLY) {
                        buttonSubs.setEnabled(true);
                        buttonSubs.setText("Upgrade Subscription");
                    }

                    break;

                case Slave.Billing_Yearly:
                    if (Slave.IS_YEARLY) {
                        buttonSubs.setEnabled(false);
                        buttonSubs.setText("Subscribed");
                        buttonSubs.setTextColor(getResources().getColor(R.color.white));
                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_unsub_btn));
                    }
                    break;

                case Slave.Billing_Pro:
                    if (Slave.IS_PRO) {
                        buttonSubs.setEnabled(false);
                        buttonSubs.setText("Subscribed");
                        buttonSubs.setTextColor(getResources().getColor(R.color.white));
                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_unsub_btn));
                    }
                    break;

                default:
                    if (!Slave.hasPurchased(this)) {
                        buttonSubs.setEnabled(true);
                        buttonSubs.setText(getResources().getString(R.string.start_free_trial));
                        buttonSubs.setTextColor(getResources().getColor(R.color.white));
                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_sub_btn));
                    }
                    break;
            }
        }

    }

    private void setDescription_text(int position) {

        if (mBillingList.get(position).button_sub_text != null
                && mBillingList.get(position).button_sub_text.length() > 0) {

            String[] str = mBillingList.get(position).button_sub_text.split("@");
            try {
                if (str[0] != null && str[1] != null && mBillingList.get(position).product_price != null) {
                    String value = str[0] + Html.fromHtml(mBillingList.get(position).product_price).toString() + str[1];
                    description_text.setText(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast() {
        Toast.makeText(BillingListActivityNew.this, this.getResources().getString(R.string.already_premium_toast), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {

        super.onResume();
    }

    void setPremiumBenifit(ArrayList<Billing> mBillingList){
        String details_description = mBillingList.get(0).details_description;
//        details_description=  "\uD83D\uDE00  Completely Ads Free\n\n????  Access Apps Update\n\n????   VIP Support";
        if (details_description!=null && !details_description.equals("")){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                if (details_description.contains("\n")){
                    String replaceData = details_description.replace("\n", "<br>");
                    tv_premium_benefits.setText(Html.fromHtml(replaceData, Html.FROM_HTML_MODE_COMPACT));

                }

            } else {
                tv_premium_benefits.setText(Html.fromHtml(mBillingList.get(0).details_description));
            }

        }

    }
}
