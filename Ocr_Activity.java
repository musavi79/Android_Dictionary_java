package com.example.final_pro;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Ocr_Activity extends AppCompatActivity {

    private MaterialButton inputImageBtn;
    String sss;

    private AppCompatButton recognizeBtn,btn_Trans;
    private ShapeableImageView imageIv;
    private EditText reTET;
    private EditText text_data;
    StringBuilder bb;

    private static final String TAG = "MAIN_TAG";
    private Uri imageUri = null;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private ProgressDialog progressDialog ;

    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

       text_data = findViewById(R.id.text_data2);
        inputImageBtn = findViewById(R.id.btnCapture);
       // reTET = findViewById(R.id.re__te);
        imageIv = findViewById(R.id.imageIv);
        recognizeBtn = findViewById(R.id.btnCopy);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputImageDialog();
            }
        });

        recognizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri == null){
                    Toast.makeText(Ocr_Activity.this, "Pick image first...", Toast.LENGTH_SHORT).show();
                }else{
                    recognizeTextFromImage();
                }
            }
        });

        btn_Trans = findViewById(R.id.btnTrans);
        btn_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
            }
        });
    }

    private void recognizeTextFromImage() {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        processImage(bitmap);

    }

    private void showInputImageDialog() {
        PopupMenu popupMenu = new PopupMenu(this,inputImageBtn);

        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                int id = menuItem.getItemId();
                if(id == 1){
                    Log.d(TAG, "onMenuItemClick: Camera Clicked... ");
                    if(checkCameraPermissions()){
                        pickImageCamera();
                    }else{

                        requestCameraPermissions();
                    }

                }else if(id == 2){

                    Log.d(TAG, "onMenuItemClick: Gallery Clicked...");

                    if(checkStoragePermission()){
                        pickImageGallery();
                    }else{

                        requestStoragePermission();
                    }
                }
                return true;
            }
        });

    }

    public void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri"+ imageUri);
                        imageIv.setImageURI(imageUri);

                    } else {
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(Ocr_Activity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        ContentValues value = new ContentValues();
        value.put(MediaStore.Images.Media.TITLE, "Sample Title");
        value.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);


    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Log.d(TAG, "onActivityResult: imageUri"+ imageUri);
                        imageIv.setImageURI(imageUri);
                    } else {
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(Ocr_Activity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }

    );

    private boolean checkStoragePermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermissions(){
        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult ;

    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{

                if(grantResults.length >0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageaAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageaAccepted){

                        pickImageCamera();
                    }else{
                        Toast.makeText(this, "camera and storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Cancelled...", Toast.LENGTH_SHORT).show();
                }


            }break;
            case STORAGE_REQUEST_CODE:{

                if(grantResults.length >0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){

                        pickImageGallery();
                    }
                    else {Toast.makeText(this, "storage permissions are required", Toast.LENGTH_SHORT).show();
                    }

                }


            }break;


        }
    }

    private void processImage(Bitmap imageBitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String apiKey = "008bee122988957";

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.ocr.space/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        OcrApi ocrApi = retrofit.create(OcrApi.class);
        Call<ResponseBody> call = ocrApi.uploadImage(apiKey, requestFile);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String result = null;
                    try {
                        result = response.body().string();
                    } catch (IOException e) {
                        Toast.makeText(Ocr_Activity.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    // Display the result in the EditText
                    String outputString = result.replaceAll("\"ParsedResults\":\\[\\{\"TextOverlay\":\\{\"Lines\":\\[\\],\"HasOverlay\":false,\"Message\":\"Text overlay is not provided as it is not requested\"\\},\"TextOrientation\":\"0\",\"FileParseExitCode\":1", "");
                    outputString = outputString.replaceAll("ErrorMessage.*", "");
                   // outputString = outputString.replaceAll("\r\n", "");
                    text_data.setText(outputString);


                } else {
                      Toast.makeText(Ocr_Activity.this, "Failed to process image", Toast.LENGTH_SHORT).show();
                       }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Ocr_Activity.this, "Faild to Get Data,Try Again!", Toast.LENGTH_SHORT).show();
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

    public void sendText() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        if (isConnectedToInternet()) {
            String trimmedSentence = text_data.getText().toString().trim();

            Python py = Python.getInstance();
            String scriptName = "trans";
            PyObject module = py.getModule(scriptName);
            PyObject getTranslationsFunc = module.get("translate_text");


            PyObject obj = getTranslationsFunc.call(trimmedSentence);
            String translatedText = obj.toString();
            String translated = translatedText.replace("\\{'status': 'success', 'data': \\{'translatedText':\"", "");




            text_data.setText(translated);
        } else {
            Toast.makeText(Ocr_Activity.this, "Check Your Connection Then Try Again", Toast.LENGTH_SHORT).show();
        }
    }


}
