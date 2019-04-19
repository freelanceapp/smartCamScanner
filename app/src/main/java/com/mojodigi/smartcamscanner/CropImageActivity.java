package com.mojodigi.smartcamscanner;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Folder_dialog;
import com.mojodigi.smartcamscanner.AsyncTasks.CreatePdf;
import com.mojodigi.smartcamscanner.AsyncTasks.getFolderAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Interfaces.OnPDFCreatedInterface;
import com.mojodigi.smartcamscanner.Model.ImageToPDFOptions;
import com.mojodigi.smartcamscanner.Model.folder_Model;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.pdfUtils.FileUtils;
import com.mojodigi.smartcamscanner.pdfUtils.PageSizeUtils;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.mojodigi.smartcamscanner.Constants.Constants.pdfFolderName;


public class CropImageActivity extends AppCompatActivity implements View.OnClickListener,OnPDFCreatedInterface,getFolderAsyncTask.foldetlistListener ,MultiSelectAdapter_Folder_dialog.folderListener {

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
    EditText fileNameEditText;

    Dialog dialog;

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

        fileNameEditText=findViewById(R.id.fileName);
        fileNameEditText.setText(Utility.getFileName());
        fileNameEditText.setSelection(fileNameEditText.getText().toString().length());
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


        rotateButton.setTypeface(Utility.typeFace_calibri(mContext));
        cropButton.setTypeface(Utility.typeFace_calibri(mContext));
        imagecount.setTypeface(Utility.typeFace_calibri(mContext));




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
                    {

                        new getFolderAsyncTask<folder_Model>(mContext,instance).execute();
                        //createPdf(fileNameEditText.getText().toString());
                    }



                }

            }
        });



    }




    private void createPdf(String fname,String pathToSave)
    {


        mPdfOptions.setImagesUri(mImagesUri);

        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        // mPdfOptions.setImageScaleType(mImageScaleType);
        mPdfOptions.setImageScaleType(Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO);
        // mPdfOptions.setPageNumStyle(mPageNumStyle);
        // mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
        // mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(fname);


        if (Utility.checkOrCreateParentDirectory()) {
            //String pathToPdf = Constants.pdfFolderName+"/";
            String pathToPdf = pathToSave+"/";
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
        else {
            Utility.dispToast(mContext,"file path does not exists" );
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

        if(success) {
            Utility.dispToast(mContext, getResources().getString(R.string.file_success));

            finish();
        }
        else
            Utility.dispToast(mContext, getResources().getString(R.string.file_failure));



    }

    @Override
    public void onFolderSelected(folder_Model contact) {

        Utility.dispToast(mContext,contact.getFolderPath());

        if(dialog!=null)
        {
            dialog.dismiss();
            createPdf(fileNameEditText.getText().toString(),contact.getFolderPath());
        }



    }

    @Override
    public void returnFolderList(ArrayList output) {


        MultiSelectAdapter_Folder_dialog adapter=new MultiSelectAdapter_Folder_dialog(mContext,output,this);

          dialog = new Dialog(mContext);
          dialog.setContentView(R.layout.dialog_folder_list);
        RecyclerView folderListRecycle=dialog.findViewById(R.id.folder_recycler_view);
        TextView cancelButton=dialog.findViewById(R.id.cancelButton);
        TextView headertxt=dialog.findViewById(R.id.headertxt);

        headertxt.setTypeface(Utility.typeFace_calibri(mContext));
        cancelButton.setTypeface(Utility.typeFace_calibri(mContext));


        ImageView addFolderButton=dialog.findViewById(R.id.addFolderButton);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        folderListRecycle.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        //folderListRecycle.setLayoutManager(new GridLayoutManager(mContext, 2));


        folderListRecycle.setAdapter(adapter);

        if(output.size()>0)
        {
            dialog.show();
        }
        else {
            createFolderDialog();
        }

        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                createFolderDialog();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private void createFolderDialog() {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_create_folder);
        dialog.show();


        TextView View_create=dialog.findViewById(R.id.View_create);
        TextView View_cancel=dialog.findViewById(R.id.View_cancel);
        TextView headertxt=dialog.findViewById(R.id.headertxt);

        headertxt.setTypeface(Utility.typeFace_calibri(mContext));
        View_create.setTypeface(Utility.typeFace_calibri(mContext));
        View_cancel.setTypeface(Utility.typeFace_calibri(mContext));


        final EditText folderName_Edit=dialog.findViewById(R.id.Edit_folder);
        folderName_Edit.setTypeface(Utility.typeFace_calibri(mContext));

        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        View_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(folderName_Edit.getText().toString().length()==0)
                {
                    folderName_Edit.setError("please write folder name");
                    return;
                }
                if(Utility.isWhitespace(folderName_Edit.getText().toString()))
                {
                    folderName_Edit.setError(mContext.getResources().getString(R.string.namerequired));

                    return;
                }

                if(Utility.checkOrCreateParentDirectory()) {
                    File file = new File(Constants.parentfolder + "/" + folderName_Edit.getText().toString().trim());

                    if (file.mkdir())
                    {
                        Utility.dispToast(mContext,getResources().getString(R.string.folder_success));
                        dialog.dismiss();

                        if(mImagesUri.size()>0) {
                            new getFolderAsyncTask<folder_Model>(mContext, instance).execute();
                        }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


}
