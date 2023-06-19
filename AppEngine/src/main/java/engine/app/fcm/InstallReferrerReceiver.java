package engine.app.fcm;//package engine.app.fcm;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import engine.app.PrintLog;
//import engine.app.rest.request.DataRequest;
//import engine.app.server.v2.DataHubHandler;
//import engine.app.socket.EngineApiController;
//import engine.app.socket.Response;
//
//
///**
// * Created by rajeev on 26/02/18.
// */
//
//public class InstallReferrerReceiver extends BroadcastReceiver {
//    private GCMPreferences preferences;
//    private DataHubHandler mHandler;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        preferences = new GCMPreferences(context);
//        try {
//            if (intent != null && intent.getAction() != null) {
//                if (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
//
//                    String referrerId = intent.getStringExtra("referrer");
//                    preferences.setreferrerId(referrerId);
//
//
//                    mHandler = new DataHubHandler();
//                    doReferrerRequest(context);
//                }
//            }
//
//        } catch (Exception e) {
//            preferences.setReferalRegister(false);
//        }
//
//
//    }
//
//
//    private void doReferrerRequest(final Context mContext) {
//        DataRequest mRequest = new DataRequest();
//        EngineApiController mController = new EngineApiController(mContext, new Response() {
//            @Override
//            public void onResponseObtained(Object response, int responseType, boolean isCachedData) {
//                mHandler.parseReferalData(mContext, response.toString());
//
//            }
//
//            @Override
//            public void onErrorObtained(String errormsg, int responseType) {
//                PrintLog.print("response referal Failed app launch" + " " + errormsg);
//                preferences.setReferalRegister(false);
//            }
//        }, EngineApiController.REFERRAL_ID_CODE);
//        mController.getReferralRequest(mRequest);
//
//
//    }
//
//
//}
