package com.example.q.swipe_tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MoreInfo_Adapter extends RecyclerView.Adapter<MoreInfo_Adapter.ViewHolder> {
    Context mContext;
    ArrayList<MainActivity.Event> events;
    int category;

    private LayoutInflater mInflater;

    public MoreInfo_Adapter(Context context, ArrayList<MainActivity.Event> target, int c){
        mContext = context;
        events = target;
        category = c;

        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.activity_more_info_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MainActivity.Event target= events.get(position);

        holder.id.setText(String.valueOf(target.ID));
        holder.date.setText(target.date);
        holder.name.setText(target.name + "(" + target.nickname + ")");
        holder.price.setText(String.valueOf(target.price));

        holder.overall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(mContext, MoreInfo_DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("target", target);
                detail.putExtras(bundle);
                detail.putExtra("category", category);

                mContext.startActivity(detail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View overall;
        TextView id, date, name, price;

        ViewHolder(View itemView) {
            super(itemView);
            overall = itemView.findViewById(R.id.overall);
            id = itemView.findViewById(R.id.id_text);
            date = itemView.findViewById(R.id.date_text);
            name = itemView.findViewById(R.id.name_text);
            price = itemView.findViewById(R.id.price_text);
        }

    }
}
