package nandroid.artesanus.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import nandroid.artesanus.gui.MenuActivity;
import nandroid.artesanus.gui.R;

/**
 * This fragment will manage DialogFragment for preferences options
 */
public class PreferencesDialogFragment extends DialogFragment
{
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogResponseListener
    {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DialogResponseListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogResponseListener) activity;
        } catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_preferences, null);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get language from shared preferences and show it
        final EditText edLanguage = (EditText)view.findViewById(R.id.preferences_ed_language);
        String language = preferences.getString("language", "Spanish");
        edLanguage.setText(language);

        // Get ip address from shared preferences and show it
        final EditText edIPAddress = (EditText)view.findViewById(R.id.preferences_ed_address);
        String ip = preferences.getString("ip_address", "0.0.0.0");
        edIPAddress.setText(ip);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = preferences.edit();
                        String lang = edLanguage.getText().toString();
                        String ip = edIPAddress.getText().toString();
                        editor.putString("language",lang);
                        editor.putString("ip_address", ip);

                        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
                        mListener.onDialogPositiveClick(PreferencesDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(PreferencesDialogFragment.this);
                    }
                })

        ;
        return builder.create();
    }

}
