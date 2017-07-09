package nandroid.artesanus.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nandroid.artesanus.adapter.MashingLogAdapter;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.http.IIsOpenValveAsyncHttpResponse;
import nandroid.artesanus.http.IsOpenValveGetController;
import nandroid.artesanus.http.PostController;

/**
 * This class is the fragment that manage the mashing tab in monitoring activity
 */
public class MonitorMashingTabFragment extends Fragment
        implements IAsyncHttpResponse,
        IIsOpenValveAsyncHttpResponse
{
    private MashingLogAdapter logAdapter;
    private ListView logListView;
    private TextView _txtViewPrimaryValue;
    private TextView _txtViewMillilitresValue;
    private int _idProcess;
    private GraphView _graph;
    private LineGraphSeries<DataPoint> _dataPoints;
    private Handler _handler;
    private RelativeLayout _containerLayout;

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

        _containerLayout = (RelativeLayout)view.findViewById(R.id.parent_rv);
        MonitoringActivity act = (MonitoringActivity)getActivity();
        _idProcess = act.GetIdMashing();

        ArrayList<Event> msgInfoList = new ArrayList<Event>();
        logAdapter  = new MashingLogAdapter(msgInfoList, getContext());
        logListView = (ListView)view.findViewById(R.id.monitoring_log_lv);
        logListView.setAdapter(logAdapter);

        _txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);
        _txtViewMillilitresValue = (TextView) view.findViewById(R.id.monitor_millilitres_value);
        _txtViewValveValue = (TextView)view.findViewById(R.id.monitor_valve_value);

        _graph = (GraphView)view.findViewById(R.id.graph);
        //ConfigureGraphView();

        Timer timer = new Timer();
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                // Ask for data every 30 secs
                RequestData(this);
            }
        };
        timer.schedule(myTask, 1000, 30000);

        /*Timer timer2 = new Timer();
        TimerTask myTask2 = new TimerTask() {
            @Override
            public void run() {
                // Ask for data every 30 secs
                RequestCommands(this);
            }
        };
        timer2.schedule(myTask2, 2000, 30000);

        Timer timer3 = new Timer();
        TimerTask myTask3 = new TimerTask() {
            @Override
            public void run() {
                // Ask for data every 30 secs
                RequestOpenValve(this);
            }
        };
        timer.schedule(myTask3, 4000, 30000);

        Timer timer4 = new Timer();
        TimerTask myTask4 = new TimerTask() {
            @Override
            public void run() {
                // Ask for data every 30 secs
                RequestMillilitres(this);
            }
        };
        timer.schedule(myTask4, 6000, 30000);*/

        _dataPoints = new LineGraphSeries<>();

        ConfigureGraphView();


        return view;
    }

    private void ConfigureGraphView()
    {

        _dataPoints.setColor(Color.RED);
        _dataPoints.setThickness(10);
        _dataPoints.setTitle(getString(R.string.monitor_graph_legend));
        _graph.addSeries(_dataPoints);

        _graph.getGridLabelRenderer().setHumanRounding(false);
        _graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        _graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(getActivity(),
                        new SimpleDateFormat("HH:mm:ss")));
        _graph.getLegendRenderer().setVisible(true);
        _graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        _graph.onDataChanged(false,false);
    }

    private void RequestData(TimerTask timerTask)
    {
        String eventsJSON = "{\"type\":\"data\",\"data\":\"temperature\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, eventsJSON);

        // Get millilitres from
        String millilitresJSON = "{\"type\":\"data\",\"data\":\"millilitres\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, millilitresJSON);

        //Get commands
        String commandsJSON = "{\"type\":\"command\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, commandsJSON);

        new IsOpenValveGetController(this).execute("/is_valve_open/"+_idProcess, "");
    }

    private void RequestMillilitres(TimerTask timerTask)
    {
        // Get millilitres from
        String millilitresJSON = "{\"type\":\"data\",\"data\":\"millilitres\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, millilitresJSON);
    }

    private void RequestCommands(TimerTask timerTask)
    {

    }

    private void RequestOpenValve(TimerTask timerTask)
    {
        new IsOpenValveGetController(this).execute("/is_valve_open/"+_idProcess, "");
    }

    public synchronized void onResume() {
        super.onResume();
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

                    if (events.size()>1)
                    {
                        DataPoint values[] = new DataPoint[events.size()];
                        int pos = 0;
                        for (Event event : events) {
                            if (event != null)
                            {
                                values[pos] = (new DataPoint(event.getTime().getTime(), event.getValue()));
                                pos++;
                            }
                        }

                        _dataPoints.resetData(values);

                        _graph.getViewport().setMaxX(events.get(events.size() - 1).getTime().getTime());
                        _graph.getViewport().setMinX(events.get(0).getTime().getTime());
                        _graph.getViewport().setXAxisBoundsManual(true);
                    }

                }
                else if (firstEvent.getData() != null && firstEvent.getData().equals("millilitres"))
                {
                    //Update text view with received mililitres
                    _txtViewMillilitresValue.setText(String.valueOf(events.get(0).getValue()));
                }
                else if (firstEvent.getType() != null && firstEvent.getType().equals("command"))
                {
                    logAdapter.clear();
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
            Snackbar.make(getView(),
                    getResources().getString(R.string.communication_error),
                    Snackbar.LENGTH_LONG)
                    .show();
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
            Snackbar.make(getView(),
                    getResources().getString(R.string.communication_error),
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

}
