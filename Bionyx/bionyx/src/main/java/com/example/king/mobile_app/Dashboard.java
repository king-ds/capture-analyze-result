package com.example.king.mobile_app;

public class Dashboard {

    private String selection;
    private String description;
    private int image;

    public Dashboard(String selection, String description, int image){
        this.selection = selection;
        this.image = image;
        this.description = description;
    }
    public String getSelection(){
        return selection;
    }
    public void setSelection(String selection){
        this.selection = selection;
    }

    public String getDescription(){
        return description;
    }
    public String setDescription(){
        return description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
