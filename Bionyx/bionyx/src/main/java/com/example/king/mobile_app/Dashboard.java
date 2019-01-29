package com.example.king.mobile_app;

public class Dashboard {

    private String selection;
    private int image;

    public Dashboard(){

    }

    public Dashboard(String selection, int image){
        this.selection = selection;
        this.image = image;
    }
    public String getSelection(){
        return selection;
    }
    public void setSelection(String selection){
        this.selection = selection;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
