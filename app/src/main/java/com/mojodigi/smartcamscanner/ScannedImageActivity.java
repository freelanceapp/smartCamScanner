package com.mojodigi.smartcamscanner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mojodigi.smartcamscanner.AsyncTasks.createFileAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Util.Utility;


public class ScannedImageActivity extends AppCompatActivity implements createFileAsyncTask.AsyncResponse {



    Context mContext;
    Toolbar toolbar;
    ImageView scannedImage;
    int REQUEST_CODE_SCAN=100;
    public static ScannedImageActivity instance;
    private Bitmap imageBitmap;
    EditText fileNameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.scanned_image_activity);
         mContext=ScannedImageActivity.this;
         instance=this;

         if(Constants.imageBitmap!=null) {
             imageBitmap = Constants.imageBitmap;
         }
         initComponents();


         }

        private void initComponents() {
        scannedImage=findViewById(R.id.scannedImage);
            fileNameEditText=findViewById(R.id.fileNameEditText);
            fileNameEditText.setText(Utility.getFileName());

        if(imageBitmap!=null)
            scannedImage.setImageBitmap(imageBitmap);

      
        }




   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            String imgPath = data.getStringExtra(ScanActivity.RESULT_IMAGE_PATH);
            imageBitmap = Utils.getBitmapFromLocation(imgPath);
            scannedImage.setImageBitmap(imageBitmap);
            scannedImage.setVisibility(View.VISIBLE);



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
                   *//* textViewName.setText(obj.getString("name"));

                   textViewAddress.setText(obj.getString("address"));*//*

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






    }*/





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scanned_image_menu, menu);
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
        RelativeLayout savePdf=dialog.findViewById(R.id.save1Layout);
        RelativeLayout saveImage=dialog.findViewById(R.id.save2Layout);
        TextView cancelShare=dialog.findViewById(R.id.cancelSave);
        savePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fileNameEditText.getText().toString().length()>0) {
                    if (imageBitmap != null) {
                        new createFileAsyncTask(mContext, instance, instance, fileNameEditText.getText().toString().trim(), imageBitmap, Constants.TYPE_PDF).execute();
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
        cancelShare.setOnClickListener(new View.OnClickListener() {
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
}
