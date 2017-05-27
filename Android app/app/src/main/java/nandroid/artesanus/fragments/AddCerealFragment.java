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
import java.util.Arrays;
import java.util.List;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.adapter.CerealAdapter;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.listener.OnCerealAddedListener;

/**
 * This class represents a dialog fragment to add cereals.
 */
public class AddCerealFragment extends DialogFragment
{

    public static final String LOGTAG = "AddCerealFragment";
    ArrayList<Cereal> cerealList;
    ListView listView;
    private static CerealAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        cerealList = new ArrayList<Cereal>();

        List<String> cerealNames = Arrays.asList(getResources().getStringArray(R.array.string_array_cereals));

        for (String name : cerealNames)
        {
            cerealList.add(new Cereal(name));
        }

        final CerealAdapter adapter = new CerealAdapter(cerealList, getContext());

        final View view  = inflater.inflate( R.layout.dialog_add_cereals, container, false);

        final ListView listView = (ListView)view.findViewById(R.id.dialog_add_cereals_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cereal dataModel = cerealList.get(position);

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
                        Cereal cereal = cerealList.get(children);
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
