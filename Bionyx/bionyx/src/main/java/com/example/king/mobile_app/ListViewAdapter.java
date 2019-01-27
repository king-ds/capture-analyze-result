package com.example.king.mobile_app;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
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
        // Declare Variables
        TextView transactionid;
        TextView owner;
        TextView uploaded;
        TextView BeauLines, ClubbedNails, Healthy, Splinter,TerryNails, YellowNails, result;
        final ImageView image;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        transactionid = (TextView) itemView.findViewById(R.id.transactionid);
        owner = (TextView) itemView.findViewById(R.id.owner);
        uploaded = (TextView) itemView.findViewById(R.id.uploaded);
        result = (TextView)itemView.findViewById(R.id.result);
//        BeauLines = (TextView)itemView.findViewById(R.id.tvBeauLines);
        //ClubbedNails = (TextView)itemView.findViewById(R.id.tvClubbedNails);
        //Healthy = (TextView)itemView.findViewById(R.id.tvHealthy);
        //Splinter = (TextView)itemView.findViewById(R.id.tvSplinterHemorrhage);
        //TerryNails = (TextView)itemView.findViewById(R.id.tvTerryNails);
        //YellowNails = (TextView)itemView.findViewById(R.id.tvYellowNail);



        // Locate the ImageView in listview_item.xml
        image = (ImageView) itemView.findViewById(R.id.image);
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) image.getLayoutParams();
        params.width = 200;
        params.height = 300;
        image.setRotation(90);
// existing height is ok as is, no need to edit it
        image.setLayoutParams(params);

        // Capture position and set results to the TextViews
        transactionid.setText(resultp.get(HistoryActivity.TRANSACTIONID));
        owner.setText(resultp.get(HistoryActivity.OWNER));
        uploaded.setText(resultp.get(HistoryActivity.UPLOADED));
        result.setText(resultp.get(HistoryActivity.STATUS));
//        BeauLines.setText(resultp.get(HistoryActivity.BEAULINES));
        //Healthy.setText(resultp.get(HistoryActivity.HEALTHY));
        //ClubbedNails.setText(resultp.get(HistoryActivity.CLUBBEDNAILS));
        //Splinter.setText(resultp.get(HistoryActivity.SPLINTER));
        //TerryNails.setText(resultp.get(HistoryActivity.TERRYNAILS));
        //YellowNails.setText(resultp.get(HistoryActivity.YELLOWNAILS));
        // Capture position and set results to the ImageView
        // Passes flag images URL into HistoryPhotoLoader.class
        historyPhotoLoader.DisplayImage(resultp.get(HistoryActivity.IMAGE), image);
        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data transactionid
                intent.putExtra("transactionid", resultp.get(HistoryActivity.TRANSACTIONID));
                // Pass all data owner
                intent.putExtra("owner", resultp.get(HistoryActivity.OWNER));
                // Pass all data uploaded
                intent.putExtra("uploaded",resultp.get(HistoryActivity.UPLOADED));
                // Pass all data percentage result
                intent.putExtra("BeauLines", resultp.get(HistoryActivity.BEAULINES));
                intent.putExtra("Healthy", resultp.get(HistoryActivity.HEALTHY));
                intent.putExtra("ClubbedNails", resultp.get(HistoryActivity.CLUBBEDNAILS));
                intent.putExtra("Splinter", resultp.get(HistoryActivity.SPLINTER));
                intent.putExtra("TerryNails", resultp.get(HistoryActivity.TERRYNAILS));
                intent.putExtra("YellowNails", resultp.get(HistoryActivity.YELLOWNAILS));
                // Pass all data image
                intent.putExtra("image", resultp.get(HistoryActivity.IMAGE));
                //Healthy and Unhealthy
                intent.putExtra("status",resultp.get(HistoryActivity.STATUS));
                //Diseases
                intent.putExtra("diseases", resultp.get(HistoryActivity.DISEASES));
                //Filtered Image
                intent.putExtra("filtered_image", resultp.get(HistoryActivity.FILTERED_IMAGE));
                //pdf image
                image.buildDrawingCache();
                Bitmap bitmap = image.getDrawingCache();
                intent.putExtra("bitmapImage",bitmap);
                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });
        return itemView;
    }
}
