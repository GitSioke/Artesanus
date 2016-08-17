package com.example.nando.arti;

import android.app.Activity;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import common.ProcessHelper;
import services.BluetoothMessageService;
import services.NotifierService;

public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_ASK_DATA = 6;
    public static final int MESSAGE_SEND_DATA = 7;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mAskButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothMessageService mBTService = null;

    // Member object for the chat services
    private NotifierService mNotifierService = null;

    // Member object for obtain Pair with data sent by paired device
    private List<Pair> mListPair = new ArrayList<Pair>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        // Set up the window layout
        setContentView(R.layout.activity_main);
        mTitle = (TextView)findViewById(R.id.main_title);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupMessenger() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the bluetooth session
        } else
        {
            if (mBTService == null) setupMessenger();
        }
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

    private void setupMessenger()
    {
        Log.d(TAG, "setupMessenger()");
        // Initialize the array adapter for the conversation thread

        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);
        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the ask button with a listener that for click events
        mAskButton = (Button) findViewById(R.id.button_ask);
        mAskButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Ask for information
                askMessage();
            }
        });

        //TODO Check that this button is working
        // Set newCraft button for start new crafting process, so start first new process
        Button newCraft = (Button) findViewById(R.id.main_new_crafting);
        newCraft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), NewProcessActivity.class);
                intent.putExtra("PROCESS", ProcessHelper.PROCESS_1);
                startActivity(intent);
            }
        });

        // Initialize the BluetoothMessageService to perform bluetooth connections
        mBTService = new BluetoothMessageService(this, mHandler);

        // Initialize the NotifierService to send data periodically
        mNotifierService = new NotifierService(this, mHandler, mBTService);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
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
        // Stop the Bluetooth messenger services
        if (mBTService != null) mBTService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    // Method to handle Discoverable feature instiantiated by option menu
    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    void sendMessage(String message)
    {
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothMessageService.STATE_CONNECTED)
        {
            Toast.makeText(this, R.string.main_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothMessageService to write
            byte[] send = message.getBytes();
            mBTService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // Ask for data button starting process
    private void askMessage()
    {
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothMessageService.STATE_CONNECTED)
        {
            Toast.makeText(this, R.string.main_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        mBTService.ask();
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
        mOutEditText.setText(mOutStringBuffer);

    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    if(D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    // The Handler that gets information back from the BluetoothMessageService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothMessageService.STATE_CONNECTED:
                            mTitle.setText(R.string.main_title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothMessageService.STATE_CONNECTING:
                            mTitle.setText(R.string.main_title_connecting);
                            break;
                        case BluetoothMessageService.STATE_LISTEN:
                        case BluetoothMessageService.STATE_NONE:
                            mTitle.setText(R.string.main_title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_ASK_DATA:
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + ""
                            + R.string.main_asking_for_data);
                    break;
                case MESSAGE_SEND_DATA:
                    byte[] sendBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String sendMessage = new String(sendBuf);
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + sendMessage);
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add(R.string.main_messenger_user_myself + writeMessage);
                    break;
                case MESSAGE_READ:
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

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), R.string.main_title_connected_to
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
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
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.main_bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    // Overrided method to handle creation of Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_option, menu);
        return true;
    }

    // Overrided method to handle menu item options as Scan and Discoverable
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
}
