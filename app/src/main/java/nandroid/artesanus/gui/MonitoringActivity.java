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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import nandroid.artesanus.adapter.MonitorTabFragmentPagerAdapter;
import nandroid.artesanus.common.AppController;
import nandroid.artesanus.fragments.UnpairedDevicesFragment;
import nandroid.artesanus.services.BluetoothMessageService;

/**
 * Created by Nando on 27/11/2016.
 */
public class MonitoringActivity extends BluetoothActivity {

    // Debugging
    private static final String TAG = "MonitoringActivity";
    private static final boolean D = true;



    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_monitoring);

        DialogFragment newFragment = new UnpairedDevicesFragment();
        newFragment.show(getSupportFragmentManager(), "MonitoringActivity");

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

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        if (enableBT())
        {
            setupMessenger();
        }
    }

    @Override
    public synchronized void onResume() {
            super.onResume();
            if(D) Log.e(TAG, "+ ON RESUME +");
            // Performing this check in onResume() covers the case in which BT was
            // not enabled during onStart(), so we were paused to enable it...
            // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
            // TODO Refactor. Call to AppController to get BTService
            if (mBTService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mBTService.getState() == BluetoothMessageService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mBTService.start();
                }
            }
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
        if (mBTService != null) mBTService.stop();
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBTService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    // Bluetooth is now enabled, so set up a messenger session
                    setupMessenger();
                }
                else
                {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.main_bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


}
