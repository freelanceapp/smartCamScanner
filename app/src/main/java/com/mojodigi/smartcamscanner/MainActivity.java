package com.mojodigi.smartcamscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mojodigi.smartcamscanner.Adapter.PagerAdapter;
import com.mojodigi.smartcamscanner.AsyncTasks.CreatePdf;
import com.mojodigi.smartcamscanner.AsyncTasks.createFileAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Interfaces.OnPDFCreatedInterface;
import com.mojodigi.smartcamscanner.Model.ImageToPDFOptions;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.pdfUtils.PageSizeUtils;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;

import static com.mojodigi.smartcamscanner.Constants.Constants.STORAGE_LOCATION;
import static com.mojodigi.smartcamscanner.Util.StringUtils.getDefaultStorageLocation;
import static com.mojodigi.smartcamscanner.pdfUtils.ImageUtils.mImageScaleType;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext=MainActivity.this;
        permissionStatus =mContext.getSharedPreferences("permissionStatus", MODE_PRIVATE);
        instance=this;
        setSupportActionBar(toolbar);
        askForPermission();

        //init pdf vars
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());
        mPdfOptions=new ImageToPDFOptions();
        //
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
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " app need stoarge permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(mContext, "Go to Permissions to Grant storage access", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                builder.setTitle("Need Permissions");
                builder.setMessage(mContext.getString(R.string.app_name) + " app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions((Activity) mContext,permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(mContext, "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void initComponents() {

        textView_Drawer_Haed=findViewById(R.id.textViewTitle);
        //textView_Drawer_Haed.setTypeface(Utility.typeFace_Gotham_Bold(mContext));
        qrScan = new IntentIntegrator(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Recent"));
        tabLayout.addTab(tabLayout.newTab().setText("All Folders"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    }

    private void setFab() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


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
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanActivity.EXTRA_BRAND_IMG_RES, R.drawable.ic_crop_white_24dp);
        intent.putExtra(ScanActivity.EXTRA_TITLE, "Crop Document");
        intent.putExtra(ScanActivity.EXTRA_ACTION_BAR_COLOR, R.color.colorPrimary);
        intent.putExtra(ScanActivity.EXTRA_LANGUAGE, "en");
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            String imgPath = data.getStringExtra(ScanActivity.RESULT_IMAGE_PATH);
            bitmapToShare = Utils.getBitmapFromLocation(imgPath);
           // scannedImage.setImageBitmap(bitmapToShare);
            //scannedImage.setVisibility(View.VISIBLE);

            if (bitmapToShare != null) {
                Intent intent = new Intent(mContext, ScannedImageActivity.class);
              //  intent.putExtra("Image", bitmapToShare);
                Constants.imageBitmap=bitmapToShare;
                startActivity(intent);
            } else {
                Utility.dispToast(mContext, "Please scan again");
            }



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
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                   /* textViewName.setText(obj.getString("name"));

                   textViewAddress.setText(obj.getString("address"));*/

                    Utility.dispToast(mContext, obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
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

                break;

        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_camera:

                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(1000)
                        //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        //.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);

                break;
            case R.id.nav_files:
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
                break;
            case R.id.nav_share:
                shareDialog();
                break;

            case  R.id.nav_privacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.privacyUrl));
                startActivity(browserIntent);
                break;
            case R.id.nav_scan:

                break;



        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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



}
