package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* start of assigning toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* end of assigning toolbar */

        /* start of importing drawer layout */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /* end of importing drawer layout */

        /* start of side bar */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu m = navigationView.getMenu();

        /* apply the montserrat font */
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
        /* end of side bar*/

        historyPhotoLoader = new HistoryPhotoLoader(this);
        profile_photo_loader = new ProfilePhotoLoader(this);

        /* start of shared preferences for user details */
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String username = prefs.getString("username","");
        String email = prefs.getString("email", "");
        String AVATAR_URL = prefs.getString("avatar_url", "");
        /* end of shared preferences for user details */

        /* start of header view and set all user details */
        View headerView = navigationView.getHeaderView(0);
        TextView Nav_UserName = headerView.findViewById(R.id.tv_Nav_UserName);
        TextView Nav_Email = headerView.findViewById(R.id.tv_Nav_Email);
        ImageView Nav_Avatar = headerView.findViewById(R.id.tv_Nav_Avatar);
        Nav_UserName.setText(username);
        Nav_Email.setText(email);
        profile_photo_loader.DisplayImage(AVATAR_URL, Nav_Avatar);
        /* end of header view and set all user details */

        /* start of about calura button and on click listener*/
        Button aboutCalura = findViewById(R.id.aboutCalura);
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
        /* end of about calura button and on click listener */

        /* start of about dabuet button and on click listener */
        Button aboutDabuet = findViewById(R.id.aboutDabuet);
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
        /* end of about dabuet button and on click listener */

        /* start of about regino button and on click listener */
        Button aboutRegino = findViewById(R.id.aboutRegino);
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
        /* end of about regino button and on click listener */

        /* start of about santiago button and on click listener */
        Button aboutSantiago = findViewById(R.id.aboutSantiago);
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
        /* end of about santiago button and on click listener */
    }

    /* start of function for applying custom font to menu item */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/montserrat.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    /* end of function for applying custom font to menu item */

    /* start of override function for back button listener */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent dashboard_intent = new Intent(AboutActivity.this, DashboardActivity.class);
            startActivity(dashboard_intent);
            AboutActivity.this.finish();
        }
    }
    /* end of override function for back button listener */

    /* start of function for on selected item in menu */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                Intent iHome = new Intent(AboutActivity.this, DashboardActivity.class);
                startActivity(iHome);
                AboutActivity.this.finish();
                break;

            case R.id.nav_profile:
                Intent iProfile = new Intent(AboutActivity.this, UserProfileActivity.class);
                startActivity(iProfile);
                AboutActivity.this.finish();
                break;

            case R.id.nav_about:
                break;

            case R.id.nav_logout:
                profile_photo_loader.clearCache();
                historyPhotoLoader.clearCache();
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                String user_log = "user_log";
                Intent iLogin = new Intent(AboutActivity.this, LoginActivity.class);
                iLogin.putExtra("user_loggedout", user_log);
                startActivity(iLogin);
                AboutActivity.this.finish();

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* end of function for on selected item in menu*/
}
