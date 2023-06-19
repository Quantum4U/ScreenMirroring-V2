package engine.app.server.v2;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import engine.app.PrintLog;
import engine.app.ecrypt.MCrypt;
import engine.app.fcm.GCMPreferences;
import engine.app.fcm.ServerResponse;
import engine.app.listener.onParseDefaultValueListener;
import engine.app.rest.response.DataResponse;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v4.AdsProviders;


/**
 * Created by hp on 9/20/2017.
 */
public final class DataHubHandler {

    private final Gson gson;
    private final MCrypt mCrypt;

    // private boolean isFromDefaultvalue=false;
    public DataHubHandler() {
        gson = new Gson();
        mCrypt = new MCrypt();
    }


    /**
     * for parsing the actual encrypted inHouse service response
     *
     * @param context  context of the application
     * @param response encrypted response contained inside "data" tag
     */
    public void parseInHouseService(Context context, String response, InHouseCallBack l) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                parseDecryptInHouse(context, dResponse, l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void parseDecryptInHouse(Context context, String response, InHouseCallBack l) {
        InHouseResponse vResp;
        try {
            if (response != null && !response.equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                vResp = gson.fromJson(response, InHouseResponse.class);

                if (vResp != null) {
                    if (vResp.message.equalsIgnoreCase(DataHubConstant.KEY_SUCESS)) {
                        if (vResp.inhouseresponse != null) {
                            l.onInhouseDownload(vResp.inhouseresponse);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * for parsing the actual encrypted version service response
     *
     * @param context  context of the application
     * @param response encrypted response contained inside "data" tag
     * @param mRl      a custom listener(call by value to func()-parseDecryptVersionData)
     */
    public void parseVersionData(Context context, String response, MasterRequestListener mRl) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print("parsing Version data encrypt" + " " + dataResponse.data);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                PrintLog.print("parsing Version data decrypt value" + " " + dResponse);

                parseDecryptVersionData(context, dResponse, mRl);

            } catch (Exception e) {
                PrintLog.print("exception version response" + " " + e);
                parseMasterData(context, new DataHubPreference(context).getAdsResponse(), null);
            }


        } else {
            parseMasterData(context, new DataHubPreference(context).getAdsResponse(), null);
        }
    }


    /**
     * for parsing the actual json obtained from decrypting the data tag of version service
     *
     * @param context  context of the application
     * @param response actual json response of version service.
     * @param mRL      a custom listener for calling master service in EngineHandler.java class
     */
    private void parseDecryptVersionData(Context context, String response, MasterRequestListener mRL) {

        VersionResponse vResp;
        DataHubConstant mConstant = new DataHubConstant(context);
        DataHubPreference preference = new DataHubPreference(context);
        try {
            if (response != null && !response.equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                vResp = gson.fromJson(response, VersionResponse.class);

                if (vResp != null) {
                    if (vResp.message.equalsIgnoreCase(DataHubConstant.KEY_SUCESS)) {
                        if (preference.getDataHubVersion().equalsIgnoreCase(vResp.app_status)) {
                            if (!preference.getAdsResponse().equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                                parseMasterData(context, preference.getAdsResponse(), null);
                            } else {
                                mRL.callMasterService();
                            }
                        } else {
                            // call master service
                            mRL.callMasterService();
                        }

                    } else {
                        if (!preference.getAdsResponse().equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                            parseMasterData(context, preference.getAdsResponse(), null);
                        } else {
                            parseMasterData(context, mConstant.parseAssetData(), null);
                        }
                    }
                } else {
                    if (!preference.getAdsResponse().equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                        parseMasterData(context, preference.getAdsResponse(), null);
                    } else {
                        parseMasterData(context, mConstant.parseAssetData(), null);
                    }
                }

            }
        } catch (Exception e) {
            PrintLog.print("Exception version parsing decrypt" + " " + e);
            parseMasterData(context, preference.getAdsResponse(), null);
        }
    }

    /**
     * for parsing the encrypted master service response
     *
     * @param context  context of the application
     * @param response encrypted response contained inside "data" object
     */
    public void parseMasterData(Context context, String response,
                                onParseDefaultValueListener onParseDefaultValueListener) {

        DataResponse dataResponse;
        System.out.println("EngineHandler.initServices ....555550000.."+"  "+response.toString());

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print(" NewEngine parsing Master data encrypt" +dataResponse.data+ "   "+"  "+response);
            System.out.println("EngineHandler.initServices ....55555111.."+dataResponse.data.toString()+"  "+response);

            try {
                System.out.println("EngineHandler.initServices ....55555.."+dataResponse.data.toString());

                String dResponse = new String(mCrypt.decrypt(dataResponse.data));

                PrintLog.print("parsing Master data decrypt value" + " " + dResponse);

                parseDecryptMasterData(context, dResponse, response, onParseDefaultValueListener);

            } catch (Exception e) {
                PrintLog.print("exception decryption" + " " + e);
                e.printStackTrace();
                parseMasterData(context, new DataHubPreference(context).getAdsResponse(), onParseDefaultValueListener);
            }
        } else {
            System.out.println("EngineHandler.initServices ....55555000011.."+"  "+response.toString());

            parseMasterData(context, new DataHubPreference(context).getAdsResponse(), onParseDefaultValueListener);
        }
    }


    /**
     * for parsing the actual json obtained from decrypting the data tag of master service
     *
     * @param context    context of the application
     * @param response   actual json response of master service, to be used to parsing and initializing Slave class
     * @param encryptKey encrypted response from server, to be stored in preferences for exception handling
     */
    private void parseDecryptMasterData(Context context,
                                        String response, String encryptKey,
                                        onParseDefaultValueListener onParseDefaultValueListener) {

        DataHubPreference preference = new DataHubPreference(context);

        try {
            if (response != null && response.length() > 100) {
                DataHubResponse hubResponse;

                if (!response.equalsIgnoreCase(DataHubConstant.KEY_NA)) {

                    hubResponse = gson.fromJson(response, DataHubResponse.class);

                    if (hubResponse != null && hubResponse.message.equalsIgnoreCase(DataHubConstant.KEY_SUCESS)) {
                        if (hubResponse.adsresponse != null && hubResponse.adsresponse.size() > 0) {

                            ArrayList<AdsResponse> adsResponses = new ArrayList<>(hubResponse.adsresponse);

                            if (adsResponses.size() > 0) {
                                loadSlaveAdResponse(adsResponses, onParseDefaultValueListener);
                            }
                        }

                        if (hubResponse.moreFeatures != null && hubResponse.moreFeatures.size() > 0) {
                            ArrayList<MoreFeature> moreFeatures = new ArrayList<>(hubResponse.moreFeatures);
                            if (moreFeatures.size() > 0) {
                                loadMoreFeatureData(moreFeatures);
                            }
                        }

                        if (hubResponse.gameProvidersResponceArrayList != null && hubResponse.gameProvidersResponceArrayList.size() > 0) {

                            Log.d("DataHubHandler", "Hello parseDecryptMasterData 0012 game provider aa" + " " + hubResponse.gameProvidersResponceArrayList.size());

                            ArrayList<GameProvidersResponce> gameresponce = new ArrayList<>(hubResponse.gameProvidersResponceArrayList);
                            if (gameresponce.size() > 0) {
                                loadGameFeatureV2(gameresponce);
                            }

                        }

                        if (hubResponse.cp != null) {
                            loadSlaveCP(hubResponse.cp);
                        }

                        if (hubResponse.cp != null) {
                            loadSlaveCP(hubResponse.cp);
                        }
                        Log.d("hello test ads load", "Hello onparsingDefault navigation 009898"
                                + " " + hubResponse.update_key);

                        preference.setDataHubVersion(hubResponse.update_key);
                        preference.setAdsResponse(encryptKey);
                        /*
                         *  preference.setJson(response) : is called just to save the response json to display it on print activity.
                         *  No other purpose of this function.
                         */
                        preference.setJSON(response);

                        if (onParseDefaultValueListener != null) {

                            Log.d("DataHubHandler", "Hello parseDecryptMasterData parseDefaultValueListener");

                            //   if(!isFromDefaultvalue){
                            //      isFromDefaultvalue=true;
                            onParseDefaultValueListener.onParsingCompleted();
                            Log.d("DataHubHandler", "Hello parseDecryptMasterData parseDefaultValueListener");
                            //   }
                        } else {
                            Log.d("DataHubHandler", "Hello parseDecryptMasterData parseDefaultValueListener .,l " + onParseDefaultValueListener);

                        }
                    } else {
                        parseMasterData(context, preference.getAdsResponse(), null);
                    }
                } else {
                    parseMasterData(context, preference.getAdsResponse(), null);
                }
            } else {
                parseMasterData(context, preference.getAdsResponse(), null);
            }


        } catch (Exception e) {
            PrintLog.print(" Enginev2 Exception get ad data" + " " + e);
            parseMasterData(context, preference.getAdsResponse(), null);
        }
    }

    public void parseFCMData(Context context, String response) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print("parsing FCM data encrypt " + dataResponse.data);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                PrintLog.print("parsing FCM data decrypt value " + dResponse);

                parseDecryptFCMData(context, dResponse);

            } catch (Exception e) {
                new GCMPreferences(context).setGCMRegister(false);
            }
        }
    }

