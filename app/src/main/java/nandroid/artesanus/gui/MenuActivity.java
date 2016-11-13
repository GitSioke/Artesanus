package nandroid.artesanus.gui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nandroid.artesanus.common.Blurred;
import nandroid.artesanus.common.ProcessHelper;
import nandroid.artesanus.fragments.UnpairedDevicesFragment;
import nandroid.artesanus.services.BluetoothMessageService;
import nandroid.artesanus.services.NotifierService;

public class MenuActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MenuActivity";
    private static final boolean D = true;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothMessageService mBTService = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_main_menu);

        // Set newCraft button for start new crafting process, so start first new process
        FloatingActionButton newCraft = (FloatingActionButton) findViewById(R.id.main_menu_new_crafting);
        newCraft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                DialogFragment newFragment = new UnpairedDevicesFragment();
                newFragment.show(getSupportFragmentManager(), "NewProcessActivity");


                Intent intent = new Intent(getBaseContext(), NewProcessActivity.class);
                intent.putExtra("PROCESS", ProcessHelper.CRAFTING_PROCESS.ESE);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        enableBT();
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BluetoothMessageService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBTService.start();
            }
        }
    }

    @Override
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
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        if (mBTService != null) mBTService.stop();
    }

    // Overrided method to handle creation of Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
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

    // Method to handle Discoverable feature instiantiated by option menu
    private void ensureDiscoverable() {
        /*if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }*/
    }

    private void setupMessenger()
    {
        Log.d(TAG, "setupMessenger()");
        // Initialize the array adapter for the conversation thread



        // Initialize the BluetoothMessageService to perform bluetooth connections
        //mBTService = new BluetoothMessageService(this, mHandler);

    }

    private boolean enableBT()
    {
        boolean retVal = false;
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If there is no adapter try to get it.
        if (mBluetoothAdapter != null)
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the bluetooth session
            } else
            {
                retVal = true;
                if (mBTService == null) setupMessenger();
            }

        }
        else
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        return retVal;
    }

    public void doPositiveClick()
    {
        //Start DeviceListActivity to scan and connect with selected device.
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void doNegativeClick()
    {
        Toast.makeText(this, R.string.menu_device_wasnt_paired, Toast.LENGTH_SHORT).show();
    }
}
