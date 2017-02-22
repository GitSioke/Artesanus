package nandroid.artesanus.gui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import nandroid.artesanus.adapter.MonitorTabFragmentPagerAdapter;
import nandroid.artesanus.common.AppController;
import nandroid.artesanus.common.BTConstants;
import nandroid.artesanus.fragments.UnpairedDevicesFragment;
import nandroid.artesanus.services.BluetoothMessageService;

/**
 * Created by Nando on 27/11/2016.
 */
public class MonitoringActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MonitoringActivity";
    private static final boolean D = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_monitoring);

        // TODO Restaurar estas lineas y eliminar de BluetoohtActivity
        //DialogFragment newFragment = new UnpairedDevicesFragment();
        //newFragment.show(getSupportFragmentManager(), "MonitoringActivity");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MonitorTabFragmentPagerAdapter(
                getSupportFragmentManager(),
                MonitoringActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.monitoring_tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.monitoring_floating_end);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    /*@Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        if (enableBT())
        {
            setupMessenger();
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");
    }

    /*@Override
    public synchronized void onResume() {
            super.onResume();
            if(D) Log.e(TAG, "+ ON RESUME +");
            // Performing this check in onResume() covers the case in which BT was
            // not enabled during onStart(), so we were paused to enable it...
            // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

            // TODO Refactor.
            if (mBTService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mBTService.getState() == BTConstants.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mBTService.start();
                }
            }
    }*/

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



    // TODO refactor to handle data written by BTService
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
