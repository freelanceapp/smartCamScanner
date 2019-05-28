package com.mojodigi.smartcamscanner.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Recent;
import com.mojodigi.smartcamscanner.AsyncTasks.AsynctaskUtility;
import com.mojodigi.smartcamscanner.AsyncTasks.deleteFileAsyncTask;
import com.mojodigi.smartcamscanner.AsyncTasks.encryptAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.LockerPasswordActivity;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.Model.pdfModel;
import com.mojodigi.smartcamscanner.PDFViewActivity;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.AlertDialogHelper;
import com.mojodigi.smartcamscanner.Util.RecyclerItemClickListener;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.Util.renameUtility;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.ArrayList;

public class RecentFragment extends Fragment  implements MultiSelectAdapter_Recent.recentListener ,AsynctaskUtility.AsyncResponse, AlertDialogHelper.AlertDialogListener ,deleteFileAsyncTask.deleteListener,renameUtility.reNameListener ,encryptAsyncTask.EncryptListener{



    RecyclerView recyclerView;
    MultiSelectAdapter_Recent multiSelectAdapter;

    ArrayList<fileModel> recent_ImgList = new ArrayList<>();
    ArrayList<fileModel> multiselect_list = new ArrayList<>();

    ActionMode mActionMode;
    boolean isMultiSelect = false;
    Menu context_menu;
    boolean isUnseleAllEnabled = false;
    private fileModel fileTorename;
    private  int renamePosition;
    AlertDialogHelper alertDialogHelper;
    //TextView nodataFound;
    ImageView nodataFound;

    static RecentFragment instance;

    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_recentfile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext=getActivity();


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(!isVisibleToUser)
        {
            if(mActionMode!=null)
                mActionMode.finish();
        }

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getActivity();
        alertDialogHelper =new AlertDialogHelper(getActivity(),this);
        recyclerView =view.findViewById(R.id.recycler_view);
        nodataFound=view.findViewById(R.id.nodataFound);


