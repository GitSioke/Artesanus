package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.adapter.CerealAdapter;
import nandroid.artesanus.gui.R;

/**
 * Created by Nando on 10/11/2016.
 */
public class AddCerealFragment extends DialogFragment
{

    public static final String LOGTAG = "AddCerealFragment";
    ArrayList<Cereal> dataModels;
    ListView listView;
    private static CerealAdapter adapter;

    public interface OnCerealAddedListener{
        void onCerealAdded(ArrayList<Cereal> cereals);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        dataModels= new ArrayList<>();

        dataModels.add(new Cereal("  Pie"));
        dataModels.add(new Cereal("Banana Bread"));
        dataModels.add(new Cereal("Cupcake"));
        dataModels.add(new Cereal("Donut"));
        dataModels.add(new Cereal("Eclair"));
        dataModels.add(new Cereal("Froyo"));
        dataModels.add(new Cereal("Gingerbread"));
        dataModels.add(new Cereal("Honeycomb"));
        dataModels.add(new Cereal("Ice Cream Sandwich"));
        dataModels.add(new Cereal("Jelly Bean"));
        dataModels.add(new Cereal("Kitkat"));
        dataModels.add(new Cereal("Lollipop"));
        dataModels.add(new Cereal("Marshmallow"));

        final CerealAdapter adapter = new CerealAdapter(dataModels, getContext());

        final View view  = inflater.inflate( R.layout.dialog_add_cereals, container, false);

        final ListView listView = (ListView)view.findViewById(R.id.dialog_add_cereals_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cereal dataModel = dataModels.get(position);

                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getName() + " API: " + dataModel.getQuantity(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        TextView tvOk = (TextView)view.findViewById(R.id.cereal_ok_button);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Cereal> addedCereals = new ArrayList<Cereal>();

                for (int children = 0; children < listView.getChildCount(); children++)
                {
                    View view = listView.getChildAt(children);
                    EditText ed = (EditText)view.findViewById(R.id.cereal_amount);
                    int amountToAdd = Integer.parseInt(ed.getText().toString());
                    if (amountToAdd > 0)
                    {
                        Cereal cereal = dataModels.get(children);
                        cereal.setQuantity(amountToAdd);
                        addedCereals.add(cereal);
                    }
                }

                FireCerealAddedListener(addedCereals);
                dismiss();
            }
        });

        TextView tvCancel = (TextView)view.findViewById(R.id.cereal_cancel_button);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void FireCerealAddedListener(ArrayList<Cereal> addedCereals)
    {
        Fragment fragment = getParentFragment();
        if (fragment instanceof OnCerealAddedListener) {
            {
                OnCerealAddedListener listener = (OnCerealAddedListener) fragment;
                listener.onCerealAdded(addedCereals);
            }
        }

        if (getActivity() instanceof OnCerealAddedListener)
        {
            OnCerealAddedListener listener = (OnCerealAddedListener) getActivity();
            listener.onCerealAdded(addedCereals);
        }

    }
}
