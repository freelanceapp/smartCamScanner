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
import com.mojodigi.smartcamscanner.Model.fileModel;
import com.mojodigi.smartcamscanner.R;
import com.mojodigi.smartcamscanner.Util.Utility;

import java.util.ArrayList;

public class MultiSelectAdapter_Recent extends RecyclerView.Adapter<MultiSelectAdapter_Recent.MyViewHolder>  implements Filterable {

    public ArrayList<fileModel> recentList=new ArrayList<>();
    public ArrayList<fileModel> recentListfiltered=new ArrayList<>();
    public ArrayList<fileModel> selected_recentList=new ArrayList<>();
    private recentListener listener;
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
                    // send selected recent in callback
                    int pos=getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION)
                        listener.onrecentSelected(recentListfiltered.get(getAdapterPosition()));
                }
            });



        }
    }

    public MultiSelectAdapter_Recent(Context context, ArrayList<fileModel> recentList, ArrayList<fileModel> selectedrecentList , recentListener listener) {

        this.mContext=context;
        this.recentList = recentList;
        this.recentListfiltered=recentList;
        this.selected_recentList = selectedrecentList;
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
        fileModel model = recentList.get(position);

        if(model.getIsImgs())
            Glide.with(mContext).load(model.getFilePath()).into(holder.imageView);
            else
                holder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pdf));



        holder.fNameView.setText(model.getFileName());

        if(selected_recentList.contains(recentList.get(position)))
            holder.ticksymbol.setVisibility(View.VISIBLE);
        else
            holder.ticksymbol.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return recentList.size();
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
                    recentListfiltered = recentList;
                } else {
                    ArrayList<fileModel> filteredList = new ArrayList<>();
                    for (fileModel row : recentList) {
                        // search condition here
                        if (row.getFileName().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    recentListfiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = recentListfiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                recentListfiltered = (ArrayList<fileModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface recentListener {
        void onrecentSelected(fileModel contact);
    }





}

