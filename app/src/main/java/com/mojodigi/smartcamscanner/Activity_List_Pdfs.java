package com.mojodigi.smartcamscanner;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_Pdf;
import com.mojodigi.smartcamscanner.AsyncTasks.AsynctaskUtility;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.pdfModel;
import com.mojodigi.smartcamscanner.Util.AlertDialogHelper;
import com.mojodigi.smartcamscanner.Util.RecyclerItemClickListener;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.io.File;
import java.util.ArrayList;

public class Activity_List_Pdfs extends AppCompatActivity implements AsynctaskUtility.AsyncResponse,MultiSelectAdapter_Pdf.pdfListener

{

    ActionMode mActionMode;
    Menu context_menu;

    RecyclerView recyclerView;
    Context mContext;
    static  Activity_List_Pdfs instance;
    boolean isMultiSelect = false;
    private SearchView searchView;
    ArrayList<pdfModel> pdfList = new ArrayList<>();
    ArrayList<pdfModel> selected_pdfList = new ArrayList<>();
    MenuItem sortView;
    private pdfModel fileTorename;
    private boolean isSearchModeActive;
    MultiSelectAdapter_Pdf multiSelectAdapter;
    private  int renamePosition;
    AlertDialogHelper alertDialogHelper;
    private boolean isUnseleAllEnabled=false;
    TextView empty_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allpdfs);

         initActivitycomponent();
         if(mContext!=null)
         new AsynctaskUtility<pdfModel>(mContext,this,Constants.REQUST_PDF_FILE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void initActivitycomponent() {

        instance=this;
        mContext=Activity_List_Pdfs.this;

        Utility.setActivityTitle(mContext,"Pdf files" );

        empty_view=findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);





        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect && position!= RecyclerView.NO_POSITION )
                    multi_select(position);

                else {

                    // openDocument(pdfList.get(position).getFilePath());
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selected_pdfList = new ArrayList<pdfModel>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);


                        Utility.hideKeyboard(Activity_List_Pdfs.this);
                        isSearchModeActive = false;
                        searchView.onActionViewCollapsed();

                        sortView.setVisible(true);
                    }
                }

                multi_select(position);

            }
        }));



    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_activity, menu);

        sortView = menu.findItem(R.id.action_sort);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        Utility.setCustomizeSeachBar(mContext,searchView);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if(multiSelectAdapter!=null)
                    multiSelectAdapter.getFilter().filter(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if(multiSelectAdapter!=null)
                    multiSelectAdapter.getFilter().filter(query.trim());
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                MenuItem item= menu.findItem(R.id.action_sort);
                item.setVisible(true);
                //invalidateOptionsMenu();
                searchView.requestFocus(0);
                //searchView.setFocusable(false);
                isSearchModeActive=false;
                Utility.hideKeyboard(Activity_List_Pdfs.this);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem item= menu.findItem(R.id.action_sort);
                item.setVisible(false);
                //invalidateOptionsMenu();
                searchView.requestFocus(1);
                searchView.setFocusable(true);
                Utility.showKeyboard(Activity_List_Pdfs.this);
                isSearchModeActive=true;


            }
        });




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }
        if(id== R.id.action_sort)
        {
            //sortDialog(mcontext);
        }
        return super.onOptionsItemSelected(item);
    }
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (selected_pdfList.contains(pdfList.get(position)))
                selected_pdfList.remove(pdfList.get(position));
            else {
                selected_pdfList.add(pdfList.get(position));
                // to  rename file contain old file;

                if(selected_pdfList.size()==1) {
                    fileTorename = pdfList.get(position);
                    renamePosition=position;
                }
                // to  rename file contain old file;
            }

            if (selected_pdfList.size() > 0) {
                mActionMode.setTitle("" + selected_pdfList.size());
                //keep  the reference of file to  be renamed
                if (pdfList.contains(selected_pdfList.get(0))) {
                    renamePosition = pdfList.indexOf(selected_pdfList.get(0));
                    fileTorename = selected_pdfList.get(0);
                }
                //keep  the reference of file to  be renamed
            }
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }

    public static Activity_List_Pdfs getInstance() {
        return instance;
    }
    public void refreshAdapterAfterRename(String newPath, String newName)
    {

        // finish  acrtion mode aftr  rename  file is  done
        if(mActionMode!=null) {
            mActionMode.finish();
        }
        fileTorename.setFilePath(newPath);
        fileTorename.setFileName(newName);
        pdfList.set(renamePosition,fileTorename);
        refreshAdapter();

    }

    private int statusBarColor;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getWindow().getStatusBarColor();
                //set your gray color
                getWindow().setStatusBarColor(getResources().getColor(R.color.onePlusAccentColor_device_default_dark));
            }

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
                    if (selected_pdfList.size() == 1)
                        //Utility.fileRenameDialog(mContext,selected_pdfList.get(0).getFilePath(), Constants.DOCUMENT,false);
                        return true;
                case R.id.action_delete:
                    if (selected_pdfList.size() >= 1) {
                        int mFileCount = selected_pdfList.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfiles);
                        alertDialogHelper.showAlertDialog("", "Delete file" + " (" + msgDeleteFile + ")", "DELETE", "CANCEL", 1, true);
                    }

                    return true;
                case R.id.action_select:
                    if (pdfList.size() == selected_pdfList.size() || isUnseleAllEnabled == true)
                        unSelectAll();
                    else
                        selectAll();
                    return true;
                case R.id.action_Share:
                    shareMultipleDocsWithNoughatAndAll();
                    return true;
                case R.id.action_details:
                  /*  if (selected_pdfList.size() == 1)
                    {
                    //DispDetailsDialog(selected_pdfList.get(0));
            }
                    else {
                        String size =calcSelectFileSize(selected_pdfList);
                        System.out.println("" + size);
                        if(size!=null)
                            Utility.multiFileDetailsDlg(mcontext,size,selected_pdfList.size());
                    }*/
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            selected_pdfList = new ArrayList<pdfModel>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getWindow().setStatusBarColor(statusBarColor);
            }
            refreshAdapter();
        }
    };



    public void refreshAdapter()
    {
        if(multiSelectAdapter!=null) {

            multiSelectAdapter.selected_pdfList = selected_pdfList;
            multiSelectAdapter.pdfList = pdfList;
            multiSelectAdapter.notifyDataSetChanged();
            selectMenuChnage();
            //finish action mode when user deselect files one by one ;
            if (selected_pdfList.size() == 0) {
                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        }
    }

    private void selectAll()
    {
        if (mActionMode != null)
        {
            selected_pdfList.clear();

            for(int i=0;i<pdfList.size();i++)
            {
                if(!selected_pdfList.contains(selected_pdfList.contains(pdfList.get(i))))
                {
                    selected_pdfList.add(pdfList.get(i));
                }
            }
            if (selected_pdfList.size() > 0)
                mActionMode.setTitle("" + selected_pdfList.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

            //to change  the unselectAll  menu  to  selectAll
            selectMenuChnage();
            //to change  the unselectAll  menu  to  selectAll

        }
    }
    private void unSelectAll()
    {
        if (mActionMode != null)
        {
            selected_pdfList.clear();

            if (selected_pdfList.size() >= 0)
                mActionMode.setTitle("" + selected_pdfList.size());
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

    private void selectMenuChnage()
    {
        if(context_menu!=null)
        {
            if(pdfList.size()==selected_pdfList.size()) {
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

            // rename  options will be visible if only i file is selected

            MenuItem item= context_menu.findItem(R.id.action_rename);
            if (selected_pdfList.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only i file is selected
        }
        invalidateOptionsMenu();
    }

    private void shareMultipleDocsWithNoughatAndAll() {

        if(selected_pdfList.size()>0)
        {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.setType("*/*");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < selected_pdfList.size(); i++) {
                    File file = new File(selected_pdfList.get(i).getFilePath());
                    Uri uri = Uri.fromFile(file);
                    files.add(uri);
                }
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(sharingIntent);
            }
            else
            {
                ArrayList<Uri> files = new ArrayList<Uri>();

                for (int i = 0; i < selected_pdfList.size(); i++)
                {
                    File file = new File(selected_pdfList.get(i).getFilePath());
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
            Toast.makeText(mContext, "No files to share", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void processFinish(ArrayList output) {

        pdfList=output;
        if(pdfList !=null && pdfList.size()>0) {

            empty_view.setVisibility(View.GONE);
            multiSelectAdapter = new MultiSelectAdapter_Pdf(mContext, pdfList, selected_pdfList, this);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(multiSelectAdapter);

        }
        else
        {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }

        }

    @Override
    public void onpdfSelected(pdfModel pdfFile) {

        String fpath=new File(pdfFile.getFilePath().toString()).getAbsolutePath();
        Utility.dispToast(mContext, fpath);
        Intent intent=new Intent(mContext,PDFViewActivity.class);
        intent.putExtra(Constants.IntentfilePath, fpath);
        startActivity(intent);

    }
}
