package com.mojodigi.smartcamscanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toolbar;


import com.mojodigi.smartcamscanner.AsyncTasks.createFileAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailCallback;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailItem;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailsAdapter;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailsManager;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;


public class ScannedImageActivity extends AppCompatActivity implements createFileAsyncTask.AsyncResponse , ThumbnailCallback {

    Context mContext;
    Toolbar toolbar;
    ImageView scannedImage;
    int REQUEST_CODE_SCAN=100;
    public static ScannedImageActivity instance;
    private Bitmap imageBitmap;
    private Bitmap imageBitmapTosave;
    EditText fileNameEditText;



    private RecyclerView thumbListView;
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.scanned_image_activity2);
         mContext=ScannedImageActivity.this;
         instance=this;


         Utility.setActivityTitle(mContext, "Scanned file");
         if(Constants.imageBitmap!=null) {
             imageBitmap = Constants.imageBitmap;
         }
         initComponents();


         }

        private void initComponents() {

              thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
              scannedImage=findViewById(R.id.scannedImage);
             fileNameEditText=findViewById(R.id.fileNameEditText);
             fileNameEditText.setText(Utility.getFileName());
            fileNameEditText.setSelection(fileNameEditText.getText().toString().length());


        if(imageBitmap!=null)
            scannedImage.setImageBitmap(imageBitmap);



            initHorizontalList();
        }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanned_image_menu, menu);
        return true;
    }

    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                //Bitmap thumbImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.photo), 640, 640, false);
                Bitmap thumbImage = Bitmap.createScaledBitmap(imageBitmap, 640, 640, false);
                //Bitmap thumbImage =imageBitmap;
                imageBitmapTosave=imageBitmap;
                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();

                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t6);



                //

                //

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) instance);
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case  R.id.action_save:
                if(imageBitmap!=null)
                {
                    saveDialog();
                }
                else
                {
                    Utility.dispToast(mContext, "Please scan a file to save");
                }

                break;
                case R.id.action_settings:
                Utility.dispToast(mContext, "settings");
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
    private void saveDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_save);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       ImageView savePdf=dialog.findViewById(R.id.savepdf);
       ImageView saveImage=dialog.findViewById(R.id.saveimage);
        Button cancelSave =dialog.findViewById(R.id.cancelSave);

        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fileNameEditText.getText().toString().length()>0) {
                    if (imageBitmapTosave != null) {
                        new createFileAsyncTask(mContext, instance, instance, fileNameEditText.getText().toString().trim(), imageBitmapTosave, Constants.TYPE_PDF).execute();
                    }
                }else
                {
                    Utility.dispToast(mContext,"file name can't be blank");
                }
                dialog.dismiss();


            }

        });
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*   boolean status=createJpg(Utility.getFileName());
                if(status)
                {
                    Utility.dispToast(mContext, "File created successfully");
                }
                else
                {
                    Utility.dispToast(mContext, "Error while creating file");
                }
                dialog.dismiss();*/
               if(fileNameEditText.getText().toString().length()>0) {
                   if (imageBitmap != null) {
                       new createFileAsyncTask(mContext, instance, instance, fileNameEditText.getText().toString().trim(),imageBitmap, Constants.TYPE_JPG).execute();
                   }
               }
               else {
                   Utility.dispToast(mContext,"file name can't be blank");
               }
                  dialog.dismiss();

            }
        });
        cancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onSuccess() {
        Utility.dispToast(mContext, "File created successfully");
        finish();
    }

    @Override
    public void onFailure() {
        Utility.dispToast(mContext, "Error while creating File");
        finish();
    }

    @Override
    public void onThumbnailClick(Filter filter) {

      // scannedImage.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.photo), 640, 640, false)));
        //scannedImage.setImageBitmap(filter.processFilter(imageBitmap));
        scannedImage.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640,640 ,false )));
        imageBitmapTosave=filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640,640 ,false ));

    }
}
