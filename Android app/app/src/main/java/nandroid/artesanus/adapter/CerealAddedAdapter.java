package nandroid.artesanus.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.listener.OnCerealRemovedListener;

/**
 *  This Adapter class link Cereal(model) class with AddedCerealFragment(view)
 */
public class CerealAddedAdapter extends ArrayAdapter<Cereal> implements View.OnClickListener
{
    private static ArrayList<Cereal> dataSet;
    Context mContext;
    private static OnCerealRemovedListener mOnItemClickLister;

    public void setOnCerealRemovedListener(OnCerealRemovedListener listener)
    {
        mOnItemClickLister = listener;
    }

    public CerealAddedAdapter(ArrayList<Cereal> data, Context context)
    {
        super(context, R.layout.list_row_cereal_added, data);
        this.dataSet = data;
        this.mContext = context;
    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtName;
        TextView txtAmount;
        ImageView imgRemove;
    }

    @Override
    public void onClick(View v)
    {
        int position= (Integer)v.getTag();
        Object object= getItem(position);
        Cereal dataModel= (Cereal)object;

        switch (v.getId())
        {
            case R.id.cereal_amount:
                Snackbar.make(v, "Release date " + dataModel.getName(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Cereal dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_cereal_added, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.row_cereal_added_txtName);
            viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.row_cereal_added_amount);
            viewHolder.imgRemove = (ImageView) convertView.findViewById(R.id.row_cereal_added_cancel_btn);
            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAmount.setText(Integer.toString(dataModel.getQuantity()));
        viewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dataSet.remove(position);
                mOnItemClickLister.onCerealRemoved(dataSet);
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

    public void add(Cereal cereal)
    {
        dataSet.add(cereal);
    }
}
