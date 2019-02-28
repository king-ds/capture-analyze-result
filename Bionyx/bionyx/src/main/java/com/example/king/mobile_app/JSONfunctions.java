package com.example.king.mobile_app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


public class JSONfunctions {

    private static final String ERROR_TAG = "E_JSONGfunctions";
    private static final String DEBUG_TAG = "D_JSONGfunctions";

    public static JSONObject getRegistrationObject(String first_name, String last_name, String username, String email, String password){
        try{
            JSONObject temp = new JSONObject();
            temp.put("first_name", first_name);
            temp.put("last_name", last_name);
            temp.put("username", username);
            temp.put("email", email);
            temp.put("password", password);
            Log.d(DEBUG_TAG, "JSON Registration Object Created");
            return temp;
        }catch(JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with JSON data creation");
        }
        return null;
    }

    public static JSONObject getUpdatedUserInformationObject(String first_name, String last_name, String username, String email){

        try{
            JSONObject temp = new JSONObject();
            temp.put("first_name", first_name);
            temp.put("last_name", last_name);
            temp.put("username", username);
            temp.put("email", email);

            Log.d(DEBUG_TAG, "JSON Updated User Information Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getBeauAnswerObject(boolean isAnswered, String answer_1, String answer_2, String answer_3, String answer_4, String answer_5,
                                                    String answer_6, String answer_7, String answer_8){

        try{
            JSONObject temp = new JSONObject();
            temp.put("isAnswered", isAnswered);
            temp.put("answer_1", answer_1);
            temp.put("answer_2", answer_2);
            temp.put("answer_3", answer_3);
            temp.put("answer_4", answer_4);
            temp.put("answer_5", answer_5);
            temp.put("answer_6", answer_6);
            temp.put("answer_7", answer_7);
            temp.put("answer_8", answer_8);

            Log.d(DEBUG_TAG, "JSON Beau Lines Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getCLubAnswerObject(boolean isAnswered, String answer_1, String answer_2, String answer_3, String answer_4, String answer_5,
                                                 String answer_6, String answer_7){

        try{
            JSONObject temp = new JSONObject();
            temp.put("isAnswered", isAnswered);
            temp.put("answer_1", answer_1);
            temp.put("answer_2", answer_2);
            temp.put("answer_3", answer_3);
            temp.put("answer_4", answer_4);
            temp.put("answer_5", answer_5);
            temp.put("answer_6", answer_6);
            temp.put("answer_7", answer_7);

            Log.d(DEBUG_TAG, "JSON Club Nails Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getSpoonAnswerObject(boolean isAnswered, String answer_1, String answer_2, String answer_3, String answer_4, String answer_5,
                                                 String answer_6, String answer_7, String answer_8){

        try{
            JSONObject temp = new JSONObject();
            temp.put("isAnswered", isAnswered);
            temp.put("answer_1", answer_1);
            temp.put("answer_2", answer_2);
            temp.put("answer_3", answer_3);
            temp.put("answer_4", answer_4);
            temp.put("answer_5", answer_5);
            temp.put("answer_6", answer_6);
            temp.put("answer_7", answer_7);
            temp.put("answer_8", answer_8);

            Log.d(DEBUG_TAG, "JSON Spoon Nails Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getTerryAnswerObject(boolean isAnswered, String answer_1, String answer_2, String answer_3, String answer_4, String answer_5,
                                                  String answer_6, String answer_7){

        try{
            JSONObject temp = new JSONObject();
            temp.put("isAnswered", isAnswered);
            temp.put("answer_1", answer_1);
            temp.put("answer_2", answer_2);
            temp.put("answer_3", answer_3);
            temp.put("answer_4", answer_4);
            temp.put("answer_5", answer_5);
            temp.put("answer_6", answer_6);
            temp.put("answer_7", answer_7);

            Log.d(DEBUG_TAG, "JSON Terrys Nails Answer Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getYellowAnswerObject(boolean isAnswered, String answer_1, String answer_2, String answer_3, String answer_4, String answer_5,
                                                  String answer_6, String answer_7, String answer_8, String answer_9){

        try{
            JSONObject temp = new JSONObject();
            temp.put("isAnswered", isAnswered);
            temp.put("answer_1", answer_1);
            temp.put("answer_2", answer_2);
            temp.put("answer_3", answer_3);
            temp.put("answer_4", answer_4);
            temp.put("answer_5", answer_5);
            temp.put("answer_6", answer_6);
            temp.put("answer_7", answer_7);
            temp.put("answer_8", answer_8);
            temp.put("answer_9", answer_9);

            Log.d(DEBUG_TAG, "JSON Yellow Nails Answer Object Created");
            return temp;
        }catch (JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with data creation");
        }
        return null;
    }

    public static JSONObject getLoginObject(String username, String password){
        try{
            JSONObject temp = new JSONObject();
            temp.put("username", username);
            temp.put("password", password);
            Log.d(DEBUG_TAG, "JSON Login Object Created");
            return temp;

        }catch(JSONException ex){
            Log.e(ERROR_TAG, "Something went wrong with JSON data creation");
        }
        return null;
    }

    public static String parseAuthToken(String response){
        try{
            JSONObject temp = new JSONObject(response);
            String token = "Token "+temp.getString("token");
            return token;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthId(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String id = temp.getString("id");
            return id;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthUsername(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String username = temp.getString("username");
            return username;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthFirstName(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String first_name = temp.getString("first_name");
            return first_name;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthLastName(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String last_name = temp.getString("last_name");
            return last_name;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthEmail(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String email = temp.getString("email");
            return email;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthDateJoined(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String date_joined = temp.getString("date_joined");
            return date_joined;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }

    public static String parseAuthProcessedImages(String response){
        try{

            JSONObject temp = new JSONObject(response);
            String processed_images = temp.getString("processed_images");
            return processed_images;
        }catch (JSONException ex){
            ex.printStackTrace();
            return "";
        }
    }
}
