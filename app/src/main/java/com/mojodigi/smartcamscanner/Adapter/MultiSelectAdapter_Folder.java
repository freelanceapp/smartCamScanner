package com.mojodigi.smartcamscanner.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mojodigi.smartcamscanner.Model.folder_Model;

import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Folder extends RecyclerView.Adapter<MultiSelectAdapter_Folder.MyViewHolder>  implements Filterable {


    Context mContext;
    public ArrayList<folder_Model> folfderList=new ArrayList<>();
    public ArrayList<folder_Model> folfderListfiltered=new ArrayList<>();
    public ArrayList<folder_Model> selected_folfderList=new ArrayList<>();

    private folderListener listener;



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_folder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        folder_Model model = folfderList.get(position);

        holder.FolderName.setText(model.getFolderName());

        holder.FolderIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_folder_24));

        if(selected_folfderList.contains(folfderList.get(position))) {
            holder.ticksymbol.setVisibility(View.VISIBLE);
            }
        else {
            holder.ticksymbol.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return folfderList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    folfderListfiltered=folfderList;
                } else {
                    ArrayList<folder_Model> filteredList = new ArrayList<>();
                    for (folder_Model row : folfderList) {
                        // search condition here
                        if (row.getFolderName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    folfderListfiltered=folfderList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = folfderListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                folfderListfiltered = (ArrayList<folder_Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView FolderIcon;
        public TextView FolderName;
        RelativeLayout ticksymbol;

        public MyViewHolder(View view) {
            super(view);
            FolderName=(TextView) view.findViewById(R.id.fNameView);
            FolderIcon=(ImageView)view.findViewById(R.id.imageView);
            ticksymbol=(RelativeLayout) view.findViewById(R.id.ticksymbol);
            FolderName.setTypeface(Utility.typeFace_Gotham_Bold(mContext));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                        listener.onFolderSelected(folfderList.get(getAdapterPosition()));
                }
            });



        }
    }


    public MultiSelectAdapter_Folder(Context mContext,ArrayList<folder_Model> folderList,ArrayList<folder_Model> selected_folfderList,folderListener listener )
   {

       this.listener=listener;
       this.mContext=mContext;
       this.folfderList=folderList;
       this.selected_folfderList=selected_folfderList;


   }
   public interface folderListener {
        void onFolderSelected(folder_Model contact);
    }
}
