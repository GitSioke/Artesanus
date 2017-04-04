package nandroid.artesanus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.common.BTConstants;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.messages.MessageInfo;

/**
 * This is the log adapter for tab fragments
 */
public class LogAdapter extends ArrayAdapter<MessageInfo> implements View.OnClickListener
{
    private static ArrayList<MessageInfo> dataSet;
    Context mContext;

    public LogAdapter(ArrayList<MessageInfo> data, Context context)
    {
        super(context, R.layout.list_row_log, data);
        this.dataSet = data;
        this.mContext=context;
    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtDate;
        TextView txtInfo;
    }

    @Override
    public void onClick(View v)
    {
        int position = (Integer)v.getTag();
        Object object = getItem(position);
        MessageInfo dataModel= (MessageInfo)object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        MessageInfo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_log, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.lv_row_log_date);
            viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.lv_row_log_info_text);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtDate.setText(dataModel.getDate().toString());
        viewHolder.txtInfo.setText(dataModel.getInformation());

        // Set text color depends on the priority of the received message.
        switch(dataModel.getPriority())
        {
            case BTConstants.LOW_PRIORITY:
                viewHolder.txtInfo.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                break;
            case BTConstants.MEDIUM_PRIORITY:
                viewHolder.txtInfo.setTextColor(ContextCompat.getColor(mContext, R.color.darkYellow));
                break;
            case BTConstants.HIGH_PRIORITY:
                viewHolder.txtInfo.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void remove(int position)
    {
        if (position >= 0 && dataSet.size() < position && dataSet.get(position) != null) {
            dataSet.remove(position);
        }
    }

    public void add(MessageInfo msg)
    {
        dataSet.add(msg);
    }

    public void remove(MessageInfo msg)
    {
        dataSet.remove(msg);
    }
}
