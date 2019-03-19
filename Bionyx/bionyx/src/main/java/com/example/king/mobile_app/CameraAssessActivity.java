package com.example.king.mobile_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;


import static com.example.king.mobile_app.BaseActivity.currentIp;

public class CameraAssessActivity extends AppCompatActivity {

    //URL
    private String SERVER_URL = "http://" + currentIp + "/api/classifyImage/";
    private String ERROR_HANDLING_URL = "http://"+ currentIp +"/api/verifyImage/";

    //REQUEST
    private String token;
    private String id;
    private InternetConnectionManager ICM;
    private String disorder, diseases;

    //STORAGE
    Uri mCurrentImageUri, mCurrentCroppedImageUri;
    String mCurrentPhotoPath, mCurrentThumbPath;
    String mCurrentPhotoName, mCurrentCroppedPhotoName;
    private Bitmap bitmap;

    //GUI ITEMS
    private static ProgressDialog mProgressDialog;
    private SweetAlertDialog pDialog;
    Button CaptureImg, Upload, okButton;
    ImageView iCaptured;
    TextView tvPlaceFingernail;
    final int CAMERA_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_assessment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ICM = new InternetConnectionManager();

        tvPlaceFingernail = findViewById(R.id.tvPlaceFingernail);
        Upload = findViewById(R.id.btnUpload);
        CaptureImg = findViewById(R.id.btnCapture);
        iCaptured = findViewById(R.id.ivCaptured);


        Animation blink_animation = AnimationUtils.loadAnimation(CameraAssessActivity.this, R.anim.blink_anim);
        tvPlaceFingernail.startAnimation(blink_animation);

