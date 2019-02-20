package com.example.king.mobile_app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class ListViewAdapter extends BaseAdapter {

    /*
    Declare variables
     */
    public SweetAlertDialog pDialog;
    private String HISTORY_INSTANCE_URL;
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HistoryPhotoLoader historyPhotoLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        historyPhotoLoader = new HistoryPhotoLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        /*
        Declared variables
         */
        final TextView transactionid;
        final TextView uploaded;
        final TextView result;
        final ImageView image;

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

        resultp = data.get(position);
        transactionid = convertView.findViewById(R.id.transactionid);
        uploaded = convertView.findViewById(R.id.uploaded);
        result = convertView.findViewById(R.id.result);
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface custom_font = Typeface.createFromAsset(am, "fonts/montserrat.ttf");

        transactionid.setTypeface(custom_font);
        uploaded.setTypeface(custom_font);
        result.setTypeface(custom_font);
        image = convertView.findViewById(R.id.image);
        transactionid.setText(resultp.get(HistoryActivity.TRANSACTIONID));
        uploaded.setText(resultp.get(HistoryActivity.UPLOADED));
        result.setText(resultp.get(HistoryActivity.STATUS));
        historyPhotoLoader.DisplayImage(resultp.get(HistoryActivity.IMAGE), image);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(context.getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setWidth(360);
                openItem.setIcon(R.drawable.ic_open);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(context.getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(360);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        /*
        Apply the created swipe menu instance to swipelistview
        */
        HistoryActivity.listView.setMenuCreator(creator);

        /*
        If the swipe menu is clicked (Delete & Open)
        */
        HistoryActivity.listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                resultp = data.get(position);
                switch (index) {
                    /*
                    Open Case
                    */
                    case 0:
                        /*
                        Pass all related data
                         */
                        String isHealthy = resultp.get(HistoryActivity.STATUS);

                        if (isHealthy.equals("Healthy")) {
                            Intent healthy_intent = new Intent(context, HealthyViewAdapter.class);
                            healthy_intent.putExtra("image", resultp.get(HistoryActivity.IMAGE));
                            healthy_intent.putExtra("status", resultp.get(HistoryActivity.STATUS));
                            healthy_intent.putExtra("transactionid", resultp.get(HistoryActivity.TRANSACTIONID));
                            image.buildDrawingCache();
                            Bitmap bitmap = image.getDrawingCache();
                            healthy_intent.putExtra("bitmapImage", bitmap);
                            context.startActivity(healthy_intent);

                        } else if (isHealthy.equals("Unhealthy")) {

                            Intent unhealthy_intent = new Intent(context, UnhealthyViewAdapter.class);
                            unhealthy_intent.putExtra("transactionid", resultp.get(HistoryActivity.TRANSACTIONID));
                            unhealthy_intent.putExtra("owner", resultp.get(HistoryActivity.OWNER));
                            unhealthy_intent.putExtra("uploaded", resultp.get(HistoryActivity.UPLOADED));
                            unhealthy_intent.putExtra("BeauLines", resultp.get(HistoryActivity.BEAULINES));
                            unhealthy_intent.putExtra("ClubbedNails", resultp.get(HistoryActivity.CLUBBEDNAILS));
                            unhealthy_intent.putExtra("Spoon", resultp.get(HistoryActivity.SPOON));
                            unhealthy_intent.putExtra("TerryNails", resultp.get(HistoryActivity.TERRYNAILS));
                            unhealthy_intent.putExtra("YellowNails", resultp.get(HistoryActivity.YELLOWNAILS));
                            unhealthy_intent.putExtra("image", resultp.get(HistoryActivity.IMAGE));
                            unhealthy_intent.putExtra("status", resultp.get(HistoryActivity.STATUS));
                            unhealthy_intent.putExtra("diseases", resultp.get(HistoryActivity.DISEASES));
                            image.buildDrawingCache();
                            Bitmap bitmap = image.getDrawingCache();
                            unhealthy_intent.putExtra("bitmapImage", bitmap);
                            context.startActivity(unhealthy_intent);
                        }
                        break;
                    /*
                    Delete Case
                    */
                    case 1:

                        String current_id = resultp.get(HistoryActivity.TRANSACTIONID);
                        HISTORY_INSTANCE_URL = "http://" + currentIp + "/api/delHistory/" + current_id + "/";
                        new DelHistoryInstance().execute();
                }
                return false;
            }
        });
        return convertView;
    }

    /*
    Delete a history instance
     */
    public class DelHistoryInstance extends AsyncTask<Void, Void, Void> {

        int response_code;
        String response_message;
        boolean isDeleted;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Removing");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL myUrl = new URL(HISTORY_INSTANCE_URL);
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

            if (isDeleted == true) {
                pDialog.dismiss();
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("History")
                        .setContentText("You have successfully removed it. Click okay to proceed")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                ((HistoryActivity)context).finish();
                                Intent intent = new Intent(context, HistoryActivity.class);
                                context.startActivity(intent);
                            }
                        })
                        .show();
            }
            else{
                pDialog.dismiss();
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
