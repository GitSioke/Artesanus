package com.example.nando.arti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MonitorActivity extends Activity {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        WebView webView = (WebView)findViewById(R.id.webView);

        webView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_res/raw/index.html");

        //Retrieve data from NewProcessActivity
        Intent intent = getIntent();
        Integer minutes = intent.getIntExtra("Time", 0);
        final TextView text = (TextView) findViewById(R.id.monitor_third_value);

        //Countdown with value set by user
        new CountDownTimer(minutes*1000*60, 1000) {

            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished/1000;
                long remainingMinutes = millisUntilFinished/(1000*60);
                int seconds = (int) remainingSeconds%60;
                int minutes = (int) remainingMinutes%60;
                text.setText( String.valueOf(minutes) + ":" + String.valueOf(seconds));
            }

            public void onFinish() {
                StartCountUp();
            }
        }.start();

    }

    public void StartCountUp()
    {
        Chronometer stopWatch = new Chronometer(this);
        long startTime = SystemClock.elapsedRealtime();
        final TextView text = (TextView) findViewById(R.id.monitor_third_value);

        stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                long elapsedMinutes = elapsedSeconds/60;

                String chronoStr = elapsedMinutes + ":" + elapsedSeconds;
                text.setText(chronoStr);
            }
        });
        stopWatch.start();
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
