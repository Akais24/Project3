package com.example.q.swipe_tab.AddEvent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Arithmetic_plus_Activity extends AppCompatActivity implements View.OnClickListener {
    int index;
    String origin;

    EditText result_view;
    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btn00, btn000;
    Button btn_remove, btn_reset, btn_plus, btn_multi, btn_divide, btn_minus, btn_enter;

    Boolean calculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_arithmetic_plus);

        setFinishOnTouchOutside(false);

        Intent from = getIntent();
        index = from.getIntExtra("index", 0);
        origin = from.getStringExtra("origin");

        result_view = findViewById(R.id.result);
        result_view.setText(origin);
        result_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);
        btn0 = findViewById(R.id.btn_0);
        btn00 = findViewById(R.id.btn_00);
        btn000 = findViewById(R.id.btn_000);
        btn_remove = findViewById(R.id.btn_remove);
        btn_reset = findViewById(R.id.btn_reset);
        btn_multi = findViewById(R.id.btn_multi);
        btn_divide = findViewById(R.id.btn_divide);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        btn_enter = findViewById(R.id.btn_enter);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btn00.setOnClickListener(this);
        btn000.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_multi.setOnClickListener(this);
        btn_divide.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_enter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        btn_enter.setText("계산");
        switch (v.getId()){
            case R.id.btn_1:
                calculated = false;
                insertText(result_view, "1");
                 break;
            case R.id.btn_2:
                calculated = false;
                insertText(result_view, "2");
                break;
            case R.id.btn_3:
                calculated = false;
                insertText(result_view, "3");
                break;
            case R.id.btn_4:
                calculated = false;
                insertText(result_view, "4");
                break;
            case R.id.btn_5:
                calculated = false;
                insertText(result_view, "5");
                break;
            case R.id.btn_6:
                calculated = false;
                insertText(result_view, "6");
                break;
            case R.id.btn_7:
                calculated = false;
                insertText(result_view, "7");
                break;
            case R.id.btn_8:
                calculated = false;
                insertText(result_view, "8");
                break;
            case R.id.btn_9:
                calculated = false;
                insertText(result_view, "9");
                break;
            case R.id.btn_0:
                calculated = false;
                insertText(result_view, "0");
                break;
            case R.id.btn_00:
                calculated = false;
                insertText(result_view, "00");
                break;
            case R.id.btn_000:
                calculated = false;
                insertText(result_view, "000");
                break;
            case R.id.btn_remove:
                calculated = false;
                int s = Math.max(result_view.getSelectionStart(), 0);
                int e = Math.max(result_view.getSelectionEnd(), 0);
                // 역으로 선택된 경우 s가 e보다 클 수 있다 때문에 이렇게 Math.min Math.max를 쓴다.
                if(Math.min(s, e) == 0) return;
                result_view.getText().replace(Math.min(s, e) - 1, Math.max(s, e), "", 0, "".length());
                break;
            case R.id.btn_reset:
                calculated = false;
                result_view.setText("");
                break;
            case R.id.btn_multi:
                calculated = false;
                insertText(result_view, "*");
                break;
            case R.id.btn_divide:
                calculated = false;
                insertText(result_view, "/");
                break;
            case R.id.btn_plus:
                calculated = false;
                insertText(result_view, "+");
                break;
            case R.id.btn_minus:
                calculated = false;
                insertText(result_view, "-");
                break;
            case R.id.btn_enter:
                if(calculated){
                    Intent returnintent = new Intent();
                    returnintent.putExtra("index", index);
                    returnintent.putExtra("value", result_view.getText().toString());
                    setResult(Activity.RESULT_OK, returnintent);
                    finish();
                }else{
                    calculated = true;
                    String currentText = result_view.getText().toString();
                    double result = 0;
                    boolean valid = true;
                    if(valid){
                        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
                        try {
                            result = (double)engine.eval(currentText);
                        } catch (ScriptException e1) {
                            Toast.makeText(getApplicationContext(), "계산에서 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                            e1.printStackTrace();
                        }
                        currentText = String.valueOf((int) result);
                        result_view.setText(currentText);
                        result_view.setSelection(result_view.getText().length());
                        btn_enter.setText("입력");
                    }else{
                        Toast.makeText(getApplicationContext(), "수식이 잘못되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    public static void insertText(EditText view, String text)
    {
        // Math.max 는 에초에 커서가 잡혀있지않을때를 대비해서 넣음.
        int s = Math.max(view.getSelectionStart(), 0);
        int e = Math.max(view.getSelectionEnd(), 0);
        // 역으로 선택된 경우 s가 e보다 클 수 있다 때문에 이렇게 Math.min Math.max를 쓴다.
        view.getText().replace(Math.min(s, e), Math.max(s, e), text, 0, text.length());
    }

//    @Override
//    public void onBackPressed(){
//        finish();
//    }


}
