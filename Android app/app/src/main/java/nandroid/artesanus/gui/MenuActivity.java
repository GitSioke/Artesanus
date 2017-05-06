package nandroid.artesanus.gui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;


public class MenuActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MenuActivity";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_main_menu);

        // Set newCraft button for start new crafting process, so start first new process
        FloatingActionButton newCraft = (FloatingActionButton) findViewById(R.id.main_menu_new_crafting);
        newCraft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewBeerCraftingActivity.class);
                startActivity(intent);
            }
        });

    }

    // Overrided method to handle creation of Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_option, menu);
        return true;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // Start beer sound effect
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beer_sound_effect);
        mediaPlayer.start();
    }
    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

}
