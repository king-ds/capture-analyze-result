package com.example.king.mobile_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
    private String SERVER_URL = "http://"+currentIp+"/uploadImg/";

    //REQUEST
    private String token;
    private String id;
    private static final int ACTION_TAKE_PHOTO_B = 1;


    //STORAGE
    private Uri mCurrentImageUri;
    private String mCurrentPhotoPath;
    private String mCurrentPhotoName;

    //GUI ITEMS
    private static ProgressDialog mProgressDialog;
    Button CaptureImg, Upload;
    ImageView iCaptured;
    TextView Result;
    private View AssessmentForm, AssessmentProgressView;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    String realPath;
    String sdk_version, real_Path, data_Values;

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
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
            }
        });


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.token = prefs.getString("token", "");
        this.id = prefs.getString("id", "");
        System.out.println(token+" "+id);

        //check for uses-permission
        //if none, add uses permission
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




    private File createFileDirectory() throws IOException{

        String folder = "Bionyx/CapturedImages";

        File imgDir = new File(Environment.getExternalStorageDirectory(), folder+"/user"+id);

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
        mCurrentImageUri = FileProvider.getUriForFile(NailAssessmentActivity.this, "com.example.king.mobile_app.provider", f );



        return f;

    }

    private void dispatchTakePictureIntent(int actionCode) {

        //Access Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //switch case using action code
        switch (actionCode) {

            //case 1: take a photo
            case ACTION_TAKE_PHOTO_B:

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
        startActivityForResult(takePictureIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {

        //Pass URI
        realPath = mCurrentImageUri.getPath();
        System.out.println("REALPAAAAAAAAAAAAAAAATH"+realPath);
        iCaptured.setImageURI(Uri.parse(mCurrentPhotoPath));
        iCaptured.setRotation(90);
    }



    private void uploadImg(){
        //iCaptured.setImageURI(Uri.parse(mCurrentPhotoPath));
        //iCaptured.setRotation(90);
        final String selectedFilePath = mCurrentPhotoPath;
        System.out.println("The image file path is " + selectedFilePath);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new UploadImageTask().execute(selectedFilePath);
            System.out.println("Uploading image....." + selectedFilePath);
        } else {
            System.out.println("No network connection available");
        }
    }


    private class UploadImageTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Create process dialog
            mProgressDialog = new ProgressDialog(NailAssessmentActivity.this);
            //Set Progress dialog title
            mProgressDialog.setTitle("Fingernail Assessment");
            //Set progress dialog message
            mProgressDialog.setMessage("Processing...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
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
                    //creating new dataoutputstream
                    //Start content wrapper
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    String dispName = "image";
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
                            Toast.makeText(NailAssessmentActivity.this, "Insufficient Memory", Toast.LENGTH_SHORT).show();
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
                        System.out.println(response);

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
                            System.out.println(Results);


                        }catch (JSONException ex){
                            ex.printStackTrace();
                            return "";
                        }
                        br.close();

                    }

                    //closing the input and output streams

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

