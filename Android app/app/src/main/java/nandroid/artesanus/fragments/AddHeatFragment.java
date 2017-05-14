package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import nandroid.artesanus.common.Heat;
import nandroid.artesanus.adapter.CerealAdapter;
import nandroid.artesanus.gui.R;

/**
 * This dialog fragment will manage the Add Heat one
 */
public class AddHeatFragment extends DialogFragment
{

    public static final String LOGTAG = "AddHeatFragment";
    private static CerealAdapter adapter;

    public interface OnHeatAddedListener{
        void onHeatAdded(Heat heat);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //CerealAdapter adapter = new CerealAdapter(dataModels, getContext());

        final View view  = inflater.inflate( R.layout.dialog_add_heat, container, false);
        final EditText etDuration = (EditText)view.findViewById(R.id.heat_et_duration);
        final EditText etTemp = (EditText)view.findViewById(R.id.heat_et_temperature);
        final EditText etStart = (EditText)view.findViewById(R.id.heat_et_start);

        TextView tvOk = (TextView)view.findViewById(R.id.heat_ok_button);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String durationStr = etDuration.getText().toString();
                Integer.parseInt(durationStr);
                String tempStr = etTemp.getText().toString();
                Integer.parseInt(tempStr);
                String startStr = etStart.getText().toString();
                Integer.parseInt(startStr);

                Heat heat = new Heat(Integer.parseInt(tempStr), Integer.parseInt(durationStr), Integer.parseInt(startStr));

                FireHeatAddedListener(heat);
                dismiss();
            }
        });

        TextView tvCancel = (TextView)view.findViewById(R.id.heat_cancel_button);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void FireHeatAddedListener(Heat heat)
    {
        Fragment fragment = getParentFragment();
        if (fragment instanceof OnHeatAddedListener)
        {
            OnHeatAddedListener listener = (OnHeatAddedListener) fragment;
            listener.onHeatAdded(heat);
        }

        if (getActivity() instanceof  OnHeatAddedListener)
        {
            OnHeatAddedListener listener = (OnHeatAddedListener) getActivity();
            listener.onHeatAdded(heat);
        }
    }
}