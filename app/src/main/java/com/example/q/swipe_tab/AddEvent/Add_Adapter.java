package com.example.q.swipe_tab.AddEvent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.q.swipe_tab.R;

import java.util.ArrayList;

public class Add_Adapter extends RecyclerView.Adapter<Add_Adapter.ViewHolder>{
    Context mContext;
    ArrayList<User> debters;
    AddActivity origin;

    private LayoutInflater mInflater;

    public Add_Adapter(Context context, ArrayList<User> target, AddActivity originactivity){
        mContext = context;
        debters = target;
        origin = originactivity;

        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_add_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User owner = debters.get(position);
        holder.name_view.setText(owner.name + "(" + owner.nickname + ")");

        holder.name_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.myDialog))
                        .setMessage("이 채무자를 삭제하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                debters.remove(position);
                                notifyDataSetChanged();
                                origin.removeDebter(position);
                            }
                        })
                        .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //finish();
                            }
                        })
                        .setCancelable(true).create().show();
                return false;
            }
        });
    }

    public void refresh(){
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return debters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_view;

        ViewHolder(View itemView) {
            super(itemView);
            name_view = itemView.findViewById(R.id.debtor_name);
        }
    }
}
