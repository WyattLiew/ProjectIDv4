package com.example.weijunn.project01;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;

import java.util.Calendar;

public class projectEditor extends AppCompatActivity {

    private EditText mProjectLocation, mContactName, mContactNumber, mProjectTitle, mProjectDescription, mProjectNotes;

    // update data
    private String selectedLocation, selectednotes, selectedName, selectedNum, selectedprojDate, selectedTitle, selectedDescription;
    private int HideMenu, selectedID;
    private boolean HIDE_MENU = false;

    ProjectDbHelper mDbHelper;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String date = "Select a date";

    //Warn the user about unsaved changes
    private boolean mPendingHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPendingHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_editor);

        initId();
        initDate();

        // Update data
        initUpdate();

        //Check unsaved changes
        initCheckUnsavedChanges();


    }

    private void initDate() {
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(projectEditor.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }

    public void initId() {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.project_update).setVisible(true);
                menu.findItem(R.id.project_add).setVisible(false);
                menu.findItem(R.id.project_delete).setVisible(true);
            } else {
                menu.findItem(R.id.project_update).setVisible(false);
                menu.findItem(R.id.project_add).setVisible(true);
                menu.findItem(R.id.project_delete).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.project_add:
                final CharSequence[] items_add = {"Save", "Cancel"};
                AlertDialog.Builder builder_add = new AlertDialog.Builder(projectEditor.this);
                builder_add.setTitle("Select options");
                builder_add.setItems(items_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_add[which].equals("Save")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            // int conNumInt = Integer.parseInt(conNumString);
                            String descriptionString = mProjectDescription.getText().toString().trim();
                            String titleString = mProjectTitle.getText().toString().trim();
                            String noteString = mProjectNotes.getText().toString().trim();
                            String projectDate = mDisplayDate.getText().toString().trim();
                            if (locationString.length() == 0 || conNameString.length() == 0 || conNumString.length() == 0 || titleString.length() == 0 || projectDate.length() == 0) {
                                checkEmptyEditText(locationString, conNameString, conNumString, titleString);
                            } else if (projectDate.matches(date)) {
                                Toast.makeText(projectEditor.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                            } else {
                                mDbHelper.insert_project(locationString, conNameString, conNumString, projectDate, descriptionString, titleString, noteString);
                                Intent intent = new Intent(projectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else if (items_add[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_add.show();
                return true;
            case R.id.project_update:
                final CharSequence[] items_update = {"Update", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(projectEditor.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Update")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            //int conNumInt = Integer.parseInt(conNumString);
                            String descriptionString = mProjectDescription.getText().toString().trim();
                            String titleString = mProjectTitle.getText().toString().trim();
                            String noteString = mProjectNotes.getText().toString().trim();
                            String projectDate = mDisplayDate.getText().toString().trim();
                            if (locationString.length() == 0 || conNameString.length() == 0 || conNumString.length() == 0 || titleString.length() == 0 || projectDate.length() == 0) {
                                checkEmptyEditText(locationString, conNameString, conNumString, titleString);
                            } else if (projectDate.matches(date)) {
                                Toast.makeText(projectEditor.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                            } else {
                                mDbHelper.update_project(selectedID, locationString, conNameString, conNumString, projectDate, descriptionString, titleString, noteString);
                                Intent intent = new Intent(projectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        } else if (items_update[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_update.show();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.project_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDbHelper.delete_project(selectedID);
                                mDbHelper.delete_allProjectAddOn(selectedID);
                                Intent intent = new Intent(projectEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;

            case android.R.id.home:
                if (!mPendingHasChanged) {
                    NavUtils.navigateUpFromSameTask(projectEditor.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(projectEditor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getIntExtra("id", -1);
        selectedLocation = receivedIntent.getStringExtra("location");
        selectedName = receivedIntent.getStringExtra("conName");
        selectedNum = receivedIntent.getStringExtra("conNum");
        selectedTitle = receivedIntent.getStringExtra("title");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectednotes = receivedIntent.getStringExtra("notes");
        HideMenu = receivedIntent.getIntExtra("HideMenu", 0);

        if (HideMenu == 1) {
            selectedprojDate = receivedIntent.getStringExtra("date");
            mDisplayDate.setText(selectedprojDate);
        }


        mProjectLocation.setText(selectedLocation);
        mContactName.setText(selectedName);
        mContactNumber.setText(selectedNum);
        mProjectTitle.setText(selectedTitle);
        mProjectDescription.setText(selectedDescription);
        mProjectNotes.setText(selectednotes);


        // Hide save menu
        if (HideMenu == 1) {
            HIDE_MENU = true;
        }
    }

    public void initCheckUnsavedChanges() {
        mProjectLocation.setOnTouchListener(mTouchListener);
        mContactName.setOnTouchListener(mTouchListener);
        mContactNumber.setOnTouchListener(mTouchListener);
        mProjectNotes.setOnTouchListener(mTouchListener);
        mProjectDescription.setOnTouchListener(mTouchListener);
        mProjectTitle.setOnTouchListener(mTouchListener);
        mDisplayDate.setOnTouchListener(mTouchListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mPendingHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //check empty text
    private void checkEmptyEditText(String locationString, String conNameString, String conNumInt, String projTitle) {

        if (TextUtils.isEmpty(locationString)) {
            mProjectLocation.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(conNameString)) {
            mContactName.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(conNumInt)) {
            mContactNumber.setError("Please fill in the blank.");
            return;
        }
        if (TextUtils.isEmpty(projTitle)) {
            mProjectTitle.setError("Please fill in the blank.");
            return;
        }
    }
}
