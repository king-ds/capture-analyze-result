package com.example.king.mobile_app;

// --- PDF GENERATORS ---
//
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
        import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

        import java.util.HashMap;

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
        this.TRANSACTION_HISTORY_URL = "http://"+currentIp+"/api/history/" + username;
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

        boolean isEmpty = true;

        @Override
        protected void onPreExecute() {
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
                if (response != null) {
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
                String message = "No history yet";
                mProgressDialog.dismiss();
                AlertDialog.Builder PopupWindow = new AlertDialog.Builder(HistoryActivity.this);
                View empty_view = getLayoutInflater().inflate(R.layout.activity_history_emptymessage, null);
                TextView empty_message = (TextView) empty_view.findViewById(R.id.tvMessage);
                Button okay_button = (Button) empty_view.findViewById(R.id.btnOkay);

                empty_message.setText(message);
                PopupWindow.setView(empty_view);
                AlertDialog dialog = PopupWindow.create();
                dialog.show();

                okay_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HistoryActivity.this.finish();
                        Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    }
                });

            } else {
                listview = (ListView) findViewById(R.id.listview);
                adapter = new ListViewAdapter(HistoryActivity.this, arraylist);
                listview.setAdapter(adapter);
                mProgressDialog.dismiss();
            }
        }
    }
}
