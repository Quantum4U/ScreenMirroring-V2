package engine.app.adshandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;

import engine.app.PrintLog;
import engine.app.fcm.FCMTopicResponse;
import engine.app.fcm.GCMPreferences;
import engine.app.listener.onParseDefaultValueListener;
import engine.app.receiver.TopicAlarmReceiver;
import engine.app.rest.request.DataRequest;
import engine.app.rest.rest_utils.RestUtils;
import engine.app.server.v2.DataHubConstant;
import engine.app.server.v2.DataHubHandler;
import engine.app.server.v2.DataHubPreference;
import engine.app.serviceprovider.Utils;
import engine.app.socket.EngineApiController;
import engine.app.socket.Response;
import engine.app.utils.EngineConstant;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by quantum4u1 on 25/04/18.
 */

public class EngineHandler {

    private DataHubPreference preference;
    private DataHubHandler mHandler;
    private DataHubConstant mConstant;
    private GCMPreferences gcmPreference;
    private InstallReferrerClient installReferrerClient;
    private Context mContext;

    public EngineHandler(Context context) {
        preference = new DataHubPreference(context);
        mHandler = new DataHubHandler();
        mConstant = new DataHubConstant(context);
        gcmPreference = new GCMPreferences(context);
        this.mContext = context;
        installReferrerClient = InstallReferrerClient.newBuilder(context).build();
    }


    void initDefaultValue(){
        /*
         *this is used only for overwrite case , because in
         * overwrite case pref ads configuraion is stored for old version
         * so for pasing for current verson default value matches version code
         *
         */

        if(preference!=null && !preference.getAppVersion().equalsIgnoreCase(DataHubConstant.KEY_NA)
                && !preference.getAppVersion().equalsIgnoreCase(RestUtils.getVersion(mContext))) {
            preference.setAdsResponse(new DataHubConstant(mContext).parseAssetData());
            preference.setCdoCount(0);
        }
    }
    void initServices(boolean fetchFromServer,onParseDefaultValueListener onParseDefaultValueListener) {
        System.out.println("EngineHandler.initServices ....11.."+fetchFromServer+"  "+ new DataHubPreference(mContext).getAdsResponse());
        if (fetchFromServer) {
            System.out.println("EngineHandler.initServices ....1100.."+fetchFromServer+"  "+ new DataHubPreference(mContext).getAdsResponse());

            doVersionRequest();
        } else {
            System.out.println("EngineHandler.initServices ....1111.."+fetchFromServer+"  "+ new DataHubPreference(mContext).getAdsResponse());

            preference.setAppVersion(RestUtils.getVersion(mContext));
                new DataHubHandler().parseMasterData(mContext, new DataHubPreference(mContext).getAdsResponse(), onParseDefaultValueListener);
        }
    }


