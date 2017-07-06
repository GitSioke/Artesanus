package nandroid.artesanus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nandroid.artesanus.common.Brew;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.R;

/**
 * This is the log adapter for tab fragments
 */
public class BrewListAdapter extends ArrayAdapter<Brew> implements View.OnClickListener
{
    private static ArrayList<Brew> dataSet;
    Context mContext;

    public BrewListAdapter(ArrayList<Brew> data, Context context)
    {
        super(context, R.layout.list_row_brew, data);
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
        Brew dataModel= (Brew)object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Brew dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_brew, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.lv_row_brew_date);
            viewHolder.txtInfo = (TextView) convertView.findViewById(R.id.lv_row_brew_info_text);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.txtDate.setText(formatter.format(dataModel.getStartDate()));
        viewHolder.txtInfo.setText(dataModel.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    public void remove(int position)
    {
        if (position >= 0 && dataSet.size() < position && dataSet.get(position) != null) {
            dataSet.remove(position);
        }
    }

    public void add(Brew brew)
    {
        dataSet.add(brew);
    }

    public void remove(Brew brew)
    {
        dataSet.remove(brew);
    }

    public void removeAll() {dataSet.clear();}
}
