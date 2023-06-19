package engine.app.inapp;

import static engine.app.adshandler.AHandler.ShowBillingPage;
import static engine.app.server.v2.DataHubConstant.quantumList;
import static engine.app.utils.EngineConstant.m24apps;
import static engine.app.utils.EngineConstant.q4u;
import static engine.app.utils.EngineConstant.quantum;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;

import java.util.ArrayList;
import java.util.List;

import app.pnd.adshandler.R;
import engine.app.adapter.BillingListAdapterNew;
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
import engine.app.serviceprovider.Utils;
import engine.app.utils.EngineConstant;

/**
 * Created by Meenu Singh on 20/05/2021.
 */
public class BillingListActivityNew extends Activity implements RecyclerViewClickListener, View.OnClickListener, InAppBillingListener {
    public static String FromSplash = "FromSplash";
    private ArrayList<Billing> mBillingList;
    private Button buttonSubs;
    private TextView textViewWithADs;
    private ImageView backArrow;
    private BillingListAdapterNew billingListAdapter;
    private TextView tv_description;
    private InAppBillingManager inAppBillingManager;
    private BillingPreference mPreference;
    private String productName;
    private GCMPreferences gcmPreference;
    private boolean isShowBackArrow;
    private static String mPackageName;
    private TextView textViewManageSubs;
    private VideoView videoViewInapp;
    private ImageView imageViewDefault;
    private String isFromSplash = "false";
    private TextView textViewDontask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_list_layout_new);
        AppAnalyticsKt.logGAEvents(this, "AN_SHOW_BILLING_PAGE");

        mPackageName = getPackageName();
        if (getIntent() != null) {
            isShowBackArrow = getIntent().getBooleanExtra(EngineConstant.isShowBackArrow, false);
            isFromSplash = getIntent().getStringExtra(FromSplash);
        }

        gcmPreference = new GCMPreferences(this);
        mPreference = new BillingPreference(this);
        mBillingList = BillingResponseHandler.getInstance().getBillingResponse();
        tv_description = findViewById(R.id.tvDescription);
        backArrow = findViewById(R.id.iv_back);
        textViewManageSubs = findViewById(R.id.manange_subs);
        textViewManageSubs.setVisibility(View.VISIBLE);
        textViewWithADs = findViewById(R.id.conti_with_ads);
        textViewWithADs.setPaintFlags(textViewWithADs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        videoViewInapp = findViewById(R.id.videoPlay);
        imageViewDefault = findViewById(R.id.defaultImage);

        textViewDontask = findViewById(R.id.dont_show_again);
        if (isFromSplash.equals("true")) {
            if (ShowBillingPage.contains("1")) {
                textViewDontask.setVisibility(View.VISIBLE);
            } else {
                textViewDontask.setVisibility(View.GONE);
                textViewWithADs.setVisibility(View.GONE);}
        }else{
            textViewDontask.setVisibility(View.GONE);
            textViewWithADs.setVisibility(View.GONE);}

        textViewDontask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gcmPreference.setDoNotShow("true");
                finishPage();}
        });
        String videoPath = Slave.ETC_4;
        if (videoPath.isEmpty() || !Utils.isNetworkConnected(this)) {
            videoViewInapp.setVisibility(View.GONE);
        } else {
            videoViewInapp.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(videoPath);
//            System.out.println("printing data ..." + videoPath + "  " + uri);
            videoViewInapp.setVideoURI(uri);
            videoViewInapp.setScaleX(1f);
            videoViewInapp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                    videoViewInapp.setVisibility(View.VISIBLE);
                    imageViewDefault.setVisibility(View.GONE);
                }
            });
            videoViewInapp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    videoViewInapp.setVisibility(View.GONE);
                    imageViewDefault.setVisibility(View.VISIBLE);