    private void doVersionRequest() {
        DataRequest dataRequest = new DataRequest();

        EngineApiController mApiController = new EngineApiController(mContext, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                PrintLog.print("response version OK" + " " + response);
                System.out.println("EngineHandler.initServices ....222.."+response.toString());

                Log.d("hello test override", "Hello onResponseObtained override 001");

                mHandler.parseVersionData(mContext, response.toString(), new DataHubHandler.MasterRequestListener() {
                    @Override
                    public void callMasterService() {
                        Log.d("hello test override", "Hello onResponseObtained override 002");

                        System.out.println("EngineHandler.initServices ....22233.."+response.toString());

                        PrintLog.print("checking version flow domasterRequest");
                        doMasterRequest();
                    }
                });


            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                PrintLog.print("response version ERROR" + " " + errormsg);
                System.out.println("EngineHandler.initServices ....33333.."+errormsg);

                if (!preference.getAdsResponse().equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                    mHandler.parseMasterData(mContext, preference.getAdsResponse(),null);
                } else {
                    mHandler.parseMasterData(mContext, mConstant.parseAssetData(),null);
                }
            }
        }, EngineApiController.VERSION_ID_CODE);
        mApiController.getVersionRequest(dataRequest);

        installReferrerRequest();
        //doReferrerRequest();
    }


    private void doMasterRequest() {
        DataRequest mMasterRequest = new DataRequest();


        EngineApiController apiController = new EngineApiController(mContext, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                PrintLog.print("response master OK" + " " + response.toString() + " :" + responseType);
                PrintLog.print("response master OK long" + " " + EngineConstant.getLongPrint(response.toString()));
                Log.d("hello test override", "Hello onResponseObtained override 003");
                System.out.println("EngineHandler.initServices ....44444.."+response.toString());

                mHandler.parseMasterData(mContext, response.toString(),null);

            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {

                PrintLog.print("response master Failed" + " " + errormsg + " :type" + " " + responseType);
                System.out.println("EngineHandler.initServices ....5555.."+ " " + errormsg + " :type" + " " + responseType);

                if (!preference.getAdsResponse().equalsIgnoreCase(DataHubConstant.KEY_NA)) {
                    mHandler.parseMasterData(mContext, preference.getAdsResponse(),null);
                } else {
                    mHandler.parseMasterData(mContext, mConstant.parseAssetData(),null);
                }
            }
        }, EngineApiController.MASTER_SERVICE_CODE);
        apiController.getMasterData(mMasterRequest);


    }

    void doGCMRequest() {
        System.out.println("353 Logs >> 00");
        if (RestUtils.isVirtual(mContext)
                || !gcmPreference.getGCMRegister()
                || !gcmPreference.getGCMID().equalsIgnoreCase("NA")) {
            System.out.println("353 Logs >> 01");
            DataRequest mRequest = new DataRequest();
            EngineApiController mApiController = new EngineApiController(mContext, new Response() {
                @Override
                public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                    mHandler.parseFCMData(mContext, response.toString());

                }

                @Override
                public void onErrorObtained(String errormsg, int responseType) {
                    System.out.println("response GCM Failed receiver" + " " + errormsg);
                    gcmPreference.setGCMRegister(false);
                }
            }, EngineApiController.GCM_SERVICE_CODE);
            mApiController.setFCMTokens(gcmPreference.getGCMID());
            mApiController.getGCMIDRequest(mRequest);

            System.out.println("EngineHandler.doGCMRequest already register");
        }

    }

    private void installReferrerRequest() {
        PrintLog.print("EngineHandler New InstallReferrer " + gcmPreference.getReferalRegister() + "  " + gcmPreference.getreferrerId());
        if (!gcmPreference.getReferalRegister() && gcmPreference.getreferrerId().equalsIgnoreCase("NA")) {
            installReferrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int i) {
                    switch (i) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            try {
                                ReferrerDetails response = installReferrerClient.getInstallReferrer();
                                String referrerUrl = response.getInstallReferrer();
                                long referrerClickTime = response.getReferrerClickTimestampSeconds();
                                long appInstallTime = response.getInstallBeginTimestampSeconds();
                                boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
                                //referrerUrl = RestUtils.getVersion(mContext) + " " + referrerUrl;

                                PrintLog.print("EngineHandler New InstallReferrer response ok.. "
                                        + referrerUrl + "  "
                                        + referrerClickTime + "  "
                                        + appInstallTime + "  "
                                        + instantExperienceLaunched + "  "
                                        + gcmPreference.getReferalRegister() + "  "
                                        + gcmPreference.getreferrerId());

                                gcmPreference.setreferrerId(referrerUrl);
                                doReferrerRequest();
                                installReferrerClient.endConnection();

                            } catch (Exception e) {
                                gcmPreference.setreferrerId("NA");
                                gcmPreference.setReferalRegister(false);
                            }
                            break;

                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            PrintLog.print("EngineHandler New InstallReferrer Response.FEATURE_NOT_SUPPORTED");
                            break;

                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            PrintLog.print("EngineHandler New InstallReferrer Response.SERVICE_UNAVAILABLE");
                            break;

                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED:
                            PrintLog.print("EngineHandler New InstallReferrer Response.SERVICE_DISCONNECTED");
                            break;

                        case InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR:
                            PrintLog.print("EngineHandler New InstallReferrer Response.DEVELOPER_ERROR");
                            break;
                    }


                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    System.out.println("EngineHandler.onInstallReferrerServiceDisconnected ");
                    //installReferrerClient.startConnection(this);
                }
            });

        }
    }

    private void doReferrerRequest() {
        if (!gcmPreference.getReferalRegister() && !gcmPreference.getreferrerId().equalsIgnoreCase("NA")) {
            DataRequest mRequest = new DataRequest();
            EngineApiController mController = new EngineApiController(mContext, new Response() {
                @Override
                public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                    PrintLog.print("response referal success" + " ");
                    mHandler.parseReferalData(mContext, response.toString());
                }

                @Override
                public void onErrorObtained(String errormsg, int responseType) {
                    PrintLog.print("response referal Failed app launch 1" + " " + errormsg);
                    gcmPreference.setReferalRegister(false);
                }
            }, EngineApiController.REFERRAL_ID_CODE);
            mController.getReferralRequest(mRequest);
        }
    }

    void doTopicsRequest() {
        /*
         *creating topic for first time or when app version change.
         */


        if (!RestUtils.getVersion(mContext).equalsIgnoreCase(String.valueOf(gcmPreference.getTopicAppVersion()))
                || !gcmPreference.getRegisterAllTopics()) {
            createTopics(mContext);
        }
    }

    private ArrayList<String> topics, allsubscribeTopic;
    private String version;

    private void createTopics(Context context) {
        String country = "C_" + RestUtils.getCountryCode(context);
        version = "AV_" + RestUtils.getVersion(context);
        String osVersion = "OS_" + RestUtils.getOSVersion(context);
        String deviceVersion = "DV_" + RestUtils.getDeviceVersion(context);
        String date = "DT_" + RestUtils.getDateofLos_Angeles();
        String month = "DT_" + RestUtils.getMonthofLos_Angeles();

        if (!RestUtils.validateJavaDate(RestUtils.getDateofLos_Angeles())) {
            date = "DT_" + RestUtils.getDate(System.currentTimeMillis());
            System.out.println("EngineHandler.createTopics not valid " + date);
        }

        topics = new ArrayList<>();
        topics.add("all");
        topics.add(country);
        topics.add(version);
        topics.add(osVersion);
        topics.add(deviceVersion);
        topics.add(date);
        topics.add(month);

        allsubscribeTopic = new ArrayList<>();
        System.out.println("EngineHandler.createTopics " + gcmPreference.getAllSubscribeTopic());
        System.out.println("EngineHandler.createTopics topic ver " + version + " " + gcmPreference.getTopicAppVersion());

        /*
         * subscribe all topic first time and boolen getAllSubscribeTopic help to know
         * that is all topic subscribe successfully or not.
         */
        if (!gcmPreference.getAllSubscribeTopic()) {
            for (int i = 0; i < topics.size(); i++) {
                subscribeToTopic(topics.get(i));
            }

        } else if (!version.equalsIgnoreCase(gcmPreference.getTopicAppVersion())) {
            /*
             * here when app version updated, we unsubsribe last topic which we saved on setTopicAppVersion preference
             * and send to server new topic
             */
            unsubscribeTopic(gcmPreference.getTopicAppVersion(), version);

        } else {
            System.out.println("EngineHandler.createTopics hi meeenuuu ");
            /*
             * here when server response failed and all topics are already subscribed successfully.
             */
            if (!gcmPreference.getRegisterAllTopics())
                doFCMTopicRequest(topics);
        }
    }


    private void subscribeToTopic(final String topicName) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topicName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        allsubscribeTopic.add(topicName);
                        if (topics.size() == allsubscribeTopic.size()) {
                            System.out.println("task successfull for all topics");
                            doFCMTopicRequest(allsubscribeTopic);
                            gcmPreference.setAllSubscribeTopic(true);
                            gcmPreference.setTopicAppVersion(version);
                        }
                        System.out.println("Subscribed to " + topicName + " topic");
                    } else {
                        System.out.println("Failed to subscribe to " + topicName + " topic");
                    }

                }
            });
        } catch (Exception e) {
            System.out.println("Subscribed to " + topicName + " topic failed " + e.getMessage());
        }

    }

    private void unsubscribeTopic(final String topicName, final String currentVersion) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("EngineHandler.createTopics unsubscribeTopic " + topicName);
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic(currentVersion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("EngineHandler.createTopics subscribeTopic " + currentVersion);
                doFCMTopicRequest(topics);
            }
        });
    }

    private void doFCMTopicRequest(ArrayList<String> list) {
        DataRequest mRequest = new DataRequest();
        EngineApiController mApiController = new EngineApiController(mContext, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("response FCM topic " + response);
                new DataHubHandler().parseFCMTopicData(response.toString(), new DataHubHandler.NotificationListener() {
                    @Override
                    public void pushFCMNotification(String json) {
                        if (json != null)
                            showFCMTopicResponse(json);
                    }
                });
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                System.out.println("response FCM topic Failed receiver" + " " + errormsg);
                gcmPreference.setRegisterAllTopics(false);
            }
        }, EngineApiController.FCM_TOPIC_CODE);
        mApiController.setAllTopics(list);
        mApiController.getFCMTopicData(mRequest);
    }

    private void showFCMTopicResponse(String response) {
        Gson gson = new Gson();
        FCMTopicResponse fcmTopicResponse = gson.fromJson(response, FCMTopicResponse.class);

        if (fcmTopicResponse.status.equalsIgnoreCase("0")) {
            gcmPreference.setRegisterAllTopics(true);
            gcmPreference.setTopicAppVersion(version);

            if (fcmTopicResponse.pushData != null) {
                try {
                    if (fcmTopicResponse.pushData.reqvalue != null && fcmTopicResponse.pushData.reqvalue.contains("#")) {
                        String[] reqValueArr = fcmTopicResponse.pushData.reqvalue.split("#");
                        String reqvalue = reqValueArr[0];
                        String ifdelay = reqValueArr[1];
                        String delaytime = reqValueArr[2];
                        if (ifdelay != null && ifdelay.equalsIgnoreCase("yes")) {
                            gcmPreference.setOnBoardNotificationId(reqvalue);
                            setFCMAlarm(mContext, Integer.parseInt(delaytime));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void setFCMAlarm(Context context, int delayTime) {
        int dtime = Utils.getRandomNo(delayTime);
        gcmPreference.setFCMRandomOnboard(dtime);
        System.out.println("response FCM topic setFCMAlarm " + dtime);
        Intent myIntent = new Intent(context, TopicAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, myIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + dtime, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + dtime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + dtime, pendingIntent);
            }
        }
    }


    /**
     * InApp success request to our server..
     */
    public void doInAppSuccessRequest(String productID) {
        DataRequest mRequest = new DataRequest();
        EngineApiController mApiController = new EngineApiController(mContext, new Response() {
            @Override
            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
                System.out.println("response INApp ok " + response);
            }

            @Override
            public void onErrorObtained(String errormsg, int responseType) {
                System.out.println("response INApp Failed " + " " + errormsg);
            }
        }, EngineApiController.INAPP_CODE);
        mApiController.setInAppData(productID);
        mApiController.getInAppSuccessData(mRequest);
    }
}
