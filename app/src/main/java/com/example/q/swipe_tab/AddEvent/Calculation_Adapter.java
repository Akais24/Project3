package com.example.q.swipe_tab.AddEvent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.q.swipe_tab.R;

import java.util.ArrayList;

public class Calculation_Adapter extends RecyclerView.Adapter<Calculation_Adapter.ViewHolder>{
    final int ARITH = 1000;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        User target = debters.get(position);
        String expression = target.name + "(" + target.nickname + ")";
        holder.name_view.setText(expression);

        holder.calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent arith = new Intent(mContext, ArithmeticActivity.class);
                arith.putExtra("index", position);
                if(holder.money_view.getText().length() > 0)
                    arith.putExtra("origin", holder.money_view.getText().toString());

                ((Activity) mContext).startActivityForResult(arith, ARITH);
            }
        });
    }

    @Override
    public int getItemCount() {
        return debters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name_view;
        EditText money_view;
        FloatingActionButton calc;

        ViewHolder(View itemView) {
            super(itemView);
            name_view = itemView.findViewById(R.id.debtor_name);
            money_view = itemView.findViewById(R.id.money);
            calc = itemView.findViewById(R.id.calc_btn);
        }
    }
}
