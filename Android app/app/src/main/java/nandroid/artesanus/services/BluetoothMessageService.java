package nandroid.artesanus.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import nandroid.artesanus.common.BTConstants;
import nandroid.artesanus.common.TransientPair;
import nandroid.artesanus.gui.MenuActivity;
import nandroid.artesanus.gui.MonitoringActivity;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothMessageService {

    // Debugging
    private static final String TAG = "BluetoothMessageService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final String ACTION_WRITE_DATA = "nandroid.artesanus.intent.action.WRITE";
    public static final String ACTION_ASK_DATA = "nandroid.artesanus.intent.action.ASK";
    public static final String ACTION_READ_DATA = "nandroid.artesanus.intent.action.READ";
    public static final String ACTION_STATE_CHANGE = "nandroid.artesanus.intent.action.STATE_CHANGE";
    public static final String ACTION_TOAST = "nandroid.artesanus.intent.action.TOAST";
    public static final String ACTION_SEND_DEVICE_NAME = "nandroid.artesanus.action.SEND_DEVICE_NAME";
    //private static final UUID MY_UUID = UUID.randomUUID();

    // Member fields
    private BluetoothAdapter mAdapter;
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;



    public BluetoothMessageService(Context context, Handler handler)
    {
        //super("BluetoothMessageService");
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BTConstants.STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state)
    {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);

        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler
                .obtainMessage(BTConstants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState()
    {
        return mState;
    }

    /**
     * Start the message service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start()
    {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(BTConstants.STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null)
        {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == BTConstants.STATE_CONNECTING)
        {
            if (mConnectThread != null)
            {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(BTConstants.STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null)
        {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BTConstants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BTConstants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        //Intent bcIntent = new Intent();
        //bcIntent.setAction(ACTION_SEND_DEVICE_NAME);
        //bcIntent.putExtra("DEVICE_NAME", device.getName());
        //mContext.sendBroadcast(bcIntent);

        setState(BTConstants.STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null)
        {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null)
        {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(BTConstants.STATE_NONE);
    }


    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out)
    {
        // Create temporary object
        ConnectedThread thread;
        // Synchronize a copy of the ConnectedThread
        synchronized (this)
        {
            if (mState != BTConstants.STATE_CONNECTED)
                return;
            thread = mConnectedThread;
        }

        // Perform the write unsynchronized
        thread.write(out);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The Pair objects to send
     * @see ConnectedThread#write(byte[])
     */
    public void write(List<Pair> out)
    {
        // Create temporary object
        ConnectedThread thread;
        // Synchronize a copy of the ConnectedThread
        synchronized (this)
        {
            if (mState != BTConstants.STATE_CONNECTED)
                return;
            thread = mConnectedThread;
        }

        // Perform the write unsynchronized
        thread.write(out);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     */
    public void ask()
    {
        // Create temporary object
        ConnectedThread thread;
        // Synchronize a copy of the ConnectedThread
        synchronized (this)
        {
            if (mState != BTConstants.STATE_CONNECTED)
                return;
            thread = mConnectedThread;
        }

        // Perform the write unsynchronized
        thread.ask();
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed()
    {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BTConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTConstants.TOAST, "Imposible conectar con el dispositivo");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothMessageService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost()
    {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BTConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BTConstants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothMessageService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try
            {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e)
            {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run()
        {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BTConstants.STATE_CONNECTED) {
                try
                {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothMessageService.this) {
                        switch (mState) {
                            case BTConstants.STATE_LISTEN:
                            case BTConstants.STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;

                            case BTConstants.STATE_NONE:
                            case BTConstants.STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }
        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try
            {
                //This is not working with > Android 4.2 versions.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                //tmp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[]{int.class}).invoke(device, 1);

            }catch (IOException e) {
               e.printStackTrace();
            }
            /*catch(Exception e)
            {
                e.printStackTrace();
            }*/
            mmSocket = tmp;
        }

        // Run method from ConnectThread
        public void run() {

            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {

                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }

                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothMessageService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //private ObjectInputStream objInStream;
        //private ObjectOutputStream objOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            //final BufferedOutputStream bufo = new BufferedOutputStream(mmOutStream);
            //final BufferedInputStream bufi = new BufferedInputStream(mmInStream);

            /*Log.d(TAG,"attempting to create OOS");

            try {
                //objOutStream = new ObjectOutputStream(bufo);
                //objInStream = new ObjectInputStream(bufi);
            } catch (StreamCorruptedException e) {
                Log.d(TAG,"Caught Corrupted Stream Exception");
                Log.w(TAG,e);

            } catch (IOException e) {
                Log.d(TAG,"Caught IOException");
                Log.w(TAG,e);
            }*/
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (mState == BTConstants.STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(BTConstants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);

                    // Start the service over to restart listening mode
                    BluetoothMessageService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {

            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BTConstants.MESSAGE_WRITE, -1, -1, buffer)
                      .sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void write(List<Pair> data) {

            try {
                StringBuilder sb = new StringBuilder();
                for (Pair pair : data)
                {
                    TransientPair tPair = new TransientPair((String) pair.first, pair.second);
                    sb.append(tPair.toString()+ "\n");
                }

                byte[] buffer = sb.toString().getBytes();
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BTConstants.MESSAGE_WRITE, -1, -1, buffer)
                      .sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        /**
         * Ask to the connected OutStream.
         */
        public void ask() {

            try {

                nandroid.artesanus.messages.Message msg = new nandroid.artesanus.messages.Message(1000, 2, Calendar.getInstance().getTime());
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                String showTxt = (gson.toJson(msg));

                byte[] buffer = showTxt.getBytes();
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BTConstants.MESSAGE_ASK_DATA, -1, -1, buffer)
                      .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during ask", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
