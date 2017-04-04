package nandroid.artesanus.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nandroid.artesanus.adapter.TabFragmentPagerAdapter;
import nandroid.artesanus.common.Brew;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.GetController;
import nandroid.artesanus.common.Heat;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.common.PostController;
import nandroid.artesanus.common.Process;
import nandroid.artesanus.fragments.AddCerealFragment;
import nandroid.artesanus.fragments.AddHeatFragment;
import nandroid.artesanus.fragments.AddHopFragment;
import nandroid.artesanus.messages.Message;

public class NewBeerCraftingActivity extends AppCompatActivity
        implements AddCerealFragment.OnCerealAddedListener,
        AddHopFragment.OnHopAddedListener,
        AddHeatFragment.OnHeatAddedListener

{

    // Debugging
    private static final String TAG = "NewBeerCraftingActivity";
    private static final boolean D = true;

    private List<Cereal> cerealsAdded = new ArrayList<Cereal>();
    private List<Hop> hopsAdded = new ArrayList<Hop>();
    private List<Heat> heatsAdded = new ArrayList<Heat>();

    String mAddress;

    private TextView mSelectedKindBeer;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_new_crafting);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabFragmentPagerAdapter(
                getSupportFragmentManager(),
                NewBeerCraftingActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.new_crafting_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.new_crafting_floating_start);
        button.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        // Send data to server and database
                        Brew brew = new Brew();
                        Date date = Calendar.getInstance().getTime();
                        brew.setStartDate(date);

                        TextView txtNameView = (TextView) findViewById(R.id.new_crafting_name_edit);
                        CharSequence charName = txtNameView.getText();
                        if (charName != null)
                            brew.setName(charName.toString());

                        TextView txtLitresView = (TextView) findViewById(R.id.new_crafting_litres_selected);
                        CharSequence charLitres = txtLitresView.getText();
                        if (charLitres != null) {
                            int litres = 0;
                            try
                            {
                                litres = (Integer.parseInt(charLitres.toString()));
                            } catch (NumberFormatException ex) {
                                litres = 0;
                            }
                            finally {
                                brew.setLitres(litres);
                            }
                        }
                        TextView txtTypeView = (TextView) findViewById(R.id.new_crafting_kind_selected);
                        CharSequence charType = txtTypeView.getText();
                        if (charType != null)
                        {
                            brew.setBeerType(charType.toString());
                        }

                        brew.setCereals(cerealsAdded);
                        brew.setHeats(heatsAdded);
                        brew.setHops(hopsAdded);

                        Event event = new Event();
                        event.setMessage("START");
                        List<Event> eventList = new ArrayList<Event>();
                        eventList.add(event);

                        Process proc = new Process();
                        proc.setType("MASHING");
                        proc.setEvents(eventList);
                        List<Process> processList = new ArrayList<Process>();
                        processList.add(proc);

                        brew.setProcesses(processList);

                        PostController controller = new PostController();



                        //PostController controller = new PostController();
                        GsonBuilder builder = new GsonBuilder();
                        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                        Gson gson = builder.create();
                        String json = (gson.toJson(brew));
                        //String json = gson.toJson(1);
                        //controller.execute("/retrieve/events/1", json);

                        controller.execute("/insert_brew", json);

                        // Start Monitoring activity
                        Intent intent = new Intent(getBaseContext(), MonitoringActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    // Get the device MAC address
                    mAddress = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {

                }
                else
                {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.main_bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
    }

    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }
    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth messenger services
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }


    @Override
    public void onCerealAdded(ArrayList<Cereal> cereals)
    {
        cerealsAdded.addAll(cereals);
    }

    @Override
    public void onHeatAdded(Heat heat)
    {
        heatsAdded.add(heat);
    }

    @Override
    public void onHopAdded(ArrayList<Hop> hops)
    {
        hopsAdded.addAll(hops);
    }
}
