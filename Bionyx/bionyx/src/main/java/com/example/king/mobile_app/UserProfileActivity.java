package com.example.king.mobile_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* start of declaring variables */
    private SweetAlertDialog pDialog;
    private ProfilePhotoLoader profile_photo_loader;
    private String SERVER_URL = "http://"+currentIp+"/api/postAvatar/";
    private String UPDATE_USER = "http://"+currentIp+"/api/updateUser/";
    private Button Take_Picture,Choose_from_gallery, View_photo, Cancel;
    private String AVATAR_URL = "";
    private String user_id = "";
    private String username = "";
    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String datejoined = "";
    private String token = "";
    private String processed_images = "";
    private TextView FirstName, LastName, Email, Username, DateJoined, UserID, Processed_Images;
    private ImageView Profile_Pic, Nav_Avatar;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_CAMERA_IMAGE = 2;
    private Uri mCurrentImageUri;
    private String mCurrentPhotoPath, mCurrentPhotoName;
    private InternetConnectionManager ICM;
    private Menu action;
    private AlertDialog dialog;
    private View SelectionView;
    /* end of declaring variables */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* start of assigning toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* end of assigning toolbar */

        /* start of declaring new import classes */
        ICM = new InternetConnectionManager();
        profile_photo_loader = new ProfilePhotoLoader(this);
        /* end of declaring new import classes  */


        /* start of assigning all text views */
        FirstName = findViewById(R.id.tv_FirstName);
        LastName = findViewById(R.id.tv_LastName);
        Email = findViewById(R.id.tv_Email);
        Username = findViewById(R.id.tv_Username);
        UserID = findViewById(R.id.tv_UserID);
        DateJoined = findViewById(R.id.tv_DateJoined);
        Profile_Pic = findViewById(R.id.iv_Avatar);
        Processed_Images = findViewById(R.id.tv_ProcessedImages);
        /* end of assingning all text views */

        /* start of shared prefences for user details */
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.username = prefs.getString("username","");
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.user_id = prefs.getString("id", "");
        this.email = prefs.getString("email", "");
        this.datejoined = prefs.getString("date_joined", "");
        this.token = prefs.getString("token", "");
        this.AVATAR_URL = prefs.getString("avatar_url", "");
        this.processed_images = prefs.getString("processed_images", "");
        this.UPDATE_USER += user_id+"/";
        /* end of shared prereferences for user details */

        /* start of set all user details and disable all edit text */
        setUserDetails();
        setDisabledEditText();
        /* end of set all user details and disable all edit text */

        /* start of creating drawer */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* end of creating drawer */

        /* start of navigation bar */
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* applying custom font to menu item */
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
        /* end of navigation bar */

        /* start of header view and set all user details */
        View headerView = navigationView.getHeaderView(0);
        TextView Nav_UserName = headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_UserName.setText(username);
        TextView Nav_Email = headerView.findViewById(R.id.tv_Nav_Email);
        Nav_Email.setText(email);
        Nav_Avatar = headerView.findViewById(R.id.tv_Nav_Avatar);
        profile_photo_loader.DisplayImage(AVATAR_URL, Nav_Avatar);
        /* end of header view and set all user details */

        /* start of dialog for selection view of profile photo */
        AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
        SelectionView = getLayoutInflater().inflate(R.layout.activity_selection_imageview, null);
        PopupWindow.setView(SelectionView);
        dialog = PopupWindow.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Take_Picture = SelectionView.findViewById(R.id.btnTakePicture);
        View_photo = SelectionView.findViewById(R.id.btnViewPicture);
        Choose_from_gallery = SelectionView.findViewById(R.id.btnChooseFromGallery);
        Cancel = SelectionView.findViewById(R.id.btnCancel);
        Take_Picture.setVisibility(View.GONE);
        Choose_from_gallery.setVisibility(View.GONE);
        View_photo.setVisibility(View.VISIBLE);
        Cancel.setVisibility(View.VISIBLE);
        /* end of dialog for selection view of profile photo */

        /* start of on click listener for profile photo image view */
        Profile_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelection();
            }
        });
        /* end of on click listener for profile photo image view */
    }

    /* start of function for applying custom font to menu items */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/montserrat.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    /* end of function for applying custom font to menu items */


    /* start of function for set all user details including photo */
    private void setUserDetails(){

        FirstName.setText(first_name);
        LastName.setText(last_name);
        Email.setText(email);
        UserID.setText(user_id);
        Username.setText(username);
        DateJoined.setText(datejoined);
        Processed_Images.setText(processed_images);
        profile_photo_loader.DisplayImage(AVATAR_URL, Profile_Pic);
    }
    /* end of function for set all user details including photo */

    /* start of function for disabling all edit text */
    private void setDisabledEditText(){

        FirstName.setEnabled(false);
        LastName.setEnabled(false);
        Email.setEnabled(false);
        Username.setEnabled(false);
        FirstName.setFocusable(false);
        LastName.setFocusable(false);
        Email.setFocusable(false);
        Username.setFocusable(false);
    }
    /* end of function for disabling all edit text */

    /* start of function for enabling all edit text */
    private void setEnabledEditText(){

        FirstName.setEnabled(true);
        LastName.setEnabled(true);
        Email.setEnabled(true);
        Username.setEnabled(true);
        FirstName.setFocusableInTouchMode(true);
        LastName.setFocusableInTouchMode(true);
        Email.setFocusableInTouchMode(true);
        Username.setFocusableInTouchMode(true);
    }
    /* end of function for enabling all edit text */

    /* start of function for add/update profile photo by using
    either camera or gallery and view current profile photo */
    private void openSelection(){

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Take_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(1);
                dialog.dismiss();
            }
        });
        Choose_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                dialog.dismiss();
            }
        });
        View_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
                View view_photo = getLayoutInflater().inflate(R.layout.activity_view_photo, null);
                PopupWindow.setView(view_photo);
                final AlertDialog view_photo_dialog = PopupWindow.create();
                view_photo_dialog.show();
                ImageView profile_photo = view_photo.findViewById(R.id.iv_ViewPhoto);
                profile_photo_loader.DisplayImage(AVATAR_URL, profile_photo);
                ImageView cancel_view_photo = view_photo.findViewById(R.id.ivCloseWindow);
                cancel_view_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view_photo_dialog.dismiss();
                    }
                });
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ImageView Close_window = SelectionView.findViewById(R.id.ivCloseWindow);
        Close_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /* end of function for add/update profile photo by using
    either camera or gallery and view current profile photo */

    /* start of creation for profile photo directory */
    private File createFileDirectory() throws IOException{

        String folder = "Bionyx/DCIM/Profile_Pic";
        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);
        if(!imgDir.exists()){
            if(!imgDir.mkdir()){
                Log.e("Profile Picture", "Oops! Failed create Profile Picture directory");
                return  null;
            }
        }
        System.out.println(imgDir);
        return imgDir;
    }
    /* end of creation for profile photo directory */

    /* start of creation for image file */
    private File createImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timestamp + "_";
        File imgDir = createFileDirectory();
        File imageF = File.createTempFile(imageFileName, ".jpg", imgDir);

        return imageF;
    }
    /* end of creation for image file */

    /* start of configuration for generated image file to get both uri and path */
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        mCurrentPhotoName = f.getName();
        mCurrentImageUri = FileProvider.getUriForFile(UserProfileActivity.this, "com.example.king.mobile_app.provider", f );
        return f;
    }
    /* end of configuration for generated image file to get both uri and path */

    /* start of function for accessing mobile phone's camera */
    public void openCamera(int actionCode){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case 1:
                File f = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;
            default:
                break;
        }
        startActivityForResult(takePictureIntent, PICK_CAMERA_IMAGE);
    }
    /* end of function for accessing mobile phone's camera */

    /* start of function for opening gallery */
    public void openGallery(){

        mCurrentImageUri = null;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    /* end of function for opening gallery */


    /* start of override function for camera and gallery result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImageUri = null;
        String filePath = null;

        AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
        View ProfilePicView = getLayoutInflater().inflate(R.layout.activity_profile_pic, null);

        ImageView Temp_ProfilePic = (ImageView)ProfilePicView.findViewById(R.id.ivTemp_ProfilePic);
        Button Upload = ProfilePicView.findViewById(R.id.btnUploadProfilePic);
        Button Cancel = ProfilePicView.findViewById(R.id.btnCancelProfilePic);

        PopupWindow.setView(ProfilePicView);
        final AlertDialog dialog = PopupWindow.create();

        switch (requestCode) {

            case PICK_IMAGE:

                filePath = null;
                if (resultCode == RESULT_OK) {
                    dialog.show();
                    selectedImageUri = data.getData();
                    if(selectedImageUri!=null){
                        try{
                            String filemanagerstring = selectedImageUri.getPath();
                            String selectedImagePath = getPath(selectedImageUri);
                            if (selectedImagePath != null) {
                                filePath = selectedImagePath;
                            } else if (filemanagerstring != null) {
                                filePath = filemanagerstring;
                            } else {
                                Snackbar.with(UserProfileActivity.this,null)
                                        .type(Type.ERROR)
                                        .message("Unknown path")
                                        .duration(Duration.SHORT)
                                        .show();
                                Log.e("Bitmap", "Unknown path");
                            }
                        }catch (Exception e){
                            Snackbar.with(UserProfileActivity.this,null)
                                    .type(Type.ERROR)
                                    .message("Internal Error")
                                    .duration(Duration.LONG)
                                    .show();
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                    }
                    Temp_ProfilePic.setImageURI(selectedImageUri);
                }
                break;

            case PICK_CAMERA_IMAGE:

                filePath = null;
                if (resultCode == RESULT_OK) {
                    dialog.show();
                    selectedImageUri = mCurrentImageUri;
                    filePath = mCurrentPhotoPath;
                    Temp_ProfilePic.setImageURI(selectedImageUri);
                } else if (resultCode == RESULT_CANCELED) {
                    Snackbar.with(UserProfileActivity.this,null)
                            .type(Type.ERROR)
                            .message("Picture was not taken")
                            .duration(Duration.SHORT)
                            .show();
                } else {
                    Snackbar.with(UserProfileActivity.this,null)
                            .type(Type.ERROR)
                            .message("Picture was not taken")
                            .duration(Duration.SHORT)
                            .show();
                }
                break;
             }
        final String selectedFilePath = filePath;
        Upload.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(ICM.isNetworkAvailable(UserProfileActivity.this)) {
                         uploadImg(selectedFilePath);
                         dialog.dismiss();
                     }
                 }
             });

        Cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /* end of override function for camera and gallery result */

    /* start of function for uploading image */
    private void uploadImg(String filepath){

        final String selectedFilePath = filepath;
        if(ICM.isNetworkAvailable(this)) {
            new UserProfileActivity.UploadProfilePicTask().execute(selectedFilePath);
        }
    }
    /* end of function for uploading image */

    /* start of asynchronous task for uploading image to django rest api */
    private class UploadProfilePicTask extends AsyncTask<String, String, String> {

        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... paths) {

            try {
                String resp = uploadFile(paths[0]);
                return "" + resp;
            } catch (Exception e) {
                return "Unable to upload image";
            }
        }

        public String uploadFile(final String selectedFilePath) {

            int serverResponseCode = 0;
            String Results = "";
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 10 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {

                Log.e("UserProfileActivity", "Source File Doesn't Exist: " + selectedFilePath);
                return selectedFilePath;

            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(SERVER_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", selectedFilePath);
                    connection.setRequestProperty("Authorization", token);
                    //creating new dataoutputstream
                    //Start content wrapper
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    String dispName = "profile_pic";
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + dispName + "\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    System.out.println("Bytes Available " + bytesAvailable);
                    //selecting the buffer size as minimum of available bytes or 1MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("Buffer Size: " + bufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //read bytes from FileInputStream(from 0th index to bufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("Total bytes: " + bytesRead);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        try {
                            //write the bytes read from inputstream
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            Snackbar.with(UserProfileActivity.this,null)
                                    .type(Type.ERROR)
                                    .message("Insufficient Memory")
                                    .duration(Duration.SHORT)
                                    .show();
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        System.out.println("Bytes Available: " + bytesAvailable + "\nBuffer Size: " + bufferSize + "\nBytes Read: " + bytesRead);
                    }
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    try {
                        serverResponseCode = connection.getResponseCode();

                    } catch (OutOfMemoryError e) {
                        Snackbar.with(UserProfileActivity.this,null)
                                .type(Type.ERROR)
                                .message("Insufficient Memory")
                                .duration(Duration.SHORT)
                                .show();;
                    }
                    String serverResponseMessage = connection.getResponseMessage();
                    Log.i("UserProfileActivity", "Server Response is " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status is ok
                    if (serverResponseCode == 201) {
                        isSuccess = true;
                        Log.e("UserProfileActivity", "File upload completed.\n\n" + fileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            sb.append(output);
                        }
                        String response = sb.toString();
                        System.out.println(response);
                        br.close();
                    }

                    //closing the input and output streams
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "File Not Found");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "URL Error!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "Cannot Read/Write File");
                }
                return Results;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            String message;

            if (isSuccess == true) {

                profile_photo_loader.clearCache();
                profile_photo_loader.DisplayImage(AVATAR_URL, Profile_Pic);
                profile_photo_loader.DisplayImage(AVATAR_URL, Nav_Avatar);
                message = "Your profile photo has uploaded.";
                pDialog.dismiss();
                Snackbar.with(UserProfileActivity.this,null)
                        .type(Type.SUCCESS)
                        .message(message)
                        .duration(Duration.SHORT)
                        .show();

            } else {
                pDialog.dismiss();
                message = "Cannot connect to server.";
                Snackbar.with(UserProfileActivity.this,null)
                        .type(Type.ERROR)
                        .message(message)
                        .duration(Duration.SHORT)
                        .show();
            }
        }
    }
    /* end of asynchronous task for uploading image to django rest api */

    /* start of asynchronous task for updating user information */
    private class UpdateUserInformation extends AsyncTask<Void, Void, Void>{

        boolean isUpdated = false;
        int response_code = 0;
        String response_message;
        final String first_name = FirstName.getText().toString().trim();
        final String last_name = LastName.getText().toString().trim();
        final String email = Email.getText().toString().trim();
        final String username = Username.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Updating");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONfunctions parser = new JSONfunctions();
            JSONObject update = parser.getUpdatedUserInformationObject(first_name, last_name, username, email);
            String message = update.toString();
            System.out.println(message);

            try {
                URL myUrl = new URL(UPDATE_USER);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("PUT");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(message.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                System.out.println(os);
                os.write(message.getBytes());
                os.flush();
                os.close();

                //Connect to url
                connection.connect();
                response_code = connection.getResponseCode();
                response_message = connection.getResponseMessage();
                if (response_code == 200) {
                    isUpdated = true;
                    Log.e("UserProfileActivity", "Server response message : " + response_message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String message;

            if(isUpdated){
                message = "User profile has updated.";
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("first_name", first_name);
                editor.putString("last_name", last_name);
                editor.putString("email", email);
                editor.putString("username", username);
                editor.apply();
                pDialog.dismiss();
                Intent refresh_intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                UserProfileActivity.this.finish();
                startActivity(refresh_intent);

                Snackbar.with(UserProfileActivity.this,null)
                        .type(Type.SUCCESS)
                        .message(message)
                        .duration(Duration.SHORT)
                        .show();
            }else{

                pDialog.dismiss();
                message = "Cannot connect to server.";
                Snackbar.with(UserProfileActivity.this,null)
                        .type(Type.ERROR)
                        .message(message)
                        .duration(Duration.SHORT)
                        .show();
            }
        }
    }
    /* end of asynchronous task for updating user information */

    /* start of function for getting path from uri */
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    /* end of function for getting path from uri */

    /* start of override function for clicking back button of phone */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent dashboard_intent = new Intent(UserProfileActivity.this, DashboardActivity.class);
            startActivity(dashboard_intent);
            UserProfileActivity.this.finish();
        }
    }
    /* end of override function for clicking back button of phone */


    /* start of override function for menu options edit, save and cancel */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_save, menu);

        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);
        action.findItem(R.id.menu_cancel).setVisible(false);

        return true;
    }

    /* start of override function for the options selected from menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_edit:

                setEnabledEditText();
                Profile_Pic.setClickable(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Username, InputMethodManager.SHOW_IMPLICIT);

                Take_Picture.setVisibility(View.VISIBLE);
                Choose_from_gallery.setVisibility(View.VISIBLE);
                View_photo.setVisibility(View.GONE);
                Cancel.setVisibility(View.GONE);
                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);
                action.findItem(R.id.menu_cancel).setVisible(true);

                return true;

            case R.id.menu_cancel:

                Take_Picture.setVisibility(View.GONE);
                Choose_from_gallery.setVisibility(View.GONE);
                View_photo.setVisibility(View.VISIBLE);
                Cancel.setVisibility(View.VISIBLE);
                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_cancel).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(false);

                setUserDetails();
                setDisabledEditText();

                return true;

            case R.id.menu_save:

                if(ICM.isNetworkAvailable(UserProfileActivity.this)) {
                    new UpdateUserInformation().execute();
                }
                Take_Picture.setVisibility(View.GONE);
                Choose_from_gallery.setVisibility(View.GONE);
                View_photo.setVisibility(View.VISIBLE);
                Cancel.setVisibility(View.VISIBLE);
                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);
                action.findItem(R.id.menu_cancel).setVisible(false);



                setUserDetails();
                setDisabledEditText();

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }
    /* end of override function for the options selected from menu */

    /* start of override function for selected options from navigation bar menu */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent iHome = new Intent(UserProfileActivity.this, DashboardActivity.class);
                startActivity(iHome);
                UserProfileActivity.this.finish();
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_about:
                Intent iAbout = new Intent(UserProfileActivity.this, AboutActivity.class);
                startActivity(iAbout);
                UserProfileActivity.this.finish();
                break;

            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                profile_photo_loader.clearCache();
                String user_log = "user_log";
                Intent iLogin = new Intent(UserProfileActivity.this, LoginActivity.class);
                iLogin.putExtra("user_loggedout", user_log);
                UserProfileActivity.this.finish();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* end of override function for selected options from navigation bar menu */
}
