package com.ex.group.folder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicWebViewActivity extends AppCompatActivity {
    WebView url;
    ImageView button_prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_web_view);


        url= findViewById(R.id.url);

        WebSettings webSettings = url.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptEnabled(true);
        url.setWebViewClient(new WebClient());
        url.loadUrl(getIntent().getStringExtra("contentsUrl"));
        button_prev=findViewById(R.id.button_prev);
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class WebClient extends  WebViewClient{


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return  true;
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


    }
}
