package com.mojodigi.smartcamscanner.Fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.mojodigi.smartcamscanner.Activity_File_List;
import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Folder;
import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Recent;
import com.mojodigi.smartcamscanner.AsyncTasks.deleteFileAsyncTask;
import com.mojodigi.smartcamscanner.AsyncTasks.getFolderAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.Model.folder_Model;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.AlertDialogHelper;
import com.mojodigi.smartcamscanner.Util.RecyclerItemClickListener;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.Util.renameUtility;

import java.io.File;
import java.util.ArrayList;

public class FolderFragment extends Fragment implements getFolderAsyncTask.foldetlistListener ,MultiSelectAdapter_Folder.folderListener,AlertDialogHelper.AlertDialogListener ,deleteFileAsyncTask.deleteListener {


    RecyclerView recyclerView;
    Context mContext;
    MultiSelectAdapter_Folder multiSelectAdapter;
    ArrayList<folder_Model> folder_List = new ArrayList<>();
    ArrayList<folder_Model> multiselect_list = new ArrayList<>();


    TextView nodataFound;

    ActionMode mActionMode;
    boolean isMultiSelect = false;
    Menu context_menu;
    AlertDialogHelper alertDialogHelper;
    boolean isUnseleAllEnabled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmnet_allfolderfile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getActivity();
        recyclerView =view.findViewById(R.id.recycler_view);
        nodataFound=view.findViewById(R.id.nodataFound);

        alertDialogHelper =new AlertDialogHelper(getActivity(),this);

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
                    multiselect_list = new ArrayList<folder_Model>();
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
            inflater.inflate(R.menu.menu_multi_select_folder, menu);
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
                   /* if(multiselect_list.size()==1)
                          renameUtility.fileRenameDialog(mContext,multiselect_list.get(0).getFolderPath());*/
                        return  true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfolders) : mFileCount + " " + getResources().getString(R.string.delfolder);
                        if(multiselect_list.get(0).getFolderPath().contains(Constants.allFilesFolder) || multiselect_list.get(0).getFolderPath().contains(Constants.pdfDirectory) ||  multiselect_list.get(0).getFolderPath().contains(Constants.imageFolderName))
                        {
                            Utility.dispToast(mContext, "App folder can't be deleted");
                            return true ;
                        }
                        alertDialogHelper.showAlertDialog("", "Delete folder"+" ("+msgDeleteFile+")", "DELETE", "CANCEL", 1, true);
                    }
                    return true;
                case R.id.action_select:
                    if(folder_List.size()==multiselect_list.size() || isUnseleAllEnabled==true)
                        unSelectAll();
                    else
                        selectAll();
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
            multiselect_list = new ArrayList<folder_Model>();
            refreshAdapter();
        }
    };

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

            for(int i=0;i<folder_List.size();i++)
            {
                if(!multiselect_list.contains(multiselect_list.contains(folder_List.get(i))))
                {
                    multiselect_list.add(folder_List.get(i));
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

    private void selectMenuChnage()
    {
        if(context_menu!=null)
        {
            if(folder_List.size()==multiselect_list.size()) {
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

          /*  MenuItem item= context_menu.findItem(R.id.action_rename);
            if (multiselect_list.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only 1 file is selected*/

        }
        getActivity().invalidateOptionsMenu();
    }




    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(folder_List.get(position)))
                multiselect_list.remove(folder_List.get(position));
            else {
                multiselect_list.add(folder_List.get(position));
                // to  rename file contain old file;

                if(multiselect_list.size()==1) {
                    /*fileTorename = recent_ImgList.get(position);
                    renamePosition=position;*/
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



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(!isVisibleToUser)
        {
            if(mActionMode!=null)
                mActionMode.finish();
        }
        if(isVisibleToUser)
        {
            if(Constants.callUpdateMethod) {
                Constants.callUpdateMethod=false;
                updateFolderList();
            }
        }

    }

    public void refreshAdapter()
    {
        multiSelectAdapter.selected_folfderList=multiselect_list;
        multiSelectAdapter.folfderList=folder_List;
        multiSelectAdapter.notifyDataSetChanged();
         selectMenuChnage();
        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
    }


   public void  updateFolderList()
   {
       new getFolderAsyncTask<folder_Model>(mContext,this).execute();
   }

    @Override
    public void onResume() {
        super.onResume();
        if(mActionMode!=null)
       {
             mActionMode.finish();
       }

        new getFolderAsyncTask<folder_Model>(mContext,this).execute();


    }

    @Override
    public void returnFolderList(ArrayList output) {


        folder_List=output;

        if(folder_List.size()>0) {
            multiSelectAdapter = new MultiSelectAdapter_Folder(mContext, folder_List, multiselect_list,this);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.setAdapter(multiSelectAdapter);
            nodataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {

            recyclerView.setVisibility(View.GONE);
            nodataFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFolderSelected(folder_Model contact) {


        //Utility.dispToast(mContext,contact.getFolderPath() );

        Intent intent=new Intent(getActivity(), Activity_File_List.class);
        intent.putExtra(Constants.folderPath, contact.getFolderPath());
        startActivity(intent);
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
                    filesTobeDleted.add(new File(multiselect_list.get(i).getFolderPath()));
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

        new getFolderAsyncTask<folder_Model>(mContext,this).execute();
    }
}