package com.mojodigi.smartcamscanner.Application;

import android.app.Application;
import android.os.StrictMode;

import com.facebook.ads.AudienceNetworkAds;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class MyApplication extends android.support.multidex.MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


         // initilize facebook add sdk
        AudienceNetworkAds.initialize(getApplicationContext());
        AudienceNetworkAds.isInAdsProcess(getApplicationContext());
        // initilize facebook add sdk

        //App mertrica sdk

        // Creating an extended library configuration.

        //Apikey in appMetricaDashboard settings  is tracking key--

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("5f97360e-9505-4e44-875e-1c9a698daeba").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.


        //uncomment it for auto tracking while  making live

        YandexMetrica.enableActivityAutoTracking(this);





    }
}





























































