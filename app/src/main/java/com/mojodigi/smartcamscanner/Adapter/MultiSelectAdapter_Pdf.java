package com.mojodigi.smartcamscanner.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import com.mojodigi.smartcamscanner.Model.pdfModel;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.Utility;


import java.util.ArrayList;

public class MultiSelectAdapter_Pdf extends RecyclerView.Adapter<MultiSelectAdapter_Pdf.MyViewHolder>  implements Filterable {

    public ArrayList<pdfModel> pdfList=new ArrayList<>();
    public ArrayList<pdfModel> pdfListfiltered=new ArrayList<>();
    public ArrayList<pdfModel> selected_pdfList=new ArrayList<>();
    private pdfListener listener;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView FileIcon;
        public CheckBox chbx;

        public TextView fileName,fileSize,fileMdate,fileDuration;
        RelativeLayout rellayout;
        public MyViewHolder(View view) {
            super(view);

            fileName=(TextView) view.findViewById(R.id.AudioFileName);
            fileSize=(TextView)view.findViewById(R.id.FileSize);
            fileMdate=(TextView)view.findViewById(R.id.FileMdate);
            fileDuration=(TextView)view.findViewById(R.id.FileDuration);
            chbx=(CheckBox) view.findViewById(R.id.chbx);
            rellayout=(RelativeLayout)view.findViewById(R.id.rellayout);
            FileIcon=(ImageView)view.findViewById(R.id.FileIcon);

            fileName.setTypeface(Utility.typeFace_calibri(mContext));
            fileSize.setTypeface(Utility.typeFace_calibri(mContext));
            fileMdate.setTypeface(Utility.typeFace_calibri(mContext));
            fileDuration.setTypeface(Utility.typeFace_calibri(mContext));




            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected pdf in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                        listener.onpdfSelected(pdfListfiltered.get(getAdapterPosition()));
                }
            });



        }
    }

    public MultiSelectAdapter_Pdf(Context context, ArrayList<pdfModel> pdfList, ArrayList<pdfModel> selectedpdfList , pdfListener listener) {
        this.mContext=context;
        this.pdfList = pdfList;
        this.pdfListfiltered=pdfList;
        this.selected_pdfList = selectedpdfList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_file, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        pdfModel model = pdfListfiltered.get(position);

        holder.fileName.setText(model.getFileName());
        holder.fileMdate.setText(model.getFileModifiedDate());
        holder.fileSize.setText(model.getFileSize());
          holder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pdf));

        if(selected_pdfList.contains(pdfList.get(position))) {
            holder.chbx.setVisibility(View.VISIBLE);  // for time being checkbox not shown   layout backgroud being changed

        }
        else {
            holder.chbx.setVisibility(View.INVISIBLE); // for time being checkbox not shown   layout backgroud being changed
        }


    }

    @Override
    public int getItemCount() {
        return pdfListfiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    pdfListfiltered = pdfList;
                } else {
                    ArrayList<pdfModel> filteredList = new ArrayList<>();
                    for (pdfModel row : pdfList) {
                        // search condition here
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    pdfListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = pdfListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                pdfListfiltered = (ArrayList<pdfModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface pdfListener {
        void onpdfSelected(pdfModel contact);
    }



}

