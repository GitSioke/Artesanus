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
import java.util.Timer;
import java.util.TimerTask;

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

        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                // Ask for data every 30 secs
                RequestData(this);
            }
        };

        timer.schedule(myTask, 30000, 30000);

        return view;
    }


    private void RequestData(TimerTask timerTask)
    {
        // Get all events related to process and brew crafting from server
        String json = "";
        new GetController(this).execute("/retrieve/events/"+_idProcess, json);
    }

    public synchronized void onResume() {
        super.onResume();
    }

    public void ProcessFinish(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try {
            BrewProcess boilProcess = gson.fromJson(output, BrewProcess.class);
            List<Event> events = boilProcess.getEvents();

            txtViewPrimaryValue.setText(String.valueOf(events.get(events.size()-1).getValue()));
            LineGraphSeries series = new LineGraphSeries<DataPoint>();
            for (Event event : events ) {
                series.appendData(new DataPoint(event.getTime(), event.getValue()), true, events.size());
            }

            _graph.addSeries(series);
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }
    }
}