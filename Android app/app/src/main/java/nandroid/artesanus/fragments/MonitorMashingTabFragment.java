package nandroid.artesanus.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nandroid.artesanus.adapter.LogAdapter;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.http.IIsOpenValveAsyncHttpResponse;
import nandroid.artesanus.http.IsOpenValveGetController;

/**
 * This class is the fragment that manage the mashing tab in monitoring activity
 */
public class MonitorMashingTabFragment extends Fragment
        implements IAsyncHttpResponse,
        IIsOpenValveAsyncHttpResponse
{
    private LogAdapter logAdapter;
    private ListView logListView;
    private TextView _txtViewPrimaryValue;
    private TextView _txtViewMillilitresValue;
    private int _idProcess;
    private GraphView _graph;
    private LineGraphSeries<DataPoint> _dataPoints;
    private Handler _handler;

    // Debugging
    private static final String TAG = "MonitorMashingTabFragment";
    private static final boolean D = true;
    private TextView _txtViewValveValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_monitoring_mashing, container, false);

        MonitoringActivity act = (MonitoringActivity)getActivity();
        _idProcess = act.GetIdMashing();

        ArrayList<Event> msgInfoList = new ArrayList<Event>();
        logAdapter  = new LogAdapter(msgInfoList, getContext());
        logListView = (ListView)view.findViewById(R.id.monitoring_log_lv);
        logListView.setAdapter(logAdapter);

        _txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);
        _txtViewMillilitresValue = (TextView) view.findViewById(R.id.monitor_millilitres_value);
        _txtViewValveValue = (TextView)view.findViewById(R.id.monitor_valve_value);

        _graph = (GraphView)view.findViewById(R.id.graph);
        ConfigureGraphView();

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

    private void ConfigureGraphView()
    {
        _dataPoints = new LineGraphSeries<>();
        _dataPoints.setColor(Color.RED);
        _dataPoints.setDataPointsRadius(10);
        _dataPoints.setThickness(3);
        _dataPoints.setDataPointsRadius(2);
        _dataPoints.setTitle("Temperature");

        _graph.getGridLabelRenderer().setHumanRounding(false);
        _graph.addSeries(_dataPoints);
        _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        _graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        _graph.getViewport().setMinY(0);
        _graph.getViewport().setMaxY(100);
        _graph.getViewport().setScrollable(true); // enables horizontal scrolling
        _graph.getViewport().setScrollableY(true); // enables vertical scrolling
        _graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        _graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        _graph.getViewport().setBorderColor(Color.MAGENTA);

    }

    private void RequestData(TimerTask timerTask)
    {
        // Get all events related to process and brew crafting from server
        //String eventsJSON = "{\"type\":\"data\",\"data\":\"temperature\"}";
        //new PostController(this).execute("/retrieve/events/"+_idProcess, eventsJSON);

        // Get millilitres from
        //String millilitresJSON = "{\"type\":\"data\",\"data\":\"millilitres\"}";
        //new PostController(this).execute("/retrieve/events/"+_idProcess, millilitresJSON);

        //Get commands
        //String commandsJSON = "{\"type\":\"command\"}";
        //new PostController(this).execute("/retrieve/events/"+_idProcess, commandsJSON);

        new IsOpenValveGetController(this).execute("/is_valve_open/"+_idProcess, "");
    }

    public synchronized void onResume() {
        super.onResume();
    }


    public void onMsgreceived(ArrayList<Event> msgs)
    {
        for (Event msg : msgs)
        {
            logAdapter.add(msg);
        }

        logListView.setAdapter(logAdapter);
    }


    @SuppressLint("LongLogTag")
    @Override
    public void ProcessFinish(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try {

            Type eventListType = new TypeToken<ArrayList<Event>>(){}.getType();
            List<Event> events = gson.fromJson(output, eventListType);

            if (events != null && events.size()>0)
            {
                Event firstEvent = events.get(0);
                if (firstEvent.getData() != null && firstEvent.getData().equals("temperature")) {
                    _txtViewPrimaryValue.setText(String.valueOf(events.get(events.size() - 1).getValue()));
                    for (Event event : events) {
                        if (event != null) {
                            _dataPoints.appendData(new DataPoint(event.getTime(), event.getValue()), true, events.size());
                        }
                    }
                    _graph.getViewport().setMaxX(events.get(events.size() - 1).getTime().getTime());
                    _graph.getViewport().setMinX(events.get(0).getTime().getTime());
                    _graph.getViewport().setXAxisBoundsManual(true);
                }
                else if (firstEvent.getData() != null && firstEvent.getData().equals("millilitres"))
                {
                    //Update text view with received mililitres
                    _txtViewMillilitresValue.setText(String.valueOf(events.get(0).getValue()));
                }
                else if (firstEvent.getType() != null && firstEvent.getType().equals("command"))
                {
                    // Update log list
                    for (Event msg : events)
                    {
                        logAdapter.add(msg);
                    }

                    logListView.setAdapter(logAdapter);
                }
            }
        }
        catch (Exception ex)
        {
                if(D) Log.e(TAG, ex.getMessage());
        }
    }

    public void ProcessIsOpenValveResponse(String output)
    {
        JsonParser parser = new JsonParser();

        try
        {
            JsonObject obj = parser.parse(output).getAsJsonObject();
            JsonElement element = obj.get("res");
            boolean isOpen = element.getAsBoolean();

            ImageView view = (ImageView)getView().findViewById(R.id.valve_icon);
            if (isOpen)
            {
                _txtViewValveValue.setText(getResources().getString(R.string.is_open));
                view.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.valve_green));
            }
            else
            {
                _txtViewValveValue.setText(getResources().getString(R.string.is_closed));
                view.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.valve_red));
            }

        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }
    }

}
