package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nandroid.artesanus.adapter.CerealAddedAdapter;
import nandroid.artesanus.adapter.HopAddedAdapter;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.common.PostController;
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
        final ImageView imgConfirmButton = (ImageView)view.findViewById(R.id.monitor_add_density_confirm);

        imgConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the views
                densityEditText.setVisibility(View.INVISIBLE);
                imgConfirmButton.setVisibility(View.INVISIBLE);
                imgDensityButton.setVisibility(View.VISIBLE);

                // Add density to database
                PostController controller = new PostController();
                GsonBuilder builder = new GsonBuilder();
                builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                Gson gson = builder.create();
                Event event = new Event();

                //event.setId_process();
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                event.setTime(date);
                event.setSource("fermentation");
                event.setValue(Long.getLong(densityEditText.getText().toString()));
                event.setData("density");
                String json = (gson.toJson(densityEditText.getText().toString()));
                controller.execute("/insert_density", json);

                // Show to user the operation has been performed
                Snackbar.make(v, R.string.density_added, Snackbar.LENGTH_SHORT)
                        .setAction("No action", null).show();
            }
        });

        imgDensityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                densityEditText.setVisibility(View.VISIBLE);
                imgConfirmButton.setVisibility(View.VISIBLE);
                imgDensityButton.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }

    public synchronized void onResume() {
        super.onResume();
    }

}
