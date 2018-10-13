package com.example.weijunn.project01.Project;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weijunn.project01.R;
import com.example.weijunn.project01.defectEditor;
import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;
import com.example.weijunn.project01.sqlitedata.Untils;
import com.example.weijunn.project01.sqlitedata.newProjectProvider;
import com.example.weijunn.project01.sqlitedata.projectAddOnContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.weijunn.project01.sqlitedata.projectAddOnContract.*;

public class projectAddOn extends AppCompatActivity{

    private static final String TAG = "projectAddOn";

    EditText mProjectAddOnNotes;
    TextView mProjectAddOnDate;
    SQLiteDatabase sqLiteDatabase;
    ProjectDbHelper mDbHelper;

    //update data
    private String  selectednotes,selectedprojDate;
    private Bitmap selectedImage;
    private int HideMenu,selectedStatus,selectedID,selectedProjectID;
    private boolean HIDE_MENU =false;


    private int mProjectStatus = 0;

    private Spinner mProjectStatusSpinner;



    //Camera
    ImageView projectAddOnImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath ;

    // For attach image to email
    File pic;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String date ="Select a date";

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
        setContentView(R.layout.activity_project_add_on);

        initId();
        initDate();
        //update data
        initUpdate();

        setupSpinner();
           //Check unsaved changes
           initCheckUnsavedChanges();


           if(getSupportActionBar()!=null){
               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
               getSupportActionBar().setDisplayShowHomeEnabled(true);
           }



        //Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.projectAddOn_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
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

    public void initId(){

        mDbHelper = new ProjectDbHelper(this);

        mProjectAddOnNotes = (EditText) findViewById(R.id.projectAddOnNotes);
        mProjectAddOnDate =(TextView) findViewById(R.id.projectAddOnDate);


        projectAddOnImage = (ImageView) findViewById(R.id.projectAddOn_img);

        mProjectStatusSpinner = (Spinner) findViewById(R.id.spinner_projectStatus);
    }


