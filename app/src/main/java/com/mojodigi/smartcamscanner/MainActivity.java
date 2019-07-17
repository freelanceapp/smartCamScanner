package com.mojodigi.smartcamscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mojodigi.smartcamscanner.Adapter.PagerAdapter;
import com.mojodigi.smartcamscanner.AddsUtility.AddConstants;
import com.mojodigi.smartcamscanner.AddsUtility.AddMobUtils;
import com.mojodigi.smartcamscanner.AddsUtility.JsonParser;
import com.mojodigi.smartcamscanner.AddsUtility.OkhttpMethods;
import com.mojodigi.smartcamscanner.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Fragments.FolderFragment;
import com.mojodigi.smartcamscanner.Model.ImageToPDFOptions;
import com.mojodigi.smartcamscanner.Util.Utility;

import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mojodigi.smartcamscanner.Constants.Constants.STORAGE_LOCATION;
import static com.mojodigi.smartcamscanner.Util.StringUtils.getDefaultStorageLocation;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    private SharedPreferences permissionStatus;

    Context mContext;
    Toolbar toolbar;
    int REQUEST_CODE_SCAN=100;
    ImageView scannedImage;
    private Bitmap bitmapToShare;
    boolean boolean_save=false;
    public static MainActivity instance;


    private IntentIntegrator qrScan;
    private TextView textView_Drawer_Haed;

    int REQUEST_CODE_CHOOSE=99;

    // List<Uri> mSelected;
    public static ArrayList<String> mImagesUri = new ArrayList<>();;


    private String mHomePath;
    private ImageToPDFOptions mPdfOptions;
    private SharedPreferences mSharedPreferences;
    PagerAdapter adapter;
    ViewPager viewPager;



    //add vars

    SharedPreferenceUtil addprefs;
    private AdView mAdView;
    RelativeLayout smaaToAddContainer;
    View adContainer;
    RelativeLayout addhoster;
    private boolean webcaldone;

    public   String appVersionName ="";
    public   TextView navAppVersion_Txt ;




    //add push notification
    private String fcm_Token ="" ;
    public   String deviceID ="";
    public   String nameOfDevice ="";

    int max_execute ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(mContext==null) {
            mContext = MainActivity.this;
        }

        permissionStatus =mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        instance = this;

        setSupportActionBar(toolbar);
        askForPermission();

        getSupportActionBar().setTitle(Utility.getString(mContext, R.string.home));

        //init pdf vars
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        mPdfOptions=new ImageToPDFOptions();
        deleteHiddenExtraFile();


        //
    }


    private void getPushToken()
    {
        /***********************Start**********************************************/

        deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("Android ID : ",""+deviceID);
        nameOfDevice = Build.MANUFACTURER+" "+Build.MODEL+" "+Build.VERSION.RELEASE;
        Log.e("Device Name : ",""+nameOfDevice);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = pinfo.versionName;
            Log.e("App Version Name : ",""+appVersionName);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch (Exception ex){ ex.printStackTrace();}



        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcm_Token = instanceIdResult.getToken();
                        Log.e("New Token : ", fcm_Token);

                        if (AddConstants.checkIsOnline(mContext)) {
                            Log.e("Network is available ", "PushNotification Called");
                            new PushNotificationCall().execute();
                        } else {
                            Log.e("No Network", "PushNotification Call failed");
                        }
                    }
                });


        if(addprefs!=null) {

            boolean status=addprefs.getBoolanValue(AddConstants.AutoStartKey, false);
            if(!status) {

                Intent intent = new Intent();
                String manufacturer = android.os.Build.MANUFACTURER;
                //showAutoStartPermDialog(manufacturer,intent);
                switch (manufacturer) {

                    case "xiaomi":
                        intent.setComponent(new ComponentName("com.miui.securitycenter",
                                "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                        break;
                    case "oppo":
                        intent.setComponent(new ComponentName("com.coloros.safecenter",
                                "com.coloros.safecenter.permission.startup.StartupAppListActivity"));

                        break;
                    case "vivo":
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                        break;
                }

                List<ResolveInfo> arrayListInfo = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                if (arrayListInfo.size() > 0) {
                    // startActivity(intent);
                    showAutoStartPermDialog(manufacturer, intent);

                }

            }
        }
    }

    private void showAutoStartPermDialog(String brandName, final Intent intent)
    {
        String appName=mContext.getResources().getString(R.string.app_name);
        final Dialog dialog =  new Dialog(mContext);
        dialog.setContentView(R.layout.autostart_dialog);
        TextView heading_Txt=dialog.findViewById(R.id.headingTxt);
        TextView desc_Txt=dialog.findViewById(R.id.desc_Txt);
        TextView ok_Txt=dialog.findViewById(R.id.ok);

        ok_Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (intent != null) {
                        addprefs.setValue(AddConstants.AutoStartKey, true);
                        dialog.dismiss();
                        startActivity(intent);
                    }
                }catch (ActivityNotFoundException e)
                {
                    e.printStackTrace();
                }

            }
        });

        heading_Txt.setText(appName+" "+mContext.getResources().getString(R.string.need_permission));
        desc_Txt.setText(brandName+" "+mContext.getResources().getString(R.string.custom_ui)+" "+appName+".\n"+mContext.getResources().getString(R.string.need_enable)+" "+appName+" "+mContext.getResources().getString(R.string.towork));

        dialog.show();

    }



    // this web call send token to  server;

    public class PushNotificationCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Log.e("deviceId ", deviceID);
                Log.e("deviceName ", nameOfDevice);
                Log.e("fcmToken ", fcm_Token);
                Log.e("appVer ", appVersionName);

                JSONObject requestObj = AddConstants.prepareFcmJsonRequest(mContext, deviceID, nameOfDevice, fcm_Token , appVersionName);
                return OkhttpMethods.CallApi(mContext, AddConstants.API_PUSH_NOTIFICATION, requestObj.toString());

            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("Push Json Response ", s);




            if (s != null  ) {
                try {
                    JSONObject mainJson = new JSONObject(s);
                    if (mainJson.has("status")) {
                        String status = JsonParser.getkeyValue_Str(mainJson, "status");
                        Log.e("status", "" + status);


                        if (status.equalsIgnoreCase("false")) {

                            if (mainJson.has("data")) {
                                JSONObject dataJson = mainJson.getJSONObject("data");
                            } else {
                                String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                Log.e("message", "" + message);
                            }
                        }
                        if (status.equalsIgnoreCase("false")) {
                            Log.e("status", "" + status);

                            if(max_execute<=5){
                                new PushNotificationCall().execute();
                                max_execute++;
                            }
                        }
                        else {
                            if(addprefs!=null)
                                addprefs.setValue(AddConstants.isFcmRegistered, true);
                        }
                    }
                } catch (JSONException e) {
                    Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.e("", "else"  );
            }

        }
    }





    private void deleteHiddenExtraFile()
    {
        // deletes  the files that are shown  to user after  decryption;;;;;

        try {
            File directory = new File(Constants.hiddenFilesFolder);
            // Get all files from a directory.
            File[] fList = directory.listFiles();
            System.out.print("" + fList);

            for (File file : fList) {
                if (file.isFile() && file.exists()) {
                    String extension=Utility.getFileExtensionfromPath(file.getAbsolutePath());
                    if(extension!=null) {
                        if (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("jpg")) {
                            Log.d("fileName-->Hiddenfile", "" + file.getAbsolutePath());
                            file.delete();
                        }
                    }
                }
            }
        }catch (Exception e)
        {

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mContext!=null) {
            if (AddConstants.checkIsOnline(mContext))
            {
                if(!webcaldone)
                    new WebCall().execute();

            }
        }
    }

    public void askForPermission()
    {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[1]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[2])) {
                //Show Information about why you need the permission

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " needs to access your storage.");

                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(Utility.getString(mContext, R.string.need_permission));
                builder.setMessage(mContext.getString(R.string.app_name) + Utility.getString(mContext, R.string.permission_required));
                builder.setPositiveButton(Utility.getString(mContext, R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        //Toast.makeText(mContext, "Go to Permissions to Grant storage access", Toast.LENGTH_LONG).show();
                        Utility.dispToast(mContext, R.string.gotopermissions);
                    }
                });
                builder.setNegativeButton(Utility.getString(mContext, R.string.cancel_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions((Activity) mContext, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], false);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            initComponents();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                initComponents();


            } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[0]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[1]) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissionsRequired[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(Utility.getString(mContext, R.string.need_permission));
                builder.setMessage(mContext.getString(R.string.app_name) + Utility.getString(mContext, R.string.permission_required));
                builder.setPositiveButton(Utility.getString(mContext, R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext,permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(Utility.getString(mContext, R.string.cancel_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //Toast.makeText(mContext, , Toast.LENGTH_LONG).show();
                Utility.dispToast(mContext, R.string.unable_to_get_permission);
            }
        }
    }



    private void initComponents() {

        navAppVersion_Txt = (TextView) findViewById(R.id.navAppVersion_Txt);

        navAppVersion_Txt.setTypeface(Utility.typeFace_calibri(mContext));

        textView_Drawer_Haed=findViewById(R.id.textViewTitle);
        //textView_Drawer_Haed.setTypeface(Utility.typeFace_Gotham_Bold(mContext));
        qrScan = new IntentIntegrator(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(Utility.getString(mContext, R.string.recent)));
        tabLayout.addTab(tabLayout.newTab().setText(Utility.getString(mContext, R.string.allfolder)));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setFab();
        changeTabsFont(tabLayout);
        createSystemDirectory();



        if(mContext!=null)
        {
            addprefs = new SharedPreferenceUtil(mContext);

            addhoster=findViewById(R.id.addhoster);
            mAdView = (AdView) findViewById(R.id.adView);
            adContainer = findViewById(R.id.adMobView);

        }



        if(addprefs!=null) {
            boolean st=addprefs.getBoolanValue(AddConstants.isFcmRegistered, false);
            System.out.print(""+st);
            //if(!addprefs.getBoolanValue(AddConstants.isFcmRegistered, false)) {
            if(addprefs.getBoolanValue(AddConstants.isFcmRegistered, false)) {
                getPushToken();
            }
        }


        AddMobUtils utils = new AddMobUtils();
        utils.dispFacebookBannerAdd(mContext, addprefs,MainActivity.this);


        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = pinfo.versionName;
            Log.e("App Version Name : ",""+appVersionName);

            if(appVersionName!=null) {
                //String appVersion = "App Version : " + appVersionName;
                String appVersion = Utility.getString(mContext, R.string.appversion)+" : " + appVersionName;
                setNavAppVersion(appVersion);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch (Exception ex){ ex.printStackTrace();}

    }

    public void setNavAppVersion(String appVersion){
        navAppVersion_Txt.setText(appVersion);
    }

    private void changeTabsFont(TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Utility.typeFace_Gotham_Bold(mContext));
                }
            }
        }
    }

    private void setFab() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();



                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                Intent cameraIntent = new Intent(MainActivity.this, ScanActivity.class);
