package com.example.weijunn.project01.RecyclerView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weijunn.project01.R;
import com.example.weijunn.project01.imageEfficiently;
import com.example.weijunn.project01.model.ProjectAddOnProvider;

import java.util.ArrayList;

public class projectAddOnAdapter extends RecyclerView.Adapter<projectAddOnAdapter.projectViewHolder> {

    ArrayList<ProjectAddOnProvider> mData = new ArrayList<>();

    public projectAddOnAdapter(ArrayList<ProjectAddOnProvider> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public projectAddOnAdapter.projectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_projectaddon,parent,false);
        projectAddOnAdapter.projectViewHolder ViewHolder = new projectAddOnAdapter.projectViewHolder(v);


        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull projectAddOnAdapter.projectViewHolder holder, int position) {

        int Status = mData.get(position).getStatus();
        if(Status == 0){
            holder.tv_status.setText("Completed");
        }else if(Status == 1){
            holder.tv_status.setText("In Progress");
        }else{
            holder.tv_status.setText("Deferred");
        }

       // holder.tv_status.setText(mData.get(position).getStatus());
        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_notes.setText(mData.get(position).getNotes());
        holder.tv_image.setImageBitmap(imageEfficiently.decodeSampledBitmapFromResource(mData.get(position).getImg(),100,100));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static class projectViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_date,tv_notes,tv_status;
        private ImageView tv_image;

        public projectViewHolder(View itemView){
            super(itemView);

            tv_status = (TextView) itemView.findViewById(R.id.projectList_Status);
            tv_date = (TextView) itemView.findViewById(R.id.projectList_Date);
            tv_notes = (TextView) itemView.findViewById(R.id.projectList_Notes);
            tv_image = (ImageView)itemView.findViewById(R.id.projectList_Image);


        }
    }
}
