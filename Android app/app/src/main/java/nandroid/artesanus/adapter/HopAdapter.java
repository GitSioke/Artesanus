package nandroid.artesanus.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.common.Hop;
import nandroid.artesanus.gui.R;


public class HopAdapter extends ArrayAdapter<Hop> implements View.OnClickListener
{
    private static ArrayList<Hop> dataSet;
    Context mContext;

    public HopAdapter(ArrayList<Hop> data, Context context)
    {
        super(context, R.layout.list_row_hop, data);
        this.dataSet = data;
        this.mContext=context;
    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtName;
        TextView txtAmount;
        TextView txtMinute;
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
            convertView = inflater.inflate(R.layout.list_row_hop, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtAmount = (EditText) convertView.findViewById(R.id.hop_amount);
            viewHolder.txtMinute = (EditText) convertView.findViewById(R.id.time);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());

        viewHolder.txtAmount.setText(String.valueOf(dataModel.getQuantity()));
        viewHolder.txtMinute.setText(String.valueOf(dataModel.getMinutes()));

        // Return the completed view to render on screen
        return convertView;
    }
}
