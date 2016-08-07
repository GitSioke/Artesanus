package com.example.nando.arti;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MeasurementsActivity extends Activity {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        WebView webView = (WebView)findViewById(R.id.webView);

        webView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_res/raw/index.html");
    }

    public class WebAppInterface
    {
        Context mContext;

        int timer =0;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public int getNextValue() {
            return 10;
        }

        @JavascriptInterface
        public int getNextTime() {
            return timer++;
        }

        @JavascriptInterface
        public int getTotal() {
            return 3;
        }

    }
}
