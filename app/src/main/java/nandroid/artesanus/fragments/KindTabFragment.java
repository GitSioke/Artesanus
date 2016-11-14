package nandroid.artesanus.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.gui.R;

/**
 * Created by Nando on 05/11/2016.
 */
public class KindTabFragment extends Fragment implements ItemPickerDialogFragment.OnItemSelectedListener,
        AddKindFragment.OnCerealAddedListener,
        AddHopFragment.OnHopAddedListener
{
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


        return view;
    }

    private void addHop()
    {
        AddHopFragment fragment = new AddHopFragment();
        fragment.show(getChildFragmentManager(), "AddHopFragmentDialog");
    }

    private void addCereal()
    {
        AddKindFragment fragment = new AddKindFragment();
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
            Snackbar.make(getView(), cereal.getName() + ":" + cereal.getAmount(), Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onHopAdded(ArrayList<Hop> hops)
    {
        for (Hop hop : hops)
        {
            Snackbar.make(getView(), hop.getName() + ":" + hop.getAmount(), Snackbar.LENGTH_SHORT).show();
        }

    }
}
