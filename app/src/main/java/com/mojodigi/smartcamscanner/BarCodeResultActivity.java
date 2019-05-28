package com.mojodigi.smartcamscanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.mojodigi.smartcamscanner.AddsUtility.AddConstants;
import com.mojodigi.smartcamscanner.AddsUtility.AddMobUtils;
import com.mojodigi.smartcamscanner.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Util.Utility;

import static com.mojodigi.smartcamscanner.Constants.Constants.QrData;


public class BarCodeResultActivity extends AppCompatActivity {

    private String QrDataIntent;
    private Context mContext;
    private SharedPreferenceUtil addprefs;
    private  View adContainer;
    private AdView mAdView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bar_cod_result);

        if(mContext==null) {
            mContext = BarCodeResultActivity.this;
        }

        Utility.setActivityTitle(mContext, Utility.getString(mContext, R.string.scan_result));

        ImageView ok_button=findViewById(R.id.okButton);
        ImageView shareButton=findViewById(R.id.shareButton);
        TextView qrResult=findViewById(R.id.qrResult);
        qrResult.setTypeface(Utility.typeFace_calibri(mContext));


        mAdView = (AdView) findViewById(R.id.adView);
        adContainer = findViewById(R.id.adMobView);

        QrDataIntent = getIntent().getExtras().getString(QrData,null );

        if(QrDataIntent!=null) {
            //Utility.dispToast(mContext, QrDataIntent);
            qrResult.setText(QrDataIntent);
        }

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(QrDataIntent!=null) {

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, QrDataIntent);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }


            }
        });

        dispAppd();

    }

    @Override
    protected void onResume() {
        super.onResume();



    }
    public void dispAppd()
    {
        addprefs = new SharedPreferenceUtil(mContext);

        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))
                adutil.displayServerBannerAdd(addprefs,adContainer , mContext);
           /* else if(AddPrioverId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
            {
                try {
                    int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                    int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                    adutil.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
                }catch (Exception e)
                {
                    String string = e.getMessage();
                    System.out.print(""+string);
                }
            }*/
            else if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mContext,addprefs , BarCodeResultActivity.this);
                adutil.dispFacebookInterestialAdds(mContext,addprefs);
            }


        }
        else {
            adutil.displayLocalBannerAdd(mAdView);
        }


        //  banner add
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
