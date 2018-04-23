package com.example.vadi.phonecall;

/**
 * Created by Shree on 3/27/2017.
 */import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.myHolder>{

    private List<ListItem> list;
    private Context mContext;
    public ListAdapter(List<ListItem> list, Context mContext){
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_recview, null);
        myHolder holder = new myHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(myHolder holder, int position) {
        ListItem listItem = list.get(position);
        holder.ph_no.setText(listItem.getNumber());
        holder.msg.setText(listItem.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class myHolder extends RecyclerView.ViewHolder{

        TextView ph_no;
        TextView msg;

        public myHolder(View itemView) {
            super(itemView);

            ph_no = (TextView) itemView.findViewById(R.id.ph_no);
            msg = (TextView) itemView.findViewById(R.id.msg);
        }
    }
}
