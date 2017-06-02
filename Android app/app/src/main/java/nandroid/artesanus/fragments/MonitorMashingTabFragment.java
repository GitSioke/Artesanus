package nandroid.artesanus.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import nandroid.artesanus.adapter.LogAdapter;
import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.XYValue;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.gui.NewBeerCraftingActivity;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.messages.MessageInfo;
import nandroid.artesanus.messages.MessageInfoMasher;

/**
 * This class is the fragment that manage the mashing tab in monitoring activity
 */
public class MonitorMashingTabFragment extends Fragment implements IAsyncHttpResponse
{
    private LogAdapter logAdapter;
    private ListView logListView;
    private TextView txtViewPrimaryValue;
    private int _idProcess;
    private GraphView _graph;
    private LineGraphSeries<DataPoint> _dataPoints;
    private Handler _handler;

    // Debugging
    private static final String TAG = "MonitorMashingTabFragment";
    private static final boolean D = true;


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

        ArrayList<MessageInfo> msgInfoList = new ArrayList<MessageInfo>();
        logAdapter  = new LogAdapter(msgInfoList, getContext());
        logListView = (ListView)view.findViewById(R.id.monitoring_log_lv);
        logListView.setAdapter(logAdapter);

        txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);

        _graph = (GraphView)view.findViewById(R.id.graph);
        _dataPoints = new LineGraphSeries<>();

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        for (int i = 0; i<20; i++)
        {
            Random r = new Random();
            int low = 30;
            int high = 100;
            int data = r.nextInt(high-low) + low;
            Date date2 = new Date(date.getTime()+60000*i);
            _dataPoints.appendData(new DataPoint(date2, data), true, 30);
        }



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

        _dataPoints = new LineGraphSeries<DataPoint>();
        _dataPoints.setColor(Color.RED);
        _dataPoints.setDataPointsRadius(10);
        _dataPoints.setThickness(3);

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


    public void onMsgreceived(ArrayList<MessageInfoMasher> msgs)
    {
        for (MessageInfoMasher msg : msgs)
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
            BrewProcess mashingBrewProcess = gson.fromJson(output, BrewProcess.class);
            List<Event> events = mashingBrewProcess.getEvents();

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
