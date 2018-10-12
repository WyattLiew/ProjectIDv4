package com.example.weijunn.project01.Project;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weijunn.project01.R;
import com.example.weijunn.project01.sqlitedata.newProjectProvider;

import java.util.List;

public class SpinnerProjectAdapter extends BaseAdapter {

    public static final String TAG ="SpinnerProjectAdapter";

    private List<newProjectProvider> mItem;
    private LayoutInflater mInflater;

    public SpinnerProjectAdapter(Context context, List<newProjectProvider> listProjects){
        this.setItems(listProjects);
        this.mInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position) : null ;
    }

    @Override
    public long getItemId(int position) {
        return (getItems() != null && !getItems().isEmpty()) ? getItems().get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if(v == null){
            v = mInflater.inflate(R.layout.spinner_item_project_addon,parent,false);
            holder = new ViewHolder();
            holder.txtProjectName = (TextView) v.findViewById(R.id.txt_project_name);
            v.setTag(holder);
        }else{
            holder = (ViewHolder) v.getTag();
        }

        newProjectProvider currentItem = (newProjectProvider) getItem(position);
        if (currentItem != null){
            holder.txtProjectName.setText(currentItem.getTitle());
        }

        return v;
    }

    public List<newProjectProvider> getItems(){
        return mItem;
    }

    public void setItems(List<newProjectProvider>mItem){
        this.mItem = mItem;
    }

    class ViewHolder {
        TextView txtProjectName;

    }
}
