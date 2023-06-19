package engine.app.serviceprovider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import app.pnd.adshandler.R;
import engine.app.adshandler.FullPagePromo;
import engine.app.campaign.CampaignConstant;
import engine.app.campaign.CampaignHandler;
import engine.app.campaign.response.AdsIcon;
import engine.app.enginev4.AdsEnum;
import engine.app.listener.AppAdsListener;
import engine.app.listener.AppFullAdsListener;
import engine.app.rest.request.DataRequest;
import engine.app.server.v2.DataHubHandler;
import engine.app.server.v2.InHouse;
import engine.app.server.v2.Slave;
import engine.app.socket.EngineApiController;
import engine.app.socket.EngineClient;
import engine.app.socket.Response;

/**
 * Created by rajeev on 29/06/17.
 */

public class InHouseAds {
    public static final String TYPE_NATIVE_MEDIUM = EngineClient.IH_NM;
    public static final String TYPE_BANNER_HEADER = EngineClient.IH_TOP_BANNER;
    public static final String TYPE_BANNER_FOOTER = EngineClient.IH_BOTTOM_BANNER;
    public static final String TYPE_NATIVE_LARGE = EngineClient.IH_NL;
    public static final String TYPE_BANNER_LARGE = EngineClient.IH_BANNER_LARGE;
    public static final String TYPE_BANNER_RECTANGLE = EngineClient.IH_BANNER_RECTANGLE;

    //public static String TYPE_CP_START = EngineClient.IH_CP_START;
    //public static String TYPE_CP_EXIT = EngineClient.IH_CP_EXIT;

    private Display display;
    private String clickBF, clickBH, clickBL, clickBR, clickNM, clickNL;

