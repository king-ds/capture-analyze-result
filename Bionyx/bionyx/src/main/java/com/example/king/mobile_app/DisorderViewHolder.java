package com.example.king.mobile_app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisorderViewHolder extends RecyclerView.ViewHolder {

    public TextView disorder_title_view;
    public TextView disorder_description_view;
    public ImageView disorder_image_view;
    public ImageView disorder_view;

    public DisorderViewHolder(View v){

        super(v);

        disorder_title_view = v.findViewById(R.id.tvDisorderTitle);
        disorder_description_view = v.findViewById(R.id.tvDisorderDescription);
        disorder_image_view = v.findViewById(R.id.ivDisorder);
        disorder_view = v.findViewById(R.id.ivViewDisorder);

    }
}
