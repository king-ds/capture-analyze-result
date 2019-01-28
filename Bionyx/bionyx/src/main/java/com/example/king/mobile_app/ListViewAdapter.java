package com.example.king.mobile_app;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
    ImageLoader imageLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);
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
        TextView transactionid;
        TextView uploaded;
        final TextView result;
        final ImageView image;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        resultp = data.get(position);

        /*
        Locate the textview in listview_item
         */
        transactionid = itemView.findViewById(R.id.transactionid);
        uploaded = itemView.findViewById(R.id.uploaded);
        result = itemView.findViewById(R.id.result);

        /*
        Locate the imageview in listview_item
         */
        image = itemView.findViewById(R.id.image);

        /*
        Capture the position and set the text to text view
         */
        transactionid.setText(resultp.get(HistoryActivity.TRANSACTIONID));
        uploaded.setText(resultp.get(HistoryActivity.UPLOADED));
        result.setText(resultp.get(HistoryActivity.STATUS));
        imageLoader.DisplayImage(resultp.get(HistoryActivity.IMAGE), image);

        /*
        Create swipe menu instance
         */
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
                        Intent intent = new Intent(context, SingleItemView.class);
                        intent.putExtra("transactionid", resultp.get(HistoryActivity.TRANSACTIONID));
                        intent.putExtra("owner", resultp.get(HistoryActivity.OWNER));
                        intent.putExtra("uploaded", resultp.get(HistoryActivity.UPLOADED));
                        intent.putExtra("BeauLines", resultp.get(HistoryActivity.BEAULINES));
                        intent.putExtra("Healthy", resultp.get(HistoryActivity.HEALTHY));
                        intent.putExtra("ClubbedNails", resultp.get(HistoryActivity.CLUBBEDNAILS));
                        intent.putExtra("Splinter", resultp.get(HistoryActivity.SPLINTER));
                        intent.putExtra("TerryNails", resultp.get(HistoryActivity.TERRYNAILS));
                        intent.putExtra("YellowNails", resultp.get(HistoryActivity.YELLOWNAILS));
                        intent.putExtra("image", resultp.get(HistoryActivity.IMAGE));
                        intent.putExtra("status", resultp.get(HistoryActivity.STATUS));
                        intent.putExtra("diseases", resultp.get(HistoryActivity.DISEASES));
                        intent.putExtra("filtered_image", resultp.get(HistoryActivity.FILTERED_IMAGE));
                        /*
                        PDF Image
                         */
                        image.buildDrawingCache();
                        Bitmap bitmap = image.getDrawingCache();
                        intent.putExtra("bitmapImage", bitmap);
                        context.startActivity(intent);

                        break;
                    /*
                    Delete Case
                     */
                    case 1:

                        String current_id = resultp.get(HistoryActivity.TRANSACTIONID);
                        HISTORY_INSTANCE_URL =  "http://"+currentIp+"/api/delHistory/"+current_id+"/";
                        new DelHistoryInstance().execute();
                }
                return false;
            }
        });
        return itemView;
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
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("History")
                        .setContentText("You have successfully removed it. Click okay to proceed")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                Intent intent = new Intent(context, HistoryActivity.class);
                                context.startActivity(intent);
                            }
                        })
                        .show();
            }
            else{
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
