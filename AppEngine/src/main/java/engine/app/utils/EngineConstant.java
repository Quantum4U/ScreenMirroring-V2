package engine.app.utils;

import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;

public class EngineConstant {

    public static final String DEVICE_ID_1 = "CE0B0B18784B2BC37B7DF29D250342AD"; //Office samsung j2
    public static final String DEVICE_ID_2 = "FBEE963E8B04CC4026D543BFFC1F3479"; //Office OPPO api 23
    public static final String DEVICE_ID_3 = "AFBAAC019C9D3C98744A01B540B4D874"; //Office Xiaomi Redmi Note 3
    public static final String DEVICE_ID_4 = "2A25AD8F00457CE180B03544F1941575"; //Office Samsung G6
    public static final String DEVICE_ID_5 = "0BB00B090AB5C7707998CC95A4E84841"; //Office Samsung j2 api 27
    public static final String DEVICE_ID_6 = "6040D60587FCD92DB8EF5219AFB04C37"; //Office OPPO A3 api22
    public static final String DEVICE_ID_7 = "81B319DB2DCF85A0FEA17FD6BDDC06F7"; //Office Samsung G5
    public static final String DEVICE_ID_8 = "6C375A950789902A01F70F395CA69522"; //Office Xiaomi Redmi Note 7
    public static final String DEVICE_ID_9 = "1A884CBAA9A6A16984C6290B52FC1A03"; //Huawei PRITI
    public static final String DEVICE_ID_10 = "D9281CD323A5304F4F1D1D44E26C81ED";//OFC J2********

    public static final String DEVICE_ID_11 = "F5C01E0C920F1DAE743DC651F5B92F13"; //Rakesh motorola
    public static final String DEVICE_ID_12 = "52404615A6E0C4125DCF550F50A1874F"; //Simu Samsung
    public static final String DEVICE_ID_13 = "3EDE534F20CF745A2250087552969B2B"; //Samsung j7 prime
    public static final String DEVICE_ID_14 = "91F605C0C7246D81B5AA5B9A104F2224"; //sweta
    public static final String DEVICE_ID_15 = "51C1B6969EA6A4D4DA13EBF84ADB1DC1";  //Monika One plus device
    public static final String DEVICE_ID_16 = "3BBAA3A3F0E66596F475B56C1FFE5E28";  //Dev Sir Redmi Note7
    public static final String DEVICE_ID_17 = "265106DAF956F1951CBFCE51D0E3C425CB"; // ofc Samsung SM-G570F
    public static final String DEVICE_ID_18 = "7770BAC7DB9AF7F57FCE0E19F63FC6C9";   // Rajeev Sir MotoRola
    public static final String DEVICE_ID_19 = "6AA665195E927D9F146F0D168B5A887A";  //Hitesh Device

    public static final String DEVICE_ID_20 = "1A56D9CC36259740945271DBE4258557"; // SamSung M30 , Android 10
    public static final String DEVICE_ID_21 = "6D2E58FD4A61733550753ED4BC9D6CC3"; //Shayan Device
    public static final String DEVICE_ID_22 = "E31CC6B4D27BDF40CC3800D8A2B47012"; //Nokia 5.3

    public static final String quantum = "quantum";
    public static final String mtool = "mtool";
    public static final String q4u = "q4u";
    public static final String qsoft = "qsoft";
    public static final String appnextg = "appnextg";
    public static final String livideo = "livideo";
    public static final String m24apps = "m24apps";
    public static final String microapp = "microapp";
    public static final String techproof = "techproof";
    public static final String apps24 = "apps24";
    public static final String thinkcoders = "thinkcoders";
    public static final String topapp = "topapp";
    public static final String indapp = "indapp";
    public static final String droidfoxes = "droidfoxes";
    public static final String berockfit = "berockfit";

    public static final String isShowBackArrow = "isShowBackArrow";

    public static final String EXIT_PROMPT_Q4U = "EXIT_PROMPT_Q4U";
    public static final String EXIT_PROMPT_QUANTUM = "EXIT_PROMPT_QUANTUM";
    public static final String EXIT_PROMPT_M24APPS = "EXIT_PROMPT_M24APPS";
    public static final String EXIT_PROMPT_APPS24 = "EXIT_PROMPT_APPS24";
    public static final String EXIT_PROMPT_THINKCODERS = "EXIT_PROMPT_THINKCODERS";
    public static final String EXIT_PROMPT_TOPAPP = "EXIT_PROMPT_TOPAPP";
    public static final String EXIT_PROMPT_DROIDFOXES = "EXIT_PROMPT_DROIDFOXES";
    public static final String EXIT_PROMPT_OTHERS = "EXIT_PROMPT_OTHERS";
    public static final String EXIT_PROMPT_BEROCKFIT = "EXIT_PROMPT_BEROCKFIT";


    public static String getLongPrint(String veryLongString) {
        int maxLogSize = 1000;
        String longdata = null;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;

            longdata = veryLongString.substring(start, end);
            Log.d("EngineConstant", "Hello getLongPrint engine data" + " " + veryLongString.substring(start, end));
        }

        return longdata;
    }

    public static RequestConfiguration addTestDeviceForAdMob() {
        ArrayList<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);

        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        testDevices.add(EngineConstant.DEVICE_ID_1);
        testDevices.add(EngineConstant.DEVICE_ID_2);
        testDevices.add(EngineConstant.DEVICE_ID_3);
        testDevices.add(EngineConstant.DEVICE_ID_4);
        testDevices.add(EngineConstant.DEVICE_ID_5);
        testDevices.add(EngineConstant.DEVICE_ID_6);
        testDevices.add(EngineConstant.DEVICE_ID_7);
        testDevices.add(EngineConstant.DEVICE_ID_8);
        testDevices.add(EngineConstant.DEVICE_ID_9);
        testDevices.add(EngineConstant.DEVICE_ID_10);
        testDevices.add(EngineConstant.DEVICE_ID_11);
        testDevices.add(EngineConstant.DEVICE_ID_12);
        testDevices.add(EngineConstant.DEVICE_ID_13);
        testDevices.add(EngineConstant.DEVICE_ID_14);
        testDevices.add(EngineConstant.DEVICE_ID_15);
        testDevices.add(EngineConstant.DEVICE_ID_16);
        testDevices.add(EngineConstant.DEVICE_ID_17);
        testDevices.add(EngineConstant.DEVICE_ID_18);
        testDevices.add(EngineConstant.DEVICE_ID_19);
        testDevices.add(EngineConstant.DEVICE_ID_20);
        testDevices.add(EngineConstant.DEVICE_ID_21);

        return new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
    }

}
