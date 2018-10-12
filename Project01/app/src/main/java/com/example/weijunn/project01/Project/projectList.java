package com.example.weijunn.project01.Project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.weijunn.project01.R;
import com.example.weijunn.project01.RecyclerView.projectAddOnAdapter;
import com.example.weijunn.project01.model.ProjectAddOnProvider;
import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;

import java.util.ArrayList;


public class projectList extends AppCompatActivity {
    private static final String TAG = "projectList";

    private int selectedID;

    private SQLiteDatabase sqLiteDatabase;
    private ProjectDbHelper projectDbHelper;
    private projectAddOnAdapter projectAddOnAdapter;
    private Cursor cursor;
    private View view;
    private RecyclerView projectRecyclerView;
    private ArrayList<ProjectAddOnProvider> listNewProjectProviders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        projectRecyclerView = (RecyclerView) findViewById(R.id.projectList_recyclerView);
        projectRecyclerView.setLayoutManager(new LinearLayoutManager(projectList.this));
        projectRecyclerView.setHasFixedSize(true);

        View emptyView = findViewById(R.id.project_empty_view);

        initObject();

        if (listNewProjectProviders.isEmpty()) {
            projectRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            projectRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        selectedID = intent.getIntExtra("id",-1);
        Log.d(TAG,"Selected ID is: "+selectedID);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.projectList_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(projectList.this,projectAddOn.class);
                intent.putExtra("id",selectedID);
                startActivity(intent);
            }
        });

    }

    public void initObject(){

        projectDbHelper = new ProjectDbHelper(this);
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.viewProjectList();

        if (cursor.moveToFirst()) {
            do {
                ProjectAddOnProvider projectAddOnProvider = new ProjectAddOnProvider(cursor.getLong(0),cursor.getInt(1),
                cursor.getString(2),cursor.getString(3),cursor.getBlob(4),cursor.getInt(5));
                listNewProjectProviders.add(projectAddOnProvider);

            } while (cursor.moveToNext());
            projectDbHelper.close();

        }

        projectAddOnAdapter = new projectAddOnAdapter(listNewProjectProviders);
        projectRecyclerView.setAdapter(projectAddOnAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
}
