package com.mojodigi.smartcamscanner.AsyncTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.folder_Model;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.CustomProgressDialog;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("NewApi")
public class getFolderAsyncTask <T> extends AsyncTask<Void, Void, ArrayList<T>> {

    public  T model_type;

    Context mContext;
    foldetlistListener delegate;



    public interface foldetlistListener<T>  {

        void returnFolderList(ArrayList<T> output);

    }

       public getFolderAsyncTask(Context mContext, foldetlistListener delegate )
       {
            this.delegate = delegate;
            this.mContext=mContext;

        }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CustomProgressDialog.show(mContext, mContext.getResources().getString(R.string.loading_msg) );
    }

    @Override
    protected ArrayList<T> doInBackground(Void... voids){

        return  (ArrayList<T>)getUserFolderList();
    }

    @Override
    protected void onPostExecute(ArrayList<T> list)  {
        super.onPostExecute(list);

        CustomProgressDialog.dismiss();
       delegate.returnFolderList(list);


    }

    public ArrayList<folder_Model> getUserFolderList()
    {
        ArrayList<folder_Model> folder_List = new ArrayList<>();

        File rootPath = new File(Constants.parentfolder);
        if(rootPath.exists())
        {
           File data[]= rootPath.listFiles();
           for(int i=0;i<data.length;i++)
           { File file = data[i];
               if(file.isDirectory())
               { folder_Model model = new folder_Model();
                   model.setFolderName(file.getName());
                   model.setFolderPath(file.getAbsolutePath());
                   folder_List.add(model);
                   }
           }

        }

        // reverse to  get  most  recent folder on top
       //Collections.reverse(folder_List);

        return folder_List;

    }


}
