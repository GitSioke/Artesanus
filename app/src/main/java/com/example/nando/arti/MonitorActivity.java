package com.example.nando.arti;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
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

import common.ProcessHelper;
import services.BluetoothMessageService;
import services.TimerService;

public class MonitorActivity extends Activity {


    TextView mThird;
    private TimerService mTimerService = null;
private ProcessHelper.CRAFTING_PROCESS mProcess;
    // Debugging
    private static final String TAG = "MonitorActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothMessageService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int UPDATE_CLOCK = 6;
    public static final int FINISH_CLOCK = 7;

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
        if(D) Log.e(TAG, "--- ON CREATE ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        if(D) Log.e(TAG, "Set content view");

        //WebView webView = (WebView)findViewById(R.id.webView);

        //webView.addJavascriptInterface(new WebAppInterface(this), "JSInterface");
        //webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("file:///android_res/raw/index.html");

        mThird = (TextView) findViewById(R.id.monitor_third_value);
        mProcess = (ProcessHelper.CRAFTING_PROCESS)getIntent().getSerializableExtra("PROCESS");

        // TODO Blueetooth section

        // Get local Bluetooth adapter
        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/


        mTimerService = new TimerService(this, mHandler);
        mTimerService.start();
/*

        //Retrieve data from NewProcessActivity
        Intent intent = getIntent();
        Integer minutes = intent.getIntExtra("Time", 0);
        final TextView text = (TextView) findViewById(R.id.monitor_third_value);
*/
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
    }

    @Override
    public void onStart()
    {
        super.onStart();
        /*if(D) Log.e(TAG, "++ ON START ++");

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

        }*/
    }

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

    // The Handler that gets information back from the TimerService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] sendBuf = (byte[]) msg.obj;
            // construct a string from the buffer
            String sendMessage = new String(sendBuf);

            switch (msg.what) {

                case UPDATE_CLOCK:
                    mThird.setText(sendMessage);
                    break;

                case FINISH_CLOCK:
                    // Set text on red
                   mThird.setText(sendMessage);
                    mThird.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    break;
            }
        }
    };


    public void EndProcess(View v)
    {
        ProcessHelper.CRAFTING_PROCESS nextProcess = ProcessHelper.nextProcess(mProcess);
        Intent intent;
        if (nextProcess.compareTo(ProcessHelper.CRAFTING_PROCESS.NONE) != 0)
        {
            intent = new Intent(this, NewProcessActivity.class);
            intent.putExtra("PROCESS", nextProcess);
        }
        else
        {
            //End of monitoring
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);

    }

}
