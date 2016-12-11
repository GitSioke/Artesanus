package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import nandroid.artesanus.adapter.LogAdapter;
import nandroid.artesanus.gui.BluetoothActivity;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.messages.MessageInfo;
import nandroid.artesanus.messages.MessageInfoMasher;

/**
 * This class is the fragment that manage the mashing tab in monitoring activity
 */
public class MonitorMashingTabFragment extends Fragment
{
    private LogAdapter logAdapter;
    private ListView logListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_monitoring_mashing, container, false);

        Button buttonStart = (Button)view.findViewById(R.id.monitoring_button_initilice);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BluetoothActivity)getActivity()).ask();
            }
        });


        ArrayList<MessageInfo> msgInfoList = new ArrayList<MessageInfo>();
        logAdapter  = new LogAdapter(msgInfoList, getContext());
        logListView = (ListView)view.findViewById(R.id.monitoring_log_lv);
        logListView.setAdapter(logAdapter);

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


}
