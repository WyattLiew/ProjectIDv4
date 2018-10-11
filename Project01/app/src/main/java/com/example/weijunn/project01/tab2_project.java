package com.example.weijunn.project01;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.weijunn.project01.RecyclerView.RecyclerTouchListener;
import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;
import com.example.weijunn.project01.sqlitedata.newProjectProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class tab2_project extends Fragment {

    private SQLiteDatabase sqLiteDatabase;
    private ProjectDbHelper projectDbHelper;
    private projectRecyclerAdapter projectRecyclerAdapter;
    private Cursor cursor;
    private View view;
    private RecyclerView projectRecyclerView;
    private ArrayList<newProjectProvider> listNewProjectProviders = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_project, container, false);


        projectRecyclerView = (RecyclerView) rootView.findViewById(R.id.project_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        projectRecyclerView.setHasFixedSize(true);

        View emptyView = rootView.findViewById(R.id.project_empty_view);

        initObject();

        if (listNewProjectProviders.isEmpty()) {
            projectRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            projectRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        projectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), projectRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                newProjectProvider newProjectProvider = listNewProjectProviders.get(position);
                Toast.makeText(getActivity().getApplicationContext(), newProjectProvider.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    public void initObject(){

        projectDbHelper = new ProjectDbHelper(getActivity().getApplicationContext());
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.viewProjectData();

        if (cursor.moveToFirst()) {
            do {
                newProjectProvider newProjectProvider = new newProjectProvider(cursor.getString(0), cursor.getString(1)
                        , cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)
                        , cursor.getString(6), cursor.getString(7));
                listNewProjectProviders.add(newProjectProvider);

            } while (cursor.moveToNext());
            projectDbHelper.close();

        }

        projectRecyclerAdapter = new projectRecyclerAdapter(listNewProjectProviders);
        projectRecyclerView.setAdapter(projectRecyclerAdapter);

    }

}
