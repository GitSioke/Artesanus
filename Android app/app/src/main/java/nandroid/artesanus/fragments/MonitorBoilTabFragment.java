package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * This class represents a fragment for a boiling tab inside MonitoringActivity
 */
public class MonitorBoilTabFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_monitoring_heating, container, false);
        return view;
    }


    public synchronized void onResume() {
        super.onResume();
    }

}