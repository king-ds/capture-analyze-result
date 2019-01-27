package com.example.king.mobile_app;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


import static com.example.king.mobile_app.BaseActivity.currentIp;

public class HistoryActivity extends AppCompatActivity implements AsyncResponse {

    private int i = -1;
    private static String username = "";
    private static String token = "";
    private static String TRANSACTION_HISTORY_URL = "";
    private static ProgressDialog getProgressDialog, delProgressDialog;
    private SweetAlertDialog pDialog;
    ImageLoader imageLoader;

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
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.username = prefs.getString("username", "");
        this.TRANSACTION_HISTORY_URL = "http://"+currentIp+"/api/history/"+username;
        new GetTransactionHistory().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        imageLoader = new ImageLoader(this);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void processFinish(String output) {
        System.out.println(output);
    }


    /*
    Get Transaction History
     */
    public class GetTransactionHistory extends AsyncTask<Void, Void, Void> {

        boolean isEmpty = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getProgressDialog = new ProgressDialog(HistoryActivity.this);
            getProgressDialog.setTitle("History");
            getProgressDialog.setMessage("Loading...");
            getProgressDialog.setIndeterminate(false);
            getProgressDialog.setCancelable(false);
            getProgressDialog.setCanceledOnTouchOutside(false);
            getProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String inputLine;
            String response;
            arraylist = new ArrayList<HashMap<String, String>>();
            try {
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
                if (response.equals("[]") || response == null) {
                    isEmpty = true;
                } else {
                    isEmpty = false;
                    try {
                        JSONArray jsonarray = new JSONArray(response);
                        for (int i = 0; i < jsonarray.length(); i++) {
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
                    } catch (JSONException e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            if (isEmpty == true) {
                System.out.println("Status: "+isEmpty);
                String message = "No history yet";
                getProgressDialog.dismiss();
                AlertDialog.Builder getWindow= new AlertDialog.Builder(HistoryActivity.this);
                View empty_view = getLayoutInflater().inflate(R.layout.activity_history_emptymessage, null);
                TextView empty_message = (TextView) empty_view.findViewById(R.id.tvMessage);
                Button okay_button = (Button) empty_view.findViewById(R.id.btnOkay);

                empty_message.setText(message);
                getWindow.setView(empty_view);
                final AlertDialog getDialog = getWindow.create();
                getDialog.show();
                getDialog.setCanceledOnTouchOutside(false);
                getDialog.setCancelable(false);

                okay_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog.dismiss();
                        HistoryActivity.this.finish();
                        Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                listview = (ListView) findViewById(R.id.listview);
                adapter = new ListViewAdapter(HistoryActivity.this, arraylist);
                listview.setAdapter(adapter);
                getProgressDialog.dismiss();
            }
        }
    }

    /*
    Asnyctask for deleting all entries in history
    */
    public class DelTransactionHistory extends AsyncTask<Void, Void, Void> {

        boolean isDeleted = false;
        int response_code = 0;
        String response_message = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Removing");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL myUrl = new URL(TRANSACTION_HISTORY_URL);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("DELETE");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                //Connect to url
                connection.connect();
                response_code = connection.getResponseCode();
                response_message = connection.getResponseMessage();
                if (response_code == 204) {
                    isDeleted = true;
                    imageLoader.clearCache();
                    Log.e("HistoryActivity", "Server response message : " + response_message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isDeleted == true) {
                pDialog.dismiss();
                new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("History")
                        .setContentText("You have successfully clear the history. Click okay to proceed")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                HistoryActivity.this.finish();
                                Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
//                delProgressDialog.dismiss();
//                String message = "You have successfully clear history. Click okay to proceed";
//                AlertDialog.Builder delWindow= new AlertDialog.Builder(HistoryActivity.this);
//                View deleted_view = getLayoutInflater().inflate(R.layout.activity_clear_history, null);
//                TextView deleted_message = (TextView) deleted_view.findViewById(R.id.tvMessage);
//                Button okay_button = (Button)deleted_view.findViewById(R.id.btnOkay);
//
//                deleted_message.setText(message);
//                delWindow.setView(deleted_view);
//                final AlertDialog delDialog = delWindow.create();
//                delDialog.show();
//                delDialog.setCancelable(false);
//                delDialog.setCanceledOnTouchOutside(false);
//
//                okay_button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        delDialog.dismiss();
//                        HistoryActivity.this.finish();
//                        Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
//                        startActivity(intent);
//                    }
//                });
//            }
        }
    }

    /*
    Side bar menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_clear:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("You won't be able to recover this information")
                        .setCancelText("No")
                        .setConfirmText("Yes, remove it")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.setTitleText("Cancelled!")
                                        .setContentText("Your information history is safe")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                new DelTransactionHistory().execute();
                                sDialog.dismiss();
                            }
                        })
                        .show();


                return true;

            case android.R.id.home:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }
}

