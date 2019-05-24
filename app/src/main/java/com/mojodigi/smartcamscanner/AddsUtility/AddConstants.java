package com.mojodigi.smartcamscanner.AddsUtility;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AddConstants
{

    public static  String NEWSURL="NewsUrl";
    public static final String WEB_URL="WebUrl";
    public static final String CLICK_PUSH_NOTIFICATION="ClickPushNotification";


    public static final String API_PUSH_NOTIFICATION = "http://onetracky.com/Apps/smart-scan/pushNotifications/setFcmToken.php";


    public static final String API_RESPONSE_CODE="apiResponseCode";

    public static final String APP_NAME_POSTFIX= "_JMM";  // will  identify the client like  lava ,carbon etc ;
    public static final String VENDOR_ID="JMMSS001";   // where SS Stands for Smart Scanner



    public static final String isFcmRegistered="fcm";



    public static final String API_URL="http://development.bdigimedia.com/riccha_dev/App-Ad-Mgmt/getAdDetailsByAppName.php";

    public static final String ADD_PROVIDER_ID="addProvId";
    //sharedPrefKeys

    // if this value comes false from  the server end we will not diaplay adds in app, means will not display adds even using the addIds from local sring.xml file;
      public static final String SHOW_ADD="show_Add";

    // this will contain mainId if  assigned for addproject  from  dashboard of add providerNetWork ;
    public static  final String APP_ID="appID";
    //this will contain bannerId incase of google /PlaceMentId in case of Inmobi /bannerId incase of SMAATO
    public static  final String BANNER_ADD_ID="bannerId";
    //this will contain interestialAddId in case of google/InMobi  and AddSpaceId in case of SMAATOO
    //it shows fullscreenAdss
     public static  final String INTERESTIAL_ADD_ID="interestialId";

     //this will contain videoAddId in case of every add providerNetWork;
    public static  final String VIDEO_ADD_ID="videoAddId";


    public static final String NOT_FOUND="0";
    public static final String NO_ADDS="No ad is currently available matching the requesting parameter.";
    public static final String APP_VERSION = "appversion";













    //JsonRequestkeys

    public static final String key_appName="appName";
    public static final String key_packageName="packageName";
    public static final String key_appVendorId="appVendorId";
    public static final String key_appManufacturer="appManufacturer";
    public static final String key_deviceModel="deviceModel";
    public static final String key_deviceId="deviceId";
    public static final String key_AppVersioName="versionName";
    public static final String key_AppversionCode="versionCode";


  //AddProviderskeys    will  decide  which type of adds  need to  be displayed in mobile app;
  // this values will  be compared with  the server key addProvId and on the basis od that adds will be displayed;


    public static final String Adsense_Admob_GooglePrivideId="1";
    public static final String InMobiProvideId="2";
    public static final String SmaatoProvideId="3";
    public static final String  FaceBookAddProividerId="4";



    public static JSONObject prepareAddJsonRequest(Context mContext, String valueVendorId, String versionName,int versionCode)
    {
        String deviceModel="";
        String deviceManufacturer="";
        String deviceId="";
        try
        {
            //get device information
            deviceModel = android.os.Build.MODEL;
            deviceManufacturer = android.os.Build.MANUFACTURER;
            deviceId= Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            System.out.print(""+deviceId);
            //get device information
        }
        catch (Exception e) {
            String str=e.getMessage();
            Log.d("Exception",""+str);
        }


        JSONObject object = new JSONObject();
        try {

            object.put(key_appName, mContext.getString(com.mojodigi.smartcamscanner.R.string.app_name)+APP_NAME_POSTFIX);
            object.put(key_packageName,mContext.getPackageName());
            object.put(key_appVendorId,valueVendorId );
            //device manufacturer;
            object.put(key_appManufacturer, deviceManufacturer);
            object.put(key_deviceModel, deviceModel);
            object.put(key_deviceId, deviceId);
            object.put(key_AppVersioName, versionName);
            object.put(key_AppversionCode, versionCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JsonRequest",object.toString() );
        return object;
    }

    public static JSONObject prepareFcmJsonRequest(Context mContext, String deviceID , String nameOfDevice , String fcm_Token , String appVersion)
    {
        JSONObject object  =  new JSONObject();
        try {
            object.put("deviceId", deviceID);
            object.put("deviceName", nameOfDevice);
            object.put("fcmToken", fcm_Token);
            object.put("appVer", appVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Json request", object.toString());
        return object;
    }



    public static boolean checkIsOnline(Context mContext)
    {
        ConnectivityManager ConnectionManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            return true;

        }
        else
        {
          return false;

        }
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }








}
