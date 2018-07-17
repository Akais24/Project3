package com.example.q.swipe_tab;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MoreInfo_Adapter extends RecyclerView.Adapter<MoreInfo_Adapter.ViewHolder> {
    Context mContext;
    ArrayList<MainActivity.Event> events;
    int category;
    Activity origin;

    private LayoutInflater mInflater;
    Gson gson = new Gson();

    public MoreInfo_Adapter(Context context, ArrayList<MainActivity.Event> target, int c, Activity from){
        mContext = context;
        events = target;
        category = c;
        origin = from;

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

        holder.overall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(new ContextThemeWrapper(origin, R.style.myDialog))
                        .setMessage("이 채무 관계를 청산하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JsonObject id_object = new JsonObject();
                                id_object.addProperty("ID", target.ID);
                                Ion.with(mContext)
                                        .load("Post", "http://52.231.153.77:8080/" + "")
                                        .setJsonObjectBody(id_object)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                simple_response newone = gson.fromJson(result, simple_response.class);
                                                if(!newone.result.equals("Success")){
                                                    Toast.makeText(mContext, "청산에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(mContext, "성공적으로 청산했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
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
