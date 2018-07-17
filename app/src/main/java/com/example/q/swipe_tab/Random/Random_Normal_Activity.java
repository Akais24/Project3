package com.example.q.swipe_tab.Random;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.q.swipe_tab.AddEvent.Calculation_Adapter;
import com.example.q.swipe_tab.R;

import java.util.ArrayList;
import java.util.Random;

public class Random_Normal_Activity extends AppCompatActivity implements View.OnClickListener {

    android.support.v7.widget.Toolbar mytoolbar;
    RecyclerView rn_rv;
    Random_Normal_Adapter mAdapter;

    EditText count_view;
    Button set, random;
    TextView lucky;

    ArrayList<String> members;

    Boolean blocking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_normal);

        mytoolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        rn_rv = findViewById(R.id.rn_rv);
        count_view = findViewById(R.id.count_space);
        set = findViewById(R.id.set_btn);
        random = findViewById(R.id.random_btn);
        lucky = findViewById(R.id.lucky_guy);

        set.setOnClickListener(this);
        random.setOnClickListener(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rn_rv.setLayoutManager(llm);
    }

    public void setrv(){
        if(count_view.getText().length() > 0 ){
            int numbers = Integer.parseInt(count_view.getText().toString());

            members = new ArrayList<>();
            for(int i=0; i<numbers; i++){
                members.add(String.valueOf(i));
            }

            mAdapter = new Random_Normal_Adapter(getApplicationContext(), members);
            rn_rv.setAdapter(mAdapter);
        }else{
            Toast.makeText(getApplicationContext(), "인원 수를 입력해주시길 바랍니다", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_btn:
                setrv();
                break;
            case R.id.random_btn:
                if(blocking){
                    new AlertDialog.Builder(this, R.style.myDialog)
                            .setMessage("정말 다시 하시겠습니까?")
                            .setNeutralButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getrandom();
                                }
                            })
                            .setPositiveButton("아니요", null)
                            .setCancelable(false).create().show();
                }else{
                    getrandom();
                }
                break;
        }
    }

    public void getrandom(){
        int index = new Random().nextInt(members.size());
        int count = 0;
        for (int childCount = rn_rv.getChildCount(), i = 0; i < childCount; ++i) {
            if(count == index) {
                final Random_Normal_Adapter.ViewHolder holder = (Random_Normal_Adapter.ViewHolder) rn_rv.getChildViewHolder(rn_rv.getChildAt(i));
                lucky.setText("행운의 주인공은 " + holder.name_view.getText().toString() + " 입니다~");
                break;
            }
            count ++;
        }
        blocking = true;
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
}
