package com.example.weijunn.project01.Project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weijunn.project01.R;
import com.example.weijunn.project01.RecyclerView.RecyclerTouchListener;
import com.example.weijunn.project01.RecyclerView.projectAddOnAdapter;
import com.example.weijunn.project01.model.ProjectAddOnProvider;
import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;

import java.io.ByteArrayOutputStream;
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

        Intent intent = getIntent();
        selectedID = intent.getIntExtra("id", -1);
        Log.d(TAG, "Selected ID is: " + selectedID);

        initObject();

        if (listNewProjectProviders.isEmpty()) {
            projectRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            projectRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.projectList_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(projectList.this, projectAddOn.class);
                intent.putExtra("projectId", selectedID);
                startActivity(intent);
            }
        });

        projectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(projectList.this, projectRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                int status = listNewProjectProviders.get(position).getStatus();
                String date = listNewProjectProviders.get(position).getDate();
                String notes = listNewProjectProviders.get(position).getNotes();

                ImageView projImage = (ImageView) view.findViewById(R.id.projectList_Image);
                projImage.setDrawingCacheEnabled(true);
                projImage.buildDrawingCache();
                final Bitmap bitmap = projImage.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();


                int HideMenu = 1;

                Long rowid = listNewProjectProviders.get(position).getId();
                Cursor data = projectDbHelper.getProjectAddOnItemID(rowid);
                Log.d(TAG, "The row id is: " + rowid);

                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                    status = data.getInt(1);
                    date = data.getString(2);
                    notes = data.getString(3);
                }
                if (itemID > -1) {
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent intent = new Intent(projectList.this, projectAddOn.class);
                    intent.putExtra("id", itemID);
                    intent.putExtra("projectId", selectedID);
                    intent.putExtra("projImage", bytes);
                    intent.putExtra("status", status);
                    intent.putExtra("date", date);
                    intent.putExtra("notes", notes);
                    intent.putExtra("HideMenu", HideMenu);
                    Log.d(TAG, "The row id is: " + rowid);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongClick(View view, int position) {
                //
            }
        }));

    }

    public void initObject() {

        projectDbHelper = new ProjectDbHelper(this);
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.viewProjectList(selectedID);

        if (cursor.moveToFirst()) {
            do {
                ProjectAddOnProvider projectAddOnProvider = new ProjectAddOnProvider(cursor.getLong(0), cursor.getInt(1),
                        cursor.getString(2), cursor.getString(3), cursor.getBlob(4), cursor.getInt(5));
                listNewProjectProviders.add(projectAddOnProvider);

            } while (cursor.moveToNext());
            projectDbHelper.close();

        }

        projectAddOnAdapter = new projectAddOnAdapter(listNewProjectProviders);
        projectRecyclerView.setAdapter(projectAddOnAdapter);

    }

}
