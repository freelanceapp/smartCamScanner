package com.mojodigi.smartcamscanner;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mojodigi.smartcamscanner.AsyncTasks.createFileAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailCallback;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailItem;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailsAdapter;
import com.mojodigi.smartcamscanner.filterUtils.ThumbnailsManager;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class CameraImageActivity extends AppCompatActivity implements createFileAsyncTask.AsyncResponse , ThumbnailCallback {

    private Context mContext;
    private Toolbar toolbar;
    private ImageView scannedImage;


    private int REQUEST_CODE_SCAN=100;

    public static CameraImageActivity instance;
    private Bitmap imageBitmap;
    private Bitmap imageBitmapTosave;
    private EditText fileNameEditText;
    private Matrix mMatrixImage ;
    private static final int CROP_IMAGE = 001;

    private RecyclerView thumbListView;

    static {

        System.loadLibrary("NativeImageProcessor");

    }


    //pinch
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    //pinch


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }
    //pinch
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            scannedImage.setScaleX(mScaleFactor);
            scannedImage.setScaleY(mScaleFactor);
            return true;
        }
    }
    //pinch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_image);

        if(mContext==null) {
            mContext = CameraImageActivity.this;
        }

        instance = this;

        Utility.setActivityTitle(mContext, "Scanned file");

        if(Constants.imageBitmap!=null) {
            imageBitmap = Constants.imageBitmap;
        }

        Intent extrasIntentCamera = getIntent();
        if (extrasIntentCamera != null) {
            Bitmap photoCameraBitmap = (Bitmap) this.getIntent().getParcelableExtra("BITMAP_PICK_CAMERA");
            imageBitmap = photoCameraBitmap;
        }

        initComponents();

    }

    private void initComponents() {

        thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
        scannedImage=findViewById(R.id.scannedImage);
  //pinch
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
 //pinch
        fileNameEditText=findViewById(R.id.fileNameEditText);

        fileNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                return false;
            }});

        fileNameEditText.setText(Utility.getFileName());
        fileNameEditText.setSelection(fileNameEditText.getText().toString().length());


        if(imageBitmap!=null)
            scannedImage.setImageBitmap(imageBitmap);
            //scannedImage.setImage(ImageSource.bitmap(imageBitmap));
        initHorizontalList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.scanned_image_menu, menu);
        getMenuInflater().inflate(R.menu.scanned_file_menu, menu);
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

                Bitmap thumbImage = Bitmap.createScaledBitmap(imageBitmap, 640, 640, false);
                //Bitmap thumbImage =imageBitmap;
                imageBitmapTosave = imageBitmap;
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

            case R.id.action_rotate:
//                 Bitmap mBitmap = imageBitmap;
//
//                 float degrees = 45 ;
//                 Bitmap rotatedImage= rotateImage(mBitmap, degrees);
//                 scannedImage.setImageBitmap(rotatedImage);
//                 mBitmap = imageBitmap;

                Bitmap bInput = imageBitmap , bOutput;
                float degrees = 90;//rotation degree
                Matrix matrix = new Matrix();
                matrix.setRotate(degrees);
                bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true);
                scannedImage.setImageBitmap(bOutput);
                //scannedImage.setImage(ImageSource.bitmap(bOutput));
                imageBitmap=bOutput;    //new  line
                bindDataToAdapter();  //new line
                break;


            case R.id.action_crop:
                if(imageBitmap!=null)
                cropImageUri(getImageUri(mContext, imageBitmap));

                break;

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


        scannedImage.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640,640 ,false )));
        //scannedImage.setImage(ImageSource.bitmap(filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640,640 ,false ))));
        imageBitmapTosave=filter.processFilter(Bitmap.createScaledBitmap(imageBitmap, 640,640 ,false ));

    }




    protected void cropImageUri(Uri picUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP",android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intent.setDataAndType(picUri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {
            Log.e("", "Your device doesn't support the crop action!");
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CROP_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    Bitmap mBitmap = null;

                    Uri selectedImageUri = data.getData();    //here

                    if(selectedImageUri==null ) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            mBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        } else {
                            mBitmap = (Bitmap) data.getExtras().get("data");
                        }
                    }
                    else if(selectedImageUri !=null && Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                    {
                        mBitmap = (Bitmap) data.getExtras().get("data");
                    }
                    else {

                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Matrix mat = new Matrix();
                    if (mBitmap != null) {
                        Bitmap mCropedBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
                                mBitmap.getWidth(), mBitmap.getHeight(), mat, true);
                        imageBitmap = mCropedBitmap;
                        scannedImage.setImageBitmap(imageBitmap);


                        bindDataToAdapter();


                    }
                    break;


                }
        }
    }


    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }
}
