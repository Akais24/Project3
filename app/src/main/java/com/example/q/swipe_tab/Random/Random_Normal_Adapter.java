package com.example.q.swipe_tab.Random;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.q.swipe_tab.R;

import java.util.ArrayList;

public class Random_Normal_Adapter extends RecyclerView.Adapter<Random_Normal_Adapter.ViewHolder> {
    Context mContext;
    ArrayList<String> targets;
    private LayoutInflater mInflater;

    public Random_Normal_Adapter(Context context, ArrayList<String> inputs){
        mContext = context;
        targets = inputs;

        mInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.activity_random_normal_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name_view.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return targets.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        EditText name_view;

        ViewHolder(View itemView) {
            super(itemView);
            name_view = itemView.findViewById(R.id.name_view);
        }
    }
}
