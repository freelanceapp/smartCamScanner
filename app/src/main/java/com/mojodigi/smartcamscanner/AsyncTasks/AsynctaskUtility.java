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
import android.service.carrier.CarrierService;
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
    ArrayList<fileModel> recentList = new ArrayList<>();
    ArrayList<pdfModel> pdfList = new ArrayList<>();
    ArrayList<fileModel> imgList = new ArrayList<>();
    ArrayList<pdfModel> AllFileList = new ArrayList<>();
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
            case Constants.REQUST_RECENT_FILE:
                recentList.clear();
                return  (ArrayList<T>)listRecentFiles(Constants.parentfolder+"/");
            case Constants.REQUST_ALL_FILE:
                AllFileList.clear();
                return  (ArrayList<T>)listRecentFiles(Constants.parentfolder+"/");
            case Constants.REQUST_PDF_FILE:
                pdfList.clear();
                return  (ArrayList<T>)listPdfFile(Constants.parentfolder+"/");
            case Constants.REQUST_IMAGES_FILE:
                imgList.clear();
                    return  (ArrayList<T>)listImages(Constants.parentfolder+"/");
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


    public  ArrayList<fileModel> listImages(String directoryName) {


        String rootPath = directoryName;
        if (Utility.checkOrCreateParentDirectory())
        {
            File directory = new File(rootPath);
            // Get all files from a directory.
            File[] fList = directory.listFiles();
            if (fList != null)
                for (File file : fList) {
                    if (file.isFile()) {

                        if(file.getAbsolutePath().endsWith(".jpg")) {
                            fileModel model = new fileModel();
                            model.setFileName(file.getName());
                            model.setFileSize(Utility.humanReadableByteCount(file.length(), true));
                            model.setFileModifiedDate(Utility.LongToDate((file.lastModified())));
                            model.setFilePath(file.getPath());
                            model.setDateToSort(file.lastModified());
                            imgList.add(model);
                        }

                    } else if (file.isDirectory()) {
                        listImages(file.getAbsolutePath());
                    }
                }

        }


        // sort  t0  get  most  recent files on top
        Collections.sort(imgList, new Comparator<fileModel>() {
            public int compare(fileModel o1, fileModel o2) {
                return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));
            }
        });


        return imgList ;

    }

    public  ArrayList<pdfModel> listPdfFile(String directoryName) {


        String rootPath = directoryName;
        if (Utility.checkOrCreateParentDirectory())
        {
            File directory = new File(rootPath);
            // Get all files from a directory.
            File[] fList = directory.listFiles();
            if (fList != null)
                for (File file : fList) {
                    if (file.isFile()) {

                        if(file.getAbsolutePath().endsWith(".pdf")) {
                            pdfModel model = new pdfModel();
                            model.setFileName(file.getName());
                            model.setFileSize(Utility.humanReadableByteCount(file.length(), true));
                            model.setFileModifiedDate(Utility.LongToDate((file.lastModified())));
                            model.setFilePath(file.getPath());
                            model.setDateToSort(file.lastModified());
                            pdfList.add(model);
                        }

                    } else if (file.isDirectory()) {
                        listPdfFile(file.getAbsolutePath());
                    }
                }

        }


        // sort  t0  get  most  recent files on top
        Collections.sort(pdfList, new Comparator<pdfModel>() {
            public int compare(pdfModel o1, pdfModel o2) {
                return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));
            }
        });


        return pdfList ;

    }

    public  ArrayList<fileModel> listRecentFiles(String directoryName) {


        String rootPath = directoryName;
        if (Utility.checkOrCreateParentDirectory())
        {
            File directory = new File(rootPath);
            // Get all files from a directory.
            File[] fList = directory.listFiles();
            if (fList != null)
                for (File file : fList) {
                    if (file.isFile()) {

                        fileModel model = new fileModel();
                        model.setFileName(file.getName());
                        model.setFileSize(Utility.humanReadableByteCount(file.length(), true));
                        model.setFileModifiedDate(Utility.LongToDate((file.lastModified())));
                        model.setFilePath(file.getPath());
                        model.setDateToSort(file.lastModified());

                        if(!file.getAbsolutePath().endsWith(".des")) {
                            if (file.getAbsolutePath().endsWith("jpg"))
                                model.setIsImgs(true);
                            else
                                model.setIsImgs(false);


                            recentList.add(model);
                        }


                    } else if (file.isDirectory()) {
                        listRecentFiles(file.getAbsolutePath());
                    }
                }

        }


      // sort  t0  get  most  recent files on top
        Collections.sort(recentList, new Comparator<fileModel>() {
                    public int compare(fileModel o1, fileModel o2) {
                        return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));
                    }
                });


        return recentList ;

    }

}
