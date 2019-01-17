package com.example.king.mobile_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NailAssessmentActivity extends BaseActivity {

    //URL
    private String SERVER_URL = "http://"+currentIp+"/api/classifyImage/";

    //REQUEST
    private String token;
    private String id;

    //STORAGE
    Uri mCurrentImageUri, mCurrentCroppedImageUri;
    String mCurrentPhotoPath, mCurrentThumbPath;
    String mCurrentPhotoName, mCurrentCroppedPhotoName;

    //GUI ITEMS
    private static ProgressDialog mProgressDialog;
    Button CaptureImg, Upload;
    ImageView iCaptured;

//    String realPath;

    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_assessment);

        Upload = (Button)findViewById(R.id.btnUpload);
        CaptureImg = (Button)findViewById(R.id.btnCapture);
        iCaptured = (ImageView)findViewById(R.id.ivCaptured);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });
        CaptureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(CAMERA_CAPTURE);
            }
        });


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.token = prefs.getString("token", "");
        this.id = prefs.getString("id", "");
        askPermissions();
    }


    private void askPermissions() {

        ArrayList<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(NailAssessmentActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(NailAssessmentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(NailAssessmentActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
        }

        if (permissions.size() > 0) {
            String[] permiss = permissions.toArray(new String[0]);

            ActivityCompat.requestPermissions(NailAssessmentActivity.this, permiss,
                    222);
        }else{

        }
        }

    /*
    Function for creating file directory (original image)
    */
    private File createFileDirectory() throws IOException{

        String folder = "DCIM/Bionyx";
        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);

        if(!imgDir.exists()){
            if(!imgDir.mkdir()){
                Log.e("NailAssessmentActivity", "Failed to create DCIM/Bionyx directory");
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
        mCurrentImageUri = Uri.fromFile(f);
//        mCurrentImageUri = FileProvider.getUriForFile(NailAssessmentActivity.this, "com.example.king.mobile_app.provider", f );
        return f;

    }

    /*
    Function for finalizing the creation of image file as well as its folder directory (Cropped Image)
     */
    private File setUpCroppedFile() throws IOException {

        File f = createCroppedImageFile();
        mCurrentThumbPath = f.getAbsolutePath();
        mCurrentCroppedPhotoName = f.getName();
        mCurrentCroppedImageUri = Uri.fromFile(f);

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
    private File createCroppedFileDirectory() throws IOException{

        String folder = "DCIM/Bionyx/Crop";
        File imgDir = new File(Environment.getExternalStorageDirectory(), folder);

        if(!imgDir.exists()){
            if(!imgDir.mkdir()){
                Log.e("NailAssessmentActivity", "Failed to create DCIM/Bionyx/Crop directory");
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
                    takePictureIntent.putExtra("return-data",true);
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {

        if (requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK){
            cropImg();
        }
        else if(requestCode == PIC_CROP && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap croppedImage = (Bitmap) extras.get("data");
            iCaptured.setImageBitmap(croppedImage);
        }
        else if(requestCode == CAMERA_CAPTURE && resultCode == RESULT_CANCELED ){
            Toast.makeText(getApplicationContext(), "Capture image cancelled", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == PIC_CROP && resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Crop image cancelled", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }

    /*
    Function for cropping image
     */
    private void cropImg(){

        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        try {

            cropIntent.setDataAndType(mCurrentImageUri, "image/*");

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);

            File f;
            try {
                f = setUpCroppedFile();
                mCurrentThumbPath = f.getAbsolutePath();
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentCroppedImageUri);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        catch(ActivityNotFoundException ex){
            ex.printStackTrace();
            mCurrentThumbPath = null;
            String error = "Your device doesn't support crop action!";
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
        startActivityForResult(cropIntent, PIC_CROP);
    }

   /*
   Function for uploading image
   */
    private void uploadImg(){

        final String selectedFilePath = mCurrentThumbPath;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UploadImageTask().execute(selectedFilePath);
            System.out.println("Uploading image....." + selectedFilePath);
        } else {
            System.out.println("No network connection available");
        }
    }

    /*
    Asynctask for uploading image to server (django-rest api)
     */
    private class UploadImageTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            mProgressDialog = new ProgressDialog(NailAssessmentActivity.this);
            mProgressDialog.setTitle("Fingernail Assessment");
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
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
            int maxBufferSize = 5*1024*1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            if(!selectedFile.isFile()){

                Log.e("NailAssessmentActivity", "Source File Doesn't Exist: "+selectedFilePath);
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
                    System.out.println("Bytes Available "+bytesAvailable);
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("Buffer Size: "+bufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("Total bytes: "+bytesRead);

                    while (bytesRead > 0) {
                        try {
                            dataOutputStream.write(buffer, 0, bufferSize);
                        } catch (OutOfMemoryError e) {
                            Toast.makeText(NailAssessmentActivity.this, "Insufficient Memory", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(NailAssessmentActivity.this, "Memory Insufficient", Toast.LENGTH_SHORT).show();
                    }
                    String serverResponseMessage = connection.getResponseMessage();
                    Log.i("NailAssessmentActivity", "Server Response is " + serverResponseMessage + ": " + serverResponseCode);

                    //response code of 200 indicates the server status is ok
                    if (serverResponseCode == 200) {

                        Log.e("NailAssessmentActivity", "File upload completed.\n\n" + fileName);
                        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder sb = new StringBuilder();
                        String output;
                        while((output = br.readLine()) != null){
                            sb.append(output);
                        }
                        String response = sb.toString();

                        try{
                            DecimalFormat df = new DecimalFormat("#0.00");
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("Status").trim();
                            String BeauLines = jsonObject.getString("Beau Lines").trim();
                            String ClubbedNails = jsonObject.getString("Clubbed Nails").trim();
                            String Healthy = jsonObject.getString("Healthy").trim();
                            String Splinter = jsonObject.getString("Splinter Hemorrhage").trim();
                            String TerryNails = jsonObject.getString("Terry's Nails").trim();
                            String YellowNails = jsonObject.getString("Yellow Nail Syndrome").trim();

                            Results = "Status: "+status+"\nBeau Lines: " +df.format(Float.parseFloat(BeauLines))  +"\nClubbed Nails: " + df.format(Float.parseFloat(ClubbedNails))
                                    +"\nHealthy: "+ df.format(Float.parseFloat(Healthy)) + "\nSpoon Nails: " + df.format(Float.parseFloat(Splinter)) +"\nTerry's Nails: "+ df.format(Float.parseFloat(TerryNails))
                                    +"\nYellow Nail Syndrome: " + df.format(Float.parseFloat(YellowNails));

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            return "";
                        }
                        br.close();
                    }

                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    Log.e("NailAssessmentActivity", "File Not Found");
                }catch (MalformedURLException e){
                    e.printStackTrace();
                    Log.e("NailAssessmentActivity", "URL Error!");
                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("NailAssessmentActivity", "Cannot Read/Write File");
                }
                return Results;
            }
        }
        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();
            AlertDialog.Builder PopupWindow = new AlertDialog.Builder(NailAssessmentActivity.this);
            View ResultView = getLayoutInflater().inflate(R.layout.activity_nail_result, null);
            TextView ResultText = (TextView)ResultView.findViewById(R.id.tvResult);
            Button OkayButton = (Button)ResultView.findViewById(R.id.btnOkay);

            PopupWindow.setView(ResultView);
            AlertDialog dialog = PopupWindow.create();
            dialog.show();
            ResultText.setText(result);
            OkayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NailAssessmentActivity.this.finish();
                    Intent intent = new Intent(NailAssessmentActivity.this, NailAssessmentActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}

