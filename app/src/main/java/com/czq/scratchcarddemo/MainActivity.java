package com.czq.scratchcarddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.czq.scratchcardview.ScratchCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScratchCardView scratchCardView = findViewById(R.id.scv_main);
        scratchCardView.setOnScratchFinishedListener(new ScratchCardView.OnScratchFinishedListener() {
            @Override
            public void finish() {
                Toast.makeText(MainActivity.this,"刮出了一阵风",Toast.LENGTH_SHORT).show();
            }
        });

        scratchCardView.setOnScratchFinishedClickListener(new ScratchCardView.OnScratchFinishedClickListener() {
            @Override
            public void click() {
                Toast.makeText(MainActivity.this,"您点击了背景图片",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
