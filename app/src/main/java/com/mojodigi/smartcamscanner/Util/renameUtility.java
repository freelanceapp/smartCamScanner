package com.mojodigi.smartcamscanner.Util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mojodigi.smartcamscanner.R;

import java.io.File;

public class renameUtility {

    reNameListener listener;
    private static boolean fileStatus;

    public interface reNameListener
    {
      public  void onRenameSuccess();
      public  void onRenameFailure();
    }

    public  boolean fileRenameDialog(final Context mContext, final String fPath,reNameListener listener) {

        this.listener=listener;
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
           if(listener!=null)
           {
                 listener.onRenameSuccess();
           }
        }
        else
        {
            listener.onRenameFailure();
        }
        return fstatus;
    }
}
