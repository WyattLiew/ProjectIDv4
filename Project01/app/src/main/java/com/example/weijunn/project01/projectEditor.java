package com.example.weijunn.project01;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;

import java.util.Calendar;

public class projectEditor extends AppCompatActivity {

    private EditText mProjectLocation, mContactName, mContactNumber, mProjectTitle, mProjectDescription, mProjectNotes;

    ProjectDbHelper mDbHelper;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);

        initId();
        initDate();

    }

    private void initDate(){
        mDisplayDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(projectEditor.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year ;
                mDisplayDate.setText(date);
            }
        };
    }

    public void initId(){
        mDbHelper = new ProjectDbHelper(this);

        mDisplayDate = (TextView) findViewById(R.id.projDate);
        mProjectTitle = (EditText) findViewById(R.id.projTitle);
        mProjectDescription = (EditText) findViewById(R.id.projDescription);
        mContactName = (EditText) findViewById(R.id.conName);
        mContactNumber = (EditText) findViewById(R.id.conNum);
        mProjectLocation = (EditText) findViewById(R.id.projLocation);
        mProjectNotes = (EditText) findViewById(R.id.projNotes);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.project_add:
                final CharSequence[] items_update = {"Save", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(projectEditor.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Save")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            int conNumInt = Integer.parseInt(conNumString);
                            String descriptionString = mProjectDescription.getText().toString().trim();
                            String titleString = mProjectTitle.getText().toString().trim();
                            String noteString = mProjectNotes.getText().toString().trim();
                            String projectDate = mDisplayDate.getText().toString().trim();
                            mDbHelper.insert_project(locationString, conNameString, conNumInt, projectDate, descriptionString, titleString, noteString);
                            Intent intent = new Intent(projectEditor.this,MainActivity.class);
                            startActivity(intent);
                        } else if (items_update[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_update.show();
                return true;

            case android.R.id.home:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
