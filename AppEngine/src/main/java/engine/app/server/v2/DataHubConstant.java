package engine.app.server.v2;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.pnd.adshandler.BuildConfig;
import engine.app.PrintLog;
import engine.app.ecrypt.MCrypt;
import engine.app.rest.request.DataRequest;
import engine.app.ui.AboutUsActivity;

import static engine.app.utils.EngineConstant.appnextg;
import static engine.app.utils.EngineConstant.apps24;
import static engine.app.utils.EngineConstant.berockfit;
import static engine.app.utils.EngineConstant.droidfoxes;
import static engine.app.utils.EngineConstant.indapp;
import static engine.app.utils.EngineConstant.livideo;
import static engine.app.utils.EngineConstant.m24apps;
import static engine.app.utils.EngineConstant.microapp;
import static engine.app.utils.EngineConstant.mtool;
import static engine.app.utils.EngineConstant.q4u;
import static engine.app.utils.EngineConstant.qsoft;
import static engine.app.utils.EngineConstant.quantum;
import static engine.app.utils.EngineConstant.techproof;
import static engine.app.utils.EngineConstant.thinkcoders;
import static engine.app.utils.EngineConstant.topapp;

/**
 * Created by hp on 9/20/2017.
 */
public class DataHubConstant {

    public static boolean IS_LIVE = true;

    public static int APP_LAUNCH_COUNT = 1;
    public static String APP_ID = BuildConfig.APP_ID;

    public static String CUSTOM_ACTION = BuildConfig.MAPPER_ACTION;

    private Context mContext;

    static String KEY_SUCESS = "success";

    public static String KEY_NA = "NA";

    private static String mPackageName;

    public DataHubConstant(Context c) {
        this.mContext = c;
        mPackageName = c.getPackageName();
    }


    String readFromAssets(String filename) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = null;
        try {
            assert reader != null;
            mLine = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (mLine != null) {
            sb.append(mLine); // process line
            try {
                mLine = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintLog.print("check for logs 01");
        return sb.toString();
    }

    public static final ArrayList<String> quantumList = new ArrayList<String>() {{
        add("com.app.autocallrecorder");
        add("com.applock.vault");
        add("app.pnd.fourg");
        add("app.pnd.speedmeter");
        add("com.app.filemanager");
        add("com.app.autocallrecorder_pro");
        add("com.appbackup.security");
        add("com.all.superbackup");
        add("com.quantum.nearbyme");
        add("com.hd.editor");
        add("com.quantam.rail");
        add("com.quantum.cleaner");
        add("com.quantum.mtracker");
        add("app.quantum.supdate");
        add("com.app.pcollage");
        add("com.app.ninja");
        add("com.app.filemanager_pro");
        add("app.pnd.speedmeter_pro");
        add("app.pnd.fourg_pro");
        add("app.quantum.supdate_pro");
        add("com.quantum.mtracker_pro");
        add("com.quantum.nearbyme_pro");
        add("com.appbackup.security_pro");
        add("com.all.superbackup_pro");
        add("com.applock.vault_pro");
        add("com.pnd.shareall");
        add("com.app.autocallrecorder.lite");
    }};


    public String parseAssetData() {
        /*
         *Here we encrypt assets data bcoz
         * our parsing is working in encrypt and decrypt from.
         */
//        return enCryptData(readFromAssets("master_link.txt"));
        return readFromAssets("master_link.txt");
    }

    private String enCryptData(String response) {
        Gson gson = new Gson();
        DataRequest mMasterRequest = new DataRequest();
        DataHubResponse dataHubResponse = gson.fromJson(response, DataHubResponse.class);

        String jsonStr = gson.toJson(dataHubResponse);

        mMasterRequest.data = getEncryptString(jsonStr);

        return gson.toJson(mMasterRequest);

    }

    private String getEncryptString(String jsonStr) {
        String value = "";
        MCrypt mcrypt = new MCrypt();
        try {
            value = MCrypt.bytesToHex(mcrypt.encrypt(jsonStr));
        } catch (Exception e) {
            PrintLog.print("exception encryption" + " " + e);
            e.printStackTrace();
        }
        return value;
    }


    public String notificationChannelName() {
        for (String s : quantumList) {
            if (mPackageName.contains(quantum)
                    || mPackageName.equalsIgnoreCase(s)) {
                return "Quantum4u";
            }
        }
        if (mPackageName.contains(mtool)
                || mPackageName.equalsIgnoreCase("com.appsbackupshare_pro")
                || mPackageName.equalsIgnoreCase("com.appsbackupshare")
                || mPackageName.equalsIgnoreCase("fnc.utm.com.flashoncallsmsreader")
                || mPackageName.equalsIgnoreCase("fnc.utm.com.flashoncallsmsreader_pro")) {

            return "MTool";
        }

        if (mPackageName.contains(q4u)
                || mPackageName.equalsIgnoreCase("com.pnd.shareall")
                || mPackageName.equalsIgnoreCase("app.phone2location")
                || mPackageName.equalsIgnoreCase("com.pnd.fourgspeed")
                || mPackageName.equalsIgnoreCase("com.q4u.qrscanner")) {

            return "Q4U";
        }

        if (mPackageName.contains(qsoft)) {
            return "QSoft";
        }

        if (mPackageName.contains(appnextg)) {
            return "AppNextG";
        }

        if (mPackageName.contains(livideo)) {
            return "LIVideo";
        }

        if (mPackageName.contains(m24apps) || mPackageName.contains("m24")) {
            return "M24Apps";
        }

        if (mPackageName.contains(microapp)) {
            return "MicorApp";
        }

        if (mPackageName.contains(techproof)) {
            return "TechProof";
        }

        if (mPackageName.contains(apps24)) {
            return "Apps24";
        }

        if (mPackageName.contains(thinkcoders)) {
            return "Think Coders";
        }

        if (mPackageName.contains(topapp) || mPackageName.contains(indapp)) {
            return "Top App";
        }

        if (mPackageName.contains(droidfoxes)) {
            return "DroidFoxes";
        }

        if (mPackageName.contains(berockfit)) {
            return "BeRockFit";
        }
        return "BeRockFit";
    }

    /**
     * @return AboutUs page will be show according to their respective package_name
     * remember to change about_us_logo icon and ic_powered_by icon.
     */
    public Class<?> showAboutUsPage() {
                return AboutUsActivity.class;


    }
}
