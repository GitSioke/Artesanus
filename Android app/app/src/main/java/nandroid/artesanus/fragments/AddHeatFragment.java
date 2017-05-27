package nandroid.artesanus.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import nandroid.artesanus.common.Heat;
import nandroid.artesanus.gui.R;

/**
 * This dialog fragment will manage the Add Heat one
 */
public class AddHeatFragment extends DialogFragment
{
    // Use this instance of the interface to deliver action events
    AddHeatListener mListener;

    AddHeatListener mFragmentListener;

    public interface AddHeatListener
    {
        void onDialogPositiveClick(DialogFragment dialog, Heat heat);
    }

    public static final String LOGTAG = "AddHeatFragment";


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_heat, null);

        final EditText etDuration = (EditText)view.findViewById(R.id.heat_et_duration);
        final EditText etTemp = (EditText)view.findViewById(R.id.heat_et_temperature);
        final EditText etStart = (EditText)view.findViewById(R.id.heat_et_start);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String durationStr = etDuration.getText().toString();
                        Integer.parseInt(durationStr);
                        String tempStr = etTemp.getText().toString();
                        Integer.parseInt(tempStr);
                        String startStr = etStart.getText().toString();
                        Integer.parseInt(startStr);

                        Heat heat = new Heat(Integer.parseInt(tempStr), Integer.parseInt(durationStr), Integer.parseInt(startStr));

                        // Pass heat info back to activity
                        mListener.onDialogPositiveClick(AddHeatFragment.this, heat);
                        mFragmentListener.onDialogPositiveClick(AddHeatFragment.this, heat);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddHeatListener)activity;
            mFragmentListener = (AddHeatListener) getParentFragment();
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddHeatListener)ctx;
            mListener = (AddHeatListener)getActivity();
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(ctx.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}