package com.example.q.swipe_tab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Statistic_Adapter extends RecyclerView.Adapter<Statistic_Adapter.ViewHolder> {
    Context mContext;
    ArrayList<MainActivity.Event> events;

    private LayoutInflater mInflater;

    public Statistic_Adapter(Context context, ArrayList<MainActivity.Event> target){
        mContext = context;
        events = target;

        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.activity_main_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.Event target= events.get(position);

        holder.money.setText(String.valueOf(target.price));
        String explan = target.name + "(" + target.nickname + ")";
        holder.name.setText(explan);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, money;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            money = itemView.findViewById(R.id.money);
        }

    }
}
