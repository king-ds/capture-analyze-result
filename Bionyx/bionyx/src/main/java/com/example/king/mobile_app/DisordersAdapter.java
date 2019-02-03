package com.example.king.mobile_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DisordersAdapter extends RecyclerView.Adapter<DisorderViewHolder> {
    private ArrayList<Disorders> disorders;

    public DisordersAdapter(ArrayList<Disorders> Data){
        disorders = Data;
    }

    @Override
    public DisorderViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disorder_recycle_items, parent, false);
        DisorderViewHolder holder = new DisorderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DisorderViewHolder holder, int position){
        holder.disorder_title_view.setText(disorders.get(position).getDisorder_title());
        holder.disorder_image_view.setImageResource(disorders.get(position).getDisorder_image());
        holder.disorder_description_view.setText(disorders.get(position).getDisorder_description());
        holder.disorder_view.setTag(R.drawable.ic_chevron_right);
    }

    @Override
    public int getItemCount(){
        return disorders.size();
    }
}
