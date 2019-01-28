package com.example.king.mobile_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;


import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity implements AsyncResponse_Login{

    //Declare all variables needed

    //Message for MainActivity
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    //Authentication Stuff
    private static final String AUTH_TOKEN_URL = "http://"+currentIp+"/api/authenticate/";
    private UserLoginTask mAuthTask = null;

    // UI references
    private InternetConnectionManager ICM;
    public EditText Username, Password;
    private TextView Register;
    private Button Login;
    private View LoginForm, ProgressView;
//    private int counter = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                String password = sharedPreferences.getString("password", "");
                if(username!="" && password!=""){

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                }
            }
        },0);

        //Assign the declared variables to edit text, text view and button layout
        //Set up the log in form
        Username = (EditText)findViewById(R.id.etUsername);
        Password = (EditText)findViewById(R.id.etPassword);
        Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.btnLogin || actionId == EditorInfo.IME_NULL){
                    startLogin();
                    return true;
                }
                return false;
            }
        });

        ICM = new InternetConnectionManager();
        Login = (Button)findViewById(R.id.btnLogin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
        Register = (TextView) findViewById(R.id.tvRegister);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        LoginForm = findViewById(R.id.login_form);
        ProgressView = findViewById(R.id.login_progress);



    }

    @Override
    public void onBackPressed() {

    }

    private void register(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean isPasswordValid(String password){

        return password.length() >= 8;
    }

    private void startLogin(){

        //If user is not very patient
        if(mAuthTask != null){
            return;
        }

        //Reset errors.
        Username.setError(null);
        Password.setError(null);

        //Store the values at the time of the button login clicked.
        String username = Username.getText().toString();
        String password = Password.getText().toString();

        //For focusing certain view and cancellation of process
        boolean cancel = false;
        View focusView = null;

        //Check for valid password, password cannot be blank.
        if(TextUtils.isEmpty(password)){
            Password.setError("This field cannot be blank");
            focusView = Password;
            cancel = true;
        }
        //Check for valid username, username cannot be blank.
        if(TextUtils.isEmpty(username)){
            Username.setError("This field cannot be blank");
            focusView = Username;
            cancel = true;
        }
        if(cancel){
            //If there was an error; don't attempt login and focus the first
            //form field that commit error
            focusView.requestFocus();
        } else {

            //Show a progress spinner, and kick off a background task
            //to perform the user login attempt
            if(ICM.isNetworkAvailable(this)) {
                showProgress(true);
                mAuthTask = new UserLoginTask(username, password, this);
                mAuthTask.execute((Void) null);
            }

        }
    }

    @Override
    public void processFinish(String token, String id, String first_name, String last_name, String username, String email, String date_joined){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("First Name", first_name);
        intent.putExtra("Last Name", last_name);
        intent.putExtra("Username", username);
        intent.putExtra("Email", email);
        intent.putExtra("Date Joined", date_joined);
        intent.putExtra("Token", token);
        intent.putExtra("Id", id);
        startActivity(intent);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            LoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            LoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            ProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            LoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Represents an asynchronous login task used to authenticate the user.

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        //Declare all variables needed for Asynchronous Task

        //Credentials
        private final String sUserName;
        private final String sPassword;

        //Check for successful authentication
        private Boolean success = false;

        //Delegate the process
        public AsyncResponse_Login delegate = null;

        UserLoginTask(String sUserName, String sPassword, AsyncResponse_Login delegate) {

            //Fetch the variable from startlogin() function
            this.sUserName = sUserName;
            this.sPassword = sPassword;
            this.delegate = delegate;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences here
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        }


        //Do while the Asynctask is running...
        @Override
        protected String doInBackground(Void... params) {
            try {
                return getToken(this.sUserName, this.sPassword);

            } catch (Exception e) {
                return "Caught some freaking exception";
            }
        }

        //Getting the user token
        protected String getToken(String username, String password) {
            JSONfunctions parser = new JSONfunctions();
            JSONObject login = parser.getLoginObject(username, password);

            String message = login.toString();
            InputStream is = null;

            //Display only the first 500 characters retrieved web page content
            int len = 500;
            try {
                //Connecting to django rest api......
                URL url = new URL(AUTH_TOKEN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //Connection is now open....
                Log.d("LoginActivity", "openConnection");

                //Set the read and connect timeout
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                //POST Method
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.d("LoginActivity", "Set up data unrelated headers");

                conn.setFixedLengthStreamingMode(message.getBytes().length);

                //Header.... -> JSON
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

                //Setup the output streaming
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(message.getBytes());
                os.flush();

                conn.connect();

                Log.d("LoginActivity", "Data is sent to API");

                //do something with response
                is = conn.getInputStream();
                //Convert InputStream into a String
                String contentAsString = readIt(is, len);

                //Make sure that the InputStream is closed after the app
                //finished using it
                if (is != null) {
                    is.close();
                }

                String serverResponseMessage = conn.getResponseMessage();
                int serverResponseCode = conn.getResponseCode();

                if (serverResponseCode == 200) {
                    this.success = true;
                } else {
                    Log.d("LoginActivity", serverResponseMessage + " " + serverResponseCode);
                }

                Log.d("LoginActivity", contentAsString);
                return contentAsString;

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("LoginActivity", "Exception");
                return "";
            }

        }

        public String readIt(InputStream stream, int len)throws IOException{
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String response)
        {
            mAuthTask = null;
            showProgress(false);
            System.out.println("Response for on post Execute "+response);
            if(this.success){
                String token = JSONfunctions.parseAuthToken(response);
                String id = JSONfunctions.parseAuthId(response);
                String first_name = JSONfunctions.parseAuthFirstName(response);
                String last_name = JSONfunctions.parseAuthLastName(response);
                String username = JSONfunctions.parseAuthUsername(response);
                String email = JSONfunctions.parseAuthEmail(response);
                String date_joined = JSONfunctions.parseAuthDateJoined(response);
                String processed_images = JSONfunctions.parseAuthProcessedImages(response);


                if(token.length()>2){

                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", sUserName);
                    editor.putString("password", sPassword);
                    editor.putString("first_name", first_name);
                    editor.putString("last_name", last_name);
                    editor.putString("email", email);
                    editor.putString("date_joined", date_joined);
                    editor.putString("id", id);
                    editor.putString("token", token);
                    editor.putString("processed_images", processed_images);
                    editor.apply();
                    this.delegate.processFinish(token, id, first_name, last_name, username, email, date_joined);
                }
            }else {
                Log.d("LoginActivity", response);
                Password.setError("Incorrect Password");
                Password.requestFocus();
            }
        }

        @Override
        protected void onCancelled(){
            mAuthTask = null;
            showProgress(false);
        }


    }

}