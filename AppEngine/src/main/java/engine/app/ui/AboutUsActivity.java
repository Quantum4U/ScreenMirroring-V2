package engine.app.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import app.pnd.adshandler.R;
import engine.app.adshandler.PromptHander;
import engine.app.server.v2.Slave;
import engine.app.serviceprovider.Utils;


/**
 * Created by Rakesh Rajput on 30/12/2020.
 */

public class AboutUsActivity extends AppCompatActivity {
    private int i = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.mToolBar);
        setSupportActionBar(toolbar);*/

        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i == 10) {
                    i = 0;
                    ShowAssetValueDialog showAssetValueDialog = new ShowAssetValueDialog(AboutUsActivity.this, new ShowAssetValueDialog.OnSelecteShowValueCallBack() {
                        @Override
                        public void onShowValueSelected(int position) {
                            Intent myIntent = new Intent(AboutUsActivity.this, PrintActivity.class);
                            myIntent.putExtra(Utils.SHOW_VALUE, position);
                            startActivity(myIntent);
                        }
                    });
                    showAssetValueDialog.setCancelable(false);
                    showAssetValueDialog.show();

                }
            }
        });
        TextView tv_query = findViewById(R.id.tv_query);

        TextView tv_appversion = findViewById(R.id.tv_appversion);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tv_appversion.setText("Ver. " + version);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String myString = "if any issues/Query please contact us ";
        int i1 = myString.indexOf("c");
        int i2 = myString.indexOf("us");
        tv_query.setMovementMethod(LinkMovementMethod.getInstance());
        tv_query.setText(myString, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable) tv_query.getText();
        ClickableSpan myClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                new Utils().sendFeedback(AboutUsActivity.this);
            }
        };
        mySpannable.setSpan(myClickableSpan, i1, i2 + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        RelativeLayout rl_website = findViewById(R.id.rl_website);
        RelativeLayout rl_our_apps = findViewById(R.id.rl_our_apps);
        RelativeLayout rl_terms_of_service = findViewById(R.id.rl_terms_of_service);
        RelativeLayout rl_privacy_policy = findViewById(R.id.rl_privacy_policy);


        rl_website.setOnClickListener(mOnClickListener);
        rl_our_apps.setOnClickListener(mOnClickListener);
        rl_terms_of_service.setOnClickListener(mOnClickListener);
        rl_privacy_policy.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.rl_website) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_WEBSITELINK)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.rl_our_apps) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_OURAPP)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.rl_terms_of_service) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_TERM_AND_COND)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.rl_privacy_policy) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_PRIVACYPOLICY)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    };

    public void followOnFacebook(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_FACEBOOK)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void followOnInstagram(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_INSTA)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void followOnTwitter(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Slave.ABOUTDETAIL_TWITTER)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFeedback(View view) {
        new Utils().sendFeedback(AboutUsActivity.this);
    }

    public void rateUS(View view) {
        new PromptHander().rateUsDialog(true, AboutUsActivity.this);

    }

    public void finishActivity(View view) {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}