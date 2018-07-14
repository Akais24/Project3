package com.example.q.swipe_tab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Calculation_Adapter extends RecyclerView.Adapter<Calculation_Adapter.ViewHolder>{
    Context mContext;
    ArrayList<User> debters;

    private LayoutInflater mInflater;

    public Calculation_Adapter(Context context, ArrayList<User> target){
        mContext = context;
        debters = target;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_calculation_item, parent, false);
        return new Calculation_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User target = debters.get(position);
        String expression = target.name + "(" + target.nickname + ")";
        holder.name_view.setText(expression);
    }

    @Override
    public int getItemCount() {
        return debters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_view;
        EditText money_view;

        ViewHolder(View itemView) {
            super(itemView);
            name_view = itemView.findViewById(R.id.debtor_name);
            money_view = itemView.findViewById(R.id.money);
        }
    }
}
