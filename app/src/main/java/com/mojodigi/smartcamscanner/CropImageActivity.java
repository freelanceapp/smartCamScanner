package com.mojodigi.smartcamscanner;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.smartcamscanner.AsyncTasks.CreatePdf;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Interfaces.OnPDFCreatedInterface;
import com.mojodigi.smartcamscanner.Model.ImageToPDFOptions;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.pdfUtils.FileUtils;
import com.mojodigi.smartcamscanner.pdfUtils.PageSizeUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.mojodigi.smartcamscanner.Constants.Constants.pdfDirectory;
import static com.mojodigi.smartcamscanner.Constants.Constants.pdfFolderName;


public class CropImageActivity extends AppCompatActivity implements View.OnClickListener,OnPDFCreatedInterface {

    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private HashMap<Integer, Uri> mCroppedImageUris = new HashMap<>();
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private ImageToPDFOptions mPdfOptions;
    public static CropImageActivity instance;


    private boolean mCurrentImageEdited = false;
    private boolean mFinishedclicked = false;


    Button cropButton,rotateButton;
    ImageView previousImageButton,nextimageButton;
    TextView imagecount;
    TextView mImagecount;
    CropImageView mCropImageView;
    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);

        mContext=CropImageActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mPdfOptions=new ImageToPDFOptions();
        instance=this;

        initActivityComponents();
        setUpCropImageView();


        mImages = MainActivity.mImagesUri;
        mFinishedclicked = false;

        for (int i = 0; i < mImages.size(); i++)
            mCroppedImageUris.put(i, Uri.fromFile(new File(mImages.get(i))));

        if (mImages.size() == 0)
            finish();

        setImage(0);
    }

    private void initActivityComponents() {
        cropButton=findViewById(R.id.cropButton);
        rotateButton=findViewById(R.id.rotateButton);
        previousImageButton=findViewById(R.id.previousImageButton);
        nextimageButton=findViewById(R.id.nextimageButton);
        imagecount=findViewById(R.id.imagecount);
        mCropImageView=findViewById(R.id.cropImageView);
        mImagecount=findViewById(R.id.imagecount);

        previousImageButton.setOnClickListener(this);
        nextimageButton.setOnClickListener(this);
        rotateButton.setOnClickListener(this);
        cropButton.setOnClickListener(this);


    }


    public void cropButtonClicked() {
        mCurrentImageEdited = false;
        String root = Environment.getExternalStorageDirectory().toString();
        //File myDir = new File(root + pdfDirectory);
        File myDir = new File(pdfFolderName+"/");
        boolean st=myDir.exists();
        Uri uri = mCropImageView.getImageUri();

        if (uri == null) {

            Utility.dispToast(mContext,getResources().getString(R.string.error_occurred ));
            return;
        }

        String path = uri.getPath();
        String fname = "cropped_im";
        if (path != null){}
            fname = "cropped_" + FileUtils.getFileName(path);

           File file = new File(myDir, fname);
           Uri fileuri=Uri.fromFile(file);
           mCropImageView.saveCroppedImageAsync(fileuri);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_done:
                mFinishedclicked = true;
                cropButtonClicked();
                return true;
            case R.id.action_skip:
                mCurrentImageEdited = false;
                nextImageClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void nextImageClicked() {
        if (!mCurrentImageEdited) {
            mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            //showSnackbar(this, R.string.save_first);
            Utility.dispToast(mContext, getResources().getString(R.string.save_first));
        }
    }

    /**
     * Initial setup of crop image view
     */
    private void setUpCropImageView() {


        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {

                Uri furi=result.getUri();

                mCroppedImageUris.put(mCurrentImageIndex, result.getUri());
                mCropImageView.setImageUriAsync(mCroppedImageUris.get(mCurrentImageIndex));

                if (mFinishedclicked) {
                    //sends data back to  the activity no need now
                    /*Intent intent = new Intent();
                    intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
                    setResult(Activity.RESULT_OK, intent);
                    finish();*/
                    mImagesUri.clear();
                    for (int i = 0; i < mCroppedImageUris.size(); i++) {
                        if (mCroppedImageUris.get(i) != null) {
                            mImagesUri.add(mCroppedImageUris.get(i).getPath());

                        }
                    }

                    if(mImagesUri.size()>0)
                    createPdf();


                }

            }
        });



    }

    private void createPdf()
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


    /**
     * Set image in cropimage view & increment counters
     * @param index - image index
     */
    private void setImage(int index) {

        mCurrentImageEdited = false;

        if (index < 0 || index >= mImages.size())
            return;

        mImagecount.setText(getString(R.string.cropImage_activityTitle) + " " + (index + 1) + " of " + mImages.size());
        mCropImageView.setImageUriAsync(mCroppedImageUris.get(index));
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();

        switch (id)
        {
            case R.id.rotateButton:
                mCurrentImageEdited = true;
                mCropImageView.rotateImage(90);
                break;

            case R.id.nextimageButton:
                if (!mCurrentImageEdited) {
                    mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages.size();
                    setImage(mCurrentImageIndex);
                } else {
                    //showSnackbar(this, R.string.save_first);
                    Utility.dispToast(mContext, getResources().getString(R.string.save_first));
                }
                break;

            case R.id.previousImageButton:
                if (!mCurrentImageEdited) {
                    if (mCurrentImageIndex == 0) {
                        mCurrentImageIndex = mImages.size();
                    }
                    mCurrentImageIndex = (mCurrentImageIndex - 1) % mImages.size();
                    setImage(mCurrentImageIndex);
                } else {
                    //showSnackbar(this, R.string.save_first);
                    Utility.dispToast(mContext, getResources().getString(R.string.save_first));
                }

                break;

            case R.id.cropButton:
                  cropButtonClicked();
                break;
        }
    }

    @Override
    public void onPDFCreationStarted() {
        Utility.dispToast(mContext, "creation started");
    }

    @Override
    public void onPDFCreated(boolean success, String path) {

        if(success)
            Utility.dispToast(mContext, "created path    -->"+path);
        else
            Utility.dispToast(mContext, "Not created");



    }
}
