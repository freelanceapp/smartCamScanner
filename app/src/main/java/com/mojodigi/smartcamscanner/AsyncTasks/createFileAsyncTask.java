package com.mojodigi.smartcamscanner.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.CustomProgressDialog;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class createFileAsyncTask extends AsyncTask<Void, Void, Boolean> {

    int FILE_TYPE;
    Context mContext;
    AsyncResponse delegate;
    Bitmap fileBitmap;
    Activity activity;
    String fileName;
    public interface AsyncResponse {
        void onSuccess();
        void onFailure();
    }
    public createFileAsyncTask(Context mContext,Activity activity,AsyncResponse delegate,String fileName,Bitmap fileBitmap,int FILE_TYPE)
    {
        this.FILE_TYPE=FILE_TYPE;
        this.mContext=mContext;
        this.delegate=delegate;
        this.fileBitmap=fileBitmap;
        this.activity=activity;
        this.fileName=fileName;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mContext, mContext.getResources().getString(R.string.msg_file));


    }

    @Override
    protected Boolean doInBackground(Void... voids) {
           if(FILE_TYPE==Constants.TYPE_JPG)
               return  createJpg(fileName);
           else
               return createPdf(fileName);

    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if(aBoolean)
            delegate.onSuccess();
        else
            delegate.onFailure();

          CustomProgressDialog.dismiss();

    }


    private  boolean createJpg(String fileName)
    {
        boolean status=false;
        String pathToJpg="";
       if(fileName!=null) {
           File file = null;
           if (Utility.checkOrCreateParentDirectory()) {
               pathToJpg = Constants.imageFolderName;
               file = new File(pathToJpg);
               if (!file.exists())
                   file.mkdir();
           }

           if (file != null && file.exists() && fileBitmap != null) {

               File fileTobecreated = new File(pathToJpg + "/" + fileName + ".jpg");
               try {
                   FileOutputStream out = new FileOutputStream(fileTobecreated);
                   fileBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                   out.flush();
                   out.close();
                   status = true;
                   if (status)
                       sendBroadcast(fileTobecreated);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }

        return status;
    }
    private void sendBroadcast(File outputFile)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(outputFile);
            scanIntent.setData(contentUri);
            mContext.sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            mContext.sendBroadcast(intent);
        }
    }

    private boolean createPdf(String fileName) {
        boolean boolean_save=false;
        File file=null;
        if(fileName!=null) {
            if (Utility.checkOrCreateParentDirectory()) {
                String pathTopdf = Constants.pdfFolderName;
                file = new File(pathTopdf);
                if (!file.exists())
                    file.mkdir();
            }


            if (file != null && file.exists()) {
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics displaymetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

                float hight = displaymetrics.heightPixels;
                float width = displaymetrics.widthPixels;

                int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(fileBitmap.getWidth(), fileBitmap.getHeight(), 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas canvas = page.getCanvas();


                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#ffffff"));
                canvas.drawPaint(paint);


                fileBitmap = Bitmap.createScaledBitmap(fileBitmap, fileBitmap.getWidth(), fileBitmap.getHeight(), true);

                paint.setColor(Color.BLUE);
                canvas.drawBitmap(fileBitmap, 0, 0, null);
                document.finishPage(page);


                // write the document content
                //String targetPdf = "/sdcard/testjmm.pdf";
                String targetPdf = Constants.pdfFolderName + "/" + fileName + ".pdf";
                File filePath = new File(targetPdf);
                try {
                    document.writeTo(new FileOutputStream(filePath));
                    //btn_convert.setText("Check PDF");
                    boolean_save = true;
                    if (boolean_save)
                        sendBroadcast(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
                }


                // close the document
                document.close();
                // to  make  the image visible in gallery;


            }
        }
        return boolean_save;
    }
}
