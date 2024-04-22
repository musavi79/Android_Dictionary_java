package com.example.final_pro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;


import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


import java.io.FileNotFoundException;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import java.util.Random;
import java.io.InputStream;


public class TextFile extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_PDF_REQUEST_CODE = 2;

    private Button buttonSelectFile,buttonSelectFile2,btnselectfile3;
    private EditText textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_file);

        buttonSelectFile = findViewById(R.id.btn_select_file);
        btnselectfile3 = findViewById(R.id.btn_select_file3);
        textView = findViewById(R.id.txt_output);
        buttonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(TextFile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                } else {
                    ActivityCompat.requestPermissions(TextFile.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            }

        });

        btnselectfile3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTextFile();
            }
        });


    }




    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // نمایش متن فایل PDF
                extractedTextFromPdf(uri);
            } else {
                Toast.makeText(this, "Invalid file URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isPersian(String s) {
        for (int i = 0; i < Character.codePointCount(s, 0, s.length()); i++) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <=0x06FF)
                return true;

        }
        return false;
    }
    InputStream inputStream;
    private void extractedTextFromPdf(Uri uri) {

        buttonSelectFile2 = findViewById(R.id.btn_select_file2);

        try {
             inputStream = TextFile.this.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {


        String fc ="";
        StringBuilder stringBuilder = new StringBuilder();
        PdfReader reader = null;

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            reader = new PdfReader(inputStream);

            int pages = reader.getNumberOfPages();
            for (int i=1;i<=pages;i++){

                fc = PdfTextExtractor.getTextFromPage(reader,i);


            }
            stringBuilder.append(fc);

            }
            reader.close();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String input_Text = stringBuilder.toString();
                   // boolean isPersian = input_Text.matches("\\p{InArabic}+|\\p{Inherited}+");


                    if (isPersian(input_Text)) {

                       StringBuilder sss = new StringBuilder();
                        // معکوس کردن کاراکترها در یک آرایه
                        //char[] reversedArray = new char[input_Text.length()];
                        for (int i = input_Text.length() -1; i > 0; i--) {
                            sss.append(input_Text.charAt(i));

                        }
                        textView.setText(sss.toString());
                    } else {
                        textView.setText(stringBuilder.toString());
                    }


                    buttonSelectFile2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            sendText();
                        }
                    });


                }
            });






        }catch (IOException e){}
                Log.d("TAG", "run: ");
            }
        }).start();
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(1000); // تولید یک عدد تصادفی بین 0 و 999
    }
    private void createTextFile() {
        String fileName = "Translate" + generateRandomNumber() + ".txt";;
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + fileName;

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(textView.getText().toString().getBytes());
            outputStream.close();
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error in Creating File", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendText() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        if (isConnectedToInternet()) {
            String trimmedSentence = textView.getText().toString().trim();

            Python py = Python.getInstance();
            String scriptName = "trans";
            PyObject module = py.getModule(scriptName);
            if(isPersian(trimmedSentence)){
                PyObject getTranslationsFunc = module.get("translate_fatext");


                PyObject obj = getTranslationsFunc.call(trimmedSentence);
                String translatedText = obj.toString();
                String translated = translatedText.replace("\\{'status': 'success', 'data': \\{'translatedText':\"", "");




                textView.setText(translated);}
            else {
            PyObject getTranslationsFunc = module.get("translate_text");


                PyObject obj = getTranslationsFunc.call(trimmedSentence);
                String translatedText = obj.toString();
                String translated = translatedText.replace("\\{'status': 'success', 'data': \\{'translatedText':\"", "");




            textView.setText(translated);
            }
        } else {
            Toast.makeText(TextFile.this, "Check Your Connection Then Try Again", Toast.LENGTH_SHORT).show();
        }
    }
    }



