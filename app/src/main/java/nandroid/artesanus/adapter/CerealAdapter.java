package nandroid.artesanus.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.gui.R;


public class CerealAdapter extends ArrayAdapter<Cereal> implements View.OnClickListener
{
    private static ArrayList<Cereal> dataSet;
    Context mContext;

    public CerealAdapter(ArrayList<Cereal> data, Context context)
    {
        super(context, R.layout.list_row_cereal, data);
        this.dataSet = data;
        this.mContext=context;
    }

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtName;
        TextView txtAmount;
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
            convertView = inflater.inflate(R.layout.list_row_cereal, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtAmount = (EditText) convertView.findViewById(R.id.cereal_amount);

            result=convertView;

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtAmount.setText(Integer.toString(dataModel.getAmount()));

        // Return the completed view to render on screen
        return convertView;
    }
}
