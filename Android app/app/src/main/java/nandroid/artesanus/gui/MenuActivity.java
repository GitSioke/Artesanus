package nandroid.artesanus.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.Locale;

import nandroid.artesanus.common.LanguageHelper;
import nandroid.artesanus.common.SharedPreferencesHelper;
import nandroid.artesanus.fragments.PreferencesDialogFragment;

public class MenuActivity extends AppCompatActivity
        implements PreferencesDialogFragment.DialogResponseListener
{
    // Debugging
    private static final String TAG = "MenuActivity";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Check language settings and set app language to this
        // TODO move to Splash when it exists
        String langCode = SharedPreferencesHelper.getLanguagePreference(this);
        LanguageHelper.changeLanguage(this, langCode);

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

    // Override method to handle creation of Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences_option:
                // Launch the dialog for preferences
                showPreferencesDialog();
                return true;
            case R.id.menu_scan_option:
                // Ensure this device is discoverable by others
                return true;
        }
        return false;
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String langCode)
    {
        String strPreferencesSaved = getResources().getString(R.string.preferences_saved);
        // Preferences option were saved, show it to user
        Snackbar.make(findViewById(R.id.ly_coordinator),
                strPreferencesSaved,
                Snackbar.LENGTH_LONG)
                .show();

        // Change locale settings in the app.
        if (LanguageHelper.changeLanguage(this, langCode))
        {
            // There was a change, re-start activity.
            this.recreate();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        String strPreferencesSaved = getResources().getString(R.string.preferences_not_saved);
        // Preferences option were saved, show it to user
        Snackbar.make(findViewById(R.id.ly_coordinator),
                strPreferencesSaved,
                Snackbar.LENGTH_LONG)
                .show();
    }

    private void showPreferencesDialog()
    {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new PreferencesDialogFragment();
        dialog.show(getSupportFragmentManager(), "PreferencesDialogFragment");
    }

}
