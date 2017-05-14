package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import nandroid.artesanus.adapter.CerealAddedAdapter;
import nandroid.artesanus.adapter.HeatAddedAdapter;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Heat;
import nandroid.artesanus.gui.R;

/**
 * This class represents a fragment for a mashing tab inside MonitoringActivity
 */
public class MashingTabFragment extends Fragment implements AddHeatFragment.OnHeatAddedListener{

    private HeatAddedAdapter heatAdapter;
    private ListView addedHeatListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_new_crafting_mashing, container, false);
        ImageView stepView = (ImageView)view.findViewById(R.id.new_crafting_add_step);
        stepView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddHeatFragment fragment = new AddHeatFragment();
                fragment.show(getChildFragmentManager(), "AddHeatFragmentDialog");
            }
        });

        ArrayList<Heat> addedHeatList = new ArrayList<Heat>();
        heatAdapter  = new HeatAddedAdapter(addedHeatList, getContext());
        addedHeatListView = (ListView)view.findViewById(R.id.new_crafting_step_added);
        addedHeatListView.setAdapter(heatAdapter);

        return view;
    }

    @Override
    public void onHeatAdded(Heat heat)
    {
        heatAdapter.add(heat);
        addedHeatListView.setAdapter(heatAdapter);
        //Snackbar.make(getView(), "Se añadió un escalón a " + ":" + heat.getTemperature(), Snackbar.LENGTH_SHORT).show();
    }
}


