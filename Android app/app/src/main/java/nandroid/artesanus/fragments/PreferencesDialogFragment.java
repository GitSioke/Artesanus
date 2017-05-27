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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nandroid.artesanus.common.LanguageHelper;
import nandroid.artesanus.gui.MenuActivity;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.listener.DialogResponseListener;

/**
 * This fragment will manage DialogFragment for preferences options
 */
public class PreferencesDialogFragment extends DialogFragment
{
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
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_preferences, null);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Get language from shared preferences and show it
        String languageCode = preferences.getString("language", "Spanish");
        int positionSelected = LanguageHelper.getLanguagePosition(languageCode);

        // Generate list view with languages
        List<String> stringArray = Arrays.asList(getResources().getStringArray(R.array.string_array_languages));
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringArray);
        final ListView listView = (ListView)view.findViewById(R.id.lv_languages);
        listView.setItemChecked(positionSelected, true);
        listView.setAdapter(itemsAdapter);
        listView.setSelected(true);

        // Get ip address from shared preferences and show it
        final EditText edIPAddress = (EditText)view.findViewById(R.id.preferences_ed_address);
        String ip = preferences.getString("ip_address", "192.168.1.40");
        edIPAddress.setText(ip);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Edit SharedPreferences with new values
                        SharedPreferences.Editor editor = preferences.edit();
                        String langCode = LanguageHelper.getLanguageCode(listView.getCheckedItemPosition());
                        String ip = edIPAddress.getText().toString();
                        editor.putString("language",langCode);
                        editor.putString("ip_address", ip);
                        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);

                        // Pass info back to activity
                        mListener.onDialogPositiveClick(PreferencesDialogFragment.this, langCode);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(PreferencesDialogFragment.this);
                    }
                });
        return builder.create();
    }

}
