package nandroid.artesanus.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import nandroid.artesanus.adapter.MonitorTabFragmentPagerAdapter;
import nandroid.artesanus.services.TimerService;

/**
 * This class controls Monitoring activity
 */
public class MonitoringActivity extends MenuActivity {

    // Debugging
    private static final String TAG = "MonitoringActivity";
    private static final boolean D = true;
    private int _idCrafting;
    private int _idMashing;
    private int _idBoiling;
    private int _idFermentation;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_monitoring);

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_measurements));

        Intent intent = getIntent();
        _idCrafting = intent.getIntExtra("id_crafting", 0);
        _idMashing = intent.getIntExtra("id_mashing" ,0);
        _idBoiling = intent.getIntExtra("id_boiling" ,0);
        _idFermentation = intent.getIntExtra("id_fermentation", 0);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MonitorTabFragmentPagerAdapter(
                getSupportFragmentManager(),
                MonitoringActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.monitoring_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //Intent intentService = new Intent(this, TimerService.class);

        // Code to define and initialize myData here

        //intentService.putExtra("someData", myData);
        //intentService.putExtra("resReceiver", theReceiver);
        //startService(intentService);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");
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
        //if (mBTService != null) mBTService.stop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int GetIdCrafting()
    {
        return _idCrafting;
    }

    public int GetIdMashing()
    {
        return _idMashing;
    }

    public int GetIdBoiling()
    {
        return _idBoiling;
    }

    public int GetIdFermentation()
    {
        return _idFermentation;
    }

}
