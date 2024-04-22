package com.example.final_pro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Saved extends AppCompatActivity {


    //DatabaseHelper databaseHelper = new DatabaseHelper(this);
   /// private ImageView del ;


    RecyclerView recyclerView;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        recyclerView = findViewById(R.id.rec);

        savedAdapters adapters = new savedAdapters
                (new DatabaseHelper(this).Get_Data(), Saved.this );
        recyclerView.setAdapter(adapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(Saved.this,
                LinearLayout.VERTICAL,false));

    }




}