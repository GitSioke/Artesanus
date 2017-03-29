package nandroid.artesanus.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Excluder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nandroid.artesanus.adapter.LogAdapter;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.GetController;
import nandroid.artesanus.common.IAsyncHttpResponse;
import nandroid.artesanus.common.Process;
import nandroid.artesanus.gui.BluetoothActivity;
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

        // TODO Get from Intent the brew id and process id
        _idProcess = 1;


        // Get all events related to process and brew crafting from server
        String json = "";
        new GetController(this).execute("/retrieve/events/"+_idProcess, json);

        ArrayList<MessageInfoMasher> messages = new ArrayList<MessageInfoMasher>();
        messages.add(new MessageInfoMasher(1000,1, Calendar.getInstance().getTime(), "Yi"));
        messages.add(new MessageInfoMasher(1024,2, Calendar.getInstance().getTime(), "Yo"));
        messages.add(new MessageInfoMasher(1040,0, Calendar.getInstance().getTime(), "Yu"));
        messages.add(new MessageInfoMasher(1050,2, Calendar.getInstance().getTime(), "Yob"));
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
        Gson gson = builder.create();
        try {
            Process mashingProcess = gson.fromJson(output, Process.class);
            List<Event> events = mashingProcess.getEvents();
            txtViewPrimaryValue.setText(String.valueOf(events.get(0).getValue()));
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }
    }
}