//                cameraIntent.putExtra(ScanActivity.EXTRA_BRAND_IMG_RES, R.drawable.ic_crop_white_24dp);
//                cameraIntent.putExtra(ScanActivity.EXTRA_TITLE, "Crop Document");
//                cameraIntent.putExtra(ScanActivity.EXTRA_ACTION_BAR_COLOR, R.color.colorPrimary);
//                cameraIntent.putExtra(ScanActivity.EXTRA_LANGUAGE, "en");
//                startActivityForResult(cameraIntent, REQUEST_CODE_SCAN);

              onScanButtonClicked();




            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void onScanButtonClicked() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Intent intent = new Intent(this, ScanActivity.class);
      /*  intent.putExtra(ScanActivity.EXTRA_BRAND_IMG_RES, R.drawable.ic_crop_white_24dp);
        intent.putExtra(ScanActivity.EXTRA_TITLE, "Crop Document");
        intent.putExtra(ScanActivity.EXTRA_ACTION_BAR_COLOR, R.color.colorPrimary);
        intent.putExtra(ScanActivity.EXTRA_LANGUAGE, "en");*/

        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {

            Bitmap bitmapPhotoCamera = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                bitmapToShare = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                Constants.imageBitmap = bitmapToShare;
            }else {
                bitmapToShare = (Bitmap)data.getExtras().get("data");
                Constants.imageBitmap = bitmapToShare;
            }

            if (bitmapToShare != null) {
                Constants.imageBitmap = bitmapToShare;
                Intent intent = new Intent(mContext, CameraImageActivity.class);
                intent.putExtra("BITMAP_PICK_CAMERA", bitmapToShare);
                startActivity(intent);
            } else {
               // Utility.dispToast(mContext, "Please scan again");
                Utility.dispToast(mContext, R.string.scan_again);
            }



