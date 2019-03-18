package com.example.king.mobile_app;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DashboardAdapter extends PagerAdapter {

    List<Dashboard> lstImages;
    Context context;
    LayoutInflater layoutInflater;
    InternetConnectionManager ICM = new InternetConnectionManager();

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
        ImageView imageView = view.findViewById(R.id.ivButton);
        TextView textSelection = view.findViewById(R.id.tvSelection);
        TextView textDescription = view.findViewById(R.id.tvDescription);
        imageView.setImageResource(lstImages.get(position).getImage());
        textSelection.setText(lstImages.get(position).getSelection());
        textDescription.setText(lstImages.get(position).getDescription());
        AssetManager am = context.getApplicationContext().getAssets();
        Typeface custom_font = Typeface.createFromAsset(am, "fonts/montserrat.ttf");
        textSelection.setTypeface(custom_font);
        textDescription.setTypeface(custom_font);

        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String options = lstImages.get(position).getSelection();
                if (options.equals("Assess")) {
                    Intent assess_intent = new Intent(context, SelectionActivity.class);
                    assess_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(assess_intent);

                } else if (options.equals("History")) {
                    Intent history_intent = new Intent(context, HistoryActivity.class);
                    history_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(history_intent);

                } else if (options.equals("Disorders")) {
                    Intent disorders_intent = new Intent(context, DiseasesActivity.class);
                    disorders_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(disorders_intent);
                }
            }
        });
        return view;
    }
}