        iCaptured.setVisibility(View.GONE);
        tvPlaceFingernail.setVisibility(View.VISIBLE);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(CameraAssessActivity.this, R.anim.fadein);
                Upload.startAnimation(animation);
                uploadImg();
            }
        });

        CaptureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CameraAssessActivity.this, R.anim.fadein);
                CaptureImg.startAnimation(animation);
                dispatchTakePictureIntent(CAMERA_CAPTURE);
            }
        });

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.token = prefs.getString("token", "");
        this.id = prefs.getString("id", "");
        askPermissions();

        getSupportActionBar().setTitle("Assess Fingernail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void askPermissions() {

        ArrayList<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(CameraAssessActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(CameraAssessActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(CameraAssessActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }

        if (permissions.size() > 0) {
            String[] permiss = permissions.toArray(new String[0]);

            ActivityCompat.requestPermissions(CameraAssessActivity.this, permiss,
                    222);
        } else {

        }
    }

    /*
    Function for creating file directory (original image)
    */
    private File createFileDirectory() throws IOException {

        String folder = "DCIM/Bionyx";
        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);

        if (!imgDir.exists()) {
            if (!imgDir.mkdir()) {
                Log.e("CameraAssessActivity", "Failed to create DCIM/Bionyx directory");
                return null;
            }
        }
        return imgDir;
    }

    /*
    Function for creating image file (original image)
    */
    private File createImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timestamp + "_";
        File imgDir = createFileDirectory();
        File imageF = File.createTempFile(imageFileName, ".jpg", imgDir);

        return imageF;
    }

    /*
    Function for finalizing the creation of image file as well as its folder directory (original image)
    */
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        mCurrentPhotoName = f.getName();
//        mCurrentImageUri = Uri.fromFile(f);
        mCurrentImageUri = FileProvider.getUriForFile(CameraAssessActivity.this, "com.example.king.mobile_app.provider", f);

        return f;

    }

    /*
    Function for finalizing the creation of image file as well as its folder directory (Cropped Image)
     */
    private File setUpCroppedFile() throws IOException {

        File f = createCroppedImageFile();
        mCurrentThumbPath = f.getAbsolutePath();
        mCurrentCroppedPhotoName = f.getName();
        mCurrentCroppedImageUri = FileProvider.getUriForFile(CameraAssessActivity.this, "com.example.king.mobile_app.provider", f);

        return f;
    }

    /*
    Function for creating image file (cropped image)
    */
    private File createCroppedImageFile() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timestamp + "_";
        File imgDir = createCroppedFileDirectory();
        File imageF = File.createTempFile(imageFileName, ".jpg", imgDir);

        return imageF;
    }

    /*
    Function for creating file directory (Cropped Image)
     */
    private File createCroppedFileDirectory() throws IOException {

        String folder = "DCIM/Bionyx/Crop";
        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);

        if (!imgDir.exists()) {
            if (!imgDir.mkdir()) {
                Log.e("CameraAssessActivity", "Failed to create DCIM/Bionyx/Crop directory");
                return null;
            }
        }
        return imgDir;
    }


    /*
    Function for accessing camera
     */
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {

            case CAMERA_CAPTURE:

                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentImageUri);
                    takePictureIntent.putExtra("return-data", true);
                } catch (IOException e) {
                    e.printStackTrace();
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        }
        startActivityForResult(takePictureIntent, CAMERA_CAPTURE);
    }

    /*
    Override function for getting the result from captured and cropped image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK) {
            if (ICM.isNetworkAvailable(this)) {
                new VerifyImageTask().execute(mCurrentPhotoPath);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                tvPlaceFingernail.clearAnimation();
                iCaptured.setVisibility(View.VISIBLE);
                tvPlaceFingernail.setVisibility(View.GONE);
                mCurrentCroppedImageUri = result.getUri();
                iCaptured.setImageResource(android.R.color.transparent);
                iCaptured.setImageURI(mCurrentCroppedImageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentCroppedImageUri);
                    saveCropImage(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            mCurrentCroppedImageUri = null;
            Toast.makeText(getApplicationContext(), "Crop image cancelled", Toast.LENGTH_SHORT).show();
        } else if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_CANCELED) {
            Snackbar.with(CameraAssessActivity.this,null)
                    .type(Type.CUSTOM, 0xff000000)
                    .message("Capture image cancelled")
                    .duration(Duration.SHORT)
                    .show();

        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    Function for cropping image
     */
    private void performCrop() {
        mCurrentCroppedImageUri = null;
        CropImage.activity(mCurrentImageUri).start(this);
    }

    /*
    Function for saving cropped image
     */
    private void saveCropImage(Bitmap image) {

        try {
            File f = setUpCroppedFile();
            if (f == null) {
                Log.d("CameraAssessActivity",
                        "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(f);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("CameraAssessActivity", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("CameraAssessActivity", "Error accessing file: " + e.getMessage());
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, f.getPath());

            getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
    Function for uploading image
    */
    private void uploadImg() {

        final String selectedFilePath = mCurrentThumbPath;
        if (ICM.isNetworkAvailable(this)) {
            new UploadImageTask().execute(selectedFilePath);
        }
    }

    /*
    Asynctask for uploading image to server (django-rest api)
     */
    private class UploadImageTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Processing");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... paths) {

            try {
                String resp = uploadFile(paths[0]);
                return "" + resp;
            } catch (Exception e) {
                return "No Image";
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
            int maxBufferSize = 5 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {

                Log.e("CameraAssessActivity", "Source File Doesn't Exist: " + selectedFilePath);
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
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    String dispName = "image";
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + dispName + "\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    System.out.println("Bytes Available " + bytesAvailable);
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("Buffer Size: " + bufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("Total bytes: " + bytesRead);

                    while (bytesRead > 0) {
                        try {
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            Toast.makeText(CameraAssessActivity.this, "Insufficient Memory", Toast.LENGTH_SHORT).show();
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    try {
                        serverResponseCode = connection.getResponseCode();

                    } catch (OutOfMemoryError e) {
                        Toast.makeText(CameraAssessActivity.this, "Memory Insufficient", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();
                    Log.i("CameraAssessActivity", "Server Response is " + serverResponseMessage + ": " + serverResponseCode);

                    /*
                    Okay request valid fingernail image
                     */
                    if (serverResponseCode == 200) {

                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String output;
                        Log.e("CameraAssessActivity", "File upload completed.\n\n" + fileName);
                        while ((output = br.readLine()) != null) {
                            sb.append(output);
                        }
                        String response = sb.toString();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status").trim();
                            disorder = jsonObject.getString("disorder").trim();
                            diseases = jsonObject.getString("diseases").trim();

                            Results = status;

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            return "";
                        }
                        br.close();
                    }
                    /*
                    Bad request not a valid fingernail image
                     */
                    else if (serverResponseCode == 400) {

                        Log.e("CameraAssessActivity", "Invalid image");
                        String message = "Bad Request";
                        Results = message;
                    }

                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "File Not Found");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "URL Error!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "Cannot Read/Write File");
                }
                return Results;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.print(result);
            if (result.equals("Healthy")) {
                pDialog.dismiss();
                new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Result")
                        .setContentText("You're healthy")
                        .setConfirmText("Assess again?")
                        .setCancelText("View history")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CameraAssessActivity.this.finish();
                                Intent intent = new Intent(CameraAssessActivity.this, CameraAssessActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                Intent intent = new Intent(CameraAssessActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                CameraAssessActivity.this.finish();
                            }
                        })
                        .show();
            } else if (result.equals("Unhealthy")) {
                pDialog.dismiss();
                new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Result")
                        .setConfirmText("Assess again?")
                        .setCancelText("View history")
                        .setContentText("You're unhealthy \n\n" +
                                "Disorder: "+disorder+"\n\n" +
                                "Considering the highest nail disorder detected, there is a possibility that you might develop: \n"+diseases+"\n\n" +
                                "DISCLAIMER\n\n" +
                                "This procedure is similar to other laboratory tests that are requested by physicians." +
                                "It is meant to help assist in arriving at an impression or a diagnosis; however, the final disposition of the patient remains with the doctor." +
                                "Whatever result this app shows, it is best to consult a physician.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                Intent intent = new Intent(CameraAssessActivity.this, CameraAssessActivity.class);
                                startActivity(intent);
                                CameraAssessActivity.this.finish();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                Intent intent = new Intent(CameraAssessActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                CameraAssessActivity.this.finish();
                            }
                        })
                        .show();

            } else if (result.equals("No Image")) {
                pDialog.dismiss();
                Snackbar.with(CameraAssessActivity.this,null)
                        .type(Type.ERROR)
                        .message("No image to be processed.")
                        .duration(Duration.SHORT)
                        .show();

            } else if (result.equals("Bad Request")) {
                pDialog.dismiss();
                new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Invalid image! Please take a valid fingernail image.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CameraAssessActivity.this.finish();
                                Intent intent = new Intent(CameraAssessActivity.this, CameraAssessActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            } else {
                pDialog.dismiss();
                Snackbar.with(CameraAssessActivity.this,null)
                        .type(Type.ERROR)
                        .message("Cannot connect to server.")
                        .duration(Duration.SHORT)
                        .show();
            }
        }
    }

    /*
    Asynctask for uploading image to server (django-rest api)
     */
    private class VerifyImageTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Analyzing" +
                    " for valid image");
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#089ac1"));
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... paths) {

            try {
                String resp = uploadFile(paths[0]);
                return "" + resp;
            } catch (Exception e) {
                return "No Image";
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
            int maxBufferSize = 5 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {

                Log.e("CameraAssessActivity", "Source File Doesn't Exist: " + selectedFilePath);
                return selectedFilePath;

            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(ERROR_HANDLING_URL);
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
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    String dispName = "image";
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + dispName + "\";filename=\""
                            + fileName + "\"" + lineEnd);
                    dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    dataOutputStream.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    System.out.println("Bytes Available " + bytesAvailable);
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("Buffer Size: " + bufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("Total bytes: " + bytesRead);

                    while (bytesRead > 0) {
                        try {
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            Toast.makeText(CameraAssessActivity.this, "Insufficient Memory", Toast.LENGTH_SHORT).show();
                        }
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    try {
                        serverResponseCode = connection.getResponseCode();

                    } catch (OutOfMemoryError e) {
                        Toast.makeText(CameraAssessActivity.this, "Memory Insufficient", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();
                    Log.i("CameraAssessActivity", "Server Response is " + serverResponseMessage + ": " + serverResponseCode);

                    /*
                    Okay request valid fingernail image
                     */
                    if (serverResponseCode == 200) {

                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String output;
                        Log.e("CameraAssessActivity", "File upload completed.\n\n" + fileName);
                        while ((output = br.readLine()) != null) {
                            sb.append(output);
                        }
                        String response = sb.toString();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message").trim();

                            Results = message;

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            return "";
                        }
                        br.close();
                    }
                    /*
                    Bad request not a valid fingernail image
                     */
                    else if (serverResponseCode == 400) {

                        Log.e("CameraAssessActivity", "Invalid image");
                        String message = "failed";
                        Results = message;
                    }

                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "File Not Found");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "URL Error!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("CameraAssessActivity", "Cannot Read/Write File");
                }
                return Results;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.print(result);
            pDialog.dismiss();
            if (result.equals("successful")) {
                new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Awesome")
                        .setContentText("Valid image! Proceed to cropping?")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                performCrop();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();

            } else if (result.equals("failed")){
                new SweetAlertDialog(CameraAssessActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Invalid image! Please take a valid fingernail image.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                CameraAssessActivity.this.finish();
                                Intent intent = new Intent(CameraAssessActivity.this, CameraAssessActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();

            } else {
                Snackbar.with(CameraAssessActivity.this,null)
                        .type(Type.ERROR)
                        .message("Cannot connect to server.")
                        .duration(Duration.SHORT)
                        .show();
            }
        }
    }

    /* Action bar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.info, menu);
        return true;
    }

    /*
    Boolean for selected options in menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_help:
                openHelpDialog();
                break;
            /* If home is selected */
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openHelpDialog(){
        System.out.println("Camera Nail Assessment : Open Help Dialog ");
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        Button okButton = (Button) alertDialog.findViewById(R.id.buttonOk);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                alertDialog.dismiss();
            }
        });



    }
}


