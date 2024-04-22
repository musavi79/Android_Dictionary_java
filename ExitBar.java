package com.example.final_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class ExitBar extends AppCompatActivity {

    private RatingBar ratingBar1;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_bar);

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());

        Button ext = findViewById(R.id.exit_button);
        ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        Button cansel = findViewById(R.id.cansel_btn);
        cansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                String url = "https://cafebazaar.ir/app/com.google.android.apps.translate";
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                setContentView(webView);
               // Toast.makeText(ExitBar.this, "Thanks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}