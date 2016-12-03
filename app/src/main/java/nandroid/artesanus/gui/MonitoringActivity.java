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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import nandroid.artesanus.adapter.MonitorTabFragmentPagerAdapter;
import nandroid.artesanus.common.AppController;
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

    // Member object for the chat services
    private BluetoothMessageService mBTService = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_monitoring);

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
        enableBT();
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

    private void setupMessenger()
    {
        Log.d(TAG, "setupMessenger()");
        // Initialize the array adapter for the conversation thread

        // Initialize the BluetoothMessageService to perform bluetooth connections
        //mBTService = new BluetoothMessageService(this);
        AppController.getInstance().startBTService();
        mBTService = AppController.getInstance().getBTService(mHandler);
    }

    /*private boolean enableBT()
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
            Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                    "Bluetooth no disponible",
                    Snackbar.LENGTH_LONG).show();
            finish();
        }

        return retVal;
    }*/

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

    // The Handler that gets information back from the BluetoothMessageService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothMessageService.STATE_CONNECTED:
                            //mTitle.setText(R.string.main_title_connected_to);
                            //mTitle.append(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();
                            Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                                    getResources().getString(R.string.main_title_connected_to),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                        case BluetoothMessageService.STATE_CONNECTING:
                            //mTitle.setText(R.string.main_title_connecting);
                            Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                                    getResources().getString(R.string.main_title_connecting),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                        case BluetoothMessageService.STATE_LISTEN:
                        case BluetoothMessageService.STATE_NONE:
                            //mTitle.setText(R.string.main_title_not_connected);
                            Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                                    getResources().getString(R.string.main_title_not_connected),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                    }
                    break;
                /*case MESSAGE_ASK_DATA:
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + ""
                            + R.string.main_asking_for_data);
                    break;*/
                /*case MESSAGE_SEND_DATA:
                    byte[] sendBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String sendMessage = new String(sendBuf);
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + sendMessage);
                    break;*/
                /*case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + writeMessage);
                    break;*/
                /*case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.equalsIgnoreCase("ASK_DATA"))
                    {

                        mNotifierService.start();
                        mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    }
                    else
                    {
                        String[] data = readMessage.split("\n");
                        for (String field :data)
                        {
                            String[] part = field.split(" : ");
                            mListPair.add(new Pair(part[0], part[1]));
                            mConversationArrayAdapter.add(mConnectedDeviceName+ ":  " + part[0] + " : " + part[1]);
                        }
                    }

                    break;*/
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), R.string.main_title_connected_to
                    //      + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                            getResources().getString(R.string.main_title_connected_to) + mConnectedDeviceName,
                            Snackbar.LENGTH_LONG).show();
                    break;
                case MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                    //      Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                            msg.getData().getString(TOAST),
                            Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };


    // This method control the pairing action of an unpair device.
    public void doPositiveClick()
    {
        //Start DeviceListActivity to scan and connect with selected device.
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    // This method control if user doesn't want to pair its device.
    public void doNegativeClick()
    {
        Snackbar.make(findViewById(R.id.main_menu_ly_coordinator),
                getResources().getString(R.string.menu_device_wasnt_paired),
                Snackbar.LENGTH_LONG).show();
    }

}
