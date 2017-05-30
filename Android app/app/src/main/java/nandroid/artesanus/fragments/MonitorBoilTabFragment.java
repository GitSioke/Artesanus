package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import nandroid.artesanus.adapter.CerealAddedAdapter;
import nandroid.artesanus.adapter.HopAddedAdapter;
import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.IAsyncHttpResponse;

/**
 * This class represents a fragment for a boiling tab inside MonitoringActivity
 */
public class MonitorBoilTabFragment extends Fragment implements IAsyncHttpResponse
{
    private int _idProcess;
    private GraphView _graph;
    private TextView txtViewPrimaryValue;

    // Debugging
    private static final String TAG = "MonitorBoilTabFragment";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MonitoringActivity act = (MonitoringActivity)getActivity();
        _idProcess = act.GetIdBoiling();

        View view  = inflater.inflate( R.layout.fragment_monitoring_heating, container, false);
        _graph = (GraphView)view.findViewById(R.id.graph);
        txtViewPrimaryValue = (TextView)view.findViewById(R.id.monitor_main_value);

        // Get all events related to process and brew crafting from server
        String json = "";
        new GetController(this).execute("/retrieve/events/"+_idProcess, json);
        return view;
    }


    public synchronized void onResume() {
        super.onResume();
    }

    @Override
    public void ProcessFinish(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try {
            BrewProcess mashingBrewProcess = gson.fromJson(output, BrewProcess.class);
            List<Event> events = mashingBrewProcess.getEvents();

            txtViewPrimaryValue.setText(String.valueOf(events.get(events.size()-1).getValue())+"º");
            LineGraphSeries series = new LineGraphSeries<DataPoint>();
            for (Event event : events ) {
                series.appendData(new DataPoint(event.getTime(), event.getValue()), true, events.size());
            }

            _graph.getGridLabelRenderer().setHumanRounding(false);
            _graph.addSeries(series);

            _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            _graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            _graph.getViewport().setScrollable(true); // enables horizontal scrolling
            _graph.getViewport().setScrollableY(true); // enables vertical scrolling
            _graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            _graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }

    }
}