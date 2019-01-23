package com.example.king.mobile_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;


public class RegisterActivity extends BaseActivity implements AsyncResponse {

    //Declare all variables needed.
    private InternetConnectionManager ICM;

    //For Authentication Related Stuff
    private static final String TOKEN_URL = "http://"+currentIp+"/api/register/";
    private static final String Success_Message = "Successfully Register";
    private static final String Failures_Message = "Check your internet connection";
    private UserRegisterTask mAuthTask = null;

    //For Registration Field
    private EditText r_username, first_name, last_name, r_email, password, confirm_password;
    private Button Register;

    //For Views
    private static ProgressDialog mProgressDialog;
    private View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Assign the declared variables.
        ICM = new InternetConnectionManager();
        r_username = (EditText)findViewById(R.id.etUsername);
        password = (EditText)findViewById(R.id.etPassword);
        confirm_password = (EditText)findViewById(R.id.etConfirm_Password);
        first_name = (EditText)findViewById(R.id.etFirstName);
        last_name = (EditText)findViewById(R.id.etLastName);
        r_email = (EditText)findViewById(R.id.etEmail);
        Register = (Button)findViewById(R.id.btnRegister);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateRegister();

            }
        });
    }

    private void initiateRegister() {

        boolean cancel = false;
        if(mAuthTask != null){
            return;
        }
        //Reset Errors.
        r_username.setError(null);
        password.setError(null);
        first_name.setError(null);
        last_name.setError(null);
        r_email.setError(null);
        confirm_password.setError(null);

        String Username = r_username.getText().toString();
        String Password = password.getText().toString();
        String Confirm_Password = confirm_password.getText().toString();
        String Firstname = first_name.getText().toString();
        String Lastname = last_name.getText().toString();
        String Email = r_email.getText().toString();

        if (TextUtils.isEmpty(Firstname)) {
            first_name.setError("This field cannot be blank");
            focusView = first_name;
            cancel = true;
        }
        else if (TextUtils.isEmpty(Lastname)) {
            last_name.setError("This field cannot be blank");
            focusView = last_name;
            cancel = true;
        }
        else if (TextUtils.isEmpty(Email)) {
            r_email.setError("This field cannot be blank");
            focusView = r_email;
            cancel = true;
        }
        else if (TextUtils.isEmpty(Username)) {
            r_username.setError("This field cannot be blank");
            focusView = r_username;
            cancel = true;
        }
        else if (TextUtils.isEmpty(Password)){
            password.setError("This field cannot be blank");
            focusView = password;
            cancel = true;
        }
        else if (TextUtils.isEmpty(Confirm_Password)) {
            confirm_password.setError("This field cannot be blank");
            focusView = confirm_password;
            cancel = true;
        }
        else if(!isPasswordMatch(Password, Confirm_Password)){
            password.setError("Password does not match");
            confirm_password.setError("Password does not match");
            focusView = confirm_password;
            cancel = true;
        }
        else if (isPasswordSameWithUsername(Password, Username)){
            password.setError("Password should not contain username");
            focusView = password;
            cancel = true;
        }
        else if (!isPasswordHasMixUpperLowerNumber(Password)){
            password.setError("Your password must be at least 8 characters long, contain at least one number and have a mixture of uppercase and lowercase letters");
            focusView = password;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if(ICM.isNetworkAvailable(this)){
                mAuthTask = new UserRegisterTask(Username, Password, Firstname, Lastname, Email, this);
                mAuthTask.execute((Void) null);
            }
        }
    }


    private boolean isPasswordMatch(String password, String confirm_password){
        return password.equals(confirm_password);
    }

    private boolean isPasswordSameWithUsername(String password, String username){
        return password.equals(username);
    }

    private boolean isPasswordHasMixUpperLowerNumber(String password) {

        char ch;
        boolean uppercaseFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        boolean lengthFlag = false;
        boolean success = false;

        if (password.length()> 8){
            lengthFlag = true;
        }

        for (int i = 0; i < password.length(); i++) {

            ch = password.charAt(i);
            if (Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                uppercaseFlag = true;
            }
            else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if (numberFlag && uppercaseFlag && lowerCaseFlag && lengthFlag) {
                success = true;
            }
        }
        return success;
    }

    public void processFinish(String response){
        if(response == Success_Message){
            Toast.makeText(RegisterActivity.this, Success_Message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
        }else{
            Toast.makeText(RegisterActivity.this, Failures_Message, Toast.LENGTH_SHORT).show();
            RegisterActivity.this.finish();

        }
    }

    //Asynchronous Task
    public class UserRegisterTask extends AsyncTask<Void, Void, String>{
        //Exception handling
        private String username_handling, email_handling;
        private final String sUsername, sPassword, sFirstName, sLastName, sEmail;
        private Boolean success = false;
        public AsyncResponse delegate = null;

        UserRegisterTask(String sUsername, String sPassword, String sFirstName, String sLastName, String sEmail, AsyncResponse delegate){
            this.sUsername = sUsername;
            this.sPassword = sPassword;
            this.sFirstName = sFirstName;
            this.sLastName = sLastName;
            this.sEmail = sEmail;
            this.delegate = delegate;
        }

        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        protected String getToken(String first_name, String last_name, String username, String password, String email){
            JSONfunctions parser = new JSONfunctions();
            JSONObject register = parser.getRegistrationObject(first_name, last_name, username, password, email);
            String message = register.toString();
//            InputStream is = null;
            //display only the first 500 characters retrieved
            int len = 500;
            try{
                URL url = new URL(TOKEN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.d("RegisterActivity", "url.openConnection");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Log.d("RegisterAtivity", "Set up data unrelated headers");
                conn.setFixedLengthStreamingMode(message.getBytes().length);
                //header crap
                conn.setRequestProperty("Content-Type", "application/json");

                //Setup sen
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                System.out.println(os);
                os.write(message.getBytes());
                os.flush();
                os.close();

                conn.connect();

                Log.d("RegisterActivity", "Data is sent");

                String serverResponseMessage = conn.getResponseMessage();
                int serverResponseCode = conn.getResponseCode();
                if(serverResponseCode == 201){
                    this.success = true;
                }

                else if(serverResponseCode == 400){
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while((output = br.readLine()) != null){
                        sb.append(output);
                    }
                    String response = sb.toString();
                    System.out.println(response);

                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.getString("message");
                        if(error.equals("Username is already used")){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    r_username.setError("Username is already used");
                                    focusView = r_username;
                                }
                            });

                        }else if(error.equals("Email is already used")){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    r_email.setError("Email is already used");
                                    focusView = r_email;
                                }
                            });
                        }else{
                            System.out.println("Something went wrong");
                        }
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }


                }else{
                    Log.d("RegisterActivity", serverResponseMessage +" "+ serverResponseCode);
                }

                return serverResponseMessage;

            }catch (Exception ex){
                ex.printStackTrace();
                Log.e("RegisterActivity", "Exeption");
                return "";
            }
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Create process dialog
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            //Set Progress dialog title
            mProgressDialog.setTitle("Creating an account");
            //Set progress dialog message
            mProgressDialog.setMessage("Loading...");
            //Set cancelable false
            mProgressDialog.setCancelable(false);
            //Set canceled on touch outside false
            mProgressDialog.setCanceledOnTouchOutside(false);
            //Set indeterminate false
            mProgressDialog.setIndeterminate(false);
            //Show progress dialog
            mProgressDialog.show();

        }
        @Override
        protected String doInBackground(Void... params){

            try{
                return getToken(this.sFirstName, this.sLastName, this.sUsername, this.sEmail, this.sPassword);
            }catch (Exception ex) {
                ex.printStackTrace();
                Log.e("RegisterActivity", "Exception");
                return "";
            }

        }
        @Override
        protected void onPostExecute(String response) {
            mAuthTask = null;
            //showProgress(false);
            mProgressDialog.dismiss();
            if (this.success) {
                delegate.processFinish(Success_Message);
            }
            else{
                Log.d("RegisterActivity", response);
                delegate.processFinish(Failures_Message);
            }

        }
        @Override
        protected void onCancelled(){
            mAuthTask = null;
            mProgressDialog.dismiss();
        }
    }


}