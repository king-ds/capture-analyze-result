package com.example.king.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisordersFragment extends Fragment {

    ArrayList<Disorders> disorder_list = new ArrayList();
    RecyclerView DisorderRecyclerView;

    String disorders_title[] = {"Beau lines", "Nail clubbing", "Spoon nails", "Terry's nails", "Yellow nails"};
    String disorders_description[] = {"Beau lines are indentations that run accross the nails.",
            "Nail clubbing occurs when the tips of the fingers enlarge and the nails curve around fingertips",
            "Spoon nails are thin and soft and shaped like a little spoon that is often capable of holding a drop of water.",
            "Most of the nails appear white except for a narrow pink band at the tip",
            "Yellow nails results in a yellowish and thicken nails"};
    int disorder_image[] = {R.drawable.vector_beau, R.drawable.vector_clubbing, R.drawable.vector_spoon,
            R.drawable.vector_terry_s, R.drawable.vector_yellow};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.activity_diseases, container, false);
        DisorderRecyclerView = view.findViewById(R.id.disorders_recyclerview);
        DisorderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager DisorderLayoutManager = new LinearLayoutManager(getActivity());
        DisorderLayoutManager.setOrientation(DividerItemDecoration.VERTICAL);
        if(disorder_list.size()>0 & DisorderRecyclerView != null){
            DisorderRecyclerView.setAdapter(new DisordersAdapter(disorder_list));
        }
        DisorderRecyclerView.setLayoutManager(DisorderLayoutManager);
        return view;
    }

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
        public void onBindViewHolder(final DisorderViewHolder holder, final int position){
            holder.disorder_title_view.setText(disorders.get(position).getDisorder_title());
            holder.disorder_image_view.setImageResource(disorders.get(position).getDisorder_image());
            holder.disorder_description_view.setText(disorders.get(position).getDisorder_description());
            holder.disorder_view.setTag(R.drawable.ic_chevron_right);

            holder.disorder_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String options = disorder_list.get(position).getDisorder_title();
                    if(options.equals("Beau lines")){
                        Intent beau_intent = new Intent(getContext(), DisorderBeau.class);
                        getContext().startActivity(beau_intent);

                    } else if (options.equals("Nail clubbing")){
                        Intent club_intent = new Intent(getContext(), DisorderClubbed.class);
                        getContext().startActivity(club_intent);

                    } else if (options.equals("Spoon nails")){
                        Intent spoon_intent = new Intent(getContext(), DisorderSpoon.class);
                        getContext().startActivity(spoon_intent);

                    } else if (options.equals("Terry's nails")){
                        Intent terry_intent = new Intent(getContext(), DisorderTerry.class);
                        getContext().startActivity(terry_intent);

                    } else if (options.equals("Yellow nails")){
                        Intent yellow_intent = new Intent(getContext(), DisorderYellow.class);
                        getContext().startActivity(yellow_intent);
                    }
                }
            });

            holder.disorder_CHTRM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String options = disorder_list.get(position).getDisorder_title();
                    if(options.equals("Beau lines")){
                        Intent beau_intent = new Intent(getContext(), DisorderBeau.class);
                        getContext().startActivity(beau_intent);

                    } else if (options.equals("Nail clubbing")){
                        Intent club_intent = new Intent(getContext(), DisorderClubbed.class);
                        getContext().startActivity(club_intent);

                    } else if (options.equals("Spoon nails")){
                        Intent spoon_intent = new Intent(getContext(), DisorderSpoon.class);
                        getContext().startActivity(spoon_intent);

                    } else if (options.equals("Terry's nails")){
                        Intent terry_intent = new Intent(getContext(), DisorderTerry.class);
                        getContext().startActivity(terry_intent);

                    } else if (options.equals("Yellow nails")){
                        Intent yellow_intent = new Intent(getContext(), DisorderYellow.class);
                        getContext().startActivity(yellow_intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount(){
            return disorders.size();
        }
    }

    public class DisorderViewHolder extends RecyclerView.ViewHolder {

        public TextView disorder_title_view;
        public TextView disorder_description_view;
        public ImageView disorder_image_view;
        public ImageView disorder_view;
        public TextView disorder_CHTRM;

        public DisorderViewHolder(View v){

            super(v);

            disorder_title_view = v.findViewById(R.id.tvDisorderTitle);
            disorder_description_view = v.findViewById(R.id.tvDisorderDescription);
            disorder_image_view = v.findViewById(R.id.ivDisorder);
            disorder_view = v.findViewById(R.id.ivViewDisorder);
            disorder_CHTRM = v.findViewById(R.id.tvCHTRM);

        }
    }

    public void initData(){

        for(int i=0; i<5; i++){

            Disorders item = new Disorders();
            item.setDisorder_title(disorders_title[i]);
            item.setDisorder_description(disorders_description[i]);
            item.setDisorder_image(disorder_image[i]);
            disorder_list.add(item);
            System.out.println("Data set");

        }
    }
}
