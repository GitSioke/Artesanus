package nandroid.artesanus.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import nandroid.artesanus.adapter.LogAdapter;
import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Event;
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
    private LineGraphSeries _dataSeries;

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

        ArrayList<MessageInfo> msgInfoList = new ArrayList<MessageInfo>();
        logAdapter  = new LogAdapter(msgInfoList, getContext());
        logListView = (ListView)view.findViewById(R.id.monitoring_log_lv);
        logListView.setAdapter(logAdapter);

        txtViewPrimaryValue = (TextView) view.findViewById(R.id.monitor_main_value);

       double x,y;
        x = 0.0;

        _graph = (GraphView)view.findViewById(R.id.graph);


        // TODO Get from Intent the brew id and process id
        _idProcess = 1;

        // Get all events related to process and brew crafting from server
        String json = "";
        new GetController(this).execute("/retrieve/events/"+_idProcess, json);

        ArrayList<MessageInfoMasher> messages = new ArrayList<MessageInfoMasher>();
        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();
        long time = date1.getTime();
        time += 12387;
        Date date2 = new Date(time);
        time += 12344;
        Date date3 = new Date(time);
        time += 1234462;
        Date date4 = new Date(time);
        messages.add(new MessageInfoMasher(1000,1, date1, "Proceso iniciado"));
        messages.add(new MessageInfoMasher(1024,2, date2, "Alarma de temperatura"));
        messages.add(new MessageInfoMasher(1040,2, date3, "Alarma de temperatura"));
        messages.add(new MessageInfoMasher(1050,1, date4, "Proceso finalizado"));
        onMsgreceived(messages);

        return view;
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
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public class WebAppInterface
    {
        Context mContext;

        int timer =0;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public int getNextValue() {
            return 10;
        }

        @JavascriptInterface
        public int getNextTime() {
            return timer++;
        }

        @JavascriptInterface
        public int getTotal() {
            return 3;
        }

    }
}
