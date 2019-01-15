package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    private ImageLoader imgLoader, imageLoader;
    private ImageButton Assess, Diseases, History;
    private String token = "";
    private String user_id = "";
    private String username = "";
    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String datejoined = "";
    private String AVATAR_URL = "http://"+currentIp+"/media/Images/user_profile_pic/";
    private TextView Nav_UserName, Nav_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        imgLoader = new ImageLoader(this);
        imageLoader = new ImageLoader(this);

        Assess = (ImageButton)findViewById(R.id.btnAssess);
        Diseases = (ImageButton)findViewById(R.id.btnDiseases);
        History = (ImageButton)findViewById(R.id.btnHistory);

        //if assess fingernail image button is clicked
        Assess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAssess();
            }
        });
        //if diseases image button is clicked
        Diseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiseases();
            }
        });
        //if history image button is clicked
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistory();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        //Session manager
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.token = prefs.getString("token", "");
        this.username = prefs.getString("username","");
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.user_id = prefs.getString("id", "");
        this.email = prefs.getString("email", "");
        this.datejoined = prefs.getString("date_joined", "");
        this.AVATAR_URL += "user_"+user_id+"/avatar.jpeg";

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("avatar_url", AVATAR_URL);
        editor.commit();
        //Verifying the session values
        System.out.println(token+" "+user_id+" "+username+" "+first_name+" "+last_name+" "+email+" "+datejoined);

        View headerView = navigationView.getHeaderView(0);
        Nav_UserName = (TextView) headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_UserName.setText(username);
        Nav_Email = (TextView)headerView.findViewById(R.id.tv_Nav_Email);
        Nav_Email.setText(email);
        ImageView Nav_Avatar = (ImageView)headerView.findViewById(R.id.tv_Nav_Avatar);
        imgLoader.DisplayImage(AVATAR_URL, Nav_Avatar);

    }

    private void startAssess(){
        Intent intent = new Intent(this, NailAssessmentActivity.class);
        startActivity(intent);
    }

    private void showHistory(){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void showDiseases(){
        Intent intent = new Intent(this, DiseasesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
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
                break;

            case R.id.nav_profile:
                Intent iProfile = new Intent(DashboardActivity.this, UserProfileActivity.class);
                startActivity(iProfile);
                break;

            case R.id.nav_about:
                Intent iAbout = new Intent(DashboardActivity.this, AboutActivity.class);
                startActivity(iAbout);
                break;


            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                imgLoader.clearCache();
                imageLoader.clearCache();
                Intent iLogin = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(iLogin);
                DashboardActivity.this.finish();

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
