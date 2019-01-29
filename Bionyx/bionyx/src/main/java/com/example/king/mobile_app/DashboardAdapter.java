package com.example.king.mobile_app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;

public class DashboardAdapter extends PagerAdapter {

    List<Dashboard> lstImages;
    Context context;
    LayoutInflater layoutInflater;

    public DashboardAdapter(List<Dashboard> lstImages, Context context){
        this.lstImages = lstImages;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lstImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((View)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        View view = layoutInflater.inflate(R.layout.card_item_dashboard, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.ivButton);
        imageView.setImageResource(lstImages.get(position).getImage());
        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options = lstImages.get(position).getSelection();
                if (options.equals("A")) {

                    Intent assess_intent = new Intent(context, NailAssessmentActivity.class);
                    assess_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(assess_intent);
                } else if (options.equals("H")) {

                    Intent history_intent = new Intent(context, HistoryActivity.class);
                    history_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(history_intent);
                } else if (options.equals("D")) {

                    Intent disorders_intent = new Intent(context, DiseasesActivity.class);
                    disorders_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(disorders_intent);
                }
            }
        });
        return view;
    }
}
