package nandroid.artesanus.gui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import nandroid.artesanus.common.BTConstants;
import nandroid.artesanus.fragments.UnpairedDevicesFragment;
import nandroid.artesanus.services.BluetoothMessageService;

/**
 * Created by Nando on 03/12/2016.
 */
public abstract class BluetoothActivity extends AppCompatActivity
{
    // Member object for the chat services
    protected BluetoothMessageService mBTService = null;

    // Name of the connected device
    private String mConnectedDeviceName = "";

    // Debugging
    private static final String TAG = "BluetoothActivity";
    private static final boolean D = true;

    // Local Bluetooth adapter
    protected BluetoothAdapter mBluetoothAdapter = null;

    // Intent request codes
    protected static final int REQUEST_CONNECT_DEVICE = 1;
    protected static final int REQUEST_ENABLE_BT = 2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Snackbar.make(findViewById(R.id.ly_coordinator),
                    R.string.blutooth_not_supported,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else if(mBTService == null)
        {
            setupMessenger();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBTService != null) {
            mBTService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        /*if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBTService.getState() == BTConstants.STATE_NONE)
            {
                // Start the Bluetooth chat services
                mBTService.start();
            }
        }*/
    }



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
        Snackbar.make(findViewById(R.id.ly_coordinator),
                getResources().getString(R.string.menu_device_wasnt_paired),
                Snackbar.LENGTH_LONG).show();
    }

    protected boolean enableBT()
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
                //if (mBTService == null) setupMessenger();
            }
        }
        else
        {
            Snackbar.make(findViewById(R.id.ly_coordinator),
                    "Bluetooth no disponible",
                    Snackbar.LENGTH_LONG).show();
            finish();
        }

        return retVal;
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
                    // Get the BluetoothDevice object
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

    protected void setupMessenger()
    {
        Log.d(TAG, "setupMessenger()");
        // Initialize the array adapter for the conversation thread

        // Initialize the BluetoothMessageService to perform bluetooth connections
        mBTService = new BluetoothMessageService(this, mHandler);
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

    // The Handler that gets information back from the BluetoothMessageService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BTConstants.MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BTConstants.STATE_CONNECTED:
                            connected();
                            Snackbar.make(findViewById(R.id.ly_coordinator),
                                    getResources().getString(R.string.main_title_connected_to) + mConnectedDeviceName,
                                    Snackbar.LENGTH_LONG).show();
                            break;
                        case BTConstants.STATE_CONNECTING:
                            Snackbar.make(findViewById(R.id.ly_coordinator),
                                    getResources().getString(R.string.main_title_connecting),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                        case BTConstants.STATE_LISTEN:
                        case BTConstants.STATE_NONE:
                            Snackbar.make(findViewById(R.id.ly_coordinator),
                                    getResources().getString(R.string.main_title_not_connected),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                    }
                    break;
                case BTConstants.MESSAGE_ASK_DATA:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String askMessage = new String(readBuf, 0, readBuf.length);
                    Snackbar.make(findViewById(R.id.ly_coordinator),
                            askMessage,
                            Snackbar.LENGTH_LONG).show();
                    break;
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
                case BTConstants.MESSAGE_READ:
                    byte[] readBufRead = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBufRead, 0, readBufRead.length);
                    if (readMessage.equalsIgnoreCase("ASK_DATA"))
                    {

                        //mNotifierService.start();
                        Snackbar.make(findViewById(R.id.ly_coordinator),
                               readMessage,
                                Snackbar.LENGTH_LONG).show();
                    }
                    else
                    {
                        Snackbar.make(findViewById(R.id.ly_coordinator),
                                readMessage,
                                Snackbar.LENGTH_LONG).show();
                        //String[] data = readMessage.split("\n");
                        //for (String field :data)
                        //{
                            //String[] part = field.split(" : ");
                            //mListPair.add(new Pair(part[0], part[1]));
                            //mConversationArrayAdapter.add(mConnectedDeviceName+ ":  " + part[0] + " : " + part[1]);
                        //}
                    }

                    break;
                case BTConstants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BTConstants.DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), R.string.main_title_connected_to
                    //      + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.ly_coordinator),
                            getResources().getString(R.string.main_title_connected_to) + mConnectedDeviceName,
                            Snackbar.LENGTH_LONG).show();
                    break;
                case BTConstants.MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                    //      Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.ly_coordinator),
                            msg.getData().getString(BTConstants.TOAST),
                            Snackbar.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public void ask()
    {
        mBTService.ask();
    }


    public void connected()
    {}

    public void startingBrewCraft()
    {}
}
