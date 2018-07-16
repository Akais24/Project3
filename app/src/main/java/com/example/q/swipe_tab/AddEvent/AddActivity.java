package com.example.q.swipe_tab.AddEvent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    final int SEARCH_CODE = 1111;
    final int CONTENT_CODE = 2222;

    Toolbar mytoolbar;

    RecyclerView rv;
    TextView count, myname;
    RelativeLayout next;

    TextView date, content, add;

    ArrayList<User> real_debters = new ArrayList<>();

    Add_Adapter mAdapter;

    public static Activity addactiviy;

    int mYear, mMonth, mDay;
    String picked_date, raw_content, unique_id;

    LinearLayout limit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        addactiviy = AddActivity.this;

        mytoolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mytoolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        rv = findViewById(R.id.rv);
        count = findViewById(R.id.count_view);
        myname = findViewById(R.id.creditor_name);
        add = findViewById(R.id.add_btn);
        next = findViewById(R.id.next);
        date = findViewById(R.id.date_btn);
        content = findViewById(R.id.content_btn);

        add.setOnClickListener(this);
        next.setOnClickListener(this);
        date.setOnClickListener(this);
        content.setOnClickListener(this);

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        updateDate();

        SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        final String nickname = test.getString("nickname", "nonickname");
        final String realname = test.getString("name", "noname");
        unique_id = test.getString("unique_id", null);

        myname.setText(realname + "(" + nickname + ")");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        mAdapter = new Add_Adapter(AddActivity.this, real_debters, this);
        rv.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Log.d("444444", "ADD");

                Intent search = new Intent(AddActivity.this, SearchUserActivity.class);
                startActivityForResult(search, SEARCH_CODE);
                break;
            case R.id.next:
                if(real_debters.size() == 0){
                    Toast.makeText(getApplicationContext(), "혼자면 아무것도 못해요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(raw_content == null || raw_content.equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주시길 바랍니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent calcul = new Intent(AddActivity.this, CalculationActivity.class);

                Bundle args = new Bundle();
                Log.d("444444", String.valueOf(real_debters));
                args.putSerializable("debters", (Serializable) real_debters);
                calcul.putExtra("BUNDLE", args);
                calcul.putExtra("date", picked_date);
                calcul.putExtra("content", raw_content);

                startActivity(calcul);
                break;
            case R.id.date_btn:
                new DatePickerDialog(AddActivity.this, R.style.myDialog, mDateSetListener, mYear, mMonth, mDay).show();
                break;
            case R.id.content_btn:
                Intent content = new Intent(AddActivity.this, ContentInputActivity.class);
                content.putExtra("previous", raw_content);
                startActivityForResult(content, CONTENT_CODE);
                break;
        }
    }

    public void removeDebter(int position){
        //debters.remove(position);
        count.setText("(총 " + String.valueOf(real_debters.size() + 1) + "명)");
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
        new AlertDialog.Builder(new ContextThemeWrapper(AddActivity.this, R.style.myDialog))
                .setMessage("메인화면으로 돌아가시겠습니까?")
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case SEARCH_CODE:
                if(resultCode == Activity.RESULT_OK){
                    User newuser = (User) data.getSerializableExtra("select");
                    if(newuser.unique_id == unique_id){
                        Toast.makeText(getApplicationContext(), "자기 자신을 추가할 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for(int i=0; i<real_debters.size(); i++){
                        if(compareUser(real_debters.get(i), newuser)){
                            Toast.makeText(getApplicationContext(), "이미 채무자 목록에 들어가있는 유저입니다", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    real_debters.add(newuser);

                    count.setText("(총 " + String.valueOf(real_debters.size() + 1) + "명)");
                    mAdapter = new Add_Adapter(AddActivity.this, real_debters, this);
                    rv.setAdapter(mAdapter);
                }
                break;
            case CONTENT_CODE:
                if(resultCode == Activity.RESULT_OK){
                    raw_content = data.getStringExtra("content");
                    Log.d("4444 result ", raw_content);
                    if(raw_content == null || raw_content.equals("")) content.setText("내용 없음");
                    content.setText(raw_content);
                }
                break;
        }
    }

    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDate();
                }
            };


    public void updateDate(){
        picked_date = String.format("%d/%d/%d", mYear, mMonth + 1, mDay);
        date.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));
    }

    public boolean compareUser(User one, User two){
        return one.unique_id.equals(two.unique_id);
    }
}
