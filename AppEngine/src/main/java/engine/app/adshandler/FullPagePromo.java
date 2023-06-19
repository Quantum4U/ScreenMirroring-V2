package engine.app.adshandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppFullAdsListener;
import app.pnd.adshandler.R;
import engine.app.rest.request.DataRequest;
import engine.app.server.v2.DataHubHandler;
import engine.app.server.v2.InHouse;
import engine.app.socket.EngineApiController;
import engine.app.socket.EngineClient;
import engine.app.socket.Response;

public class FullPagePromo extends Activity implements DataHubHandler.InHouseCallBack {
    private ImageView adsimage;
    private String type;
    private String clickLink;
    private RelativeLayout imageRL;
    private WebView webView;
    /**
     * Please make sure fullAdsListener should be null after return any listener
     * bcoz it's static, it's called two times in failed case..
     */
    private static AppFullAdsListener fullAdsListener = null;

    public static void onStart(Context context, String type, String src, String link, AppFullAdsListener listener) {
        fullAdsListener = listener;
        Intent intent = new Intent(context, FullPagePromo.class);
        intent.putExtra("type", type);
        intent.putExtra("src", src);
        intent.putExtra("link", link);
        context.startActivity(intent);
        //fullAdsListener.onFullAdLoaded();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fullpageprompt);
        adsimage = findViewById(R.id.adsimage);
        Button exit = findViewById(R.id.exit);
        imageRL = findViewById(R.id.imageRL);
        webView = findViewById(R.id.webView);
        // fullAdsListener = this;
        System.out.println("here is the type type 0" + " ");
        try {
            Intent intent = getIntent();
            if (intent != null) {
                type = intent.getStringExtra("type");
                System.out.println("here is the type type 1" + " " + type);
            }
        } catch (Exception e) {
            System.out.println("here is the type type 2" + " " + type);
            type = EngineClient.IH_FULL;
        }
        if (type == null) {
            type = EngineClient.IH_FULL;
        }

        DataRequest request = new DataRequest();
        EngineApiController mController = new EngineApiController(FullPagePromo.this, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(FullPagePromo.this, response.toString(), FullPagePromo.this);
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                System.out.println("here is the onerr" + " " + errormsg);
                if (fullAdsListener != null) {
                    fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, errormsg);
                    fullAdsListener = null;
                }
            }
        }, EngineApiController.INHOUSE_CODE);
        System.out.println("here is the type type 3" + " " + type);
        mController.setInHouseType(type);
        mController.getInHouseData(request);


        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
              /*  if (fullAdsListener != null) {
                    fullAdsListener.onFullAdClosed();
                }
                finish();*/
                onBackPressed();
            }
        });

        adsimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (clickLink != null && !clickLink.isEmpty()) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        //i.setData(Uri.parse(MasterData.cp_link));
                        i.setData(Uri.parse(clickLink));
                        startActivity(i);
                    } else {
                        onBackPressed();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*if(fullAdsListener!=null) {
                    fullAdsListener.onFullAdClosed();
                }
                finish();*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("NewEngine FullPagePromo.onBackPressed..");
        if (fullAdsListener != null) {
            fullAdsListener.onFullAdClosed();
            fullAdsListener = null;
        }
    }

    @Override
    public void onInhouseDownload(InHouse inHouse) {
        System.out.println("here is the onInhouseDownload " + inHouse.html + " " + inHouse.src + " " + inHouse.clicklink);
        if (inHouse.campType != null) {
            if (inHouse.campType.equalsIgnoreCase("webhtml")) {
                loadWebHtml(inHouse);

            } else if (inHouse.campType.equalsIgnoreCase("html")) {
                loadHTML(inHouse);

            } else {
                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                    this.clickLink = inHouse.clicklink;
                }

                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                    webView.setVisibility(View.GONE);
                    imageRL.setVisibility(View.VISIBLE);
                    Picasso.get().load(inHouse.src).into(adsimage);
                } else {
                    if (fullAdsListener != null) {
                        fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "camType Null");
                        fullAdsListener = null;
                    }
                }
            }
        } else {
            if (fullAdsListener != null) {
                fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "camType Null");
                fullAdsListener = null;
            }
        }

    }


    private void loadWebHtml(InHouse inHouse) {
        if (inHouse.html != null && inHouse.html.contains("html")) {
            imageRL.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDisplayZoomControls(false);

            webView.setWebViewClient(new NavWebViewClient(this));

            webView.loadUrl(inHouse.html);
            //webView.loadUrl("https://www.quantum4u.in/");

        } else {
            if (fullAdsListener != null) {
                fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "camType Null");
                fullAdsListener = null;
            }
        }
    }

    private void loadHTML(InHouse inHouse) {
        if (inHouse.html != null) {
            imageRL.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            //webView.loadUrl(inHouse.html);
            webView.loadData(inHouse.html, "text/html", null);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDisplayZoomControls(false);

//                webView.setWebViewClient(new NavWebViewClient());
        } else {
            if (fullAdsListener != null) {
                fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "camType Null");
                fullAdsListener = null;
            }
        }


    }

    private static class NavWebViewClient extends WebViewClient {
        private Activity activity;

        public NavWebViewClient(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            if (fullAdsListener != null) {
                fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "failed in house");
                activity.finish();
                fullAdsListener = null;
            }
        }


        /*@Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (fullAdsListener != null) {
                fullAdsListener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "Inhouse Errorcode " + errorResponse.getStatusCode());
                activity.finish();
                fullAdsListener = null;
            }
        }*/
    }


}
