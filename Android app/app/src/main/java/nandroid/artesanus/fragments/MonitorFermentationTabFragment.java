package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.adapter.CerealAddedAdapter;
import nandroid.artesanus.adapter.HopAddedAdapter;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.gui.R;

/**
 * This class handles the Monitor tab fragment for Fermentation process
 */
public class MonitorFermentationTabFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_monitoring_fermenter, container, false);
        final ImageView imgDensityButton = (ImageView)view.findViewById(R.id.monitor_add_density_icon);
        final EditText densityEditText = (EditText)view.findViewById(R.id.monitor_density_edit_text);
        imgDensityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                densityEditText.setVisibility(View.VISIBLE);
                densityEditText.onEndBatchEdit();
                imgDensityButton.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }

    public synchronized void onResume() {
        super.onResume();
    }

}
