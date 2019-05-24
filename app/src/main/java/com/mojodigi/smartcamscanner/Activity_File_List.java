package com.mojodigi.smartcamscanner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdView;
import com.mojodigi.smartcamscanner.Adapter.MultiSelectAdapter_ListFile;
import com.mojodigi.smartcamscanner.AddsUtility.AddConstants;
import com.mojodigi.smartcamscanner.AddsUtility.AddMobUtils;
import com.mojodigi.smartcamscanner.AddsUtility.SharedPreferenceUtil;
import com.mojodigi.smartcamscanner.AsyncTasks.decryptAsynscTask;
import com.mojodigi.smartcamscanner.AsyncTasks.deleteFileAsyncTask;
import com.mojodigi.smartcamscanner.AsyncTasks.getFolderFilesAsyncTask;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.Util.AlertDialogHelper;
import com.mojodigi.smartcamscanner.Util.RecyclerItemClickListener;
import com.mojodigi.smartcamscanner.Util.Utility;
import com.mojodigi.smartcamscanner.Util.renameUtility;

import java.io.File;
import java.util.ArrayList;

public class Activity_File_List  extends AppCompatActivity implements getFolderFilesAsyncTask.fileListListener , AlertDialogHelper.AlertDialogListener ,MultiSelectAdapter_ListFile.fileListener,deleteFileAsyncTask.deleteListener,renameUtility.reNameListener, decryptAsynscTask.decryptListener {
    private static boolean fileStatus;
    Context mContext;
    RecyclerView recyclerView;
    MultiSelectAdapter_ListFile multiSelectAdapter;

    ArrayList<fileModel> file_List = new ArrayList<>();
    ArrayList<fileModel> multiselect_list = new ArrayList<>();

    ActionMode mActionMode;
    boolean isMultiSelect = false;
    Menu context_menu;
    boolean isUnseleAllEnabled = false;
    private fileModel fileTorename;
    private int renamePosition;
    AlertDialogHelper alertDialogHelper;
    static  Activity_File_List instance;

    TextView nodataFound;
    String folderPath;
    File fileToDelete;