    public void getBannerFooter(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.banner_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    @Override
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " INHOUSE SRC NULL ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickBF = inHouse.clicklink;
                                }
                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);
                            }
                        }
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);
            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickBF != null && !clickBF.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickBF));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void getBannerHeader(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.banner_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE banner header" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    @Override
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " Inhouse src null ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickBH = inHouse.clicklink;
                                }
                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);
                            }
                        } else {
                            listener.onAdFailed(AdsEnum.ADS_INHOUSE, " Inhouse campType null or not valid ");
                        }


                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);
            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickBH != null && !clickBH.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickBH));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void getBannerLarge(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.banner_large_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    @Override
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " INHOUSE SRC NULL ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickBL = inHouse.clicklink;
                                }

                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);

                            }
                        }
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);
            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickBL != null && !clickBL.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickBL));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void getBannerRectangle(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int) context.getResources().getDimension(R.dimen.banner_rectangle_width), (int) context.getResources().getDimension(R.dimen.banner_rectangle_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    @Override
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " INHOUSE SRC NULL ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickBR = inHouse.clicklink;
                                }

                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);
                            }
                        }
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);
            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickBR != null && !clickBR.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickBR));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void showNativeMedium(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.native_medium_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    @Override
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " INHOUSE SRC NULL ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickNM = inHouse.clicklink;
                                }

                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);
                            }
                        }
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);

            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickNM != null && !clickNM.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickNM));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void showNativeLarge(@NotNull final Context context, String type, final AppAdsListener listener) {
        display = ((Activity) context).getWindowManager().getDefaultDisplay();

        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.native_large_height));
        final LinearLayout ll = new LinearLayout(context);
        ll.setLayoutParams(params);


        DataRequest request = new DataRequest();
        EngineApiController controller = new EngineApiController(context, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("here is the response of INHOUSE" + " " + response);
                new DataHubHandler().parseInHouseService(context, response.toString(), new DataHubHandler.InHouseCallBack() {
                    public void onInhouseDownload(InHouse inHouse) {
                        if (inHouse.campType != null && !inHouse.campType.equals("")) {
                            if (inHouse.campType.equalsIgnoreCase("image")) {
                                ImageView iv = new ImageView(context);
                                iv.setLayoutParams(params);
                                ll.addView(iv);
                                if (inHouse.src != null && !inHouse.src.isEmpty()) {
                                    Picasso.get()
                                            .load(inHouse.src)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                            .resize(display.getWidth(), iv.getHeight())
                                            .placeholder(R.drawable.blank)
                                            .into(iv);
                                    Drawable bmp = iv.getDrawable();
                                    ll.setOrientation(LinearLayout.HORIZONTAL);
                                    ll.setBackground(bmp);
                                    listener.onAdLoaded(ll);
                                } else {
                                    listener.onAdFailed(AdsEnum.ADS_INHOUSE, " INHOUSE SRC NULL ");
                                }

                                if (inHouse.clicklink != null && !inHouse.clicklink.isEmpty()) {
                                    clickNL = inHouse.clicklink;
                                }


                            } else {
                                LayoutInflater inflater = LayoutInflater.from(context);
                                LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_web,
                                        ll, false);

                                populateWebView(inHouse.campType, adView, inHouse.html, listener);
                                ll.addView(adView);
                                listener.onAdLoaded(ll);
                            }
                        }
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, errormsg);

            }
        }, EngineApiController.INHOUSE_CODE);
        controller.setInHouseType(type);
        controller.getInHouseData(request);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickNL != null && !clickNL.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(clickNL));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    public void loadGridViewNativeAdsView(final Activity ctx, String id, AppAdsListener listener) {
        System.out.println("InHouseAds.loadGridViewNativeAdsView " + id);
        LinearLayout nativeAdContainer = new LinearLayout(ctx);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_inhouse_grid_layout,
                nativeAdContainer, false);

        ImageView icon = adView.findViewById(R.id.inhouse_img);
        TextView title = adView.findViewById(R.id.inhouse_text);
        RelativeLayout rl = adView.findViewById(R.id.inhouse_lay);

        CampaignHandler handler = CampaignHandler.getInstance();
        final CampaignConstant campaignConstant = new CampaignConstant(ctx);
        final ArrayList<AdsIcon> list = handler.loadIconAdsList();
        if (list != null && list.size() > 0) {
//            moreLayout.setVisibility(View.GONE);
            if (list.get(0).src != null && !list.get(0).src.equalsIgnoreCase("") &&
                    list.get(0).src.startsWith("http:")) {
                Picasso.get().load(list.get(0).src).placeholder(R.drawable.app_icon).into(icon);
            }

            if (!list.get(0).srctext.equalsIgnoreCase("")) {
                title.setText(list.get(0).srctext);
            }

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Slave.hasPurchased(ctx)) {
                        campaignConstant.crossPromotionDialog(ctx,
                                new CampaignConstant.OnCPDialogClick() {
                                    @Override
                                    public void clickOK() {
                                        if (list.size() > 0) {
                                            if (list.get(0).subtype.equalsIgnoreCase(CampaignConstant.CAMPAIGN_TYPE_DEEPLINK)) {
                                                campaignConstant.openDeepLink(ctx, list.get(0).page_id);
                                            } else if (list.get(0).subtype.equalsIgnoreCase(CampaignConstant.CAMPAIGN_TYPE_URL)) {
                                                if (list.get(0).clickurl != null && !list.get(0).clickurl.equalsIgnoreCase("") &&
                                                        list.get(0).clickurl.startsWith("http:")) {
                                                    campaignConstant.openURL(ctx, list.get(0).clickurl);
                                                }

                                            }
                                        } else {
                                            new Utils().moreApps(ctx);
                                        }
                                    }

                                }, list.get(0).headertext, list.get(0).description, list.get(0).bgcolor, list.get(0).textcolor, list.get(0).src);
                    }
                }
            });

            listener.onAdLoaded(nativeAdContainer);

        } else {
            listener.onAdFailed(AdsEnum.ADS_INHOUSE, "list can't be null or list size will be > 0 ");
        }


    }


    public void showFullAds(Context context, String type, String src, String link, AppFullAdsListener listener) {
        System.out.println("NewEngine InHouseAds.showFullAds " + type + " " + src + " " + link);
        if (type != null /*&& src != null && link != null*/) {

            FullPagePromo.onStart(context, type, src, link, listener);

//            Intent intent = new Intent(context, FullPagePromo.class);
//            intent.putExtra("type", type);
//            intent.putExtra("src", src);
//            intent.putExtra("link", link);
//            context.startActivity(intent);
//            listener.onFullAdLoaded();
        } else {
            listener.onFullAdFailed(AdsEnum.FULL_ADS_INHOUSE, "type null ");
        }
    }

    private void populateWebView(@NotNull String campType, @NotNull LinearLayout adView, String data, AppAdsListener listener) {
        WebView webView = adView.findViewById(R.id.webView);
        if (campType.equalsIgnoreCase("html")) {
            if (data != null) {
                webView.loadData(data, "text/html", null);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);

                // webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new NavWebViewClient());
            } else {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, "load data null");
            }

        } else if (campType.equalsIgnoreCase("webhtml")) {
            if (data != null && data.contains("html")) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setDisplayZoomControls(false);

                webView.setWebViewClient(new NavWebClient(listener));
                webView.loadUrl(data);

            } else {
                listener.onAdFailed(AdsEnum.ADS_INHOUSE, "check inhouse response");
            }

        }


    }


    private static class NavWebClient extends WebViewClient {
        private AppAdsListener listener;

        public NavWebClient(AppAdsListener listener) {
            this.listener = listener;
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
            listener.onAdFailed(AdsEnum.ADS_INHOUSE, "failed in house");
        }


       /* @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            listener.onAdLoaded(null);
            listener.onAdFailed(AdsEnum.ADS_INHOUSE, "Inhouse Errorcode " + errorResponse.getStatusCode());
        }*/


    }


}
