package com.example.final_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

public class Search_offline extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private MultiAutoCompleteTextView editSearchOffline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_offline);

        Button findbtn = findViewById(R.id.findbutton);
        TextView resultTextView = findViewById(R.id.txtResult);
        ImageView imgView = findViewById(R.id.imgView);
        editSearchOffline = findViewById(R.id.edtSearchOffline);

       //حدس املا
        databaseHelper = new DatabaseHelper(this);
        ArrayList<String> combinedValues = databaseHelper.getCombinedValues();
        List<String> combinedList = new ArrayList<>(combinedValues);
        ArrayAdapter adapters = new ArrayAdapter(Search_offline.this,android.R.layout.simple_list_item_1,combinedList);
        editSearchOffline.setAdapter(adapters);
        editSearchOffline.setTokenizer(new CustomTokenizer());
        editSearchOffline.setThreshold(2);


        //راهنما
        new TapTargetSequence(this).targets(
                TapTarget.forView(editSearchOffline,"Offline Translation Search Bar","In this view, you can search the meanings of English or Persian word.")
                        .outerCircleColor(R.color.mango)
                        .targetCircleColor(android.R.color.white)
                        .titleTextSize(16)
                        .titleTextColor(android.R.color.white)
                        .descriptionTextSize(13)
                        .descriptionTextColor(R.color.black)
                        .transparentTarget(true)
                        .textColor(android.R.color.black)
                        .textTypeface(Typeface.SERIF)

                        .dimColor(android.R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(true)
                        .targetRadius(85),


                TapTarget.forView(findbtn,"Search Button","Tap the button to display the result.")
                        .outerCircleColor(R.color.mango)
                        .targetCircleColor(android.R.color.white)
                        .titleTextSize(16)
                        .titleTextColor(android.R.color.white)
                        .descriptionTextSize(13)
                        .descriptionTextColor(R.color.black)
                        .transparentTarget(true)
                        .textColor(android.R.color.black)
                        .textTypeface(Typeface.SERIF)

                        .dimColor(android.R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(true)
                        .targetRadius(85)).listener(new TapTargetSequence.Listener() {
            @Override
            public void onSequenceFinish() {

                Toast.makeText(Search_offline.this, "Great!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {



            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {

            }
        }).start();




        databaseHelper = new DatabaseHelper(this);

        //سرچ افلاین
        findbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String searchText = editSearchOffline.getText().toString().trim();
                TextView txtResult = findViewById(R.id.txtTitle);
                txtResult.setText("Meaning :");
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateFlg(searchText);
                    }
                });
                if (imgView.getVisibility() == View.VISIBLE)
                    Log.d("TAG", "");
                else
                    imgView.setVisibility(View.VISIBLE);

                if (!searchText.isEmpty()) {
                    String[] columns = {"en", "fa"};
                    String selection = "en = ? OR fa = ? OR fa LIKE ?";
                    String[] selectionArgs = {searchText, searchText, searchText + "%"};
                    String limit = "3";
                    Cursor cursor = databaseHelper.getReadableDatabase().query("en_fa", columns, selection, selectionArgs, null, null, null,limit);
                    if (cursor.moveToFirst()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        do {
                            String en  = cursor.getString(0);

                                String fa = cursor.getString(1);
                            stringBuilder.append(en).append(" - ").append(fa).append("\n");


                        } while (cursor.moveToNext());
                        resultTextView.setText(stringBuilder.toString());
                    } else {
                        resultTextView.setText("No Results Found.");
                    }

                    cursor.close();




                }
            }
        });
    }

    //لغات ذخیره شده
    private void updateFlg(String searchText) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("flg", 1);
        String whereClause = "en = ? OR fa = ? OR fa LIKE ?";
        String[] whereArgs = {searchText, searchText, searchText + "%"};
        db.update("en_fa", values, whereClause, whereArgs);
        db.close();
        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
    }


}
