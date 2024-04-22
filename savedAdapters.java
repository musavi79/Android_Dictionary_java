package com.example.final_pro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class savedAdapters extends RecyclerView.Adapter<savedAdapters.ViewHolder>  {

    ArrayList<saved_model> saveddModel;
   DatabaseHelper databaseHelper;


    Activity activity;

    public savedAdapters(ArrayList<saved_model> saveddModel , Activity activity) {
        this.saveddModel = saveddModel;
        this.activity = activity;
    }



    @NonNull
    @Override
    public savedAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rows,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull savedAdapters.ViewHolder holder, int position) {
        //holder.txtId.setText(saveddModel.get(position).getId());
        //final int currentPosition = position;
        holder.txtEn.setText(saveddModel.get(position).getEn());
        holder.txtFa.setText(saveddModel.get(position).getFa());

    }

    @Override
    public int getItemCount() {
        return saveddModel.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtEn,txtFa;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEn = itemView.findViewById(R.id.textView1);
            txtFa = itemView.findViewById(R.id.textView2);
        }

    }

    public void reFlg(String en, String fa) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("flg", 0);
        String whereClause = "en = ? OR fa = ? OR fa LIKE ?";
        String[] whereArgs = {en,fa};
        db.update("en_fa", values, whereClause, whereArgs);
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + "en_fa", null);
    }


}