    SharedPreferenceUtil addprefs;
    View adContainer;
    RelativeLayout smaaToAddContainer;
    private AdView mAdView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listfile);

        mContext = Activity_File_List.this;
         instance=this;
        Utility.setActivityTitle(mContext, "Files");

        alertDialogHelper = new AlertDialogHelper(mContext, this);
        recyclerView = findViewById(R.id.recycler_view);
        nodataFound=findViewById(R.id.nodataFound);


        mAdView = (AdView) findViewById(R.id.adView);
        adContainer = findViewById(R.id.adMobView);


         folderPath = getIntent().getExtras().getString(Constants.folderPath);
       //Utility.dispToast(mContext, folderPath);
        if (folderPath != null && folderPath.contains(Constants.allFilesFolder))
            new getFolderFilesAsyncTask<fileModel>(mContext, "", true,this).execute();
        else
            new getFolderFilesAsyncTask<fileModel>(mContext, folderPath, false,this).execute();



         recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick (View view,int position){
            if (isMultiSelect && position != RecyclerView.NO_POSITION)
                multi_select(position);

            else {


            }
        }
        @Override
        public void onItemLongClick (View view,int position){
            if (!isMultiSelect) {
                multiselect_list = new ArrayList<fileModel>();
                isMultiSelect = true;

                if (mActionMode == null) {
                    mActionMode = startActionMode(mActionModeCallback);

                }
            }

            multi_select(position);

        }
    }));
        dispAppd();
}

    public void dispAppd()
    {
        addprefs = new SharedPreferenceUtil(mContext);

        AddMobUtils adutil = new AddMobUtils();

        if(AddConstants.checkIsOnline(mContext) && adContainer !=null && addprefs !=null)
        {
            String AddPrioverId=addprefs.getStringValue(AddConstants.ADD_PROVIDER_ID, AddConstants.NOT_FOUND);
            if(AddPrioverId.equalsIgnoreCase(AddConstants.Adsense_Admob_GooglePrivideId))
                adutil.displayServerBannerAdd(addprefs,adContainer , mContext);

           /* else if(AddPrioverId.equalsIgnoreCase(AddConstants.SmaatoProvideId))
            {
                try {
                    int publisherId = Integer.parseInt(addprefs.getStringValue(AddConstants.APP_ID, AddConstants.NOT_FOUND));
                    int addSpaceId = Integer.parseInt(addprefs.getStringValue(AddConstants.BANNER_ADD_ID, AddConstants.NOT_FOUND));
                    adutil.displaySmaatoBannerAdd(smaaTobannerView, smaaToAddContainer, publisherId, addSpaceId);
                }catch (Exception e)
                {
                    String string = e.getMessage();
                    System.out.print(""+string);
                }
            }*/

            else if(AddPrioverId.equalsIgnoreCase(AddConstants.FaceBookAddProividerId))
            {
                adutil.dispFacebookBannerAdd(mContext,addprefs , Activity_File_List.this);
                adutil.dispFacebookInterestialAdds(mContext,addprefs);
            }


        }
        else {
            adutil.displayLocalBannerAdd(mAdView);
        }


        //  banner add
    }

    @Override
    protected void onResume() {
        super.onResume();





        if(mActionMode!=null)
        {
            mActionMode.finish();
        }

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select_file_list, menu);
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
                    // fileRenameDialog(mContext,multiselect_list.get(0).getFilePath());
                    {
                        renameUtility obj = new renameUtility();
                        obj.fileRenameDialog(mContext, multiselect_list.get(0).getFilePath(),instance );
                    }

                        return  true;
                case R.id.action_delete:
                    if(multiselect_list.size()>=1) {
                        int mFileCount = multiselect_list.size();
                        String msgDeleteFile = mFileCount > 1 ? mFileCount + " " + getResources().getString(R.string.delfiles) : mFileCount + " " + getResources().getString(R.string.delfile);
                        alertDialogHelper.showAlertDialog("", "Delete Image"+" ("+msgDeleteFile+")", "DELETE", "CANCEL", 1, true);
                    }
                    return true;
                case R.id.action_select:
                    if(file_List.size()==multiselect_list.size() || isUnseleAllEnabled==true)
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
            if (multiselect_list.contains(file_List.get(position)))
                multiselect_list.remove(file_List.get(position));
            else {
                multiselect_list.add(file_List.get(position));
                // to  rename file contain old file;

                if(multiselect_list.size()==1) {
                    fileTorename = file_List.get(position);
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
        multiSelectAdapter.selected_FileList=multiselect_list;
        multiSelectAdapter.fileList=file_List;
        multiSelectAdapter.notifyDataSetChanged();
         selectMenuChnage();
        //finish action mode when user deselect files one by one ;
        if(multiselect_list.size()==0) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
        }
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

            for(int i=0;i<file_List.size();i++)
            {
                if(!multiselect_list.contains(multiselect_list.contains(file_List.get(i))))
                {
                    multiselect_list.add(file_List.get(i));
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
            if(file_List.size()==multiselect_list.size()) {
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
            if (multiselect_list.size()==1)
                item.setVisible(true);
            else
                item.setVisible(false);

            // rename  options will be visible if only i file is selected

        }
        invalidateOptionsMenu();
    }



    @Override
    public void returnFileList(ArrayList output) {

        System.out.print(""+output);

        file_List=output;

        if(file_List.size()>0) {
            multiSelectAdapter = new MultiSelectAdapter_ListFile(mContext, file_List, multiselect_list, this);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.setAdapter(multiSelectAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            nodataFound.setVisibility(View.GONE);
        }else
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
            Toast.makeText(mContext, "No files to share", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void onListFileSelected(fileModel filemodel) {

        if(!filemodel.getFilePath().endsWith(Constants.hiddenFileExtension)) {
            if (!filemodel.getIsImgs()) {
                Intent intent = new Intent(mContext, PDFViewActivity.class);
                intent.putExtra(Constants.IntentfilePath, filemodel.getFilePath());
                startActivity(intent);
            } else {
                //display  images
                Utility.OpenFileWithNoughtAndAll(filemodel.getFilePath(), mContext, getResources().getString(R.string.file_provider_authority));

            }
        }
        else {
            new decryptAsynscTask(mContext, new File[]{new File(filemodel.getFilePath().toString())}, this, Constants.encryptionPassword).execute();
        }





    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void ondeleteSucceed(ArrayList<String> deletedFiles) {

        if (folderPath != null && folderPath.contains(Constants.allFilesFolder))
            new getFolderFilesAsyncTask<fileModel>(mContext, "", true,this).execute();
        else
            new getFolderFilesAsyncTask<fileModel>(mContext, folderPath, false,this).execute();
    }


    public  boolean fileRenameDialog(final Context mContext, final String fPath) {

        File f = new File(fPath);
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_file_rename);
        // Set dialog title

        TextView headertxt = dialog.findViewById(R.id.headertxt);
        final EditText Edit_Rename = dialog.findViewById(R.id.Edit_Rename);


        TextView View_save = dialog.findViewById(R.id.View_save);
        TextView View_cancel = dialog.findViewById(R.id.View_cancel);

        headertxt.setTypeface(Utility.typeFace_calibri(mContext));
        Edit_Rename.setTypeface(Utility.typeFace_calibri(mContext));
        View_cancel.setTypeface(Utility.typeFace_calibri(mContext));
        View_save.setTypeface(Utility.typeFace_calibri(mContext));


        if (f != null && !f.isDirectory()) {    //!f.isDirectory() new lines
            String extension = Utility.getFileExtensionfromPath(fPath.toLowerCase());
            Edit_Rename.setText(f.getName());
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            if (extension != null)
                // set cusror position ahead of file  extension ;
                Edit_Rename.setSelection(f.getName().length() - (extension.length() + 1));
            else
                Edit_Rename.setSelection(f.getName().length());
        }



        View_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        View_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utility.isWhitespace(Edit_Rename.getText().toString()))
                {
                    Edit_Rename.setError(mContext.getResources().getString(R.string.namerequired));

                    return;
                }

                if (Utility.IsNotEmpty(Edit_Rename))
                {
                    fileStatus = renameFile(mContext, fPath, Edit_Rename.getText().toString());
                    dialog.dismiss();
                } else {
                    Edit_Rename.setError(mContext.getResources().getString(R.string.emty_error));
                }

            }
        });


        dialog.show();


        return fileStatus;
    }
    public  boolean renameFile(Context pctx, String oldfpath, String newName) {

        File oldFile = new File(oldfpath);
        int i = oldfpath.lastIndexOf(File.separator);
        String pathstr = (i > -1) ? oldfpath.substring(0, i) : oldfpath;

        String nPath = pathstr + "/" + newName;
        File latestname = new File(pathstr + "/" + newName);



        boolean fstatus = oldFile.renameTo(latestname);
         if(fstatus)
         {
             Utility.RunMediaScan(pctx, latestname);
             Utility.RunMediaScan(pctx, oldFile);

         }

      if(fstatus)
      {
          updateAfterRename();
      }
        return fstatus;
    }





    private  void updateAfterRename()
    {
        finsiActionMode();


        if (folderPath != null && folderPath.contains(Constants.allFilesFolder))
            new getFolderFilesAsyncTask<fileModel>(mContext, "", true,this).execute();
        else
            new getFolderFilesAsyncTask<fileModel>(mContext, folderPath, false,this).execute();

    }

    @Override
    public void onRenameSuccess() {
        updateAfterRename();
    }

    @Override
    public void onRenameFailure() {
        finsiActionMode();
        Utility.dispToast(mContext, "Error while renaming file ");
    }
    private void finsiActionMode()
    {
        if(mActionMode!=null)
            mActionMode.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(fileToDelete!=null) {
            fileToDelete.delete();  // delete  the sown file;
            fileToDelete = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(fileToDelete!=null)
            fileToDelete.delete();

    }

    @Override
    public void OnDeCryptFinish(File fileDecrypted) {

        if(fileDecrypted !=null)
        {
           fileToDelete=fileDecrypted;

            if (fileDecrypted.getAbsolutePath().endsWith("pdf")) {
                Intent intent = new Intent(mContext, PDFViewActivity.class);
                intent.putExtra(Constants.IntentfilePath, fileDecrypted.getAbsolutePath());
                startActivity(intent);
            } else {
                //display  images
                Utility.OpenFileWithNoughtAndAll(fileDecrypted.getAbsolutePath(), mContext, getResources().getString(R.string.file_provider_authority));

            }
        }
    }




   /* private void DispDetailsDialog( fileModel fileProperty )
    {

        if(fileProperty.getFilePath() !=null)
        {

            String[] splitPath = fileProperty.getFilePath().split("/");
            String fName = splitPath[splitPath.length - 1];

            Dialog dialog = new Dialog(Activity_File_List.this);
            dialog.setContentView(R.layout.dialog_file_property);
            // Set dialog title

            TextView FileName = dialog.findViewById(R.id.FileName);
            TextView FilePath = dialog.findViewById(R.id.FilePath);
            TextView FileSize = dialog.findViewById(R.id.FileSize);
            TextView FileDate = dialog.findViewById(R.id.FileDate);
            TextView Resolution = dialog.findViewById(R.id.Resolution);
            TextView resltxt=dialog.findViewById(R.id.resltxt);

            TextView Oreintation = dialog.findViewById(R.id.ort);
            TextView oreinttxt=dialog.findViewById(R.id.oreinttxt);

            Oreintation.setVisibility(View.GONE);
            oreinttxt.setVisibility(View.GONE);
            resltxt.setVisibility(View.GONE);
            Resolution.setVisibility(View.GONE);




            FileName.setText(fName);
            FilePath.setText(fileProperty.getFilePath());
            FileSize.setText(fileProperty.getFileSize());
            FileDate.setText(fileProperty.getFileModifiedDate());



            dialog.show();
        }
    }


    public static void multiFileDetailsDlg(Context ctx, String totalSize, int fileCount) {

        final Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.multifiledetais_dialog);
        // Set dialog title

        TextView FileNum = dialog.findViewById(R.id.FileNum);
        TextView FileSize = dialog.findViewById(R.id.FileSizem);
        TextView close = dialog.findViewById(R.id.close);
        FileNum.setText(String.valueOf(fileCount));
        FileSize.setText(totalSize);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public String calcSelectFileSize(ArrayList<fileModel> fileList)
    {
        long totalSize=0;

        for(int i=0;i<fileList.size();i++)
        {
            fileModel m =  fileList.get(i);
            File f= new File(m.getFilePath());
            totalSize+=f.length();
        }

        return  Utility.humanReadableByteCount(totalSize,true);
    }

*/
}
