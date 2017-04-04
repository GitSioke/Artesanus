package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.adapter.CerealAddedAdapter;
import nandroid.artesanus.adapter.HopAddedAdapter;
import nandroid.artesanus.gui.R;

/**
 * This fragment class control CerealTab from NewBeerCraftingActivity to get information about cereals.
 */
public class KindTabFragment extends Fragment
        implements ItemPickerDialogFragment.OnItemSelectedListener,
            AddCerealFragment.OnCerealAddedListener,
            AddHopFragment.OnHopAddedListener
{
    private HopAddedAdapter hopAdapter;
    private ListView hopListView;
    private CerealAddedAdapter cerealAdapter;
    private ListView cerealListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate( R.layout.fragment_new_crafting_kind, container, false);
        TextView textView = (TextView) view.findViewById(R.id.new_crafting_kind_selected);
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onKindButtonClick();
            }
        });

        ImageView addCerealImageView = (ImageView) view.findViewById(R.id.new_crafting_add_cereal);
        addCerealImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addCereal();
            }
        });

        ImageView addHopImageView = (ImageView) view.findViewById(R.id.new_crafting_add_hop);
        addHopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHop();
            }
        });


        ArrayList<Hop> addedHopList = new ArrayList<Hop>();
        hopAdapter  = new HopAddedAdapter(addedHopList, getContext());
        hopListView = (ListView)view.findViewById(R.id.new_crafting_hop_added_lv);
        hopListView.setAdapter(hopAdapter);


        ArrayList<Cereal> addedCerealList = new ArrayList<Cereal>();
        cerealAdapter  = new CerealAddedAdapter(addedCerealList, getContext());
        cerealListView = (ListView)view.findViewById(R.id.new_crafting_cereals_added_lv);
        cerealListView.setAdapter(cerealAdapter);

        return view;
    }

    public synchronized void onResume() {
        super.onResume();
    }

    private void addHop()
    {
        AddHopFragment fragment = new AddHopFragment();
        fragment.show(getChildFragmentManager(), "AddHopFragmentDialog");
    }

    private void addCereal()
    {
        AddCerealFragment fragment = new AddCerealFragment();
        fragment.show(getChildFragmentManager(), "AddKindFragmentDialog");
    }

    public void onKindButtonClick() {
        ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();
        pickerItems.add(new ItemPickerDialogFragment.Item("C. Avena", "C.Avena"));
        pickerItems.add(new ItemPickerDialogFragment.Item("C. Cebada", "C. Cebada"));
        pickerItems.add(new ItemPickerDialogFragment.Item("C. Maiz", "C. Maiz"));
        pickerItems.add(new ItemPickerDialogFragment.Item("C. Trigo", "C. Trigo"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Chocolate", "Chocolate"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Cristal60", "Cristal60"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Cristal120", "Cristal120"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Munich", "Munich"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Pale Ale", "Pale Ale"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Pilsen", "Pilsen"));
        pickerItems.add(new ItemPickerDialogFragment.Item("Trigo", "Trigo"));

        ItemPickerDialogFragment dialog = ItemPickerDialogFragment.newInstance(
                getContext().getString(R.string.title_item_picker),
                pickerItems,
                -1
        );

        dialog.show(getChildFragmentManager(), "ItemPicker");
    }

    @Override
    public void onItemSelected(ItemPickerDialogFragment fragment, ItemPickerDialogFragment.Item item, int index)
    {
        String selectedValue = item.getStringValue();
        TextView textView = (TextView) getView().findViewById(R.id.new_crafting_kind_selected);
        if (!selectedValue.equals(textView.getText()))
        {
            textView.setText(selectedValue);
        }
    }

    @Override
    public void onCerealAdded(ArrayList<Cereal> cereals)
    {
        for (Cereal cereal : cereals)
        {
            cerealAdapter.add(cereal);

            //Snackbar.make(getView(), hop.getName() + ":" + hop.getAmount(), Snackbar.LENGTH_SHORT).show();
        }

        cerealListView.setAdapter(cerealAdapter);
    }

    @Override
    public void onHopAdded(ArrayList<Hop> hops)
    {
        for (Hop hop : hops)
        {
            hopAdapter.add(hop);
            //Snackbar.make(getView(), hop.getName() + ":" + hop.getAmount(), Snackbar.LENGTH_SHORT).show();
        }

        hopListView.setAdapter(hopAdapter);
    }
}
