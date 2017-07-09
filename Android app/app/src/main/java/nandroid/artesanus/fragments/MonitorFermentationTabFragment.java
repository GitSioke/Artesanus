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
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nandroid.artesanus.adapter.FermentationLogAdapter;
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
public class MonitorFermentationTabFragment extends Fragment
        implements IAsyncHttpResponse,
        IIsOpenValveAsyncHttpResponse
{
    private FermentationLogAdapter logAdapter;
    private ListView logListView;
    private TextView _txtViewPrimaryValue;
    private TextView _txtViewMillilitresValue;
    private int _idProcess;
    private GraphView _graph;
    private LineGraphSeries<DataPoint> _dataPoints;

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




        View view  = inflater.inflate( R.layout.fragment_monitoring_fermenter, container, false);

        final ImageView imgDensityButton = (ImageView)view.findViewById(R.id.monitor_add_density_icon);
        final EditText densityEditText = (EditText)view.findViewById(R.id.monitor_density_edit_text);
        final ImageView imgConfirmButton = (ImageView)view.findViewById(R.id.monitor_add_density_confirm);
        final TextView txSecondValue = (TextView)view.findViewById(R.id.monitor_densiometer_value);

        imgConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the views
                densityEditText.setVisibility(View.INVISIBLE);
                imgConfirmButton.setVisibility(View.INVISIBLE);
                imgDensityButton.setVisibility(View.VISIBLE);
                txSecondValue.setVisibility(View.VISIBLE);

                // Add density to database
                GsonBuilder builder = new GsonBuilder();
                builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                Gson gson = builder.create();

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();

                Event event = new Event.Builder()
                        .time(date)
                        .source("fermentation")
                        .data("density")
                        .value(Double.parseDouble(densityEditText.getText().toString()))
                        .type("data")
                        .id_process(_idProcess)
                        .build();


                String json = gson.toJson(event);
                new PostController(MonitorFermentationTabFragment.this)
                        .execute("/insert/last_density/fermentation/", json);

                // Show to user the operation has been performed
                StringBuilder strBuild = new StringBuilder()
                        .append(getResources().getString(R.string.density_added))
                        .append(event.getValue())
                        .append(getResources().getString(R.string.density_measure));

                Snackbar.make(v,
                        strBuild.toString(),
                        Snackbar.LENGTH_SHORT)
                        .show();

                // Modify view
                txSecondValue.setText(Double.toString(event.getValue()));

                // Hide and show the views
                densityEditText.setVisibility(View.INVISIBLE);
                imgConfirmButton.setVisibility(View.INVISIBLE);
                imgDensityButton.setVisibility(View.VISIBLE);
                txSecondValue.setVisibility(View.VISIBLE);
            }
        });

        imgDensityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                densityEditText.setVisibility(View.VISIBLE);
                imgConfirmButton.setVisibility(View.VISIBLE);
                imgDensityButton.setVisibility(View.INVISIBLE);
                txSecondValue.setVisibility(View.INVISIBLE);
            }
        });

        MonitoringActivity act = (MonitoringActivity)getActivity();
        _idProcess = act.GetIdFermentation();

        ArrayList<Event> msgInfoList = new ArrayList<Event>();
        logAdapter  = new FermentationLogAdapter(msgInfoList, getContext());
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

        _dataPoints = new LineGraphSeries<>();

        ConfigureGraphView();

        timer.schedule(myTask, 30000, 30000);

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
        // Get all events related to process and brew crafting from server
        /*String eventsJSON = "{\"type\":\"data\",\"data\":\"temperature\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, eventsJSON);

        // Get millilitres from
        String millilitresJSON = "{\"type\":\"data\",\"data\":\"millilitres\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, millilitresJSON);

        //Get commands
        String commandsJSON = "{\"type\":\"command\"}";
        new PostController(this).execute("/retrieve/events/"+_idProcess, commandsJSON);

        new IsOpenValveGetController(this).execute("/is_valve_open/"+_idProcess, "");*/
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

