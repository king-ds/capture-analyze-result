package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HistoryPhotoLoader historyPhotoLoader;
    private ProfilePhotoLoader profile_photo_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        historyPhotoLoader = new HistoryPhotoLoader(this);
        profile_photo_loader = new ProfilePhotoLoader(this);


        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        String email = prefs.getString("email", "");
        String AVATAR_URL = prefs.getString("avatar_url", "");
        View headerView = navigationView.getHeaderView(0);
        TextView Nav_UserName = (TextView) headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_UserName.setText(username);
        TextView Nav_Email = (TextView)headerView.findViewById(R.id.tv_Nav_Email);
        Nav_Email.setText(email);
        ImageView Nav_Avatar = (ImageView)headerView.findViewById(R.id.tv_Nav_Avatar);
        profile_photo_loader.DisplayImage(AVATAR_URL, Nav_Avatar);


//        Popup Start
        Button aboutCalura = (Button) findViewById(R.id.aboutCalura);
        aboutCalura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AboutActivity.this);
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
                AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(AboutActivity.this);
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
                AlertDialog.Builder mBuilder3 = new AlertDialog.Builder(AboutActivity.this);
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
                AlertDialog.Builder mBuilder4 = new AlertDialog.Builder(AboutActivity.this);
                View mView4 = getLayoutInflater().inflate(R.layout.devsantiago, null);

                mBuilder4.setView(mView4);
                AlertDialog dialog = mBuilder4.create();
                dialog.show();
            }
        });


//        Popup End




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                Intent iHome = new Intent(AboutActivity.this, DashboardActivity.class);
                startActivity(iHome);
                break;

            case R.id.nav_profile:
                Intent iProfile = new Intent(AboutActivity.this, UserProfileActivity.class);
                startActivity(iProfile);
                break;

            case R.id.nav_about:
                break;

            case R.id.nav_logout:
                profile_photo_loader.clearCache();
                historyPhotoLoader.clearCache();
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                Intent iLogin = new Intent(AboutActivity.this, LoginActivity.class);
                startActivity(iLogin);

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