        instance=this;

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!=RecyclerView.NO_POSITION )
                    multi_select(position);

                else {


                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<fileModel>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode =getActivity().startActionMode(mActionModeCallback);

                    }
                }

                multi_select(position);

            }
        }));


    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.action_rename:
                    if(multiselect_list.size()==1) {
                        renameUtility obj=new renameUtility();
                        obj.fileRenameDialog(mContext, multiselect_list.get(0).getFilePath(), instance);
                    }
                    return  true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                        alertDialogHelper.showAlertDialog("", Utility.getString(mContext, R.string.delete_file)+" ("+msgDeleteFile+")", Utility.getString(mContext, R.string.menu_item_delete), Utility.getString(mContext, R.string.cancel_txt), 1, true);
                    }
                    return true;
                case R.id.action_select:
                    if(recent_ImgList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();

                    return  true;
                case  R.id.action_Share:
                    shareMultipleFilesWithNoughatAndAll();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1) {
                        Utility.DispDetailsDialog(mContext,multiselect_list.get(0));
                    }
                    else {
                        String size =Utility.calcSelectFileSize(multiselect_list);
                        System.out.println("" + size);
                        if(size!=null)
                            Utility.multiFileDetailsDlg(mContext,size,multiselect_list.size());
                    }
                    return true;
                default:
                    return false;



                case R.id.action_private:

                    if(Utility.isManualPasswordSet()) {
                        if (multiselect_list.size() >= 1) {


                            if (Utility.createOrFindAppDirectory())
                            {
                                File[] f = new File[multiselect_list.size()];
                                for (int i = 0; i < multiselect_list.size(); i++) {
                                    File file = new File(multiselect_list.get(i).getFilePath());
                                    f[i] = file;
                                }
                                if (f.length >= 1)
                                    new encryptAsyncTask(mContext, f, Constants.encryptionPassword,instance).execute();
                                else
                                    Utility.dispToast(mContext, getResources().getString(R.string.filenotfound));
                            }

                            else
                            {
                                Utility.dispToast(mContext,getResources().getString(R.string.directorynotfound));
                            }



                        }
                    }
                    else {
                        Intent i = new Intent(mContext, LockerPasswordActivity.class);
                        startActivity(i);
                    }


                    return true;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<fileModel>();
            refreshAdapter();
        }
    };

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(recent_ImgList.get(position)))
                multiselect_list.remove(recent_ImgList.get(position));
            else {
                multiselect_list.add(recent_ImgList.get(position));
                // to  rename file contain old file;

                if(multiselect_list.size()==1) {
                    fileTorename = recent_ImgList.get(position);
                    renamePosition=position;
                }
                // to  rename file contain old file;
            }

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }
    private void selectMenuChnage()
    {
        if(context_menu!=null)
        {
            if(recent_ImgList.size()==multiselect_list.size()) {
                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_selectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_unselectAll));
                        isUnseleAllEnabled=true;
                    }
                }
            }
            else {

                for (int i = 0; i < context_menu.size(); i++) {
                    MenuItem item = context_menu.getItem(i);
                    if (item.getTitle().toString().equalsIgnoreCase(getResources().getString(R.string.menu_unselectAll))) {
                        item.setTitle(getResources().getString(R.string.menu_selectAll));
                        isUnseleAllEnabled=false;
                    }
                }

            }

            // rename  options will be visible if only 1 file is selected

            MenuItem item= context_menu.findItem(R.id.action_rename);
            if (multiselect_list.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only i file is selected

        }
        getActivity().invalidateOptionsMenu();
    }

    private void unSelectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            if (multiselect_list.size() >= 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll

            if (mActionMode != null) {
                mActionMode.finish();
            }



            refreshAdapter();

        }
    }

    private void selectAll()
    {
        if (mActionMode != null)
        {
            multiselect_list.clear();

            for(int i=0;i<recent_ImgList.size();i++)
            {
                if(!multiselect_list.contains(multiselect_list.contains(recent_ImgList.get(i))))
                {
                    multiselect_list.add(recent_ImgList.get(i));
                }
            }
            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();
            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll

        }
    }

    private void shareMultipleFilesWithNoughatAndAll() {

        if(multiselect_list.size()>0)
        {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("*/*");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++) {
                    File file = new File(multiselect_list.get(i).getFilePath());
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(sharingIntent);
            }
            else
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < multiselect_list.size(); i++)
                {
                    File file = new File(multiselect_list.get(i).getFilePath());
                    Uri uri = FileProvider.getUriForFile(mContext, getResources().getString(R.string.file_provider_authority), file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(sharingIntent);

            }

        }
        else
        {
            Toast.makeText(mContext, R.string.no_file_to_share, Toast.LENGTH_SHORT).show();
        }

    }


    public void refreshAdapter()
    {
        multiSelectAdapter.selected_recentList=multiselect_list;
        multiSelectAdapter.recentList=recent_ImgList;
        multiSelectAdapter.notifyDataSetChanged();
        selectMenuChnage();
        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        finishActionMode();
        deleteJunkFiles();
        new AsynctaskUtility<fileModel>(mContext,this,Constants.REQUST_RECENT_FILE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

           private void deleteJunkFiles()
        {
            try {
                File directory = new File(Constants.pdfFolderName);
                // Get all files from a directory.
                File[] fList = directory.listFiles();
                System.out.print("" + fList);

                for (File file : fList) {
                    if (file.isFile()) {
                        if (file.getAbsolutePath().contains("cropped") && file.exists()) {
                            Log.d("fileName-->>", "" + file.getAbsolutePath());
                            file.delete();
                        }
                    }
                }
            }catch (Exception e)
            {

            }


    }


    private void finishActionMode()
    {
        if(mActionMode!=null)
            mActionMode.finish();
    }


    @Override
    public void onrecentSelected(fileModel recentFile) {

        // Utility.dispToast(mContext, recentFile.getFileName());
        if(recentFile.getIsImgs())
        {

            Utility.OpenFileWithNoughtAndAll(recentFile.getFilePath(),mContext,getResources().getString(R.string.file_provider_authority));
        }
        else {

            String fpath=new File(recentFile.getFilePath().toString()).getAbsolutePath();
           // Utility.dispToast(mContext, fpath);
            Intent intent=new Intent(mContext,PDFViewActivity.class);
            intent.putExtra(Constants.IntentfilePath, fpath);
            startActivity(intent);
        }
    }

    @Override
    public void processFinish(ArrayList output) {

        recent_ImgList=output;

        if(recent_ImgList.size()>0) {
            multiSelectAdapter = new MultiSelectAdapter_Recent(mContext, recent_ImgList, multiselect_list, this);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.setAdapter(multiSelectAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            nodataFound.setVisibility(View.GONE);
        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            nodataFound.setVisibility(View.VISIBLE);
        }




    }

    @Override
    public void onPositiveClick(int from) {


        if(from==1)
        {
            if(multiselect_list.size()>0)
            {

                ArrayList<File> filesTobeDleted=new ArrayList<>();
                for(int i=0;i<multiselect_list.size();i++)
                {
                    filesTobeDleted.add(new File(multiselect_list.get(i).getFilePath()));
                }
                new deleteFileAsyncTask(mContext,this,filesTobeDleted).execute();

                if(mActionMode!=null)
                {
                    mActionMode.finish();
                }

            }

        }



    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void ondeleteSucceed(ArrayList<String> deletedFiles) {
        //relaod the data if flie is  deleted
        // for the time beo=ing the varibale deletedFiles is not being used but it contains the path of the files deleted
        new AsynctaskUtility<fileModel>(mContext,this,Constants.REQUST_RECENT_FILE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRenameSuccess() {

        Utility.dispToast(mContext, R.string.rename_success);
        if(mActionMode!=null)
        {
            mActionMode.finish();
        }
        new AsynctaskUtility<fileModel>(mContext,this,Constants.REQUST_RECENT_FILE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onRenameFailure() {
        if(mActionMode!=null)
        {
            mActionMode.finish();
        }

        Utility.dispToast(mContext, R.string.rename_error);

    }

    @Override
    public void onEncryptSuccessful() {
        // remove  the  file from the lsit  and refresh the adapte and finish  action mode;
        if(multiselect_list.size()>0)
        {
            for(int i=0;i<multiselect_list.size();i++)
            {
                recent_ImgList.remove(multiselect_list.get(i));
            }
        }
        multiselect_list.clear();
        multiSelectAdapter.notifyDataSetChanged();
        if(mActionMode !=null)
            mActionMode.finish();
    }
}