package engine.app.exitapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import app.pnd.adshandler.R;
import engine.app.adshandler.AHandler;
import engine.app.analytics.EngineAnalyticsConstant;
import engine.app.exitapp.Type6.Type6Adapter;
import engine.app.fcm.MapperUtils;
import engine.app.listener.RecyclerViewClickListener;
import engine.app.server.v2.ExitAppListResponse;
import engine.app.server.v2.Slave;
import engine.app.serviceprovider.Utils;

public class ExitAdsActivity extends AppCompatActivity implements RecyclerViewClickListener, View.OnClickListener , DiscreteScrollView.OnItemChangedListener<Type6Adapter.ViewHolder>{
    private String exitType;
    private LinearLayout exit_ads,rl_parentPro;
    private LinearLayout linearLayoutExitType4;
    private ImageView imageViewExitType4;
    public static final String EXIT_MAPPER_FOR_APP ="Exit_Mapper_For_App";


    private InfiniteScrollAdapter<?> infiniteAdapter;
    private List<ExitAppListResponse> exitAppListResponses;
    ImageView imageButtom;
    TextView title;
    TextView subTitle;
    TextView btn;
    RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_layout_type);
        Intent intent = getIntent();
        if (intent != null) {
            exitType = intent.getStringExtra(EngineAnalyticsConstant.Companion.getExitPageType());
        }
        exit_ads = findViewById(R.id.exit_native_large);
        linearLayoutExitType4 = findViewById(R.id.ll_type4);
        imageViewExitType4 = findViewById(R.id.exit_type4_banner);

        rl_parentPro = findViewById(R.id.rl_parentPro);

        if(exitType.equals(Slave.EXIT_TYPE6)){
            imageButtom = findViewById(R.id.iv_pro);
            title = findViewById(R.id.tv_pro_title);
            subTitle = findViewById(R.id.tv_pro_subtitle);
            btn = findViewById(R.id.btn_pro);
            ratingBar = findViewById(R.id.ratingBar1);
        }

        try {
            onSetExitTypeData();
            onSetButtomLayout();
        } catch (Exception e) {
            Log.d("ExitAdsActivity", "ExitAdsActivity onCreate ..." + e.getMessage());
        }

    }

    private void onSetButtomLayout() {
        Log.d("fvbjdf", "Test onSetButtomLayout..." + Slave.Exit_Msz_Text + "  " + Slave.Exit_Neg_Button_Bg + "  " +
                Slave.Exit_Neg_Button_Text + "  " + Slave.Exit_Neg_Button_TextColor);

        ImageView imageViewBanner = findViewById(R.id.exit_banner);
        TextView textViewExitMessage = findViewById(R.id.txt_exit);
        textViewExitMessage.setText(Slave.Exit_Msz_Text);


        TextView textViewExitNo = findViewById(R.id.exit_btn_no);
        textViewExitNo.setText(Slave.Exit_Neg_Button_Text);
        textViewExitNo.setTextColor(Color.parseColor(Slave.Exit_Neg_Button_Bg));
        textViewExitNo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Slave.Exit_Neg_Button_TextColor)));

        TextView textViewExitYes = findViewById(R.id.exit_btn_yes);
        textViewExitYes.setText(Slave.Exit_Pos_Button_Text);
        textViewExitYes.setTextColor(Color.parseColor(Slave.Exit_Pos_Button_TextColor));
        textViewExitYes.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Slave.Exit_Pos_Button_Bg)));
        Log.d("fvbjdf", "Test onSetButtomLayout..." + Slave.Exit_Msz_Text + "  " + Slave.Exit_Buttom_Banner_Src);
      //  Picasso.get().load(Slave.Exit_Buttom_Banner_Src).into(imageViewBanner);

        if(Slave.Exit_Buttom_Banner_Src!=null && !Slave.Exit_Buttom_Banner_Src.isEmpty()){
            setImageFromPicasso(Slave.Exit_Buttom_Banner_Src, imageViewBanner,R.drawable.ic_default_exit_image);
        }else {
            onLoadErrorImage(imageViewBanner,R.drawable.ic_default_exit_image);
        }

    }

    private void onMoreDefaultLayout(){
        RelativeLayout relativeLayoutDefaultMore = findViewById(R.id.rl_default_more_apps);
        onSetVisisbility(relativeLayoutDefaultMore, View.VISIBLE);
        relativeLayoutDefaultMore.setOnClickListener(this);
    }
    private void onSetType1_2() {
        if (Slave.hasPurchased(this) || !Utils.isNetworkConnected(this)) {
         onMoreDefaultLayout();
        } else {
            switch (exitType) {
                case Slave.EXIT_TYPE2:
                    exit_ads.addView(AHandler.getInstance().getNativeLarge(this));
                    break;
                case Slave.EXIT_TYPE3:
                    exit_ads.addView(AHandler.getInstance().getBannerRectangle(this));
                    break;
            }
        }
    }

    private void onSetExitTypeData() {
        Log.d("ExitAdsActivity", "Enginev2 Exit page type.." + exitType);
        switch (exitType) {
            case Slave.EXIT_TYPE2:
                onSetType1_2();
                break;
            case Slave.EXIT_TYPE3:
                onSetType1_2();
                break;
            case Slave.EXIT_TYPE4:
                onSetExitHeader();
                onSetVisisbility(linearLayoutExitType4, View.VISIBLE);
                onSetVisisbility(exit_ads, View.GONE);
                if(Slave.Exit_Top_Banner_Src!=null && !Slave.Exit_Top_Banner_Src.isEmpty()){
                    setImageFromPicasso(Slave.Exit_Top_Banner_Src, imageViewExitType4,0);
                }else {
                    onLoadErrorImage(imageViewExitType4,0);
                }

                imageViewExitType4.setOnClickListener(this);

                break;
            case Slave.EXIT_TYPE5:
                onSetExitHeader();
                RecyclerView recyclerView = findViewById(R.id.exit_type5_rv);
                onSetVisisbility(linearLayoutExitType4, View.VISIBLE);
                onSetVisisbility(imageViewExitType4, View.GONE);
                onSetVisisbility(exit_ads, View.GONE);
                onSetVisisbility(recyclerView, View.VISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                ArrayList<ExitAppListResponse> mExitList = new ArrayList<>();
                mExitList.addAll(Slave.ExitAppList);
                ExitListAdapter exitListAdapter = new ExitListAdapter(this, mExitList, this);
                recyclerView.setAdapter(exitListAdapter);
                break;
            case Slave.EXIT_TYPE6:
                onSetExitHeader();
                onSetVisisbility(exit_ads, View.GONE);
                showType6();
                break;
        }
    }

    private void onSetExitHeader(){
        TextView textViewType4Header = findViewById(R.id.exit_type4_header);
        textViewType4Header.setText(Slave.Exit_Top_Banner_Header);
    }

    private void onSetVisisbility(View view, int isVisible) {
        view.setVisibility(isVisible);

    }

    public void appExitExit(View view) {
        finishAffinity();
    }

    public void closeExitPromptExit(View view) {
        finish();
//        Slave.onExitCount++;
//        if(Slave.onExitCount==5){
//            Slave.onExitCount =0;
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Slave.onExitCount++;
//        if(Slave.onExitCount==5){
//            Slave.onExitCount =0;
//        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.exit_type4_banner) {
            switch (Slave.Exit_Top_Banner_Click_type) {
                case "url":
                    if (Slave.Exit_Top_Banner_Click_Value != null && !Slave.Exit_Top_Banner_Click_Value.isEmpty()) {
                        onRedirectUrl(Slave.Exit_Top_Banner_Click_Value);
                    }
                    break;
                case "deeplink":
                    //need to make confirmation
                   // callingForMapper(Slave.Exit_Top_Banner_Click_type,Slave.Exit_Top_Banner_Click_Value);
                    launchAppWithMapper(Slave.Exit_Top_Banner_Click_type,Slave.Exit_Top_Banner_Click_Value);
                    break;
            }
        }
        if (view.getId() == R.id.rl_default_more_apps) {
            new Utils().moreApps(this);
        }
    }

    private void setImageFromPicasso(String src, ImageView imageView, int placeHolder) {

//        Log.d("fvbjdf","NewEngine showFullAdsOnLaunch type 4 "
//                +Slave.Exit_Buttom_Banner_Src +"  "+src+"  "+placeHolder);
            Picasso.get()
                    .load(src)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("fvbjdf", "NewEngine showFullAdsOnLaunch type 4 fail  " + placeHolder + "  " + src);
                            onLoadErrorImage(imageView, placeHolder);
                        }
                    });

    }

    private void onLoadErrorImage(ImageView imageView, int placeHolder){

        if(Slave.EXIT_TYPE.equals(Slave.EXIT_TYPE4) && placeHolder==0){
            onSetVisisbility(imageViewExitType4, View.GONE);
            onMoreDefaultLayout();
        }else {
            // Try again online if cache failed
            Picasso.get()
                    .load(placeHolder)
                    .error(placeHolder)
                    .into(imageView);
        }
    }

    @Override
    public void onListItemClicked(View mView, String reDirectUrl) {
        if (mView != null && reDirectUrl != null && !reDirectUrl.isEmpty()) {
            onRedirectUrl(reDirectUrl);
        }
    }

    @Override
    public void onViewClicked(View mView, int position) {

    }

    private void onRedirectUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void showType6() {
        try {
            rl_parentPro.setVisibility(View.VISIBLE);
            DiscreteScrollView itemPicker = findViewById(R.id.discreteList);
            onSetVisisbility(itemPicker, View.VISIBLE);
            onSetVisisbility(rl_parentPro, View.VISIBLE);
            exitAppListResponses = Slave.ExitAppList;

            itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
            itemPicker.addOnItemChangedListener(this);
            infiniteAdapter = InfiniteScrollAdapter.wrap(new Type6Adapter(exitAppListResponses, this));
            itemPicker.setAdapter(infiniteAdapter);
            itemPicker.setItemTransitionTimeMillis(150);
            itemPicker.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
        }catch (Exception e){
            Log.d("ExitAdsActivity", "Test showType6.."+e.getMessage());
        }

    }


    @Override
    public void onCurrentItemChanged(@Nullable Type6Adapter.ViewHolder viewHolder, int i) {

        int positionInDataSet = infiniteAdapter.getRealPosition(i);
        final ExitAppListResponse exitAppListResponse = exitAppListResponses.get(positionInDataSet);

        Log.d("ExitAdsActivity", "Hello onCurrentItemChanged oopss " + " " +
                exitAppListResponses.get(positionInDataSet).app_list_src);

        if(exitAppListResponse.app_list_icon_src!=null && !exitAppListResponse.app_list_icon_src.isEmpty()){
            setImageFromPicasso(exitAppListResponse.app_list_icon_src, imageButtom, R.drawable.ic_exit_app_list_default);
        }else {
            onLoadErrorImage(imageViewExitType4,0);
        }
        title.setText("" + exitAppListResponse.app_list_title);
        subTitle.setText("" + exitAppListResponse.app_list_subtitle);
        btn.setText("" + exitAppListResponse.app_list_button_text);
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(exitAppListResponse.app_list_button_bg)));
        btn.setTextColor(
                ColorStateList.valueOf(Color.parseColor(exitAppListResponse.app_list_button_text_color)));
        ratingBar.setRating(Float.parseFloat((exitAppListResponse.app_list_rate_count)));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (exitAppListResponse.app_list_redirect != null && !exitAppListResponse.app_list_redirect.isEmpty()) {
                    onRedirectUrl(exitAppListResponse.app_list_redirect);
                }
            }
        });

    }


    private void launchAppWithMapper(String type, String value) {
        Log.d("EXitPageWithType", "Checking ExitPage Type4 DeepLink .."+type+"  "+value);

        Intent intent = new Intent(EXIT_MAPPER_FOR_APP);
        intent.putExtra(MapperUtils.keyType, type);
        intent.putExtra(MapperUtils.keyValue, value);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

       finish();
    }
}


