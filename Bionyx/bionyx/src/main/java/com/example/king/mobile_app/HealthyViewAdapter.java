package com.example.king.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HealthyViewAdapter extends AppCompatActivity {

    public String image, status, transactionid;
    HistoryPhotoLoader historyPhotoLoader = new HistoryPhotoLoader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.healthyview);

        Intent healthy_intent = getIntent();
        image = healthy_intent.getStringExtra("image");
        status = healthy_intent.getStringExtra("status");
        transactionid = healthy_intent.getStringExtra("transactionid");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction #: "+transactionid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView txtstatus = findViewById(R.id.tvStatus);
        ImageView imgnail = findViewById(R.id.image);

        txtstatus.setText(status);
        historyPhotoLoader.DisplayImage(image, imgnail);
    }

    private void generatePDF(){

        Rectangle pagesize = PageSize.LETTER;
        Document document = new Document(pagesize);
        Date date = new Date();
        String strDataFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDataFormat);
        final String formattedDate = dateFormat.format(date);
        boolean success;
    }

    /*
    Action bar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_item, menu);
        return true;
    }
    /*
    Boolean for selected options in menu
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_item:

            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
