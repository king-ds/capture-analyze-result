package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


import static com.example.king.mobile_app.BaseActivity.currentIp;

public class HistoryActivity extends AppCompatActivity implements AsyncResponse {

    private int i = -1;
    private static String username = "";
    private static String TRANSACTION_HISTORY_URL = "";
    private SweetAlertDialog pDialog;
    HistoryPhotoLoader historyPhotoLoader;

    static SwipeMenuListView listView;
    ArrayList<HashMap<String, String>> arraylist;
    JSONObject jsonobject;
    ListView listview;
    ListViewAdapter adapter;
    static String TRANSACTIONID = "id";
    static String OWNER = "owner";
    static String UPLOADED = "uploaded";
    static String DISORDER = "disorder";
    static String RESULT = "result";
    static String IMAGE = "image";
    static String STATUS = "status";
    static String DISEASES = "diseases";
    static String ISANSWERED = "isAnswered";
    static String ANSWER_1 = "answer_1";
    static String ANSWER_2 = "answer_2";
    static String ANSWER_3 = "answer_3";
    static String ANSWER_4 = "answer_4";
    static String ANSWER_5 = "answer_5";
    static String ANSWER_6 = "answer_6";
    static String ANSWER_7 = "answer_7";
    static String ANSWER_8 = "answer_8";
    static String ANSWER_9 = "answer_9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.username = prefs.getString("username", "");
        this.TRANSACTION_HISTORY_URL = "http://"+currentIp+"/api/history/"+username+"/";
        new GetTransactionHistory().execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        historyPhotoLoader = new HistoryPhotoLoader(this);
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
            pDialog = new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String inputLine;
            String response;
            arraylist = new ArrayList<HashMap<String, String>>();
            try {
                URL myUrl = new URL(TRANSACTION_HISTORY_URL);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(is);
                StringBuilder sb = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                br.close();
                is.close();
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
                            map.put("id", jsonobject.getString("id"));
                            map.put("image", jsonobject.getString("image"));
                            map.put("owner", jsonobject.getString("owner"));
                            map.put("uploaded", jsonobject.getString("uploaded"));
                            map.put("disorder", jsonobject.getString("disorder"));
                            map.put("status", jsonobject.getString("status"));
                            map.put("diseases", jsonobject.getString("diseases"));
                            map.put("isAnswered", jsonobject.getString("isAnswered"));
                            map.put("answer_1", jsonobject.getString("answer_1"));
                            map.put("answer_2", jsonobject.getString("answer_2"));
                            map.put("answer_3", jsonobject.getString("answer_3"));
                            map.put("answer_4", jsonobject.getString("answer_4"));
                            map.put("answer_5", jsonobject.getString("answer_5"));
                            map.put("answer_6", jsonobject.getString("answer_6"));
                            map.put("answer_7", jsonobject.getString("answer_7"));
                            map.put("answer_8", jsonobject.getString("answer_8"));
                            map.put("answer_9", jsonobject.getString("answer_9"));
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
            pDialog.dismiss();
            if (isEmpty == true) {
                new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning")
                        .setContentText("No history yet")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                HistoryActivity.this.finish();
                            }
                        })
                        .show();
            } else {
                listView = findViewById(R.id.listview);
                adapter = new ListViewAdapter(HistoryActivity.this, arraylist);
                listView.setAdapter(adapter);

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
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.connect();
                response_code = connection.getResponseCode();
                response_message = connection.getResponseMessage();
                if (response_code == 204) {
                    isDeleted = true;
                    historyPhotoLoader.clearCache();
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
            pDialog.dismiss();
            if (isDeleted == true) {
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
        }
    }

    /*
    Action bar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    /*
    Boolean for selected options in menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            /*
            If clear is selected
             */
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

                                sDialog.setTitleText("Cancelled!")
                                        .setContentText("Your history information is safe")
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

            /*
            If home is selected
             */
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

