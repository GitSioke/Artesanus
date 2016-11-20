package nandroid.artesanus.gui;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Hop;

/**
 * Created by Nando on 15/11/2016.
 */
public class HopAddedAdapter extends ArrayAdapter<Hop> implements View.OnClickListener
{
    private static ArrayList<Hop> dataSet;
    Context mContext;

    public HopAddedAdapter(ArrayList<Hop> data, Context context)
    {
        super(context, R.layout.list_row_hop_added, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public interface OnItemRemoved
    {

    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtName;
        TextView txtAmount;
        TextView txtMinutes;
        ImageView imgRemove;
    }

    @Override
    public void onClick(View v)
    {
        int position= (Integer)v.getTag();
        Object object= getItem(position);
        Hop dataModel= (Hop)object;

        switch (v.getId())
        {
            case R.id.hop_amount:
                Snackbar.make(v, "Release date " + dataModel.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Hop dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_hop_added, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.row_hop_added_txtName);
            viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.row_hop_added_amount);
            viewHolder.txtMinutes = (TextView) convertView.findViewById(R.id.row_hop_added_time);
            viewHolder.imgRemove = (ImageView) convertView.findViewById(R.id.row_cereal_added_cancel_btn);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAmount.setText(Integer.toString(dataModel.getAmount()));
        /*viewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Remove element from list
                Integer index = (Integer) v.getTag();
                dataSet.remove(index.intValue());
                notifyDataSetChanged();
            }
        });*/

        // Return the completed view to render on screen
        return convertView;
    }

    public void remove(int position)
    {
        if (position >= 0 && dataSet.size() < position && dataSet.get(position) != null) {
            dataSet.remove(position);
        }
    }

    public void add(Hop hop)
    {
        dataSet.add(hop);
    }
}