package nandroid.artesanus.gui;

import android.content.Intent;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
import nandroid.artesanus.common.SharedPreferencesHelper;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.HTTPController;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.http.PostController;
import nandroid.artesanus.fragments.AddHeatFragment;
import nandroid.artesanus.listener.OnCerealAddedListener;
import nandroid.artesanus.listener.OnHopAddedListener;

public class NewBeerCraftingActivity extends MenuActivity
        implements OnCerealAddedListener,
        OnHopAddedListener,
        AddHeatFragment.AddHeatListener,
        IAsyncHttpResponse
{
    // Debugging
    private static final String TAG = "NewBeerCraftingActivity";
    private static final boolean D = true;

    private List<Cereal> cerealsAdded = new ArrayList<Cereal>();
    private List<Hop> hopsAdded = new ArrayList<Hop>();
    private List<Heat> heatsAdded = new ArrayList<Heat>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_new_crafting);

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_newcrafting));

        //TODO Remove
        HTTPController.setIP(SharedPreferencesHelper.getIPAddressPreference(this));

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
                        new PostController(NewBeerCraftingActivity.this).execute("/insert_brew", json);
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

    // Deals with positive click on AddHeat Dialog
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Heat heat)
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
                .time(date)
                .build();
        List<Event> eventList = new ArrayList<Event>();
        eventList.add(startEvent);

        BrewProcess mashingProc = new BrewProcess.Builder()
                .type("mashing")
                .events(eventList)
                .startTime(date)
                .build();
        BrewProcess boilingProc = new BrewProcess.Builder()
                .type("boiling")
                .build();
        BrewProcess fermentingProc = new BrewProcess.Builder()
                .type("fermentation")
                .build();

        List<BrewProcess> brewProcessList = new ArrayList<BrewProcess>();
        brewProcessList.add(mashingProc);
        brewProcessList.add(boilingProc);
        brewProcessList.add(fermentingProc);
        brew.setProcesses(brewProcessList);

        return brew;
    }

    @Override
    public void ProcessFinish(String output)
    {
        JsonParser parser = new JsonParser();
        try
        {
            JsonObject obj = parser.parse(output).getAsJsonObject();
            int idCrafting = obj.get("id_crafting").getAsInt();
            int idMashing = obj.get("id_mashing").getAsInt();
            int idFermentation = obj.get("id_fermentation").getAsInt();
            int idBoiling = obj.get("id_boiling").getAsInt();

            Intent intent = new Intent(getBaseContext(), MonitoringActivity.class);
            intent.putExtra("id_crafting", idCrafting);
            intent.putExtra("id_mashing", idMashing);
            intent.putExtra("id_boiling", idBoiling);
            intent.putExtra("id_fermentation", idFermentation);
            startActivity(intent);
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
            Snackbar.make(findViewById(R.id.ly_coordinator),
                    getResources().getString(R.string.communication_error),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

    }
}
