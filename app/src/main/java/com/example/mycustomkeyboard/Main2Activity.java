package com.example.mycustomkeyboard;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity implements View.OnTouchListener {

    private EditText et;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;
    private MyKeyBoardView keyBoardView;
    private LinearLayout root;
    private RelativeLayout keyboardRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        et = findViewById(R.id.et);
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        root =  findViewById(R.id.root);
        keyBoardView =  findViewById(R.id.mykeyboard);
        keyboardRoot =  findViewById(R.id.mykeyboard_root);

        et.setOnTouchListener(this);
        et1.setOnTouchListener(this);
        et2.setOnTouchListener(this);
        et3.setOnTouchListener(this);
        et4.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            keyBoardView.setAttachToEditText((EditText) v, root, keyboardRoot);
        }
        return true;
    }
}
