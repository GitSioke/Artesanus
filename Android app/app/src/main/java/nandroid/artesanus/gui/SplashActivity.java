package nandroid.artesanus.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nandroid.artesanus.common.Brew;
import nandroid.artesanus.common.LanguageHelper;
import nandroid.artesanus.common.SharedPreferencesHelper;
import nandroid.artesanus.http.HTTPController;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.path;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivity";
    private static final boolean D = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        setContentView(R.layout.activity_splash);

        // configure and start pouring sound effect and video
        StartSplashSoundNVideo();

        // Get shared preferences and configure app with them
        ConfigureSettings();
    }

    private void StartSplashSoundNVideo()
    {
        VideoView videoView = (VideoView)findViewById(R.id.splah_vv);
        String pathToVideo = "android.resource://" + getPackageName() + "/" + R.raw.splash_pouring_beer;
        videoView.setVideoURI(Uri.parse(pathToVideo));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                //Start Menu activity when video ends.
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
        });

        // Display video at full size
        /*DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);*/

        videoView.start();

        // Start beer sound effect
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beer_sound_effect);
        mediaPlayer.start();
    }

    private void ConfigureSettings()
    {
        // Check language settings and set app language to this.
        String langCode = SharedPreferencesHelper.getLanguagePreference(this);
        LanguageHelper.changeLanguage(this, langCode);

        // Check ip_address in settings and set to http controller.
        String ip_address = SharedPreferencesHelper.getIPAddressPreference(this);
        HTTPController.setIP(ip_address);
    }
}
