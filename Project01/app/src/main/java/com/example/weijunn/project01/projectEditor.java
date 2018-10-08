package com.example.weijunn.project01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v7.app.ActionBar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.weijunn.project01.sqlitedata.PendingContract;
import com.example.weijunn.project01.sqlitedata.ProjectDbHelper;
import com.example.weijunn.project01.sqlitedata.Untils;
import com.example.weijunn.project01.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class projectEditor extends AppCompatActivity {

    EditText mProjectLocation, mContactName, mContactNumber, mDefect1, mDefect2, mDefect3, mPendingComment, mProjectManager,mProjectDate;

    ProjectDbHelper mDbHelper;
    private Spinner mProjectTypeSpinner;

    //Camera
    ImageView projectImage;
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final int REQUEST_PERMISSION = 200;
    String imageFilePath ;

    // report string
    String reportMessage;

    // For attach image to email
    File pic;

    // Update data
    private String selectedLocation , selectedComments, selectedName,selectedNum,selectedprojManager,selectedprojDate,selectedDefect1,selectedDefect2,selectedDefect3;
    private int HideMenu,selectedID;
    private Bitmap selectedImage;
    private boolean HIDE_MENU =false;

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

        // init Id
        initId();

        // Spinner
        setupSpinner();


        //Check permission

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        // Update data
        initUpdate();

        //Check unsaved changes
        initCheckUnsavedChanges();


    }

    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(projectEditor.this);
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

                projectImage.setImageURI(Uri.parse(imageFilePath));
                Log.e("Attachment Path:", imageFilePath);

                Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();


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
                projectImage.setImageURI(selectImageUri);
                Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();


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
/** Insert data Spare
    private void insert_Pending(){
        String locationString = mProjectLocation.getText().toString().trim();
        String conNameString = mContactName.getText().toString().trim();
        String conNumString = mContactNumber.getText().toString().trim();
        int conNumInt = Integer.parseInt(conNumString);
        String defect1String = mDefect1.getText().toString().trim();
        String defect2String = mDefect2.getText().toString().trim();
        String defect3String = mDefect3.getText().toString().trim();
        String penCommentString = mPendingComment.getText().toString().trim();
        Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();

        ProjectDbHelper mDbHelper = new ProjectDbHelper(this);
        //Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

         //Create a ContentValues object where column names are the keys,
         // and Toto's pending attributes are the values

        ContentValues values = new ContentValues();
        values.put(ProjectDbHelper.COLUMN_PROJECT_LOCATION, locationString);
        values.put(ProjectDbHelper.COLUMN_CONTACT_NAME, conNameString);
        values.put(ProjectDbHelper.COLUMN_CONTACT_NUMBER, conNumInt);
        values.put(ProjectDbHelper.COLUMN_DEFECT_1, defect1String);
        values.put(ProjectDbHelper.COLUMN_DEFECT_2, defect2String);
        values.put(ProjectDbHelper.COLUMN_DEFECT_3, defect3String);
        values.put(ProjectDbHelper.COLUMN_DEFECT_COMMENTS, penCommentString);
        values.put(ProjectDbHelper.COLUMN_DEFECT_IMG,imgBitmap);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        long dbInsert = db.insert(ProjectDbHelper.TABLE_NAME_PENDING,null,values);

        if(dbInsert != -1){
            Toast.makeText(this,"New row added, new row id: " + dbInsert, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Error ..",Toast.LENGTH_SHORT).show();
        }

        db.close();

    }
 **/

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter projectTypeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_projectType_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        projectTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mProjectTypeSpinner.setAdapter(projectTypeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mProjectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(R.string.project_type1)) {
                        // type 1
                    }else if (selection.equals(R.string.project_type2)){
                        // type 2
                    } else {
                        // type 3
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 // Unknown
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(Build.VERSION.SDK_INT > 11) {
            invalidateOptionsMenu();
            if (HIDE_MENU) {
                menu.findItem(R.id.action_update).setVisible(true);
                menu.findItem(R.id.action_save).setVisible(false);
                menu.findItem(R.id.action_delete).setVisible(true);
            }else {
                menu.findItem(R.id.action_update).setVisible(false);
                menu.findItem(R.id.action_save).setVisible(true);
                menu.findItem(R.id.action_delete).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Create dialog to send email / store data
                final CharSequence[] items = {"Save and email", "Save only", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(projectEditor.this);
                builder.setTitle("Select options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which].equals("Save and email")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            int conNumInt = Integer.parseInt(conNumString);
                            String projManager = mProjectManager.getText().toString().trim();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String defect2String = mDefect2.getText().toString().trim();
                            String defect3String = mDefect3.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();
                            mDbHelper.insert_pending(locationString,conNameString,conNumInt,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString, Untils.getBytes(imgBitmap));

                            reportMessage = createReportSummary(locationString,conNameString,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString);
                            Intent emailIntent = new Intent (Intent.ACTION_SEND);
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("image/*");
                            //Uri imageUri = Uri.parse("Path:: " + imageFilePath);
                            emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(pic));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Reports");
                            emailIntent.putExtra(Intent.EXTRA_TEXT,reportMessage);
                            if (emailIntent.resolveActivity(getPackageManager())!=null){
                                startActivity(emailIntent);
                            }
                            finish();

                        } else if (items[which].equals("Save only")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            int conNumInt = Integer.parseInt(conNumString);
                            String projManager = mProjectManager.getText().toString().trim();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String defect2String = mDefect2.getText().toString().trim();
                            String defect3String = mDefect3.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();
                            mDbHelper.insert_pending(locationString,conNameString,conNumInt,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString, Untils.getBytes(imgBitmap));
                            finish();

                        } else if (items[which].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;

            case R.id.action_update:
                //Create dialog to send email / store data
                final CharSequence[] items_update = {"Update and email", "Update only", "Cancel"};
                AlertDialog.Builder builder_update = new AlertDialog.Builder(projectEditor.this);
                builder_update.setTitle("Select options");
                builder_update.setItems(items_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items_update[which].equals("Update and email")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            int conNumInt = Integer.parseInt(conNumString);
                            String projManager = mProjectManager.getText().toString().trim();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String defect2String = mDefect2.getText().toString().trim();
                            String defect3String = mDefect3.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();
                            mDbHelper.update_pending(selectedID,locationString,conNameString,conNumInt,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString, Untils.getBytes(imgBitmap));

                            reportMessage = createReportSummary(locationString,conNameString,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString);
                            Intent emailIntent = new Intent (Intent.ACTION_SEND);
                            //emailIntent.setData(Uri.parse("mailto:"));
                            emailIntent.setType("image/*");
                            //Uri imageUri = Uri.parse("Path:: " + imageFilePath);
                            emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(pic));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Reports");
                            emailIntent.putExtra(Intent.EXTRA_TEXT,reportMessage);
                            if (emailIntent.resolveActivity(getPackageManager())!=null){
                                startActivity(emailIntent);
                            }else{
                                Toast.makeText(projectEditor.this, "There are no email clients installed.",Toast.LENGTH_SHORT).show();
                            }
                            finish();

                        } else if (items_update[which].equals("Update only")) {
                            String locationString = mProjectLocation.getText().toString().trim();
                            String conNameString = mContactName.getText().toString().trim();
                            String conNumString = mContactNumber.getText().toString().trim();
                            int conNumInt = Integer.parseInt(conNumString);
                            String projManager = mProjectManager.getText().toString().trim();
                            String projectDate = mProjectDate.getText().toString().trim();
                            String defect1String = mDefect1.getText().toString().trim();
                            String defect2String = mDefect2.getText().toString().trim();
                            String defect3String = mDefect3.getText().toString().trim();
                            String penCommentString = mPendingComment.getText().toString().trim();
                            Bitmap imgBitmap = ((BitmapDrawable)projectImage.getDrawable()).getBitmap();
                            mDbHelper.update_pending(selectedID,locationString,conNameString,conNumInt,projManager,projectDate,defect1String,defect2String,defect3String,penCommentString, Untils.getBytes(imgBitmap));
                            finish();

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
                                mDbHelper.delete_pending(selectedID);
                                finish();
                            }
                        };
                showDeleteDialog(deleteButtonClickListener);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if(!mPendingHasChanged){
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
        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedLocation = receivedIntent.getStringExtra("location");
        selectedName = receivedIntent.getStringExtra("conName");
        selectedNum = receivedIntent.getStringExtra("conNum");
        selectedprojManager = receivedIntent.getStringExtra("projManager");
        selectedprojDate = receivedIntent.getStringExtra("projDate");
        selectedDefect1 = receivedIntent.getStringExtra("defect1");
        selectedDefect2 = receivedIntent.getStringExtra("defect2");
        selectedDefect3 = receivedIntent.getStringExtra("defect3");
        selectedComments = receivedIntent.getStringExtra("comments");
        selectedImage = receivedIntent.getParcelableExtra("projImage");
        HideMenu = receivedIntent.getIntExtra("HideMenu",0);


        mProjectLocation.setText(selectedLocation);
        mContactName.setText(selectedName);
        mContactNumber.setText(selectedNum);
        mProjectManager.setText(selectedprojManager);
        mProjectDate.setText(selectedprojDate);
        mDefect1.setText(selectedDefect1);
        mDefect2.setText(selectedDefect2);
        mDefect3.setText(selectedDefect3);
        mPendingComment.setText(selectedComments);
        projectImage.setImageBitmap(selectedImage);

        // Hide save menu
        if(HideMenu == 1){
        HIDE_MENU = true;
        }
    }

    public void initId(){

        mDbHelper = new ProjectDbHelper(this);

        mProjectLocation = (EditText) findViewById(R.id.edit_project_location);
        mContactName =(EditText) findViewById(R.id.edit_client_name);
        mContactNumber =(EditText) findViewById(R.id.edit_client_contact);
        mDefect1 =(EditText) findViewById(R.id.defect_1);
        mDefect2 =(EditText) findViewById(R.id.defect_2);
        mDefect3 =(EditText) findViewById(R.id.defect_3);
        mPendingComment =(EditText) findViewById(R.id.pending_comment);
        mProjectManager = (EditText) findViewById(R.id.edit_project_manager);
        mProjectDate = (EditText) findViewById(R.id.edit_project_date);

        projectImage = (ImageView) findViewById(R.id.project_img);

        mProjectTypeSpinner = (Spinner) findViewById(R.id.spinner_projectType);
    }

    @SuppressLint("StringFormatInvalid")
    private String createReportSummary(String location , String conName, String projManager, String date, String defect1, String defect2, String defect3, String comments){

        String reportMessage = "Hi " + conName;
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_location,location);
        reportMessage += "\n" + getString(R.string.report_summary_projManager,projManager);
        reportMessage += "\n" + getString(R.string.report_summary_date,date);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_description);
        reportMessage += "\n" + getString(R.string.report_summary_defect_1,defect1);
        reportMessage += "\n" + getString(R.string.report_summary_defect_2,defect2);
        reportMessage += "\n" + getString(R.string.report_summary_defect_3,defect3);
        reportMessage += "\n" + "\n" + getString(R.string.report_summary_comments,comments);
        reportMessage += "\n" +"\n" + getString(R.string.report_summary_Thank_you);

        return reportMessage;
    }

    public void initCheckUnsavedChanges(){
        mProjectLocation.setOnTouchListener(mTouchListener);
        mContactName.setOnTouchListener(mTouchListener);
        mContactNumber.setOnTouchListener(mTouchListener);
        mDefect1.setOnTouchListener(mTouchListener);
        mDefect2.setOnTouchListener(mTouchListener);
        mDefect3.setOnTouchListener(mTouchListener);
        mPendingComment.setOnTouchListener(mTouchListener);
        mProjectManager.setOnTouchListener(mTouchListener);
        mProjectDate.setOnTouchListener(mTouchListener);
        projectImage.setOnTouchListener(mTouchListener);

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
}
