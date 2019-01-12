package com.example.king.sample_api;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class DiseasesActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView disbeaus, disclubbed, dissplinter, disterry, disyellow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);
        disbeaus = (CardView) findViewById(R.id.dis_beuasline);
        disclubbed = (CardView) findViewById(R.id.dis_clubbednails);
        dissplinter = (CardView) findViewById(R.id.dis_splinter);
        disterry = (CardView) findViewById(R.id.dis_terrynail);
        disyellow = (CardView) findViewById(R.id.dis_yellownails);
        //Add Click listener to the Card
        disbeaus.setOnClickListener(this);
        disclubbed.setOnClickListener(this);
        dissplinter.setOnClickListener(this);
        disterry.setOnClickListener(this);
        disyellow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()){
            case R.id.dis_beuasline: i = new Intent(this,disease_beausline.class);startActivity(i); break;
            case R.id.dis_clubbednails: i = new Intent(this,disease_clubbed.class);startActivity(i); break;
            case R.id.dis_splinter: i = new Intent(this,disease_splinter_haemorrhages.class);startActivity(i); break;
            case R.id.dis_terrynail: i = new Intent(this,disease_terry.class);startActivity(i); break;
            case R.id.dis_yellownails: i = new Intent(this,disease_yellownails.class);startActivity(i); break;
            default:break;
        }

    }
}
