package com.mojodigi.smartcamscanner.AsyncTasks;


import android.content.Context;
import android.os.AsyncTask;

import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.CustomProgressDialog;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class getFolderFilesAsyncTask  <T> extends AsyncTask<Void, Void, ArrayList<T>> {


    Context mContext;
    fileListListener delaget;
    String folderPath;
    ArrayList<fileModel> allFilesList=new ArrayList<>();
    boolean isRequestingAllFiles;
    public interface fileListListener<T>  {

        void returnFileList(ArrayList<T> output);

    }

    public getFolderFilesAsyncTask(Context mContext,String folderPath ,boolean isRequestingAllFiles,fileListListener delegate)
    {
      this.mContext=mContext;
      this.delaget=delegate;
      this.folderPath=folderPath;
      this.isRequestingAllFiles=isRequestingAllFiles;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mContext,mContext.getResources().getString(R.string.loading_msg) );
    }

    @Override
    protected ArrayList<T> doInBackground(Void... voids) {

        if(isRequestingAllFiles)
           return  (ArrayList<T>)listAllfiles(Constants.parentfolder+"/");
        else
            return  (ArrayList<T>)getFileList();

    }

    @Override
    protected void onPostExecute(ArrayList<T> fileList) {
        super.onPostExecute(fileList);

        CustomProgressDialog.dismiss();
        delaget.returnFileList(fileList);
    }



    private ArrayList<fileModel> getFileList() {
        ArrayList<fileModel> fileList = new ArrayList<>();
        File rootPath=new File(folderPath);

        if (rootPath.exists() && rootPath.isDirectory()) {
            File[] files = rootPath.listFiles();

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                fileModel model = new fileModel();
                model.setFileName(f.getName());
                model.setFileSize(Utility.humanReadableByteCount(f.length(), true));
                model.setFileModifiedDate(Utility.LongToDate((f.lastModified())));
                model.setFilePath(f.getPath());
                model.setDateToSort(f.lastModified());

                if(f.getAbsolutePath().endsWith("jpg"))
                {
                    model.setIsImgs(true);
                }
               // model.setIsImgs(true);
                fileList.add(model);
            }

            // sort  t0  get  most  recent files on top
            Collections.sort(fileList, new Comparator<fileModel>() {
                public int compare(fileModel o1, fileModel o2) {
                    return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));
                }
            });


            return fileList;
        } else {
            return fileList;
        }


    }

    public  ArrayList<fileModel> listAllfiles(String directoryName) {


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
                            String ext=Utility.getFileExtensionfromPath(file.getAbsolutePath());
                            // hidden files will not be included in all  files folder;
                            if(!ext.equalsIgnoreCase(Constants.hiddenFileExtension)) {
                                if (file.getAbsolutePath().endsWith("jpg")) {
                                    model.setIsImgs(true);
                                }
                                allFilesList.add(model);
                            }


                    } else if (file.isDirectory()) {
                        listAllfiles(file.getAbsolutePath());
                    }
                }

        }


        // sort  t0  get  most  recent files on top
        Collections.sort(allFilesList, new Comparator<fileModel>() {
            public int compare(fileModel o1, fileModel o2) {
                return Utility.longToDate(o2.getDateToSort()).compareTo(Utility.longToDate(o1.getDateToSort()));
            }
        });


        return allFilesList ;

    }
}