//                    System.out.println("BillingListActivityNew.onError.." + i + "  " + i1);
                    return false;
                }
            });
            // starts the video
            videoViewInapp.start();
        }

        inAppBillingManager = new InAppBillingManager(this, this);
        buttonSubs = findViewById(R.id.subs_now);
        onSetUiAfterPurchase();

        buttonSubs.setOnClickListener(this);
        textViewWithADs.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        textViewManageSubs.setOnClickListener(this);

        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               try{
                   if(mBillingList.size()>1) {
                       mRecyclerView.smoothScrollToPosition(mBillingList.size() - 1);
                   }
               }catch(Exception e){}
           }
       },2000);
        if (mBillingList != null && mBillingList.size() > 0) {
            billingListAdapter = new BillingListAdapterNew(this, mBillingList, this);
            mRecyclerView.setAdapter(billingListAdapter);
            if (billingListAdapter.getItem(0).details_description != null) {
                String str = billingListAdapter.getItem(0).details_description;
                try {
                    String[] inAppStr = str.split("\n\n");
                    for (int i = 0; i < inAppStr.length; i++) {
                        String posStr = inAppStr[i];
                        TextView textView = new TextView(this);
                        LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.inapp_des_icon, 0, 0, 0);
                        textView.setCompoundDrawablePadding(10);
                        textView.setText(posStr);
                        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
                        textView.setLayoutParams(dim);
                    }
                } catch (Exception e) {
                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setText(str);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.black));
                    textView.setLayoutParams(dim);
                }
            }
        }
    }

 private void finishPage() {
        setResult(RESULT_OK);
        finish();
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

    private void onSetUiAfterPurchase() {
        if (Slave.hasPurchased(this)) {
            textViewWithADs.setText(getResources().getString(R.string.continuet));
            textViewWithADs.setVisibility(View.GONE);
            backArrow.setVisibility(View.VISIBLE);
//            buttonSubs.setVisibility(View.GONE);
        } else {
            textViewWithADs.setText(getResources().getString(R.string.continue_with_ads));
        }
    }


    @Override
    public void onViewClicked(int position) {
        Billing b = mBillingList.get(position);

        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK");

        if(Slave.hasPurchased(this)) {
            showDataAccordingToPurchase();
        }else {
            buttonSubs.setText(b.button_text);
        }
        if (b.details_description.contains("#")) {
            String[] splitText = b.details_description.split("#");
            tv_description.setText("");
            String s= splitText[0]+""+Html.fromHtml(b.product_price).toString()+""+splitText[1];
            tv_description.setText(s);
        } else {
            tv_description.setText(b.details_description);
        }
    }

    @Override
    public void onViewClicked(View mView, int position) {
        Billing b = mBillingList.get(position);
        if(Slave.hasPurchased(this)) {
            showDataAccordingToPurchase();
        }else {
            buttonSubs.setText(b.button_text);
        }

    }

    private void onClickForPurchaseButton(View mView, int position) {
        Billing b = mBillingList.get(position);
        productName = b.product_offer_text;

        Log.d("BillingListActivity", "Test onViewClicked...." + b.billing_type);
        switch (b.billing_type) {
            case Slave.Billing_Free:
                if (!Slave.hasPurchased(this)) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_FREE");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }
                break;
            case Slave.Billing_Pro:
                if (!Slave.IS_PRO) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_PRO");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }
                break;

            case Slave.Billing_Weekly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY && !Slave.IS_MONTHLY) && !Slave.IS_WEEKLY) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_WEEKLY");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }
                break;
            case Slave.Billing_Monthly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY && !Slave.IS_QUARTERLY) && !Slave.IS_MONTHLY) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_MONTHLY");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }

                break;
            case Slave.Billing_Quarterly:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY && !Slave.IS_HALFYEARLY) && !Slave.IS_QUARTERLY) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_QUARTERLY");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }

                break;
            case Slave.Billing_HalfYear:
                if ((!Slave.IS_PRO && !Slave.IS_YEARLY) && !Slave.IS_HALFYEARLY) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_FREE_HALFYEARLY");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
                }

                break;

            case Slave.Billing_Yearly:
                if (!Slave.IS_PRO && !Slave.IS_YEARLY) {
                    AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_ITEM_CLICK_YEARLY");

                    onClickPurchase(b);
                } else {
                    Toast.makeText(BillingListActivityNew.this, "You are already a premium member", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.subs_now) {
            if (billingListAdapter != null) {
                onClickForPurchaseButton(view, billingListAdapter.getSelectedPos());
            }
//            Toast.makeText(this, getResources().getString(R.string.tap_on_select_plans), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.conti_with_ads) {
            finishPage();
        } else if (id == R.id.manange_subs) {
            openPlaystoreAccount();
        } else if (id == R.id.iv_back) {
            finishPage();
        }
    }

    @Override
    public void onPurchaseSuccess(ArrayList<Purchase> purchase) {
        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_PURCHASE_SUCCESSFULL");

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
        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SET_PURCHASEDATA");

        for (Billing b : BillingResponseHandler.getInstance().getBillingResponse()) {
            switch (b.billing_type) {
                case Slave.Billing_Pro:
                    if (productId.contains(b.product_id)) {
                        mPreference.setPro(true);
                        Slave.IS_PRO = mPreference.isPro();
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_Pro");

                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Weekly:
                    if (productId.contains(b.product_id)) {
                        mPreference.setWeekly(true);
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_Weekly");

                        Slave.IS_WEEKLY = mPreference.isWeekly();
                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Monthly:
                    if (productId.contains(b.product_id)) {
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_Monthly");

                        mPreference.setMonthly(true);
                        Slave.IS_MONTHLY = mPreference.isMonthly();
                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Quarterly:
                    if (productId.contains(b.product_id)) {
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_Quarterly");

                        mPreference.setQuarterly(true);
                        Slave.IS_QUARTERLY = mPreference.isQuarterly();
                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_HalfYear:
                    if (productId.contains(b.product_id)) {
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_HalfYear");

                        mPreference.setHalfYearly(true);
                        Slave.IS_HALFYEARLY = mPreference.isHalfYearly();
                        showPurchaseDialog(b.product_id);
                        break;
                    }

                case Slave.Billing_Yearly:
                    if (productId.contains(b.product_id)) {
                        AppAnalyticsKt.logGAEvents(this, "AN_BILLING_PAGE_SUCCESSFULL_Billing_Yearly");

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

    private void showPurchaseDialog(String productId) {
        final Dialog dialog = new Dialog(this, R.style.BaseTheme);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.purchase_ok);

        AppAnalyticsKt.logGAEvents(this, EngineAnalyticsConstant.Companion.getFIREBASE_BILLING_PURCHASE_EVENT()+""+productId);

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
        }
    }

    @Override
    public void onBackPressed() {

        finishPage();
        super.onBackPressed();
    }

    private void openPlaystoreAccount() {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions"));
            startActivity(browserIntent);
        } catch (Exception e) {
            Log.d("BillingListActivity", "Test openPlaystoreAccount.." + e.getMessage());
        }
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
//                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_unsub_btn));
                    }
                    break;

                case Slave.Billing_Pro:
                    if (Slave.IS_PRO) {
                        buttonSubs.setEnabled(false);
                        buttonSubs.setText("Subscribed");
//                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_unsub_btn));
                    }
                    break;

//                default:
//                    if (!Slave.hasPurchased(this)) {
//                        buttonSubs.setEnabled(true);
//                        buttonSubs.setText(getResources().getString(R.string.start_free_trial));
//                        buttonSubs.setBackgroundDrawable(getResources().getDrawable(R.drawable.inapp_sub_btn));
//                    }
//                    break;
            }
        }

    }

}
