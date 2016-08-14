package com.example.nando.arti;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import services.BluetoothMessageService;

public class MonitorActivity extends Activity {

    private TextView mTextView;

    // Debugging
    private static final String TAG = "MonitorActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothMessageService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothMessageService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // TODO Revisar si hay que eliminar estas vistas Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothMessageService mChatService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        WebView webView = (WebView)findViewById(R.id.webView);

        webView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_res/raw/index.html");


        // TODO Blueetooth section

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }



        //Retrieve data from NewProcessActivity
        Intent intent = getIntent();
        Integer minutes = intent.getIntExtra("Time", 0);
        final TextView text = (TextView) findViewById(R.id.monitor_third_value);

        // Start a service to control notifications
        //Intent serviceInt = new Intent(this, NotifierService.class);
        //serviceInt.putExtra("Minutes", minutes);
        //startService(serviceInt);

        // TODO Envio de notificaciones
        /*
        NotificationManager notificationMan = (NotificationManager)getSystemService(Activity.NOTIFICATION_SERVICE);
        int requestID = (int) System.currentTimeMillis();
        Intent notificationIntent = new Intent(getApplicationContext(), MonitorActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the icon, scrolling text and timestamp
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(MonitorActivity.this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setAutoCancel(true)
                .setContentText(getResources().getString(R.string.notification_time_up))
                .setWhen(System.currentTimeMillis() + minutes * 1000)
                .setContentIntent(contentIntent);

        int notifyID = 001;

        notificationMan.notify(notifyID, builder.build());*/


        // TODO Countdown
        /*
        //Countdown with value set by user
        new CountDownTimer(minutes*1000*60, 1000) {

            public void onTick(long millisUntilFinished)
            {
                long remainingSeconds = millisUntilFinished/1000;
                long remainingMinutes = millisUntilFinished/(1000*60);
                int seconds = (int) remainingSeconds%60;
                int minutes = (int) remainingMinutes%60;
                text.setText( String.valueOf(minutes) + ":" + String.valueOf(seconds));
            }

            public void onFinish() {
                //StartCountUp();
            }
        }.start();*/
    }

    /*@Override
    public void onStart()
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else
        {
            if (mChatService == null)
                setupChat();
        }
    }*/

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothMessageService.STATE_NONE)
            {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    /*private void setupChat()
    {
        Log.d(TAG, "setupChat()");
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);
        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        // TODO mOutEditText.setOnEditorActionListener(mWriteListener);
        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        // Initialize the BluetoothMessageService to perform bluetooth connections
        mChatService = new BluetoothMessageService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }*/

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
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothMessageService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.monitor_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothMessageService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    public void StartCountUp()
    {
        Chronometer stopWatch = new Chronometer(this);
        long startTime = SystemClock.elapsedRealtime();
        final TextView text = (TextView) findViewById(R.id.monitor_third_value);

        stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                long elapsedMinutes = elapsedSeconds/60;

                String chronoStr = elapsedMinutes + ":" + elapsedSeconds;
                text.setText(chronoStr);
            }
        });

        stopWatch.start();
    }

    public class WebAppInterface
    {
        Context mContext;

        int timer =0;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public int getNextValue() {
            return 10;
        }

        @JavascriptInterface
        public int getNextTime() {
            return timer++;
        }

        @JavascriptInterface
        public int getTotal() {
            return 3;
        }

    }
}
