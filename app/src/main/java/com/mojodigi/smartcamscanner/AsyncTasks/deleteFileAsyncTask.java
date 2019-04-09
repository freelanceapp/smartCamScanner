package com.mojodigi.smartcamscanner.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.mojodigi.smartcamscanner.Util.CustomProgressDialog;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.util.ArrayList;

public class deleteFileAsyncTask extends AsyncTask<Void,Void,Integer> {


    Context mContext;
    ArrayList<File> filesTobeDeleted;
    int counter;
    deleteListener delegate;
    ArrayList<String> deletedFiles;

    public deleteFileAsyncTask(Context mContext,deleteListener delegate,ArrayList<File> filesTobeDeleted)
    {
        deletedFiles=new ArrayList<>();
        this.mContext=mContext;
        this.filesTobeDeleted=filesTobeDeleted;
        this.delegate=delegate;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mContext, "Deleting file");
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        for(int i=0;i<filesTobeDeleted.size();i++)
        {
            String deleFilePath=filesTobeDeleted.get(i).getAbsolutePath();
            if(filesTobeDeleted.get(i).delete())
            {
                deletedFiles.add(deleFilePath);
                counter++;
            }

        }
        return counter;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        CustomProgressDialog.dismiss();

        //Utility.dispToast(mContext,counter+" file deleted");
        delegate.ondeleteSucceed(deletedFiles);


    }
    public interface deleteListener
    {
       void ondeleteSucceed(ArrayList<String> deletedFiles);
    }
}
