package nandroid.artesanus.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nandroid.artesanus.common.Hop;
import nandroid.artesanus.adapter.CerealAdapter;
import nandroid.artesanus.adapter.HopAdapter;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.listener.OnHopAddedListener;

/**
 * This class represents a dialog fragment for add hops
 */
public class AddHopFragment extends DialogFragment
{

    public static final String LOGTAG = "AddHopFragment";
    ArrayList<Hop> hopList;
    ListView listView;
    private static CerealAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        hopList = new ArrayList<Hop>();
        List<String> hopNames = Arrays.asList(getResources().getStringArray(R.array.string_array_hops));

        for (String name : hopNames)
        {
            hopList.add(new Hop(name));
        }

        HopAdapter adapter = new HopAdapter(hopList, getContext());

        final View view  = inflater.inflate( R.layout.dialog_add_hops, container, false);

        final ListView listView = (ListView)view.findViewById(R.id.dialog_add_hops_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Hop hop = hopList.get(position);

                Snackbar.make(view, hop.getName() + "\n" + hop.getName() + " API: " + hop.getQuantity(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        TextView tvOk = (TextView)view.findViewById(R.id.hop_ok_button);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Hop> addedHops = new ArrayList<Hop>();

                for (int children = 0; children < listView.getChildCount(); children++)
                {
                    View view = listView.getChildAt(children);
                    EditText ed = (EditText)view.findViewById(R.id.hop_amount);
                    int amountToAdd = Integer.parseInt(ed.getText().toString());
                    if (amountToAdd > 0)
                    {
                        Hop hop = hopList.get(children);
                        hop.setQuantity(amountToAdd);
                        addedHops.add(hop);
                    }
                }

                FireHopAddedListener(addedHops);

                dismiss();
            }
        });

        TextView tvCancel = (TextView)view.findViewById(R.id.hop_cancel_button);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void FireHopAddedListener(ArrayList<Hop> addedHops)
    {
        Fragment fragment = getParentFragment();
        if (fragment instanceof OnHopAddedListener) {

            OnHopAddedListener listener = (OnHopAddedListener) fragment;
            listener.onHopAdded(addedHops);
        }

        if (getActivity() instanceof OnHopAddedListener)
        {
            OnHopAddedListener listener = (OnHopAddedListener) getActivity();
            listener.onHopAdded(addedHops);
        }
    }

}
