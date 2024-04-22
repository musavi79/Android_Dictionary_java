package com.example.final_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public class remove_words extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private EditText editRemove;
    private Button removebtn;
    //btnRemove-----edtRemove


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_words);

        databaseHelper = new DatabaseHelper(this);
        editRemove = findViewById(R.id.edtRemove);
        removebtn = findViewById(R.id.btnRemove);



        new TapTargetSequence(this).targets(
                TapTarget.forView(editRemove,"Update Field","To remove the word from dictionary, enter desired word in English.")
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


                TapTarget.forView(removebtn,"Saved Change Button","")
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
                Toast.makeText(remove_words.this, "Great!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {

            }
        }).start();

                removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = editRemove.getText().toString();
                if (word.isEmpty())
                    Toast.makeText(remove_words.this, "Make Sure The Field Is Not Empty. Also, You Are Not Allowed To Use Persian Expressions", Toast.LENGTH_LONG).show();
                else{
                    databaseHelper.deleteRowsWithSharedId(word);

                     }

            }
        });

    }
}