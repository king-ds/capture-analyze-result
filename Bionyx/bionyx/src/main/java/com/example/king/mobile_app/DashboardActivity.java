package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.SubMenu;
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

    /* start of declaring variables */
    List<Dashboard> lstImages = new ArrayList<>();
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private HistoryPhotoLoader historyPhotoLoader;
    private ProfilePhotoLoader profile_photo_loader;
    private String token = "";
    private String user_id = "";
    private String username = "";
    private String first_name = "";
    private String last_name = "";
    private String email = "";
    private String datejoined = "";
    private String AVATAR_URL = "http://"+currentIp+"/media/Images/user_profile_pic/";
    private TextView Nav_UserName, Nav_Email;
    /* end of declaring variables */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        /* start of setting up the horizontal infinite cycle view pager */
        initData();
        HorizontalInfiniteCycleViewPager pager = findViewById(R.id.view_pager);
        DashboardAdapter adapter = new DashboardAdapter(lstImages, getBaseContext());
        pager.setAdapter(adapter);
        /* end of setting up the horizontal infinite cycle view pager */

        /* start of declaring new variables from import classes */
        historyPhotoLoader = new HistoryPhotoLoader(this);
        profile_photo_loader = new ProfilePhotoLoader(this);
        /* end of declaring new variables from import classes */

        /* start of toolbar configuration */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        /* end of toolbar configuration */

        /* start of drawer configuration */
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* end of drawer configuration */

        /* start of navigation view configuration */
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();

        /* apply custom font to menu items */
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
        /* end of navigation view configuration */

        /* start of shared preferences for user details */
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
        /* end of shared preferences for user details */

        /* start of header view configuration */
        View headerView = navigationView.getHeaderView(0);
        Nav_UserName = headerView.findViewById(R.id.tv_Nav_UserName);
        Nav_Email = headerView.findViewById(R.id.tv_Nav_Email);
        ImageView Nav_Avatar = headerView.findViewById(R.id.tv_Nav_Avatar);
        /* end of header view configuration */

        /* start of set all user details on header view */
        profile_photo_loader.DisplayImage(AVATAR_URL, Nav_Avatar);
        Nav_UserName.setText(username);
        Nav_Email.setText(email);
        /* end of set all user details on header view */
    }

    /* start of function for initialization of data for view pager */
    private void initData() {
        lstImages.add(new Dashboard("Assess", "Capture fingernail and identify possible diseases", R.drawable.dash_camera_lb200dp));
        lstImages.add(new Dashboard("History", "View the previous transaction", R.drawable.dash_history_yellow200dp));
        lstImages.add(new Dashboard("Disorders", "List the different fingernail disorders", R.drawable.dash_disorders_red200dp));
    }
    /* end of function for initialization of data for view pager */

    /* start of function for custom font on menu items */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/montserrat.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    /* end of function for custom font on menu items */

    /* start of override function for mobile phone back button */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
    /* end of override function for mobile phone back button */

    /* start of override function for selected item on navigation bar */
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
                this.finish();
                break;

            case R.id.nav_about:
                Intent iAbout = new Intent(DashboardActivity.this, AboutActivity.class);
                startActivity(iAbout);
                this.finish();
                break;

            case R.id.nav_logout:
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                profile_photo_loader.clearCache();
                historyPhotoLoader.clearCache();
                Intent iLogin = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(iLogin);
                DashboardActivity.this.finish();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* end of override function for selected item on navigation bar */
}