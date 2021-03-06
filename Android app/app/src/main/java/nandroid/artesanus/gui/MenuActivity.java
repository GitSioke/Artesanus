package nandroid.artesanus.gui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import nandroid.artesanus.common.LanguageHelper;
import nandroid.artesanus.fragments.PreferencesDialogFragment;
import nandroid.artesanus.listener.DialogResponseListener;

public class MenuActivity extends AppCompatActivity
        implements DialogResponseListener
{
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;
    private static boolean _wasReset = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

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
            case R.id.menu_credits_option:
                // Ensure this device is discoverable by others
                ShowCreditDialog();
                return true;
        }
        return false;
    }

    private void ShowCreditDialog()
    {
        // Create an instance of the dialog fragment and show it
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_credits);
        dialog.show();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
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
        // Change locale settings in the app.
        if (LanguageHelper.changeLanguage(this, langCode))
        {
            _wasReset = true;
            // There was a change, re-start activity.
            this.recreate();
        }

        String strPreferencesSaved = getResources().getString(R.string.preferences_saved);
        // Preferences option were saved, show it to user
        Snackbar.make(findViewById(R.id.ly_coordinator),
                strPreferencesSaved,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        String strSnack = getResources().getString(R.string.preferences_not_saved);
        if (_wasReset)
        {
            // There was a previous reset, show a preferences saved message
            strSnack = getResources().getString(R.string.preferences_saved);
        }

        // Preferences option were saved, show it to user
        Snackbar.make(findViewById(R.id.ly_coordinator),
                strSnack,
                Snackbar.LENGTH_LONG)
                .show();

        _wasReset = false;
    }

    private void showPreferencesDialog()
    {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new PreferencesDialogFragment();
        dialog.show(getSupportFragmentManager(), "PreferencesDialogFragment");
    }

}
