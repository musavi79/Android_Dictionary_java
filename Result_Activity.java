package com.example.final_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Result_Activity extends AppCompatActivity {

    private TextView textResult4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textResult4 = findViewById(R.id.txtResult4);
        String result2 = getIntent().getStringExtra("translatedText");
        textResult4.setText(result2);

    }
}