package nandroid.artesanus.gui;

import android.content.Intent;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nandroid.artesanus.adapter.TabFragmentPagerAdapter;
import nandroid.artesanus.common.Brew;
import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Cereal;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.common.Heat;
import nandroid.artesanus.common.Hop;
import nandroid.artesanus.http.PostController;
import nandroid.artesanus.fragments.AddCerealFragment;
import nandroid.artesanus.fragments.AddHeatFragment;
import nandroid.artesanus.fragments.AddHopFragment;

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

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_newcrafting));

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
                        Brew brew = PopulateBrew();
                        
                        // Parse brew content to json
                        GsonBuilder builder = new GsonBuilder();
                        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                        Gson gson = builder.create();
                        String json = (gson.toJson(brew));

                        // Send post request
                        PostController controller = new PostController();
                        controller.setIP(PreferenceManager.
                                getDefaultSharedPreferences(v.getContext()).getString("ip_address", "192.168.1.40"));
                        controller.execute("/insert_brew", json);

                        // Start Monitoring activity
                        Intent intent = new Intent(getBaseContext(), MonitoringActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

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

    // Overrided method to handle creation of Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_option, menu);
        return true;
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


    private Brew PopulateBrew()
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

        // Create a new start event for mashing process
        Event startEvent = new Event.Builder()
                .source("mashing")
                .type("command")
                .message("start")
                .build();
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(startEvent);

        BrewProcess mashingProc = new BrewProcess.Builder()
                .type("mashing")
                .events(eventList)
                .build();
        BrewProcess fermentingProc = new BrewProcess.Builder()
                .type("fermentation")
                .build();
        BrewProcess boilingProc = new BrewProcess.Builder()
                .type("boiling")
                .build();

        List<BrewProcess> brewProcessList = new ArrayList<BrewProcess>();
        brewProcessList.add(mashingProc);
        brewProcessList.add(boilingProc);
        brewProcessList.add(fermentingProc);
        brew.setProcesses(brewProcessList);

        return brew;
    }
}
