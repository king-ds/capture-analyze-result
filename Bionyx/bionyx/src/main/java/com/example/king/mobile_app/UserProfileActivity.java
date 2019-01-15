package com.example.king.mobile_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageLoader imgLoader, imageLoader;
    private String SERVER_URL = "http://"+currentIp+"/postAvatar/";
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
    private ImageView Profile_Pic;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_CAMERA_IMAGE = 2;
    String realPath;
    private Uri mCurrentImageUri;
    private String mCurrentPhotoPath;
    private String mCurrentPhotoName;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageLoader = new ImageLoader(this);
        imgLoader = new ImageLoader(this);
        FirstName = (TextView)findViewById(R.id.tv_FirstName);
        LastName = (TextView)findViewById(R.id.tv_LastName);
        Email = (TextView)findViewById(R.id.tv_Email);
        Username = (TextView)findViewById(R.id.tv_Username);
        UserID = (TextView)findViewById(R.id.tv_UserID);
        DateJoined = (TextView)findViewById(R.id.tv_DateJoined);
        Profile_Pic = (ImageView) findViewById(R.id.iv_Avatar);
        Processed_Images = (TextView)findViewById(R.id.tv_ProcessedImages);

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


        FirstName.setText(first_name);
        LastName.setText(last_name);
        Email.setText(email);
        UserID.setText(user_id);
        Username.setText(username);
        DateJoined.setText(datejoined);
        Processed_Images.setText(processed_images);

        imgLoader.DisplayImage(AVATAR_URL, Profile_Pic);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView Nav_UserName = (TextView) headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_UserName.setText(username);
        TextView Nav_Email = (TextView)headerView.findViewById(R.id.tv_Nav_Email);
        Nav_Email.setText(email);
        ImageView Nav_Avatar = (ImageView)headerView.findViewById(R.id.tv_Nav_Avatar);
        imgLoader.DisplayImage(AVATAR_URL, Nav_Avatar);

        Profile_Pic.bringToFront();
        Profile_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
                View SelectionView = getLayoutInflater().inflate(R.layout.activity_selection_imageview, null);

                PopupWindow.setView(SelectionView);
                AlertDialog dialog = PopupWindow.create();
                dialog.show();
                Button Take_Picture = (Button)SelectionView.findViewById(R.id.btnTakePicture);
                Take_Picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera(1);
                    }
                });
                Button Choose_from_gallery = (Button)SelectionView.findViewById(R.id.btnChooseFromGallery);
                Choose_from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();
                    }
                });
            }
        });

    }

    private File createFileDirectory() throws IOException{

        String folder = "Bionyx/Profile_Pic";

        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);

        //Create the storage directory if it does not exist
        if(!imgDir.exists()){
            if(!imgDir.mkdir()){
                Log.e("Finger Nails", "Oops! Failed create Finger Nails directory");
                return  null;
            }
        }
        System.out.println(imgDir);
        return imgDir;
    }



    //Create image file name
    private File createImageFile() throws IOException {

        //Set the date
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //Assign an image file name
        String imageFileName = "IMG_" + timestamp + "_";
        //Directory for image
        File imgDir = createFileDirectory();
        //Create Temporary File with suffix .jpg
        File imageF = File.createTempFile(imageFileName, ".jpg", imgDir);

        //return the created image
        return imageF;
    }

    //Set up the created image
    private File setUpPhotoFile() throws IOException {

        //Assign the created image to variable file "f"
        File f = createImageFile();
        //Get and assign the path of an image
        mCurrentPhotoPath = f.getAbsolutePath();
        //Get and assign the name of an image
        mCurrentPhotoName = f.getName();
        //Get and assign the uri of an image
//        mCurrentImageUri = Uri.fromFile(f);
        mCurrentImageUri = FileProvider.getUriForFile(UserProfileActivity.this, "com.example.king.mobile_app.provider", f );



        return f;

    }

    public void openCamera(int actionCode){

        //Access Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //switch case using action code
        switch (actionCode) {

            //case 1: take a photo
            case 1:

                //Reset
                File f = null;

                try {
                    //Get the photo
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
        }//switch
        startActivityForResult(takePictureIntent, PICK_CAMERA_IMAGE);

    }

    public void openGallery(){

        mCurrentImageUri = null;
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImageUri = null;
        String filePath = null;

        AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
        View ProfilePicView = getLayoutInflater().inflate(R.layout.activity_profile_pic, null);

        ImageView Temp_ProfilePic = (ImageView)ProfilePicView.findViewById(R.id.ivTemp_ProfilePic);
        Button Upload = (Button)ProfilePicView.findViewById(R.id.btnUploadProfilePic);
        Button Cancel = (Button)ProfilePicView.findViewById(R.id.btnCancelProfilePic);

        PopupWindow.setView(ProfilePicView);
        AlertDialog dialog = PopupWindow.create();


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
                                Toast.makeText(getApplicationContext(), "Unknown path",
                                        Toast.LENGTH_LONG).show();
                                Log.e("Bitmap", "Unknown path");
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_LONG).show();
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                    }
                    Temp_ProfilePic.setImageURI(selectedImageUri);
                    Temp_ProfilePic.setRotation(90);
                }
                break;

            case PICK_CAMERA_IMAGE:

                filePath = null;
                if (resultCode == RESULT_OK) {

                    dialog.show();
                    selectedImageUri = mCurrentImageUri;
                    filePath = mCurrentPhotoPath;
                    Temp_ProfilePic.setImageURI(selectedImageUri);
                    Temp_ProfilePic.setRotation(90);

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
             }

        final String selectedFilePath = filePath;
        Upload.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     uploadImg(selectedFilePath);

                 }
             });
        Cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        }

    private void uploadImg(String filepath){
        //iCaptured.setImageURI(Uri.parse(mCurrentPhotoPath));
        //iCaptured.setRotation(90);
        final String selectedFilePath = filepath;

        System.out.println("The image file path is " + selectedFilePath);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UserProfileActivity.UploadProfilePicTask().execute(selectedFilePath);
            System.out.println("Uploading image....." + selectedFilePath);
        } else {
            System.out.println("No network connection");
        }
    }

    private class UploadProfilePicTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Create process dialog
            mProgressDialog = new ProgressDialog(UserProfileActivity.this);
            //Set Progress dialog title
            mProgressDialog.setTitle("Updating Profile Picture");
            //Set progress dialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
            //Show progress dialog
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... paths){

            try{
                String resp = uploadFile(paths[0]);
                return ""+resp;
            }catch (Exception e){
                return "Unable to upload image";
            }
        }


        public String uploadFile(final String selectedFilePath){

            int serverResponseCode = 0;
            String Results = "";
            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 10*1024*1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if(!selectedFile.isFile()){

                Log.e("UserProfileActivity", "Source File Doesn't Exist: "+selectedFilePath);
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
                    System.out.println("Bytes Available "+bytesAvailable);
                    //selecting the buffer size as minimum of available bytes or 1MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("Buffer Size: "+bufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //read bytes from FileInputStream(from 0th index to bufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("Total bytes: "+bytesRead);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        try {
                            //write the bytes read from inputstream
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            Toast.makeText(UserProfileActivity.this, "Insufficient Memory", Toast.LENGTH_SHORT).show();
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        System.out.println("Bytes Available: "+bytesAvailable+"\nBuffer Size: "+bufferSize+"\nBytes Read: "+bytesRead);
                    }
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    try {
                        serverResponseCode = connection.getResponseCode();

                    } catch (OutOfMemoryError e) {
                        Toast.makeText(UserProfileActivity.this, "Memory Insufficient", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();
                    Log.i("UserProfileActivity", "Server Response is " + serverResponseMessage + ": " + serverResponseCode);



                    //response code of 200 indicates the server status is ok
                    if (serverResponseCode == 200) {

                        Log.e("UserProfileActivity", "File upload completed.\n\n" + fileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String output;
                        while((output = br.readLine()) != null){
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

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "File Not Found");
                }catch (MalformedURLException e){
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "URL Error!");
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("UserProfileActivity", "Cannot Read/Write File");
                }
                return Results;
            }
        }
        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();
            imgLoader.clearCache();
            imgLoader.DisplayImage(AVATAR_URL, Profile_Pic);


//            AlertDialog.Builder PopupWindow = new AlertDialog.Builder(UserProfileActivity.this);
//            View ResultView = getLayoutInflater().inflate(R.layout.activity_nail_result, null);
//            TextView ResultText = (TextView)ResultView.findViewById(R.id.tvResult);
//
//            PopupWindow.setView(ResultView);
//            AlertDialog dialog = PopupWindow.create();
//            dialog.show();
//            ResultText.setText(result);
        }
    }


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



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                Intent iHome = new Intent(UserProfileActivity.this, DashboardActivity.class);
                startActivity(iHome);
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_about:
                Intent iAbout = new Intent(UserProfileActivity.this, AboutActivity.class);
                startActivity(iAbout);
                break;

            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                imgLoader.clearCache();
                imageLoader.clearCache();
                Intent iLogin = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(iLogin);

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
