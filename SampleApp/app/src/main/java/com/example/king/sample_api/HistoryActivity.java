package com.example.king.sample_api;

// --- PDF GENERATORS ---
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.File;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends BaseActivity implements AsyncResponse {

    private static String username = "";
    private static String token = "";
    private static String TRANSACTION_HISTORY_URL = "";
    private static ProgressDialog mProgressDialog;

    private Button ClearHistory;
    ArrayList<HashMap<String, String>> arraylist;
    JSONObject jsonobject;
    ListView listview;
    ListViewAdapter adapter;
    static String TRANSACTIONID = "id";
    static String OWNER = "owner";
    static String UPLOADED = "uploaded";
    static String RESULT = "result";
    static String BEAULINES = "BeauLines";
    static String CLUBBEDNAILS = "ClubbedNails";
    static String HEALTHY = "Healthy";
    static String SPLINTER = "Splinter";
    static String TERRYNAILS = "TerryNails";
    static String YELLOWNAILS = "YellowNails";
    static String IMAGE = "image";
    static String STATUS = "status";
    static String DISEASES = "diseases";
    static String FILTERED_IMAGE = "filtered_image";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

        this.username = prefs.getString("username", "");
        System.out.println(username);
        this.TRANSACTION_HISTORY_URL = "http://"+currentIp+"/history/"+username;
        new GetTransactionHistory().execute();



    }

    @Override
    public void processFinish(String output) {
        System.out.println(output);
    }


    /*
    Get Transaction History
     */
    public class GetTransactionHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Create process dialog
            mProgressDialog = new ProgressDialog(HistoryActivity.this);
            //Set Progress dialog title
            mProgressDialog.setTitle("History");
            //Set progress dialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            //Show progress dialog
            mProgressDialog.show();

        }
        @Override
        protected Void doInBackground(Void... params) {
            String inputLine;
            String response;
            //Create an arrray
            arraylist = new ArrayList<HashMap<String, String>>();

            try {
                //Create URL object
                URL myUrl = new URL(TRANSACTION_HISTORY_URL);

                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

                //Set methods and timeouts
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);

                //Connect to url
                connection.connect();

                //To read input or response from API
                //Create new InputStreamReader
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                //Create new buffered reader
                BufferedReader br = new BufferedReader(is);
                //and String Builder
                StringBuilder sb = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                //Close InputStream and Buffered reader
                br.close();
                is.close();
                //Set our result equal to string builder
                response = sb.toString();
                System.out.println(response);
                try {

                    JSONArray jsonarray = new JSONArray(response);

                    for(int i = 0; i<jsonarray.length(); i++){
                        HashMap<String, String> map = new HashMap<String, String>();
                        jsonobject = jsonarray.getJSONObject(i);

                        //Retrieve JSON Objects
                        map.put("id", jsonobject.getString("id"));
                        map.put("image", jsonobject.getString("image"));
                        map.put("owner", jsonobject.getString("owner"));
                        map.put("uploaded", jsonobject.getString("uploaded"));
                        map.put("BeauLines", jsonobject.getString("beau_lines"));
                        map.put("ClubbedNails", jsonobject.getString("clubbed_nails"));
                        map.put("Healthy", jsonobject.getString("healthy"));
                        map.put("Splinter", jsonobject.getString("splinter_hemorrhage"));
                        map.put("TerryNails", jsonobject.getString("terry_nails"));
                        map.put("YellowNails", jsonobject.getString("yellow_nails"));
                        map.put("status", jsonobject.getString("status"));
                        map.put("diseases", jsonobject.getString("diseases"));
                        map.put("filtered_image", jsonobject.getString("filtered_image"));
                        arraylist.add(map);
                    }
                }catch (JSONException e){
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            //Locate the listview in listview_main.xml
            listview = (ListView)findViewById(R.id.listview);
            //Pass the results into ListViewAdapterter.java
            adapter = new ListViewAdapter(HistoryActivity.this, arraylist);
            //Set the adapter to the ListView
            listview.setAdapter(adapter);
            //Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
}