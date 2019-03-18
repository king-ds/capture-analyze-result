package com.example.king.mobile_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity {

    private Button btnGallery, btnCamera, btnRealTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Assess Selection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnRealTime = findViewById(R.id.btnRealTime);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(SelectionActivity.this, R.anim.fadein);
                btnCamera.startAnimation(animation);
                openCamera();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(SelectionActivity.this, R.anim.fadein);
                btnGallery.startAnimation(animation);
                openGallery();
            }
        });

        btnRealTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(SelectionActivity.this, R.anim.fadein);
                btnRealTime.startAnimation(animation);
                openRealTime();
            }
        });
    }

    private void openCamera(){

        Intent intent = new Intent(SelectionActivity.this, RemindersActivity.class);
        startActivity(intent);
    }

    private void openGallery(){
        Intent intent = new Intent(SelectionActivity.this, GalleryAssessActivity.class);
        startActivity(intent);
    }

    private void openRealTime(){
        Intent intent = new Intent(SelectionActivity.this, RT_RemindersActivity.class);
        startActivity(intent);
    }

    /* Boolean for selected options in menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*
            If home is selected
             */
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
