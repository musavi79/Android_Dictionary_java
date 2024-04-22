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

public class add_words extends AppCompatActivity {

    EditText edtWord,edtDefinition;
    String phrase,translate;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_words);

        edtWord = findViewById(R.id.edtWord);
        edtDefinition = findViewById(R.id.edt_definition);
        dbHelper = new DatabaseHelper(this);
        Button saveBtn2 = findViewById(R.id.save_Btn);

        new TapTargetSequence(this).targets(
                TapTarget.forView(edtWord,"Update Field","To add new word or update the dictionary, enter the English word here.")
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


                TapTarget.forView(edtDefinition,"Update Field","To add new Translation of  word or update the dictionary, enter the Persian translation here.")
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
                        .targetRadius(85)

                ,
                TapTarget.forView(saveBtn2,"Save Changed Button","")
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
                Toast.makeText(add_words.this, "Great!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {



            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {

            }
        }).start();
        saveBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String englishWord = edtWord.getText().toString();
                String persianWord = edtDefinition.getText().toString();
                if (englishWord.isEmpty() || persianWord.isEmpty()) {
                    Toast.makeText(add_words.this, "Please Enter Both English And Persian Words", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean wordExists = dbHelper.checkIfWordExists(englishWord);

                if (wordExists) {
                    // Update the Persian word for the existing English word
                    dbHelper.updatePersianWord(englishWord, persianWord);
                    Toast.makeText(add_words.this, "Word updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert the new word into the database
                    dbHelper.insertNewWord(englishWord, persianWord);
                    Toast.makeText(add_words.this, "Word added successfully", Toast.LENGTH_SHORT).show();
                }

                // Clear the EditTexts
                edtWord.setText("");
                edtDefinition.setText("");
            }



        });


    }
}