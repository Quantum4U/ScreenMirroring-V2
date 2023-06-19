package engine.app.adshandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import app.pnd.adshandler.R;
import engine.app.PrintLog;
import engine.app.analytics.AppAnalyticsKt;
import engine.app.analytics.EngineAnalyticsConstant;
import engine.app.exitapp.ExitAdsActivity;
import engine.app.inapp.InAppReviewManager;
import engine.app.listener.InAppReviewListener;
import engine.app.server.v2.DataHubConstant;
import engine.app.server.v2.Slave;
import engine.app.serviceprovider.Utils;

public class PromptHander {

    void checkForNormalUpdate(Context context) {
        PrintLog.print("checking " + Slave.UPDATES_updateTYPE);
        if (Slave.UPDATES_updateTYPE.equals(Slave.IS_NORMAL_UPDATE)) {

            if (!getCurrentAppVersion(context).equals(
                    Slave.UPDATES_version)) {
                if (DataHubConstant.APP_LAUNCH_COUNT % 3 == 0) {
                    openNormalupdateAlert(context, Slave.UPDATES_prompttext, false);
                }

            }
        }

    }

    void checkForForceUpdate(Context context) {
        try {
            if (Slave.UPDATES_updateTYPE.equals(Slave.IS_FORCE_UPDATE)) {

                if (!getCurrentAppVersion(context).equals(
                        Slave.UPDATES_version)) {

                    openNormalupdateAlert(context, Slave.UPDATES_prompttext, true);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getCurrentAppVersion(Context context) {
        try {
            PackageInfo pInfo;
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int verCode = pInfo.versionCode;

            return "" + verCode;

        } catch (Exception e) {
            return "100";
        }

    }

    private void openNormalupdateAlert(final Context context, String msg, final boolean is_force) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("Update App");
        alertDialogBuilder.setIcon(R.drawable.app_icon);
        alertDialogBuilder.setMessage(msg);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("UPDATE NOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(Slave.UPDATES_appurl));
                        context.startActivity(i);
                        dialog.cancel();
                        if (is_force) {
                            ((Activity) context).finish();
                        }
                    }
                });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("NO THANKS!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                        if (is_force) {
                            ((Activity) context).finish();
                        }

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
        if (is_force) {
            alertDialog.setCancelable(false);
        } else {
            alertDialog.setCancelable(true);
        }
    }


    public void crossPromotionDialog(Context context, OnCPDialogClick onCPDialogClick, String header, String mDescription, String bgColor, String textColor, String cpURL) {
        mCPDialogClick = onCPDialogClick;
        final Dialog dialog = new Dialog(context, R.style.BaseTheme);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.remove_ads);

        Picasso.get().load(cpURL).placeholder(R.drawable.app_icon).into((ImageView) dialog.findViewById(R.id.cpIcon));

        TextView btn_dismiss = dialog.findViewById(R.id.btn_dismiss);
        TextView btn_download_now = dialog.findViewById(R.id.btn_download_now);

        TextView title = dialog.findViewById(R.id.title);
        TextView description = dialog.findViewById(R.id.description);
        FrameLayout frame_dialog = dialog.findViewById(R.id.frame_dialog);

        GradientDrawable gd = (GradientDrawable) frame_dialog.getBackground();
        gd.setColor(Color.parseColor(bgColor));
        frame_dialog.setBackground(gd);

        title.setText(textColor);
        description.setText(textColor);

        title.setText(header);
        description.setText(mDescription);

        btn_download_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mCPDialogClick.clickOK();
            }
        });

        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private OnCPDialogClick mCPDialogClick;

    public interface OnCPDialogClick {
        void clickOK();
    }

    public void rateUsDialog(boolean showCustom , final Activity context) {
        if(showCustom){
            rateUsDialogAll(context);
        }else {
           new InAppReviewManager(context).initReviews(new InAppReviewListener() {
               @Override
               public void OnReviewComplete() {

               }

               @Override
               public void OnReviewFailed() {

               }
           });
        }

    }

  /*  private void rateUsDialogAll(final Context context) {

        final RatingBar ratingBar;
        *//*
         * final Dialog dialog = new Dialog(MainMenu.this,
         * android.R.style.Theme_Light_NoTitleBar);
         *//*
        final Dialog dialog = new Dialog(context, R.style.BaseTheme);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rate_us_layout);

        ratingBar = dialog.findViewById(R.id.ratingBar1);
        final LinearLayout parent = dialog.findViewById(R.id.parent);

        System.out.println("PromptHander.rateUsDialog 001 " + Slave.RATE_APP_BG_COLOR + " " + Slave.RATE_APP_rateapptext);
        if (Slave.RATE_APP_BG_COLOR != null && !Slave.RATE_APP_BG_COLOR.equals("")) {
            GradientDrawable gd = (GradientDrawable) parent.getBackground();
            gd.setColor(Color.parseColor(Slave.RATE_APP_BG_COLOR));
            parent.setBackground(gd);
        }

        final TextView tvRateDescription = dialog.findViewById(R.id.tvRateDescription);
        if (Slave.RATE_APP_rateapptext != null && !Slave.RATE_APP_rateapptext.equals("")) {
            tvRateDescription.setText(Slave.RATE_APP_rateapptext);
        }

        final TextView tv_Header = dialog.findViewById(R.id.tv_Header);
        if (Slave.RATE_APP_HEADER_TEXT != null && !Slave.RATE_APP_HEADER_TEXT.equals("")) {
            tv_Header.setText(Slave.RATE_APP_HEADER_TEXT);
        }

        final Button submit = dialog.findViewById(R.id.submitlinear);
        final LinearLayout one = dialog.findViewById(R.id.one);
        final LinearLayout two = dialog.findViewById(R.id.two);
        final LinearLayout three = dialog.findViewById(R.id.three);
        final LinearLayout four = dialog.findViewById(R.id.four);
        final LinearLayout five = dialog.findViewById(R.id.five);
       // final Button dismiss = dialog.findViewById(R.id.remindlinear);
        final ImageView dismiss = dialog.findViewById(R.id.btn_dismiss);



        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ExitAdsActivity) {
                    AppAnalyticsKt.logGAEvents(context, EngineAnalyticsConstant.Companion.getGA_RATE_US_DISMISS_BTN());
                }
                dialog.dismiss();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float n = ratingBar.getRating();
                if (context instanceof ExitAdsActivity) {
                    AppAnalyticsKt.logGAEvents(context, EngineAnalyticsConstant.Companion.getGA_RATE_US_SUBMIT_BTN_STAR_());

                }
                if (n == 0) {
                    Toast.makeText(context, "Please select rating stars", Toast.LENGTH_SHORT).show();
                } else if (n <= 3 & n > 0) {
                    PrintLog.print("Rate App URL is 0 PPPP");
                    dialog.dismiss();
                    new Utils().sendFeedback(context);

                } else {
                    PrintLog.print("Rate App URL is 0 ");
                    new Utils().rateUs(context);
                    dialog.dismiss();


                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                float n = ratingBar.getRating();
                if (n <= 0) {
                    submit.setTextColor(context.getResources().getColor(R.color.dark_grey));
                } else {
                    submit.setTextColor(context.getResources().getColor(android.R.color.black));
                    submit.setEnabled(true);
                }
            }
        });
        dialog.show();

    }*/



    private void rateUsDialogAll(final Context context) {

        final RatingBar ratingBar;
        /*
         * final Dialog dialog = new Dialog(MainMenu.this,
         * android.R.style.Theme_Light_NoTitleBar);
         */
        final Dialog dialog = new Dialog(context, R.style.BaseTheme);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rate_us_layout);

        ratingBar = dialog.findViewById(R.id.ratingBar1);
        final LinearLayout parent = dialog.findViewById(R.id.parent);

        System.out.println("PromptHander.rateUsDialog 001 " + Slave.RATE_APP_BG_COLOR + " " + Slave.RATE_APP_rateapptext);
        if (Slave.RATE_APP_BG_COLOR != null && !Slave.RATE_APP_BG_COLOR.equals("")) {
            GradientDrawable gd = (GradientDrawable) parent.getBackground();
            gd.setColor(Color.parseColor(Slave.RATE_APP_BG_COLOR));
            parent.setBackground(gd);
        }

        final TextView tvRateDescription = dialog.findViewById(R.id.tvRateDescription);
        if (Slave.RATE_APP_rateapptext != null && !Slave.RATE_APP_rateapptext.equals("")) {
            tvRateDescription.setText(Slave.RATE_APP_rateapptext);
        }

        final TextView tv_Header = dialog.findViewById(R.id.tv_Header);
        if (Slave.RATE_APP_HEADER_TEXT != null && !Slave.RATE_APP_HEADER_TEXT.equals("")) {
            tv_Header.setText(Slave.RATE_APP_HEADER_TEXT);
        }

        final Button submit = dialog.findViewById(R.id.submitlinear);
        final Button dismiss = dialog.findViewById(R.id.remindlinear);
      //  final ImageView dismiss = dialog.findViewById(R.id.btn_dismiss_rate);


        dismiss.setOnClickListener(view -> {
            if (context instanceof ExitAdsActivity) {
                AppAnalyticsKt.logGAEvents(context, EngineAnalyticsConstant.Companion.getGA_RATE_US_DISMISS_BTN());
            }
            dialog.dismiss();

        });

        submit.setOnClickListener(view -> {
            float n = ratingBar.getRating();
            if (context instanceof ExitAdsActivity) {
                AppAnalyticsKt.logGAEvents(context, EngineAnalyticsConstant.Companion.getGA_RATE_US_SUBMIT_BTN_STAR_());
            }
            if (n == 0) {
                Toast.makeText(context, "Please select rating stars", Toast.LENGTH_SHORT).show();
            } else if (n <= 3 & n > 0) {
                PrintLog.print("Rate App URL is 0 PPPP");
                dialog.dismiss();
                new Utils().sendFeedback(context);

            } else {
                PrintLog.print("Rate App URL is 0 ");
                new Utils().rateUs(context);
                dialog.dismiss();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                float n = ratingBar.getRating();
                if (n <= 0) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                }
            }
        });
        dialog.show();

    }


}
