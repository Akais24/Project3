package com.example.q.swipe_tab.AddEvent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.q.swipe_tab.R;

import java.util.ArrayList;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder>{
    Context mContext;
    ArrayList<User> debters;
    SearchUserActivity origin;

    private LayoutInflater mInflater;

    public Search_Adapter(Context context, ArrayList<User> targets, SearchUserActivity originactivity){
        mContext = context;
        debters = targets;
        origin = originactivity;

        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.activity_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User target = debters.get(position);
        String expresssion = target.name + "(" + target.nickname + ")";

        holder.name_view.setText(expresssion);
        holder.name_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                origin.selectItem(target);
            }
        });
    }

    @Override
    public int getItemCount() {
        return debters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name_view;

        ViewHolder(View itemView) {
            super(itemView);
            name_view = itemView.findViewById(R.id.name_alias);
        }
    }
}
