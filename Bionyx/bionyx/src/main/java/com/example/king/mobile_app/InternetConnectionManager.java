package com.example.king.mobile_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class InternetConnectionManager {

    public static boolean isNetworkAvailable(Context context){

        boolean hasInternet = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                hasInternet = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                hasInternet = true;
            }
        } else {
            hasInternet = false;
            Toast.makeText(context,
                    "No internet connection", Toast.LENGTH_SHORT)
                    .show();
        }
        return hasInternet;
    }
}
