package com.example.king.sample_api;

public interface AsyncResponse_Login {
    void processFinish(String token, String id, String first_name, String last_name, String email, String username, String date_joined);
}
