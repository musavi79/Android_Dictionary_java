package com.example.final_pro;

import android.content.ActivityNotFoundException;
import android.content.Context;
import java.io.IOException;
import com.chaquo.python.PyObject;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.DataOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.io.InputStream;
//import okhttp3.Call;


public class DashboardActivity extends AppCompatActivity {

    //define
    ViewPager viewPager;
    TableLayout tableLayout;


    private DatabaseHelper databaseHelper;

    private SQLiteDatabase database;
    private MultiAutoCompleteTextView edtTxtOnline;

    private Button btnOnline,butnMic,btnExit ;

    private TextView txtRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        butnMic = findViewById(R.id.btnMic);
        btnOnline = findViewById(R.id.btnOnline);
        edtTxtOnline = findViewById(R.id.edtTxtOnline);
        txtRandom = findViewById(R.id.txtRandom);
        LinearLayout offline = findViewById(R.id.offlineLayout);
        LinearLayout cameral = findViewById(R.id.camera_lay);
        LinearLayout remove = findViewById(R.id.removeLayout);
        LinearLayout saved = findViewById(R.id.ttoolsLayout);
        LinearLayout add = findViewById(R.id.addLayout);
        LinearLayout toolsLayout = findViewById(R.id.toolsLayout);
        btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(DashboardActivity.this,ExitBar.class);
                startActivity(intent9);
            }
        });

       //حدس املای لغات
        databaseHelper = new DatabaseHelper(this);
        ArrayList<String> combinedValues = databaseHelper.getCombinedValues();
        List<String> combinedList = new ArrayList<>(combinedValues);
        ArrayAdapter adapter = new ArrayAdapter(DashboardActivity.this,android.R.layout.simple_list_item_1,combinedList);
        edtTxtOnline.setAdapter(adapter);
        edtTxtOnline.setTokenizer(new CustomTokenizer());
        edtTxtOnline.setThreshold(2);

        searchOnline();
        //guideView();
        //ارتباط با دیتابیس
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.updateDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        database = databaseHelper.getReadableDatabase();

       //نمایش رندوم -نقل قول
        randomView();
        //راهنما

        new TapTargetSequence(this).targets(
                TapTarget.forView(edtTxtOnline,"Online Translation Search Bar","In this view, you can enter your phrases in English or Farsi and wait for the result.Don't forget to check your connection")
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

                TapTarget.forView(butnMic,"Microphone Button","Speak Farsi or English.")
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


                TapTarget.forView(btnOnline,"Online Translation Button","Tap the button to display the result.")
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
                TapTarget.forView(offline,"Offline Translation Menu","Translate words into both languages.")
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
                TapTarget.forView(cameral,"Image Translator","In this menu, you can translate images containing your texts.")
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
                TapTarget.forView(saved,"PhrasesBook","Saved words are displayed here.")
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
                TapTarget.forView(add,"Insert Words Menu","In this menu, you can add your favorite words to the dictionary.")
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
                TapTarget.forView(toolsLayout,"PDF Translator","Translate your PDF file here.")
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
                TapTarget.forView(remove,"Delete Words menu","In this menu, you can remove the desired words from the dictionary.")
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

                ,TapTarget.forView(txtRandom,"Random Phrases","Here you can learn about English terms.")
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
                TapTarget.forView(btnExit,"Exit Button","")
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

                Toast.makeText(DashboardActivity.this, "Great!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {



            }

            @Override
            public void onSequenceCanceled(TapTarget lastTarget) {

            }
        }).start();

        cameral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardActivity.this, Ocr_Activity.class);
                startActivity(intent1);
            }
        });


        //open adding words activity
        findViewById(R.id.addLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(DashboardActivity.this, add_words.class);
                startActivity(intent2);
            }
        });
        //open removing words activity
        findViewById(R.id.removeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(DashboardActivity.this, remove_words.class);
                startActivity(intent3);
            }
        });

        findViewById(R.id.offlineLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(DashboardActivity.this, Search_offline.class);
                startActivity(intent4);
            }
        });

        findViewById(R.id.ttoolsLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent(DashboardActivity.this, Saved.class);
                startActivity(intent5);
            }
        });

        findViewById(R.id.toolsLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent19 = new Intent(DashboardActivity.this,TextFile.class);
                startActivity(intent19);
            }
        });

        butnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent10 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent10.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                String[] languages = {"fa", "en"};
                intent10.putExtra(RecognizerIntent.EXTRA_LANGUAGE,languages);
                try {

                    startActivityForResult(intent10,100);

                }catch (ActivityNotFoundException e){
                    Toast.makeText(DashboardActivity.this, "Your device not supported...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edtTxtOnline.setText(res.get(0));
            }
        }
    }

    //سرچ انلاین
    private void searchOnline() {


        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
         btnOnline = findViewById(R.id.btnOnline);
        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnectedToInternet()) {

                    String inputText = edtTxtOnline.getText().toString().trim();
                    Python py = Python.getInstance();
                    String scriptName = "onlineT";
                    PyObject obj = py.getModule(scriptName).callAttr("get_translations", inputText);
                    String translated = obj.toString();
                    String translatedText = translated.replaceAll("[\",'\\]\\[]", "");
                    Intent intent8 = new Intent(DashboardActivity.this, Result_Activity.class);
                    intent8.putExtra("translatedText", translatedText);
                    startActivity(intent8);
                    edtTxtOnline.setText("");


                }
                else
                    Toast.makeText(DashboardActivity.this, "Check Your Connection Then Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    //Display random data
    public void randomView() {

        txtRandom = findViewById(R.id.txtRandom);

        databaseHelper = new DatabaseHelper(this);
        String result = databaseHelper.getRandomData();
        txtRandom.setText(result);
    }

}