package com.example.q.swipe_tab;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ContentInputActivity extends AppCompatActivity implements View.OnClickListener {
    Button yes, no;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_input);

        Intent fromadd = getIntent();

        yes = findViewById(R.id.yes_btn);
        no = findViewById(R.id.no_btn);
        content = findViewById(R.id.content_space);
        content.setText(fromadd.getStringExtra("previous"));

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.yes_btn:
                Intent returnintent = new Intent();
                returnintent.putExtra("content", String.valueOf(content.getText()));
                setResult(Activity.RESULT_OK, returnintent);
                finish();
                break;
            case R.id.no_btn:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                break;
        }
    }
}
