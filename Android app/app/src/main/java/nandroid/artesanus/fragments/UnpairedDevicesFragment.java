package nandroid.artesanus.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import nandroid.artesanus.gui.R;
import nandroid.artesanus.gui.BluetoothActivity;

/**
 * Created by Nando on 01/11/2016.
 */
public class UnpairedDevicesFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.unpaired_fragment_no_pair_device)
                .setPositiveButton(R.string.unpaired_fragment_pair_devices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((BluetoothActivity)getActivity()).doPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((BluetoothActivity)getActivity()).doNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
