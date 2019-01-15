package com.example.king.mobile_app;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class zzzPangTryNiJover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zzz_pang_try_ni_jover);

        Button aboutCalura = (Button) findViewById(R.id.aboutCalura);
        aboutCalura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(zzzPangTryNiJover.this);
                View mView = getLayoutInflater().inflate(R.layout.devcalura, null);

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });

        Button aboutDabuet = (Button) findViewById(R.id.aboutDabuet);
        aboutDabuet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(zzzPangTryNiJover.this);
                View mView2 = getLayoutInflater().inflate(R.layout.devdabuet, null);

                mBuilder2.setView(mView2);
                AlertDialog dialog = mBuilder2.create();
                dialog.show();
            }
        });

        Button aboutRegino = (Button) findViewById(R.id.aboutRegino);
        aboutRegino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder3 = new AlertDialog.Builder(zzzPangTryNiJover.this);
                View mView3 = getLayoutInflater().inflate(R.layout.devregino, null);

                mBuilder3.setView(mView3);
                AlertDialog dialog = mBuilder3.create();
                dialog.show();
            }
        });

        Button aboutSantiago = (Button) findViewById(R.id.aboutSantiago);
        aboutSantiago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(zzzPangTryNiJover.this);
                View mView4 = getLayoutInflater().inflate(R.layout.devsantiago, null);

                mBuilder4.setView(mView4);
                AlertDialog dialog = mBuilder4.create();
                dialog.show();
            }
        });
    }
}
