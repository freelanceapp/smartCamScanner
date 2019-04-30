package com.mojodigi.smartcamscanner.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mojodigi.smartcamscanner.Model.folder_Model;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Folder_dialog extends RecyclerView.Adapter<MultiSelectAdapter_Folder_dialog.MyViewHolder>  implements Filterable {


    Context mContext;
    public ArrayList<folder_Model> folfderList=new ArrayList<>();
    private folderListener listener;

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_folde_dialog, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        folder_Model model = folfderList.get(position);

        holder.FolderName.setText(model.getFolderName());

        holder.FolderIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_folder_24));

       /* if(selected_pdfList.contains(pdfList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed

        }
        else {
            holder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
        }*/

    }

    @Override
    public int getItemCount() {
        return folfderList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView FolderIcon;
        public TextView FolderName;

        public MyViewHolder(View view) {
            super(view);
            FolderName=(TextView) view.findViewById(R.id.fNameView);
            FolderIcon=(ImageView)view.findViewById(R.id.fImageView);
            FolderName.setTypeface(Utility.typeFace_calibri(mContext));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected pdf in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                        listener.onFolderSelected(folfderList.get(getAdapterPosition()));
                }
            });



        }
    }


    public MultiSelectAdapter_Folder_dialog(Context mContext, ArrayList<folder_Model> folderList, folderListener listener )
   {

       this.listener=listener;
       this.mContext=mContext;
       this.folfderList=folderList;


   }
   public interface folderListener {
        void onFolderSelected(folder_Model contact);
    }
}