    private void parseDecryptFCMData(Context context, String response) {

        if (response != null) {
            GCMPreferences preferences = new GCMPreferences(context);
            ServerResponse gcmResponse = gson.fromJson(response, ServerResponse.class);
            PrintLog.print("response GCM OK receiver" + " " + gcmResponse.status + " " + gcmResponse.message + " " + gcmResponse.reqvalue);

            if (gcmResponse.status.equals("0")) {
                RestUtils.saveAllGCMValue(context);
                preferences.setGCMRegister(true);
                preferences.setVirtualGCMID(gcmResponse.reqvalue);
                preferences.setGCMID("NA");
            } else {
                preferences.setGCMRegister(false);
            }

        }

    }


    public void parseNotificationData(String response, NotificationListener l) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print("parsing Notification data encrypt" + " " + dataResponse.data);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                PrintLog.print("parsing Notification data decrypt value" + " " + dResponse);

                l.pushFCMNotification(dResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void parseReferalData(Context context, String response) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print("parsing FCM data encrypt " + dataResponse.data);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                PrintLog.print("parsing FCM data decrypt value " + dResponse);

                parseDecryptReferalData(context, dResponse);

            } catch (Exception e) {
                new GCMPreferences(context).setReferalRegister(false);
            }
        }
    }

    private void parseDecryptReferalData(Context context, String response) {


        if (response != null) {
            GCMPreferences preferences = new GCMPreferences(context);

            ServerResponse gcmResponse = gson.fromJson(response, ServerResponse.class);
            PrintLog.print("response referal OK app launch" + " " + gcmResponse.status + " " + gcmResponse.status + " " + gcmResponse.reqvalue);
            if (gcmResponse.status.equals("0")) {
                preferences.setReferalRegister(true);
            } else {
                preferences.setReferalRegister(false);
            }
        }

    }

    public void parseFCMTopicData(String response, NotificationListener l) {
        DataResponse dataResponse;

        if (response != null) {
            dataResponse = gson.fromJson(response, DataResponse.class);

            PrintLog.print("parsing FCMTopicData data encrypt" + " " + dataResponse.data);

            try {
                String dResponse = new String(mCrypt.decrypt(dataResponse.data));
                PrintLog.print("parsing FCMTopicData data decrypt value" + " " + dResponse);

                l.pushFCMNotification(dResponse);
            } catch (Exception e) {
                PrintLog.print("parsing FCMTopicData Exception " + " " + e.getMessage());
            }
        }
    }

    private void loadSlaveAdResponse(List<AdsResponse> list, onParseDefaultValueListener onParseDefaultValueListener) {

        try{
            if (list != null && list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {
                    AdsResponse adsResponse = list.get(i);
                    if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_TOP_BANNER)) {
                        PrintLog.print("0555 checking Type Top Banner");
                        Slave.TOP_BANNER_clicklink = adsResponse.clicklink;
                        Slave.TOP_BANNER_start_date = adsResponse.start_date;
                        Slave.TOP_BANNER_nevigation = adsResponse.navigation;
                        Slave.TOP_BANNER_call_native = adsResponse.call_native;
                        Slave.TOP_BANNER_rateapptext = adsResponse.rateapptext;
                        Slave.TOP_BANNER_rateurl = adsResponse.rateurl;
                        Slave.TOP_BANNER_email = adsResponse.email;
                        Slave.TOP_BANNER_updateTYPE = adsResponse.updatetype;
                        Slave.TOP_BANNER_appurl = adsResponse.appurl;
                        Slave.TOP_BANNER_prompttext = adsResponse.prompttext;
                        Slave.TOP_BANNER_version = adsResponse.version;
                        Slave.TOP_BANNER_moreurl = adsResponse.moreurl;
                        Slave.TOP_BANNER_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.TOP_BANNER_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_BOTTOM_BANNER)) {
                        Slave.BOTTOM_BANNER_clicklink = adsResponse.clicklink;
                        Slave.BOTTOM_BANNER_start_date = adsResponse.start_date;
                        Slave.BOTTOM_BANNER_nevigation = adsResponse.navigation;
                        Slave.BOTTOM_BANNER_call_native = adsResponse.call_native;
                        Slave.BOTTOM_BANNER_rateapptext = adsResponse.rateapptext;
                        Slave.BOTTOM_BANNER_rateurl = adsResponse.rateurl;
                        Slave.BOTTOM_BANNER_email = adsResponse.email;
                        Slave.BOTTOM_BANNER_updateTYPE = adsResponse.updatetype;
                        Slave.BOTTOM_BANNER_appurl = adsResponse.appurl;
                        Slave.BOTTOM_BANNER_prompttext = adsResponse.prompttext;
                        Slave.BOTTOM_BANNER_version = adsResponse.version;
                        Slave.BOTTOM_BANNER_moreurl = adsResponse.moreurl;
                        Slave.BOTTOM_BANNER_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.BOTTOM_BANNER_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_BANNER_LARGE)) {
                        Slave.LARGE_BANNER_clicklink = adsResponse.clicklink;
                        Slave.LARGE_BANNER_start_date = adsResponse.start_date;
                        Slave.LARGE_BANNER_nevigation = adsResponse.navigation;
                        Slave.LARGE_BANNER_call_native = adsResponse.call_native;
                        Slave.LARGE_BANNER_rateapptext = adsResponse.rateapptext;
                        Slave.LARGE_BANNER_rateurl = adsResponse.rateurl;
                        Slave.LARGE_BANNER_email = adsResponse.email;
                        Slave.LARGE_BANNER_updateTYPE = adsResponse.updatetype;
                        Slave.LARGE_BANNER_appurl = adsResponse.appurl;
                        Slave.LARGE_BANNER_prompttext = adsResponse.prompttext;
                        Slave.LARGE_BANNER_version = adsResponse.version;
                        Slave.LARGE_BANNER_moreurl = adsResponse.moreurl;
                        Slave.LARGE_BANNER_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.LARGE_BANNER_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_BANNER_RECTANGLE)) {
                        Slave.RECTANGLE_BANNER_clicklink = adsResponse.clicklink;
                        Slave.RECTANGLE_BANNER_start_date = adsResponse.start_date;
                        Slave.RECTANGLE_BANNER_nevigation = adsResponse.navigation;
                        Slave.RECTANGLE_BANNER_call_native = adsResponse.call_native;
                        Slave.RECTANGLE_BANNER_rateapptext = adsResponse.rateapptext;
                        Slave.RECTANGLE_BANNER_rateurl = adsResponse.rateurl;
                        Slave.RECTANGLE_BANNER_email = adsResponse.email;
                        Slave.RECTANGLE_BANNER_updateTYPE = adsResponse.updatetype;
                        Slave.RECTANGLE_BANNER_appurl = adsResponse.appurl;
                        Slave.RECTANGLE_BANNER_prompttext = adsResponse.prompttext;
                        Slave.RECTANGLE_BANNER_version = adsResponse.version;
                        Slave.RECTANGLE_BANNER_moreurl = adsResponse.moreurl;
                        Slave.RECTANGLE_BANNER_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.RECTANGLE_BANNER_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_FULL_ADS)) {
                        PrintLog.print("0555 checking Type Full Ads");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_FULL_ADS value is " + adsResponse);
                        Slave.FULL_ADS_clicklink = adsResponse.clicklink;
                        Slave.FULL_ADS_start_date = adsResponse.start_date;
                        Slave.FULL_ADS_nevigation = adsResponse.navigation;
                        Slave.FULL_ADS_call_native = adsResponse.call_native;
                        Slave.FULL_ADS_rateapptext = adsResponse.rateapptext;
                        Slave.FULL_ADS_rateurl = adsResponse.rateurl;
                        Slave.FULL_ADS_email = adsResponse.email;
                        Slave.FULL_ADS_updateTYPE = adsResponse.updatetype;
                        Slave.FULL_ADS_appurl = adsResponse.appurl;
                        Slave.FULL_ADS_prompttext = adsResponse.prompttext;
                        Slave.FULL_ADS_version = adsResponse.version;
                        Slave.FULL_ADS_moreurl = adsResponse.moreurl;
                        Slave.FULL_ADS_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.FULL_ADS_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_LAUNCH_FULL_ADS)) {
                        PrintLog.print("NewEngine 0555 checking Type Top Launch Full Ads" + onParseDefaultValueListener);

                      //  if (onParseDefaultValueListener != null) {
                            PrintLog.print(" Enginev2 Parging tag Slave.TYPE_LAUNCH_FULL_ADS value is " + adsResponse.src+"  "+adsResponse.show_after);

                        Log.d("DataHubHandler", "NewEngine showFullAdsOnLaunch TYPE_LAUNCH_FULL_ADS value is  "+ adsResponse.src+"  "+adsResponse.show_after);

                        Slave.LAUNCH_FULL_ADS_clicklink = adsResponse.clicklink;
                            Slave.LAUNCH_FULL_ADS_start_date = adsResponse.start_date;
                            Slave.LAUNCH_FULL_ADS_nevigation = adsResponse.navigation;
                            Slave.LAUNCH_FULL_ADS_call_native = adsResponse.call_native;
                            Slave.LAUNCH_FULL_ADS_rateapptext = adsResponse.rateapptext;
                            Slave.LAUNCH_FULL_ADS_rateurl = adsResponse.rateurl;
                            Slave.LAUNCH_FULL_ADS_email = adsResponse.email;
                            Slave.LAUNCH_FULL_ADS_updateTYPE = adsResponse.updatetype;
                            Slave.LAUNCH_FULL_ADS_appurl = adsResponse.appurl;
                            Slave.LAUNCH_FULL_ADS_prompttext = adsResponse.prompttext;
                            Slave.LAUNCH_FULL_ADS_version = adsResponse.version;
                            Slave.LAUNCH_FULL_ADS_moreurl = adsResponse.moreurl;
                            Slave.LAUNCH_FULL_ADS_src = adsResponse.src;
                            Slave.LAUNCH_FULL_ADS_show_after = adsResponse.show_after;

                            if (adsResponse.providers != null) {
                                List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                                if (providers.size() > 0) {
                                    Slave.LAUNCH_FULL_ADS_providers = providers;
                                }
                            }
                       // }
                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_EXIT_FULL_ADS)) {
                        PrintLog.print("0555 checking Type FULL ADS");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_EXIT_FULL_ADS value is " + adsResponse + "  "
                                + adsResponse.exit_type);

                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_EXIT_FULL_ADS value is 111 " + "  "
                                + adsResponse.exit_type);

                        Slave.EXIT_TYPE = adsResponse.exit_type;
                        if (adsResponse.exit_type != null && adsResponse.exit_type.equals(Slave.EXIT_TYPE2)) {
                            loadExitType(adsResponse.exitPromptResponceArrayList);
                        } else if (adsResponse.exit_type != null && adsResponse.exit_type.equals(Slave.EXIT_TYPE3)) {
                            loadExitType(adsResponse.exitPromptResponceArrayList);
                        } else if (adsResponse.exit_type != null && adsResponse.exit_type.equals(Slave.EXIT_TYPE4)) {
                            loadExitType(adsResponse.exitPromptResponceArrayList);
                            loadExitType4TopBanner(adsResponse.exitPromptResponceArrayList);
                        }else if (adsResponse.exit_type != null && adsResponse.exit_type.equals(Slave.EXIT_TYPE5)) {
                            loadExitType(adsResponse.exitPromptResponceArrayList);
                            loadExitType5TopList(adsResponse.exitPromptResponceArrayList);
                        }else if (adsResponse.exit_type != null && adsResponse.exit_type.equals(Slave.EXIT_TYPE6)) {
                            loadExitType(adsResponse.exitPromptResponceArrayList);
                            loadExitType5TopList(adsResponse.exitPromptResponceArrayList);
                        }else {
                            Slave.EXIT_TYPE = Slave.EXIT_TYPE1;
                        }

