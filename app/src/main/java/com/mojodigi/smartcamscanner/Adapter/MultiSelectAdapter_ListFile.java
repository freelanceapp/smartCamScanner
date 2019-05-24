package com.mojodigi.smartcamscanner.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mojodigi.smartcamscanner.Constants.Constants;
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_ListFile extends RecyclerView.Adapter<MultiSelectAdapter_ListFile.MyViewHolder>  implements Filterable {

    public ArrayList<fileModel> fileList=new ArrayList<>();
    public ArrayList<fileModel> fileListfiltered=new ArrayList<>();
    public ArrayList<fileModel> selected_FileList=new ArrayList<>();
    private fileListener listener;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        RelativeLayout ticksymbol;
        TextView fNameView;


        public TextView fileName,fileSize,fileMdate,fileDuration;
        RelativeLayout rellayout;
        public MyViewHolder(View view) {
            super(view);

            imageView=(ImageView)view.findViewById(R.id.imageView);
            ticksymbol=(RelativeLayout) view.findViewById(R.id.ticksymbol);
            fNameView=view.findViewById(R.id.fNameView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected ListFile in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                        listener.onListFileSelected(fileListfiltered.get(getAdapterPosition()));
                }
            });



        }
    }

    public MultiSelectAdapter_ListFile(Context context, ArrayList<fileModel> fileList, ArrayList<fileModel> selectedfileList , fileListener listener) {

        this.mContext=context;
        this.fileList = fileList;
        this.fileListfiltered=fileList;
        this.selected_FileList = selectedfileList;
        this.listener = listener;




    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        fileModel model = fileList.get(position);

        String str=Utility.getFileExtensionfromPath(model.getFilePath());
        System.out.print(""+str);
        if(Utility.getFileExtensionfromPath(model.getFilePath()).equalsIgnoreCase(Constants.hiddenFileExtension))
        {
            holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hiddenfile_icon));// set icon here
        }
        else {
            if (model.getIsImgs())
                Glide.with(mContext).load(model.getFilePath()).into(holder.imageView);
            else
                holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pdf));
        }

        holder.fNameView.setText(model.getFileName());

        if(selected_FileList.contains(fileList.get(position)))
            holder.ticksymbol.setVisibility(View.VISIBLE);
        else
            holder.ticksymbol.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    fileListfiltered = fileList;
                } else {
                    ArrayList<fileModel> filteredList = new ArrayList<>();
                    for (fileModel row : fileList) {
                        // search condition here
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    fileListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = fileListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                fileListfiltered = (ArrayList<fileModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface fileListener {
        void onListFileSelected(fileModel contact);
    }





}

