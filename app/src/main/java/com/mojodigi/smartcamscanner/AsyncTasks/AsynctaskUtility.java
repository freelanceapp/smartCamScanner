package com.mojodigi.smartcamscanner.AsyncTasks;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.sax.EndElementListener;
import android.util.Log;

import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.pdfModel;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.Model.imgModel;

import com.mojodigi.smartcamscanner.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.CustomProgressDialog;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

//

public class AsynctaskUtility<T> extends AsyncTask<Void, Void, ArrayList<T>> {

    private Context mContext;
    boolean boolean_folder;
    public  T model_type;
    int fileStorageType;

    public AsynctaskUtility(Context mContext, AsyncResponse delegate, int fileStorageType)
    {
        this.mContext=mContext;
        this.delegate=delegate;
        this.fileStorageType=fileStorageType;
        }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        CustomProgressDialog.show(mContext,mContext.getResources().getString(R.string.loading_msg));
    }

    // you may separate this or combined to caller class.
    public interface AsyncResponse<T>  {
        void processFinish(ArrayList<T> output);
    }
    public AsyncResponse delegate = null;


    @Override
    protected ArrayList<T> doInBackground(Void... voids) {

        switch (fileStorageType)
        {
            case 1:
                return  (ArrayList<T>)lstPdfFiles();

            case 2:
                return  (ArrayList<T>)lstRecentFiles();

            case 3:
                return  (ArrayList<T>)lstPdfFiles();

                }
        return null;


    }


    @Override
    protected void onPostExecute(ArrayList<T> list) {

        delegate.processFinish(list);

        CustomProgressDialog.dismiss();

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();


        }



  public ArrayList<pdfModel> lstPdfFiles()
  {
      ArrayList<pdfModel> pdfList =new ArrayList<>();
      String rootPathStr=Constants.pdfFolderName+"/";
      File rootPath = new File(rootPathStr);

    if(rootPath.exists() && rootPath.isDirectory())
    { File[] files = rootPath.listFiles();

     for(int i=0;i<files.length;i++)
        {
            File f = files[i];
            pdfModel model=new pdfModel();
            model.setFileName(f.getName());
            model.setFileSize(Utility.humanReadableByteCount(f.length(),true));
            model.setFileModifiedDate(Utility.LongToDate((f.lastModified())));
            model.setFilePath(f.getPath());
            pdfList.add(model);
        }
        return  pdfList;
    }
      else
    {
       return pdfList;
    }


  }

    public ArrayList<fileModel> lstRecentFiles()

    {
        ArrayList<fileModel> recentList = new ArrayList<>();
        String rootPathStr = Constants.pdfFolderName + "/";
        File rootPath = new File(rootPathStr);

        if (rootPath.exists() && rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                fileModel model = new fileModel();
                model.setFileName(f.getName());
                model.setFileSize(Utility.humanReadableByteCount(f.length(), true));
                model.setFileModifiedDate(Utility.LongToDate((f.lastModified())));
                model.setFilePath(f.getPath());
                recentList.add(model);
            }
            ArrayList<fileModel> imgList=getImgs();
            recentList.addAll(imgList);
            return recentList;
        } else {
            return recentList;
        }

        }

    private ArrayList<fileModel> getImgs() {
        ArrayList<fileModel> recentList = new ArrayList<>();
        String rootPathStr = Constants.imageFolderName + "/";
        File rootPath = new File(rootPathStr);

        if (rootPath.exists() && rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                fileModel model = new fileModel();
                model.setFileName(f.getName());
                model.setFileSize(Utility.humanReadableByteCount(f.length(), true));
                model.setFileModifiedDate(Utility.LongToDate((f.lastModified())));
                model.setFilePath(f.getPath());
                model.setIsImgs(true);
                recentList.add(model);
            }

            return recentList;
        } else {
            return recentList;
        }


    }
}
