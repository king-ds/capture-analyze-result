package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import java.util.ArrayList;
import java.util.List;
import static com.example.king.mobile_app.BaseActivity.currentIp;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    List<Dashboard> lstImages = new ArrayList<>();
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    private ImageLoader imgLoader, imageLoader;
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

        initData();
        HorizontalInfiniteCycleViewPager pager = findViewById(R.id.view_pager);
        DashboardAdapter adapter = new DashboardAdapter(lstImages, getBaseContext());
        pager.setAdapter(adapter);

        imgLoader = new ImageLoader(this);
        imageLoader = new ImageLoader(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
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
        Nav_UserName = headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_Email = headerView.findViewById(R.id.tv_Nav_Email);
        ImageView Nav_Avatar = headerView.findViewById(R.id.tv_Nav_Avatar);

        Nav_UserName.setText(username);
        Nav_Email.setText(email);
        imgLoader.DisplayImage(AVATAR_URL, Nav_Avatar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

    }

    private void initData() {
        lstImages.add(new Dashboard("A", R.drawable.dash_camera));
        lstImages.add(new Dashboard("H", R.drawable.dash_history));
        lstImages.add(new Dashboard("D", R.drawable.dash_disorders));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}