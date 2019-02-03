package com.example.king.mobile_app;

public class Disorders {

    String disorder_title;
    String disorder_description;
    int disorder_image;

    public String getDisorder_title(){
        return disorder_title;
    }

    public void setDisorder_title(String disorder_title){
        this.disorder_title = disorder_title;
    }

    public String getDisorder_description(){
        return disorder_description;
    }

    public void setDisorder_description(String disorder_description){
        this.disorder_description = disorder_description;
    }

    public int getDisorder_image(){
        return disorder_image;
    }

    public void setDisorder_image(int disorder_image){
        this.disorder_image = disorder_image;
    }
}
