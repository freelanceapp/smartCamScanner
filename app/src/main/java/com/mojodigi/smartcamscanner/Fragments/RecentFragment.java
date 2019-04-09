package com.mojodigi.smartcamscanner.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Recent;
import com.mojodigi.smartcamscanner.AsyncTasks.AsynctaskUtility;
import com.mojodigi.smartcamscanner.AsyncTasks.deleteFileAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.Model.pdfModel;
import com.mojodigi.smartcamscanner.PDFViewActivity;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.AlertDialogHelper;
import com.mojodigi.smartcamscanner.Util.RecyclerItemClickListener;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.io.InterruptedIOException;
import java.util.ArrayList;

public class RecentFragment extends Fragment  implements MultiSelectAdapter_Recent.recentListener ,AsynctaskUtility.AsyncResponse, AlertDialogHelper.AlertDialogListener ,deleteFileAsyncTask.deleteListener {



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
        }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getActivity();
        alertDialogHelper =new AlertDialogHelper(getActivity(),this);
        recyclerView =view.findViewById(R.id.recycler_view);




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

                case R.id.action_move:
                    Utility.dispToast(mContext,"Move");
                    return true;
                case R.id.action_encrypt:
                    Utility.dispToast(mContext,"encrypt");
                    return true;


                case R.id.action_rename:
                    if(multiselect_list.size()==1)
                      //  Utility.fileRenameDialog(mcontext,multiselect_list.get(0).getFilePath(),Constants.DOCUMENT,false);
                    return  true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                        alertDialogHelper.showAlertDialog("", "Delete Image"+" ("+msgDeleteFile+")", "DELETE", "CANCEL", 1, true);
                    }
                    return true;
                case R.id.action_select:
                   /* if(recent_ImgList.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();*/
                    return  true;
                case  R.id.action_Share:
                    //shareMultipleDocsWithNoughatAndAll();
                    return  true;
                case R.id.action_details:
                    if(multiselect_list.size()==1) {
                        //DispDetailsDialog(multiselect_list.get(0));
                    }
                    else {
                        /*String size =calcSelectFileSize(multiselect_list);
                        System.out.println("" + size);
                        if(size!=null)
                            Utility.multiFileDetailsDlg(mcontext,size,multiselect_list.size());*/
                    }
                    return true;
                default:
                    return false;
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
    public void refreshAdapter()
    {
        multiSelectAdapter.selected_recentList=multiselect_list;
        multiSelectAdapter.recentList=recent_ImgList;
        multiSelectAdapter.notifyDataSetChanged();
       // selectMenuChnage();
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
        new AsynctaskUtility<fileModel>(mContext,this,2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onrecentSelected(fileModel recentFile) {

        Utility.dispToast(mContext, recentFile.getFileName());
        if(recentFile.getIsImgs())
        {


        }
        else {

            String fpath=new File(recentFile.getFilePath().toString()).getAbsolutePath();
            Utility.dispToast(mContext, fpath);
            Intent intent=new Intent(mContext,PDFViewActivity.class);
            intent.putExtra(Constants.IntentfilePath, fpath);
            startActivity(intent);

        }


        }

    @Override
    public void processFinish(ArrayList output) {

        recent_ImgList=output;

        multiSelectAdapter=new MultiSelectAdapter_Recent(mContext,recent_ImgList,multiselect_list,this);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(multiSelectAdapter);



    }

    @Override
    public void onPositiveClick(int from) {


        if(from==1)
        {
            if(multiselect_list.size()>0)
            {


                Utility.dispToast(mContext, ""+multiselect_list.size());

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
        new AsynctaskUtility<fileModel>(mContext,this,2).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}