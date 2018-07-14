package com.example.q.swipe_tab.AddEvent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.q.swipe_tab.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class CalculationActivity extends AppCompatActivity implements View.OnClickListener {
    android.support.v7.widget.Toolbar mytoolbar;
    RecyclerView calcul_rv;
    RelativeLayout next;
    Button divide;
    CheckBox checkbox;
    EditText total;

    Calculation_Adapter mAdapter;
    ArrayList<User> debters;
    String picked_date, raw_content, unique_id;

    LinearLayoutManager llm = new LinearLayoutManager(this);
    Gson gson = new Gson();

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        mytoolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        Intent add = getIntent();
        Bundle args= add.getBundleExtra("BUNDLE");
        debters = (ArrayList<User>) args.getSerializable("debters");
        picked_date = add.getStringExtra("date");
        raw_content = add.getStringExtra("content");

        Log.d("444444", String.valueOf(debters));
        Log.d("444444", String.valueOf(picked_date));
        Log.d("444444", String.valueOf(raw_content));

        calcul_rv = findViewById(R.id.calc_rv);
        next = findViewById(R.id.next);
        divide = findViewById(R.id.divide);
        checkbox = findViewById(R.id.checkbox);
        total = findViewById(R.id.total_amount);

        next.setOnClickListener(this);
        divide.setOnClickListener(this);

        llm.setOrientation(LinearLayoutManager.VERTICAL);
        calcul_rv.setLayoutManager(llm);
        mAdapter = new Calculation_Adapter(CalculationActivity.this, debters);
        calcul_rv.setAdapter(mAdapter);


        final SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        unique_id = test.getString("unique_id", null);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("업로드 중입니다");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(new ContextThemeWrapper(CalculationActivity.this, R.style.myDialog))
                .setMessage("이전 화면으로 돌아가시겠습니까?")
                .setNeutralButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .setCancelable(true).create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.divide:
                int number= debters.size();
                if(checkbox.isChecked()) number += 1;
                if(total.getText().length() <=0){
                    Toast.makeText(getApplicationContext(), "총 금액을 입력해주십시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                int each = Integer.parseInt(total.getText().toString()) / number;
                Log.d("4444444", String.valueOf(each));
                setmoney(each);
                break;
            case R.id.next:
                mProgressDialog.show();
                Log.d("444444", "nextbutton pressed " + String.valueOf(debters.size()));
                ArrayList<Integer> amounts = new ArrayList<>();
                for (int childCount = calcul_rv.getChildCount(), i = 0; i < childCount; ++i) {
                    final Calculation_Adapter.ViewHolder holder = (Calculation_Adapter.ViewHolder) calcul_rv.getChildViewHolder(calcul_rv.getChildAt(i));
                    if(holder.money_view.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "금액을 입력해주시길 바랍니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int raw_amount = Integer.parseInt(holder.money_view.getText().toString());
                    amounts.add(raw_amount);
                }

                final int[] count = {0};
                for(int i = 0; i<debters.size(); i++){
                    final User target = debters.get(i);
                    JsonObject json = new JsonObject();
                    json.addProperty("creditor", unique_id);
                    json.addProperty("debtor", target.unique_id);
                    if(amounts.get(i) > 0) json.addProperty("price", amounts.get(i));
                    json.addProperty("date", picked_date);
                    json.addProperty("info", raw_content);


                    Log.d("44444", "in loop");
                    Ion.with(getApplicationContext())
                            .load("POST", "http://52.231.153.77:8080/make_new_event")
                            .setJsonObjectBody(json)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    count[0]++;
                                    if(result == null){
                                        Toast.makeText(getApplicationContext(), getString(R.string.server_die), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    simple_response newone = gson.fromJson(result, simple_response.class);
                                    Log.d("4444", String.valueOf(result));
                                    if(!newone.result.equals("Success")){
                                        String message = target.name + "(" + target.nickname + ") : " + "업로드 실패";
                                        Log.d("44444", message);
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    }else{
                                        String message = target.name + "(" + target.nickname + ") : " + "업로드 성공";
                                        Log.d("44444", message);
                                    }

                                    if(count[0] == debters.size()){
                                        mProgressDialog.hide();
                                        AddActivity addActivity = (AddActivity) AddActivity.addactiviy;
                                        addActivity.finish();
                                        finish();
                                    }
                                }
                            });
                }
                break;
        }

    }

    public void setmoney(int amount){
        for (int childCount = calcul_rv.getChildCount(), i = 0; i < childCount; ++i) {
            final Calculation_Adapter.ViewHolder holder = (Calculation_Adapter.ViewHolder) calcul_rv.getChildViewHolder(calcul_rv.getChildAt(i));
            holder.money_view.setText(String.valueOf(amount));
        }
    }


}