//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState)
//    {
//        MonitoringActivity act = (MonitoringActivity)getActivity();
//        _idProcess = act.GetIdFermentation();
//
//        View view  = inflater.inflate( R.layout.fragment_monitoring_fermenter, container, false);
//        _graph = (GraphView)view.findViewById(R.id.graph);
//
//        final ImageView imgDensityButton = (ImageView)view.findViewById(R.id.monitor_add_density_icon);
//        final EditText densityEditText = (EditText)view.findViewById(R.id.monitor_density_edit_text);
//        final ImageView imgConfirmButton = (ImageView)view.findViewById(R.id.monitor_add_density_confirm);
//        final TextView txSecondValue = (TextView)view.findViewById(R.id.monitor_second_value);
//
//        imgConfirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Hide and show the views
//                densityEditText.setVisibility(View.INVISIBLE);
//                imgConfirmButton.setVisibility(View.INVISIBLE);
//                imgDensityButton.setVisibility(View.VISIBLE);
//                txSecondValue.setVisibility(View.VISIBLE);
//
//                // Add density to database
//                GsonBuilder builder = new GsonBuilder();
//                builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
//                Gson gson = builder.create();
//
//                Calendar cal = Calendar.getInstance();
//                Date date = cal.getTime();
//
//                Event event = new Event.Builder()
//                        .time(date)
//                        .source("fermentation")
//                        .data("density")
//                        .value(Double.parseDouble(densityEditText.getText().toString()))
//                        .type("data")
//                        .id_process(_idProcess)
//                        .build();
//
//
//                String json = gson.toJson(event);
//                new PostController(MonitorFermentationTabFragment.this)
//                        .execute("/insert/last_density/fermentation/", json);
//
//                // Show to user the operation has been performed
//                StringBuilder strBuild = new StringBuilder()
//                        .append(getResources().getString(R.string.density_added))
//                        .append(event.getValue())
//                        .append(getResources().getString(R.string.density_measure));
//
//                Snackbar.make(v,
//                        strBuild.toString(),
//                        Snackbar.LENGTH_SHORT)
//                        .show();
//
//                // Modify view
//                txSecondValue.setText(Double.toString(event.getValue()));
//
//                // Hide and show the views
//                densityEditText.setVisibility(View.INVISIBLE);
//                imgConfirmButton.setVisibility(View.INVISIBLE);
//                imgDensityButton.setVisibility(View.VISIBLE);
//                txSecondValue.setVisibility(View.VISIBLE);
//            }
//        });
//
//        imgDensityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                densityEditText.setVisibility(View.VISIBLE);
//                imgConfirmButton.setVisibility(View.VISIBLE);
//                imgDensityButton.setVisibility(View.INVISIBLE);
//                txSecondValue.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);
//
//        Timer timer = new Timer();
//        TimerTask myTask = new TimerTask() {
//            @Override
//            public void run() {
//                // Ask for data every 30 secs
//                RequestData(this);
//            }
//        };
//
//        timer.schedule(myTask, 30000, 30000);
//
//        return view;
//    }
//
//    private void RequestData(TimerTask timerTask)
//    {
//        // Get all events related to process and brew crafting from server
//        String json = "";
//        new GetController(this).execute("/retrieve/events/"+_idProcess, json);
//    }
//
//    public synchronized void onResume() {
//        super.onResume();
//    }
//
//    public void ProcessFinish(String output)
//    {
//        GsonBuilder builder = new GsonBuilder();
//        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
//        Gson gson = builder.create();
//        try {
//            BrewProcess fermentationBrewProcess = gson.fromJson(output, BrewProcess.class);
//            List<Event> events = fermentationBrewProcess.getEvents();
//
//            txtViewPrimaryValue.setText(String.valueOf(events.get(events.size()-1).getValue()));
//            LineGraphSeries series = new LineGraphSeries<DataPoint>();
//            for (Event event : events ) {
//                series.appendData(new DataPoint(event.getTime(), event.getValue()), true, events.size());
//            }
//
//            _graph.addSeries(series);
//        }
//        catch (Exception ex)
//        {
//            if(D) Log.e(TAG, ex.getMessage());
//            Snackbar.make(getView(),
//                getResources().getString(R.string.communication_error),
//                Snackbar.LENGTH_LONG)
//                .show();
//        }
//    }
//}