    /** intent Camera code Start **/
    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(projectAddOn.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try{
                            photoFile = createImageFile();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        if (photoFile != null) {
                            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.weijunn.project01.provider", photoFile);

                            List<ResolveInfo> resolvedIntentActivities = getApplicationContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                                String packageName = resolvedIntentInfo.activityInfo.packageName;
                                getApplicationContext().grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }
                    }

                } else if (items[which].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select File"),SELECT_FILE);
                    //startActivityForResult(intent, SELECT_FILE);

                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();

                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                //Bundle bundle = data.getExtras();
                //final Bitmap bmp = (Bitmap) bundle.get("data");
                //projectImage.setImageBitmap(bmp);

                projectAddOnImage.setImageURI(Uri.parse(imageFilePath));
                projectAddOnImage.setMinimumHeight(512);
                Log.e("Attachment Path:", imageFilePath);

                Bitmap imgBitmap = ((BitmapDrawable)projectAddOnImage.getDrawable()).getBitmap();


                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        pic = new File(root, "pic.png");
                        FileOutputStream out = new FileOutputStream(pic);
                        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                }

            } else if (requestCode == SELECT_FILE) {
                Uri selectImageUri = data.getData();
                projectAddOnImage.setImageURI(selectImageUri);
                Bitmap imgBitmap = ((BitmapDrawable)projectAddOnImage.getDrawable()).getBitmap();


                try {
                    File root = Environment.getExternalStorageDirectory();
                    if (root.canWrite()){
                        pic = new File(root, "pic.png");
                        FileOutputStream out = new FileOutputStream(pic);
                        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e("BROKEN", "Could not write file " + e.getMessage());
                }

            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0 ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Thanks for granting Permission", Toast.LENGTH_SHORT).show();;
            }
        }
    }

    /** intent Camera code end **/



    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter projectTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_projectStatus_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        projectTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mProjectStatusSpinner.setAdapter(projectTypeSpinnerAdapter);

        if(selectedStatus == 0) {
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("Completed");
            mProjectStatusSpinner.setSelection(selectionPosition);
        }else if(selectedStatus == 1){
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("In Progress");
            mProjectStatusSpinner.setSelection(selectionPosition);
        }else{
            int selectionPosition = projectTypeSpinnerAdapter.getPosition("Deferred");
            mProjectStatusSpinner.setSelection(selectionPosition);
        }

        // Set the integer mSelected to the constant values
        mProjectStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Completed")) {
                        mProjectStatus = ProjectAddOnEntry.COMPLETED;
                    }else if (selection.equals("In Progress")){
                        mProjectStatus = ProjectAddOnEntry.IN_PROGRESS;
                    } else if(selection.equals("Deferred")){
                        mProjectStatus = ProjectAddOnEntry.DEFERRED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mProjectStatus = ProjectAddOnEntry.COMPLETED;
            }

        });
    }

    private void initDate(){
        mProjectAddOnDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(projectAddOn.this,
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
                mProjectAddOnDate.setText(date);
            }
        };
    }

    public void initUpdate() {
        Intent receivedIntent = getIntent();
        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedProjectID = receivedIntent.getIntExtra("projectId",-1);
        selectedStatus = receivedIntent.getIntExtra("status",0);
        selectednotes = receivedIntent.getStringExtra("notes");
        HideMenu = receivedIntent.getIntExtra("HideMenu",0);

        if(HideMenu ==1) {
            byte[] bytes = receivedIntent.getByteArrayExtra("projImage");
            selectedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            selectedprojDate = receivedIntent.getStringExtra("date");
            mProjectAddOnDate.setText(selectedprojDate);
            selectedProjectID = receivedIntent.getIntExtra("projectId",-1);
        }



        projectAddOnImage.setImageBitmap(selectedImage);
        mProjectAddOnNotes.setText(selectednotes);


        // Hide save menu
        if(HideMenu == 1){
            HIDE_MENU = true;
        }
    }

    public void initCheckUnsavedChanges(){
        mProjectAddOnDate.setOnTouchListener(mTouchListener);
        projectAddOnImage.setOnTouchListener(mTouchListener);
        mProjectAddOnNotes.setOnTouchListener(mTouchListener);
        mProjectStatusSpinner.setOnTouchListener(mTouchListener);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,  discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete,  deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_addon, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.action_update).setVisible(true);
                menu.findItem(R.id.action_addon).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(true);
            }else {
                menu.findItem(R.id.action_update).setVisible(false);
                menu.findItem(R.id.action_addon).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_addon:
                final CharSequence[] items_add = {"Save", "Cancel"};
                AlertDialog.Builder builder_add = new AlertDialog.Builder(projectAddOn.this);
                builder_add.setTitle("Select options");
                builder_add.setItems(items_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_add[which].equals("Save")) {
                            String noteString = mProjectAddOnNotes.getText().toString().trim();
                            String projectDate = mProjectAddOnDate.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectAddOnImage.getDrawable()).getBitmap();
                          // mSelectedProject = (newProjectProvider) mSpinnerProjectID.getSelectedItem();
                            if(imgBitmap == null){
                                Toast.makeText(projectAddOn.this,"Image cannot be null.",Toast.LENGTH_SHORT).show();
                            }else if(projectDate.matches(date)){
                                Toast.makeText(projectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mDbHelper.insert_projectAddOn(mProjectStatus, projectDate, noteString, Untils.getBytes(imgBitmap), selectedProjectID);
                                Intent intent = new Intent(projectAddOn.this, projectList.class);
                                intent.putExtra("id", selectedProjectID);
                                startActivity(intent);
                            }
                        } else if (items_add[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder_add.show();
                return true;
            case R.id.action_update:
                final CharSequence[] items_update = {"Update", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(projectAddOn.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Update")) {
                            String noteString = mProjectAddOnNotes.getText().toString().trim();
                            String projectDate = mProjectAddOnDate.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectAddOnImage.getDrawable()).getBitmap();
                            // mSelectedProject = (newProjectProvider) mSpinnerProjectID.getSelectedItem();
                            if(imgBitmap == null){
                                Toast.makeText(projectAddOn.this,"Image cannot be null.",Toast.LENGTH_SHORT).show();
                            }else if(projectDate.matches(date)){
                                Toast.makeText(projectAddOn.this,"Please select a date.",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mDbHelper.update_projectAddOn(selectedID, mProjectStatus, projectDate, noteString, Untils.getBytes(imgBitmap), selectedProjectID);
                                Intent intent = new Intent(projectAddOn.this, projectList.class);
                                intent.putExtra("id", selectedProjectID);
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
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDbHelper.delete_projectAddOn(selectedID);
                                Intent intent = new Intent(projectAddOn.this, projectList.class);
                                intent.putExtra("id", selectedProjectID);
                                startActivity(intent);
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;

            case android.R.id.home:
                if(!mPendingHasChanged){
                    Intent intent = new Intent(projectAddOn.this,projectList.class);
                    intent.putExtra("id",selectedProjectID);
                    startActivity(intent);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                Intent intent = new Intent(projectAddOn.this,projectList.class);
                                intent.putExtra("id",selectedProjectID);
                                startActivity(intent);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
