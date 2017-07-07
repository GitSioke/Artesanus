package nandroid.artesanus.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nandroid.artesanus.adapter.BrewListAdapter;
import nandroid.artesanus.common.Brew;
import nandroid.artesanus.common.SharedPreferencesHelper;
import nandroid.artesanus.fragments.RSBlurFragment;
import nandroid.artesanus.http.GetController;
import nandroid.artesanus.http.HTTPController;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.http.IRetrieveBrewsAsyncHttpResponse;
import nandroid.artesanus.http.RetrieveBrewsGetController;
import nandroid.artesanus.listener.OnBrewSelectedListener;

public class MainActivity extends MenuActivity
    implements IAsyncHttpResponse,
        OnBrewSelectedListener,
        IRetrieveBrewsAsyncHttpResponse
{
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    private BrewListAdapter _brewAdapter;
    private ListView _brewListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_main_menu);

        HTTPController.setIP(SharedPreferencesHelper.getIPAddressPreference(this));

        ArrayList<Brew> msgBrewList = new ArrayList<Brew>();
        _brewAdapter  = new BrewListAdapter(msgBrewList, this);
        _brewAdapter.setListener(this);
        _brewListView = (ListView)findViewById(R.id.brews_lv);
        _brewListView.setAdapter(_brewAdapter);

        // Set newCraft button for start new crafting process, so start first new process
        FloatingActionButton newCraft = (FloatingActionButton) findViewById(R.id.main_menu_new_crafting);
        newCraft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewBeerCraftingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        new RetrieveBrewsGetController(this).execute("/retrieve/brews/");
        if(D) Log.e(TAG, "++ ON START ++");
    }
    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    @Override
    public void ProcessFinish(String output)
    {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(output).getAsJsonObject();

        try {
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
        }

    }

    @Override
    public void onBrewSelected(Brew brewSelected)
    {
        new GetController(this).execute("/retrieve/subprocesses/id/"+brewSelected.getId());
    }

    @Override
    public void ProcessRetrieveBrewsResponse(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try
        {
            Type eventListType = new TypeToken<ArrayList<Brew>>(){}.getType();
            List<Brew> brews = gson.fromJson(output, eventListType);
            if (brews != null && brews.size() > 0)
            {
                _brewAdapter.removeAll();

                for (Brew brew : brews)
                {
                    _brewAdapter.add(brew);
                }

                _brewListView.setAdapter(_brewAdapter);
            }
            else
            {
                // There is no brews show blur message to start a brew crafting process
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new RSBlurFragment()).commit();
            }
        }
        catch (Exception ex)
        {
            if(D) Log.e(TAG, ex.getMessage());
        }
    }
}
