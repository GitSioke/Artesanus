package nandroid.artesanus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.R;

/**
 * This is the log adapter for tab fragments
 */
public class FermentationLogAdapter extends ArrayAdapter<Event> implements View.OnClickListener
{
    private static String PACKAGE_NAME;
    private static ArrayList<Event> dataSet;
    Context mContext;

    public FermentationLogAdapter(ArrayList<Event> data, Context context)
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
        Event dataModel= (Event)object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Event dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        PACKAGE_NAME = mContext.getPackageName();

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

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.txtDate.setText(formatter.format(dataModel.getTime()));

        // We format the command received from database
        String identifier = "command_" + dataModel.getMessage();
        int resId = mContext.getResources().getIdentifier(identifier, "string", PACKAGE_NAME);
        viewHolder.txtInfo.setText(mContext.getResources().getString(resId));


        // Return the completed view to render on screen
        return result;
    }

    public void remove(int position)
    {
        if (position >= 0 && dataSet.size() < position && dataSet.get(position) != null) {
            dataSet.remove(position);
        }
    }

    public void add(Event msg)
    {
        dataSet.add(msg);
    }

    public void remove(Event msg)
    {
        dataSet.remove(msg);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataSet != null)
        {
            count = dataSet.size();
        }
        return count;
    }
}