//            String imgPath = data.getStringExtra(ScanActivity.RESULT_IMAGE_PATH);
//            bitmapToShare = Utils.getBitmapFromLocation(imgPath);
//
//            // scannedImage.setImageBitmap(bitmapToShare);
//            //scannedImage.setVisibility(View.VISIBLE);
//
//            if (bitmapToShare != null) {
//                Intent intent = new Intent(mContext, ScannedImageActivity.class);
//                //  intent.putExtra("Image", bitmapToShare);
//                Constants.imageBitmap = bitmapToShare;
//                startActivity(intent);
//            } else {
//                Utility.dispToast(mContext, "Please scan again");
//            }




//            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                getContentResolver().delete(uri, null, null);
//                viewHolder.image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }


        // to read qr code

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String readData="";
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                //Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                Utility.dispToast(mContext, R.string.resultnotfound);
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());

                    Intent intent = new Intent(mContext,BarCodeResultActivity.class);
                    intent.putExtra(Constants.QrData, obj.toString());
                    startActivity(intent);

                    // Utility.dispToast(mContext, obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast

                    Intent intent = new Intent(mContext,BarCodeResultActivity.class);
                    intent.putExtra(Constants.QrData, result.getContents().toString());
                    startActivity(intent);

                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }

        // to read qr code

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mImagesUri.clear();
            //mSelected = Matisse.obtainResult(data);
            mImagesUri.addAll(Matisse.obtainPathResult(data));
            Log.d("Matisse", "mSelected: " + mImagesUri);

            cropImage();
        }

        //to get files from



        //createPdf();


    }
    private void cropImage() {
        Intent intent = new Intent(mContext, CropImageActivity.class);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    /**
     * Saves Current Image with grayscale filter
     */


   /* private void saveCurrentImageInGrayscale() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();
            Picasso picasso = Picasso.with(mContext);
            Transformation transformation = new GrayscaleTransformation();

            for (int countElements = mImagesUri.size() - 1; countElements >= 0; countElements--) {
                String fileName = String.format(getString(R.string.filter_file_name),
                        String.valueOf(System.currentTimeMillis()), "grayscale");
                File outFile = new File(dir, fileName);
                String imagePath = outFile.getAbsolutePath();
                picasso.load(new File(mImagesUri.get(countElements)))
                        .transform(transformation)
                        .into(getTarget(imagePath));
                mImagesUri.remove(countElements);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }*/



   /* private void createPdf()
    {


        mPdfOptions.setImagesUri(mImagesUri);

         mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        // mPdfOptions.setImageScaleType(mImageScaleType);
         mPdfOptions.setImageScaleType(Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO);
        // mPdfOptions.setPageNumStyle(mPageNumStyle);
       // mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
       // mPdfOptions.setPageColor(mPageColor);
            mPdfOptions.setOutFileName("testMultiplePdf"+mImagesUri.size());


        if (Utility.checkOrCreateParentDirectory()) {
            String pathToPdf = Constants.pdfFolderName+"/";
            File fileLocation = new File(pathToPdf);

            if (fileLocation.exists())
                new CreatePdf(mContext, mPdfOptions, pathToPdf, instance).execute();
            else
            {
                if(fileLocation.mkdir())
                {
                    if(fileLocation.exists())
                        new CreatePdf(mContext, mPdfOptions, pathToPdf, instance).execute();
                }
            }
        }

    }

*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case  R.id.action_scanQr:
                if(qrScan !=null) {
                    qrScan.initiateScan();
                }
                break;

            case R.id.action_create_folder:
                //Utility.dispToast(mContext, "create folder");
                createFolderDialog();
                break;

        }



        return super.onOptionsItemSelected(item);
    }




    private void createFolderDialog() {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_create_folder);
        dialog.show();


        TextView View_create=dialog.findViewById(R.id.View_create);
        TextView View_cancel=dialog.findViewById(R.id.View_cancel);
        final EditText folderName_Edit=dialog.findViewById(R.id.Edit_folder);


        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        View_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Utility.checkOrCreateParentDirectory()) {
                    File file = new File(Constants.parentfolder + "/" + folderName_Edit.getText().toString().trim());

                    if (file.mkdir())
                    {
                        Utility.dispToast(mContext,getResources().getString(R.string.folder_success));
                        dialog.dismiss();

                        Fragment page = (Fragment) adapter.instantiateItem(viewPager,  viewPager.getCurrentItem());
                        //Log.e("Fragment " , ""+page);
                        if (viewPager.getCurrentItem() == 1 && page != null) {
                            ((FolderFragment) page).updateFolderList();
                        }

                        Constants.callUpdateMethod = true;

                        viewPager.setCurrentItem(1);

                    }
                    else
                    {
                        Utility.dispToast(mContext,getResources().getString(R.string.folder_failure) );
                        dialog.dismiss();
                    }


                }


            }
        });

    }

    private void  createSystemDirectory()
    {
        if(Utility.checkOrCreateParentDirectory())
        {


            String AllFiles=Constants.allFilesFolder;
            File allFileFolder=new File(AllFiles);
            if (!allFileFolder.exists())
                allFileFolder.mkdir();

            String pathTopdf = Constants.pdfFolderName;
            File pdfFolder = new File(pathTopdf);
            if (!pdfFolder.exists())
                pdfFolder.mkdir();



            String pathToImages=Constants.imageFolderName;
            File imgFolder=new File(pathToImages);
            if (!imgFolder.exists())
                imgFolder.mkdir();


            String pathToPrivateFiles=Constants.hiddenFilesFolder;
            File privateFilesFolder=new File(pathToPrivateFiles);
            if (!privateFilesFolder.exists())
                privateFilesFolder.mkdir();



        }


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_gallery:

                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(1000)
                        //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        //.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);

                break;
           /* case R.id.nav_files:
                Intent intent =new Intent(this,Activity_List_Pdfs.class);
                startActivity(intent);
                break;
            case R.id.nav_pdf:
                Intent intent1 =new Intent(this,PDFViewActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_imgs:
                Utility.dispToast(mContext,"Images");
                Intent inten =new Intent(this,fileTabActivity.class);
                startActivity(inten);
                break;*/
            case R.id.nav_share:
                shareApp();
                break;

            case  R.id.nav_privacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.privacyUrl));
                startActivity(browserIntent);
                break;

            case R.id.nav_scan:

                if(qrScan !=null) {
                    qrScan.initiateScan();
                }

                break;



        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.mojodigi.smartcamscanner&hl=en");
        startActivity(Intent.createChooser(share,  getResources().getString(R.string.app_name)));


    }

    private void shareDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_share);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RelativeLayout sharePdf=dialog.findViewById(R.id.share1Layout);
        RelativeLayout shareImage=dialog.findViewById(R.id.share2Layout);
        TextView cancelShare=dialog.findViewById(R.id.cancelShare);
        sharePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.dispToast(mContext, "share pdf ");
                //  createPdf(Utility.getFileName());

            }
        });
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.dispToast(mContext, "share image ");

            }
        });
        cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });




        dialog.show();

    }



    @SuppressLint("NewApi")
    public class WebCall extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... strings) {
            String versioName="0";
            int versionCode=0;
            try {
                versioName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionCode = getPackageManager().getPackageInfo(getPackageName(),0 ).versionCode;

                Log.d("currentVersion", "" + versioName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                // handel any other exception
            }

            try {
                JSONObject requestObj= AddConstants.prepareAddJsonRequest(mContext, AddConstants.VENDOR_ID , versioName ,versionCode );

                return OkhttpMethods.CallApi(mContext,AddConstants.API_URL,requestObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return ""+e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("JsonResponse", s);
            long ms2=System.currentTimeMillis();
            System.out.print("Milliseconds after-->>"+ms2);
            Log.e("Milliseconds after-->>", ""+ms2);
            if (addprefs != null)
            {
                int responseCode=addprefs.getIntValue(AddConstants.API_RESPONSE_CODE, 0);

                if (s != null  && responseCode==200 ) {
                    try {
                        JSONObject mainJson = new JSONObject(s);
                        if (mainJson.has("status")) {
                            String status = JsonParser.getkeyValue_Str(mainJson, "status");

                            String newVersion=JsonParser.getkeyValue_Str(mainJson,"appVersion");
                            addprefs.setValue(AddConstants.APP_VERSION, newVersion);
                            //addprefs.setValue(AddConstants.APP_VERSION, "1.10");

                            if (status.equalsIgnoreCase("true")) {

                                String adShow = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                if (adShow.equalsIgnoreCase("true")) {
                                    if (mainJson.has("data")) {
                                        JSONObject dataJson = mainJson.getJSONObject("data");
                                        AddMobUtils util = new AddMobUtils();
                                        String show_Add = JsonParser.getkeyValue_Str(mainJson, "AdShow");

                                        String adProviderId =JsonParser.getkeyValue_Str(dataJson, "adProviderId");
                                        String adProviderName = JsonParser.getkeyValue_Str(dataJson, "adProviderName");
                                        //String newVersion=JsonParser.getkeyValue_Str(dataJson,"appVersion");

                                        String appId_PublisherId = JsonParser.getkeyValue_Str(dataJson, "appId_PublisherId");
                                        String bannerAdId = JsonParser.getkeyValue_Str(dataJson, "bannerAdId");
                                        String interstitialAdId = JsonParser.getkeyValue_Str(dataJson, "interstitialAdId");
                                        String videoAdId = JsonParser.getkeyValue_Str(dataJson, "videoAdId");


                                        /*String appId_PublisherId = "ca-app-pub-3940256099942544~3347511713";//testID
                                        String bannerAdId = "ca-app-pub-3940256099942544/6300978111"; //testId
                                        String interstitialAdId = "ca-app-pub-3940256099942544/1033173712";//testId
                                        String videoAdId = "ca-app-pub-3940256099942544/5224354917";//testId*/


                                        Log.d("AddiDs", adProviderName + " ==" + appId_PublisherId + "==" + bannerAdId + "==" + interstitialAdId + "==" + videoAdId);


                                        //check for true value above in code so  can put true directly;
                                        try {
                                            addprefs.setValue(AddConstants.SHOW_ADD, Boolean.parseBoolean(show_Add));
                                        }catch (Exception e)
                                        {
                                            // IN CASE OF EXCEPTION CONSIDER  FALSE AS THE VALUE WILL NOT BE TRUE,FALSE.
                                            addprefs.setValue(AddConstants.SHOW_ADD, false);
                                        }
                                        // addprefs.setValue(AddConstants.APP_VERSION, newVersion);
                                        //addprefs.setValue(AddConstants.APP_VERSION, "1.22");
                                        addprefs.setValue(AddConstants.ADD_PROVIDER_ID, adProviderId);
                                        addprefs.setValue(AddConstants.APP_ID, appId_PublisherId);
                                        addprefs.setValue(AddConstants.BANNER_ADD_ID, bannerAdId);
                                        addprefs.setValue(AddConstants.INTERESTIAL_ADD_ID, interstitialAdId);
                                        addprefs.setValue(AddConstants.VIDEO_ADD_ID, videoAdId);

                                        if (adContainer != null  && adProviderId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))

                                        {
                                            // requst googleAdd

                                            util.displayServerBannerAdd(addprefs, adContainer, mContext);
                                            // util.showInterstitial(addprefs,HomeActivity.this, interstitialAdId);
                                            //util.displayRewaredVideoAdd(addprefs,mContext, videoAdId);
                                            webcaldone=true;

                                        }
                                        else if (adProviderId.equalsIgnoreCase(AddConstants.InMobiProvideId))
                                        {

                                            // inmobi adds not being implemented in this version
                                            // inmobi adds not being implemented in this version

                                        }
                                        else  if(adProviderId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
                                        {
                                            util.dispFacebookBannerAdd(mContext, addprefs,MainActivity.this);
                                            webcaldone=true;
                                        }
                                    } else {
                                        String message = JsonParser.getkeyValue_Str(mainJson, "message");
                                        Log.d("message", "" + message);
                                    }
                                } else {
                                    String message = JsonParser.getkeyValue_Str(mainJson, "message");

                                    Log.d("message", "" + message);
                                }
                            }

                            dispUpdateDialog();
                        }

                    } catch (JSONException e) {
                        Log.d("jsonParse", "error while parsing json -->" + e.getMessage());
                        e.printStackTrace();
                    }


                } else {
                    // display loccal AddiDs Adds;
                    if (mAdView != null) {
                        AddMobUtils util = new AddMobUtils();
                        util.displayLocalBannerAdd(mAdView);

                    }
                }


            }

        }
    }
    private void dispUpdateDialog() {
        try {
            String currentVersion = "0";
            String newVersion="0";
            if(addprefs!=null)
                newVersion=addprefs.getStringValue(AddConstants.APP_VERSION, AddConstants.NOT_FOUND);

            try {
                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                Log.d("currentVersion", "" + currentVersion);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (Float.parseFloat(newVersion) > Float.parseFloat(currentVersion) && !newVersion.equalsIgnoreCase("0"))

            {
                if (mContext != null) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_version_update);
                    long time = addprefs.getLongValue("displayedTime", 0);
                    long diff=86400000; // one day
                    //long diff=60000; // one minute;

                    if (time < System.currentTimeMillis() - diff) {
                        dialog.show();
                        addprefs.setValue("displayedTime", System.currentTimeMillis());
                    }

                    TextView later = dialog.findViewById(R.id.idDialogLater);
                    TextView updateNow = dialog.findViewById(R.id.idDialogUpdateNow);
                    TextView idVersionDetailsText = dialog.findViewById(R.id.idVersionDetailsText);
                    TextView idAppVersionText = dialog.findViewById(R.id.idAppVersionText);
                    TextView idVersionTitleText = dialog.findViewById(R.id.idVersionTitleText);


                    idVersionTitleText.setTypeface(Utility.typeFace_calibri(mContext));
                    idVersionDetailsText.setTypeface(Utility.typeFace_calibri(mContext));
                    idAppVersionText.setTypeface(Utility.typeFace_calibri(mContext));
                    later.setTypeface(Utility.typeFace_calibri(mContext));
                    updateNow.setTypeface(Utility.typeFace_calibri(mContext));

                    idAppVersionText.setText(newVersion);

                    later.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });


                    updateNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String appPackageName = getPackageName(); // package name of the app
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }


                            dialog.dismiss();
                        }
                    });


                }


            }
        }
        catch (Exception e)
        {

        }

    }



}
