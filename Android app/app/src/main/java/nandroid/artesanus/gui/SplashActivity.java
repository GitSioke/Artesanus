package nandroid.artesanus.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;
import nandroid.artesanus.common.LanguageHelper;
import nandroid.artesanus.common.SharedPreferencesHelper;
import nandroid.artesanus.http.HTTPController;

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
