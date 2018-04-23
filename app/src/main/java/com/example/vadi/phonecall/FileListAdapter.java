package com.example.vadi.phonecall;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;



class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileListViewHolder> {
    private ArrayList<ListItemRecords> listItemRecords;
    private Context context;

    FileListAdapter(ArrayList<ListItemRecords> listItemRecords, Context context) {
        this.listItemRecords = listItemRecords;
        this.context = context;
    }

    @Override
    public FileListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);
        return new FileListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileListViewHolder holder, int position) {
        holder.name.setText(listItemRecords.get(position).getName());
        holder.path.setText(listItemRecords.get(position).getPath());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return listItemRecords.size();
    }

    class FileListViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView path;

        FileListViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            path = itemView.findViewById(R.id.path);
        }
    }
}
