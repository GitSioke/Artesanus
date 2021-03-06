package nandroid.artesanus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import nandroid.artesanus.common.Heat;
import nandroid.artesanus.listener.*;
import nandroid.artesanus.gui.R;

/**
 *  This adapter class deals with Heat elements added.
 */
public class HeatAddedAdapter extends ArrayAdapter<Heat> implements View.OnClickListener
{
    private static ArrayList<Heat> dataSet;
    Context mContext;
    private static OnHeatRemovedListener mOnItemClickLister;

    public HeatAddedAdapter(ArrayList<Heat> data, Context context)
    {
        super(context, R.layout.list_row_heat_added, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public void setHeatRemovedListener(OnHeatRemovedListener listener)
    {
        mOnItemClickLister = listener;
    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtTemperature;
        TextView txtStart;
        TextView txtDuration;
        ImageView imgRemove;
    }

    @Override
    public void onClick(View v)
    {
        int position= (Integer)v.getTag();
        Object object= getItem(position);
        Heat dataModel= (Heat)object;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Heat dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_heat_added, parent, false);
            viewHolder.txtTemperature = (TextView) convertView.findViewById(R.id.row_heat_added_temperature);
            viewHolder.txtStart = (TextView) convertView.findViewById(R.id.row_heat_added_start);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.row_heat_added_duration);
            viewHolder.imgRemove = (ImageView) convertView.findViewById(R.id.row_heat_added_remove_btn);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtTemperature.setText(Integer.toString(dataModel.getTemperature()));
        viewHolder.txtStart.setText(Integer.toString(dataModel.getStartTime()
        ));
        viewHolder.txtDuration.setText(Integer.toString(dataModel.getDuration()));

        viewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.remove(position);
                mOnItemClickLister.onHeatRemoved(dataSet);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public void remove(int position)
    {
        if (position >= 0 && dataSet.size() < position && dataSet.get(position) != null) {
            dataSet.remove(position);
        }
    }

    public void add(Heat heat)
    {
        dataSet.add(heat);
    }

    public void remove(Heat heat)
    {
        dataSet.remove(heat);
    }
}