//                        loadExitType(adsResponse.exitPromptResponceArrayList);
//                        loadExitType5TopList(adsResponse.exitPromptResponceArrayList);

//                    Slave.EXIT_FULL_ADS_clicklink = adsResponse.clicklink;
//                    Slave.EXIT_FULL_ADS_start_date = adsResponse.start_date;
//                    Slave.EXIT_FULL_ADS_nevigation = adsResponse.navigation;
//                    Slave.EXIT_FULL_ADS_call_native = adsResponse.call_native;
//                    Slave.EXIT_FULL_ADS_rateapptext = adsResponse.rateapptext;
//                    Slave.EXIT_FULL_ADS_rateurl = adsResponse.rateurl;
//                    Slave.EXIT_FULL_ADS_email = adsResponse.email;
//                    Slave.EXIT_FULL_ADS_updateTYPE = adsResponse.updatetype;
//                    Slave.EXIT_FULL_ADS_appurl = adsResponse.appurl;
//                    Slave.EXIT_FULL_ADS_prompttext = adsResponse.prompttext;
//                    Slave.EXIT_FULL_ADS_version = adsResponse.version;
//                    Slave.EXIT_FULL_ADS_moreurl = adsResponse.moreurl;
//                    Slave.EXIT_FULL_ADS_src = adsResponse.src;
//                    Slave.EXIT_SHOW_AD_ON_EXIT_PROMPT = adsResponse.show_ad_on_exit_prompt;
//                    Slave.EXIT_SHOW_NATIVE_AD_ON_EXIT_PROMPT = adsResponse.show_native_ad_on_exit_prompt;
//
//                    if (adsResponse.providers != null) {
//                        List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
//                        if (providers.size() > 0) {
//                            Slave.EXIT_FULL_ADS_providers = providers;
//                        }
//                    }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_NATIVE_MEDIUM)) {
                        PrintLog.print("0555 checking Type NATIVE MEDIUM");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_NATIVE_MEDIUM value is " + adsResponse.ad_id);
                        Slave.NATIVE_MEDIUM_clicklink = adsResponse.clicklink;
                        Slave.NATIVE_MEDIUM_start_date = adsResponse.start_date;
                        Slave.NATIVE_MEDIUM_nevigation = adsResponse.navigation;
                        Slave.NATIVE_MEDIUM_call_native = adsResponse.call_native;
                        Slave.NATIVE_MEDIUM_rateapptext = adsResponse.rateapptext;
                        Slave.NATIVE_MEDIUM_rateurl = adsResponse.rateurl;
                        Slave.NATIVE_MEDIUM_email = adsResponse.email;
                        Slave.NATIVE_MEDIUM_updateTYPE = adsResponse.updatetype;
                        Slave.NATIVE_MEDIUM_appurl = adsResponse.appurl;
                        Slave.NATIVE_MEDIUM_prompttext = adsResponse.prompttext;
                        Slave.NATIVE_MEDIUM_version = adsResponse.version;
                        Slave.NATIVE_MEDIUM_moreurl = adsResponse.moreurl;
                        Slave.NATIVE_MEDIUM_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.NATIVE_MEDIUM_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_NATIVE_LARGE)) {
                        PrintLog.print("0555 checking Type NATIVE LARGE");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_NATIVE_LARGE value is " + adsResponse);
                        Slave.NATIVE_LARGE_clicklink = adsResponse.clicklink;
                        Slave.NATIVE_LARGE_start_date = adsResponse.start_date;
                        Slave.NATIVE_LARGE_nevigation = adsResponse.navigation;
                        Slave.NATIVE_LARGE_call_native = adsResponse.call_native;
                        Slave.NATIVE_LARGE_rateapptext = adsResponse.rateapptext;
                        Slave.NATIVE_LARGE_rateurl = adsResponse.rateurl;
                        Slave.NATIVE_LARGE_email = adsResponse.email;
                        Slave.NATIVE_LARGE_updateTYPE = adsResponse.updatetype;
                        Slave.NATIVE_LARGE_appurl = adsResponse.appurl;
                        Slave.NATIVE_LARGE_prompttext = adsResponse.prompttext;
                        Slave.NATIVE_LARGE_version = adsResponse.version;
                        Slave.NATIVE_LARGE_moreurl = adsResponse.moreurl;
                        Slave.NATIVE_LARGE_src = adsResponse.src;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.NATIVE_LARGE_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_REWARDED_VIDEO)) {
                        Slave.REWARDED_VIDEO_status = adsResponse.ads_status;
                        Slave.REWARDED_VIDEO_start_date = adsResponse.start_date;
                        Slave.REWARDED_VIDEO_nevigation = adsResponse.navigation;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.REWARDED_VIDEO_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_SUGGESTED_ADS)) {
                        Slave.SUGGESTED_ADS_call_native = adsResponse.call_native;
                        Slave.SUGGESTED_ADS_start_date = adsResponse.start_date;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.SUGGESTED_ADS_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_APP_OPEN_ADS)) {
                        Slave.APP_OPEN_ADS_status = adsResponse.ads_status;
                        Slave.APP_OPEN_ADS_start_date = adsResponse.start_date;
                        Slave.APP_OPEN_ADS_nevigation = adsResponse.navigation;

                        if (adsResponse.providers != null) {
                            List<AdsProviders> providers = new ArrayList<>(adsResponse.providers);
                            if (providers.size() > 0) {
                                Slave.APP_OPEN_ADS_providers = providers;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_RATE_APP)) {
                        PrintLog.print("0555 checking Type RATE APP");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_RATE_APP value is " + adsResponse.rateapptext);
                        Slave.RATE_APP_ad_id = adsResponse.ad_id;
                        Slave.RATE_APP_provider_id = adsResponse.provider_id;
                        Slave.RATE_APP_clicklink = adsResponse.clicklink;
                        Slave.RATE_APP_start_date = adsResponse.start_date;
                        Slave.RATE_APP_nevigation = adsResponse.navigation;
                        Slave.RATE_APP_call_native = adsResponse.call_native;
                        Slave.RATE_APP_rateapptext = adsResponse.rateapptext;
                        Slave.RATE_APP_rateurl = adsResponse.rateurl;
                        Slave.RATE_APP_email = adsResponse.email;
                        Slave.RATE_APP_updateTYPE = adsResponse.updatetype;
                        Slave.RATE_APP_appurl = adsResponse.appurl;
                        Slave.RATE_APP_prompttext = adsResponse.prompttext;
                        Slave.RATE_APP_version = adsResponse.version;
                        Slave.RATE_APP_moreurl = adsResponse.moreurl;
                        Slave.RATE_APP_src = adsResponse.src;

                        Slave.RATE_APP_BG_COLOR = adsResponse.bgcolor;
                        Slave.RATE_APP_HEADER_TEXT = adsResponse.headertext;
                        Slave.RATE_APP_TEXT_COLOR = adsResponse.textcolor;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_FEEDBACK)) {
                        PrintLog.print("0555 checking Type FEEDBACK");
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_FEEDBACK value is " + adsResponse);
                        Slave.FEEDBACK_ad_id = adsResponse.ad_id;
                        Slave.FEEDBACK_provider_id = adsResponse.provider_id;
                        Slave.FEEDBACK_clicklink = adsResponse.clicklink;
                        Slave.FEEDBACK_start_date = adsResponse.start_date;
                        Slave.FEEDBACK_nevigation = adsResponse.navigation;
                        Slave.FEEDBACK_call_native = adsResponse.call_native;
                        Slave.FEEDBACK_rateapptext = adsResponse.rateapptext;
                        Slave.FEEDBACK_rateurl = adsResponse.rateurl;
                        Slave.FEEDBACK_email = adsResponse.email;
                        Slave.FEEDBACK_updateTYPE = adsResponse.updatetype;
                        Slave.FEEDBACK_appurl = adsResponse.appurl;
                        Slave.FEEDBACK_prompttext = adsResponse.prompttext;
                        Slave.FEEDBACK_version = adsResponse.version;
                        Slave.FEEDBACK_moreurl = adsResponse.moreurl;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_UPDATES)) {
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_UPDATES value is " + adsResponse.updatetype
                                + " " + adsResponse.appurl);
                        Slave.UPDATES_ad_id = adsResponse.ad_id;
                        Slave.UPDATES_provider_id = adsResponse.provider_id;
                        Slave.UPDATES_clicklink = adsResponse.clicklink;
                        Slave.UPDATES_start_date = adsResponse.start_date;
                        Slave.UPDATES_nevigation = adsResponse.navigation;
                        Slave.UPDATES_call_native = adsResponse.call_native;
                        Slave.UPDATES_rateapptext = adsResponse.rateapptext;
                        Slave.UPDATES_rateurl = adsResponse.rateurl;
                        Slave.UPDATES_email = adsResponse.email;
                        Slave.UPDATES_updateTYPE = adsResponse.updatetype;
                        Slave.UPDATES_appurl = adsResponse.appurl;
                        Slave.UPDATES_prompttext = adsResponse.prompttext;
                        Slave.UPDATES_version = adsResponse.version;
                        Slave.UPDATES_moreurl = adsResponse.moreurl;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_MORE_APPS)) {
                        PrintLog.print(" Enginev2 Parging tag Slave.TYPE_MORE_APPS value is " + adsResponse);
                        Slave.MOREAPP_ad_id = adsResponse.ad_id;
                        Slave.MOREAPP_provider_id = adsResponse.provider_id;
                        Slave.MOREAPP_clicklink = adsResponse.clicklink;
                        Slave.MOREAPP_start_date = adsResponse.start_date;
                        Slave.MOREAPP_nevigation = adsResponse.navigation;
                        Slave.MOREAPP_call_native = adsResponse.call_native;
                        Slave.MOREAPP_rateapptext = adsResponse.rateapptext;
                        Slave.MOREAPP_rateurl = adsResponse.rateurl;
                        Slave.MOREAPP_email = adsResponse.email;
                        Slave.MOREAPP_updateTYPE = adsResponse.updatetype;
                        Slave.MOREAPP_appurl = adsResponse.appurl;
                        Slave.MOREAPP_prompttext = adsResponse.prompttext;
                        Slave.MOREAPP_version = adsResponse.version;
                        Slave.MOREAPP_moreurl = adsResponse.moreurl;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_ETC)) {
                        PrintLog.print("0555 checking Type ETC");
                        Slave.ETC_1 = adsResponse.etc1;
                        Slave.ETC_2 = adsResponse.etc2;
                        Slave.ETC_3 = adsResponse.etc3;
                        Slave.ETC_4 = adsResponse.etc4;
                        Slave.ETC_5 = adsResponse.etc5;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_SHARE)) {
                        PrintLog.print("0555 checking Type SHARE");
                        Slave.SHARE_TEXT = adsResponse.sharetext;
                        Slave.SHARE_URL = adsResponse.shareurl;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_ADMOB_STATIC)) {
                        PrintLog.print("0555 checking Type ADMOB STATIC ");
                        PrintLog.print("1110 here is am ");
                        Slave.ADMOB_NATIVE_MEDIUM_ID_STATIC = adsResponse.admob_native_medium_id;
                        Slave.ADMOB_BANNER_ID_STATIC = adsResponse.admob_banner_id;
                        Slave.ADMOB_FULL_ID_STATIC = adsResponse.admob_full_id;
                        Slave.ADMOB_NATIVE_LARGE_ID_STATIC = adsResponse.admob_native_large_id;
                        Slave.ADMOB_BANNER_ID_LARGE_STATIC = adsResponse.admob_bannerlarge_id;
                        Slave.ADMOB_BANNER_ID_RECTANGLE_STATIC = adsResponse.admob_bannerrect_id;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_REMOVE_ADS)) {
                        Slave.REMOVE_ADS_DESCRIPTION = adsResponse.description;
                        Slave.REMOVE_ADS_BGCOLOR = adsResponse.bgcolor;
                        Slave.REMOVE_ADS_TEXTCOLOR = adsResponse.textcolor;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_ABOUT_DETAILS)) {
                        Slave.ABOUTDETAIL_DESCRIPTION = adsResponse.description;
                        Slave.ABOUTDETAIL_OURAPP = adsResponse.ourapp;
                        Slave.ABOUTDETAIL_WEBSITELINK = adsResponse.websitelink;
                        Slave.ABOUTDETAIL_PRIVACYPOLICY = adsResponse.ppolicy;
                        Slave.ABOUTDETAIL_TERM_AND_COND = adsResponse.tandc;
                        Slave.ABOUTDETAIL_FACEBOOK = adsResponse.facebook;
                        Slave.ABOUTDETAIL_INSTA = adsResponse.instagram;
                        Slave.ABOUTDETAIL_TWITTER = adsResponse.twitter;
                        Slave.ABOUTDETAIL_FAQ = adsResponse.faq;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_EXIT_NON_REPEAT)) {

                        if (adsResponse.counts != null) {
                            ArrayList<NonRepeatCount> counts = new ArrayList<>(adsResponse.counts);
                            if (counts.size() > 0) {
                                Slave.EXIT_NON_REPEAT_COUNT = counts;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_EXIT_REPEAT)) {
                        Slave.EXIT_REPEAT_RATE = adsResponse.rate;
                        Slave.EXIT_REPEAT_EXIT = adsResponse.exit;
                        Slave.EXIT_REPEAT_FULL_ADS = adsResponse.full;
                        Slave.EXIT_REPEAT_REMOVEADS = adsResponse.removeads;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_LAUNCH_NON_REPEAT)) {

                        if (adsResponse.launch_counts != null) {
                            ArrayList<LaunchNonRepeatCount> launch_counts = new ArrayList<>(adsResponse.launch_counts);
                            if (launch_counts.size() > 0) {
                                Slave.LAUNCH_NON_REPEAT_COUNT = launch_counts;
                            }
                        }

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_LAUNCH_REPEAT)) {
                        Slave.LAUNCH_REPEAT_RATE = adsResponse.launch_rate;
                        Slave.LAUNCH_REPEAT_EXIT = adsResponse.launch_exit;
                        Slave.LAUNCH_REPEAT_FULL_ADS = adsResponse.launch_full;
                        Slave.LAUNCH_REPEAT_REMOVEADS = adsResponse.launch_removeads;

                    } else if (adsResponse.type.equalsIgnoreCase(Slave.TYPE_INAPP_BILLING)) {
                        Slave.INAPP_PUBLIC_KEY = adsResponse.public_key;

                        if (adsResponse.billing != null) {
                            ArrayList<Billing> billing = new ArrayList<>(adsResponse.billing);
                            if (billing.size() > 0) {
                                BillingResponseHandler.getInstance().setBillingResponse(adsResponse.billing);
                            }
                        }
                    }
                }


            }
        }catch (Exception e){ }
    }

    private void loadSlaveCP(DataHubCP cp) {
        if (cp != null) {
            Slave.CP_cpname = cp.cpname;
            Slave.CP_navigation_count = cp.navigation_count;
            Slave.CP_is_start = cp.is_start;
            Slave.CP_is_exit = cp.is_exit;
            Slave.CP_startday = cp.startday;
            Slave.CP_package_name = cp.package_name;
            Slave.CP_camp_img = cp.camp_img;
            Slave.CP_camp_click_link = cp.camp_click_link;
            PrintLog.print(" Enginev2 Parging tag Slave.CP  cp.cpname " + cp.cpname + " Slave.CP_camp_click_link " + Slave.CP_camp_click_link);

        }
    }

    private void loadMoreFeatureData(ArrayList<MoreFeature> moreFeatures) {
        MoreFeatureResponseHandler.getInstance().setMoreFeaturesListResponse(moreFeatures);
    }

    public interface InHouseCallBack {
        void onInhouseDownload(InHouse inHouse);
    }

    public interface MasterRequestListener {
        void callMasterService();
    }

    public interface NotificationListener {
        void pushFCMNotification(String json);
    }

    private void loadGameFeatureV2(ArrayList<GameProvidersResponce> gameProvidersResponces) {
        GameServiceV2ResponseHandler.getInstance().setGameV2FeaturesListResponse(gameProvidersResponces);
    }

    private void loadExitType(ExitPromptButtomResponce exitPromptButtomResponce) {

        if(exitPromptButtomResponce.exit_buttom_banner_src==null || exitPromptButtomResponce.exit_buttom_banner_src.isEmpty()
                ||exitPromptButtomResponce.exit_pos_button_bg==null || exitPromptButtomResponce.exit_pos_button_bg.isEmpty() ||
                exitPromptButtomResponce.exit_pos_button_text==null || exitPromptButtomResponce.exit_pos_button_text.isEmpty() ||
                exitPromptButtomResponce.exit_pos_button_textcolor==null || exitPromptButtomResponce.exit_pos_button_textcolor.isEmpty() ||
                exitPromptButtomResponce.exit_neg_button_bg==null || exitPromptButtomResponce.exit_neg_button_bg.isEmpty() ||
                exitPromptButtomResponce.exit_neg_button_text ==null || exitPromptButtomResponce.exit_neg_button_text.isEmpty() ||
                exitPromptButtomResponce.exit_neg_button_textcolor==null || exitPromptButtomResponce.exit_neg_button_textcolor.isEmpty() ||
                exitPromptButtomResponce.exit_msz_text ==null|| exitPromptButtomResponce.exit_msz_text.isEmpty()){
            Slave.EXIT_TYPE = Slave.EXIT_TYPE1;
            return;
        }

        Slave.Exit_Buttom_Banner_Src = exitPromptButtomResponce.exit_buttom_banner_src;
        Slave.Exit_Pos_Button_Bg = exitPromptButtomResponce.exit_pos_button_bg;
        Slave.Exit_Pos_Button_Text = exitPromptButtomResponce.exit_pos_button_text;
        Slave.Exit_Pos_Button_TextColor = exitPromptButtomResponce.exit_pos_button_textcolor;
        Slave.Exit_Neg_Button_Bg = exitPromptButtomResponce.exit_neg_button_bg;
        Slave.Exit_Neg_Button_Text = exitPromptButtomResponce.exit_neg_button_text;
        Slave.Exit_Neg_Button_TextColor = exitPromptButtomResponce.exit_neg_button_textcolor;
        Slave.Exit_Msz_Text = exitPromptButtomResponce.exit_msz_text;
        Slave.Exit_Top_Banner_Header = exitPromptButtomResponce.top_banner_header;

        loadExitType4TopBanner(exitPromptButtomResponce);
    }

    private void loadExitType4TopBanner(ExitPromptButtomResponce exitPromptButtomResponce ) {
        ExitType4TopBannerResponce exitType4TopBannerResponce = exitPromptButtomResponce.exitType4TopBannerResponce;
        if(Slave.EXIT_TYPE.equals(Slave.EXIT_TYPE4)) {
            if(exitType4TopBannerResponce ==null || exitType4TopBannerResponce.banner_scr==null || exitType4TopBannerResponce.banner_scr.isEmpty()){
                Slave.EXIT_TYPE = Slave.EXIT_TYPE1;
                return;
            }
            Log.d("fvbjdf","NewEngine showFullAdsOnLaunch type 4 dhfgjhd "
                    +exitType4TopBannerResponce.banner_scr +"  "+Slave.Exit_Top_Banner_Src+"  "+exitType4TopBannerResponce.banner_click_type);
            Slave.Exit_Top_Banner_Src = exitType4TopBannerResponce.banner_scr;
            Slave.Exit_Top_Banner_Click_type = exitType4TopBannerResponce.banner_click_type;
            Slave.Exit_Top_Banner_Click_Value = exitType4TopBannerResponce.banner_click_value;
        }
    }

    private void loadExitType5TopList(ExitPromptButtomResponce exitPromptButtomResponce ) {
        if(Slave.EXIT_TYPE.equals(Slave.EXIT_TYPE5) || Slave.EXIT_TYPE.equals(Slave.EXIT_TYPE6)) {
            if(exitPromptButtomResponce.exitType4TopBannerResponce.app_list==null){
                Slave.EXIT_TYPE = Slave.EXIT_TYPE1;
                return;
            }
            Slave.ExitAppList = exitPromptButtomResponce.exitType4TopBannerResponce.app_list;
        }

    }
}
