package nandroid.artesanus.common;

/**
 * Defines several constants used between {@link nandroid.artesanus.services.BluetoothMessageService}
 * and the UI.
 */
public abstract class BTConstants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_ASK_DATA = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    //Constants that indicate the kind of message send or received
    public static final int MESSAGE_START_PROCESS = 0; //Command to start brew process
    public static final int MESSAGE_STARTED_PROCESS = 1; //Acknowledge of start process command
    public static final int MESSAGE_HEATING_WATER = 2; // Pre-heating water process started
    public static final int MESSAGE_DATA_MASHING = 3; // Data coming from Mashing process
    public static final int MESSAGE_PUMPING_MASHER = 4; // Pump engine working
    public static final int MESSAGE_CAUDALIMETER = 5; // Caudalimeter data incoming
    public static final int MESSAGE_MASHING_RESISTENCE_ON = 6; // Resistence started for Heating process
    public static final int MESSAGE_MASHING_RESISTENCE_OFF = 7; // Resistence shutdown for Heating process
    public static final int MESSAGE_HEATING_STARTED = 8; // Resistence started
    public static final int MESSAGE_HEATING_DATA = 9; //Data incoming from Heating process
    public static final int MESSAGE_HEATING_OPEN_GATE = 10; // Opening gate at the end of Heating process
    public static final int MESSAGE_HEATING_CLOSE_GATE = 11; // Closing gate
    public static final int MESSAGE_START_FERMENTER = 12; // Command to start fermenting process
    public static final int MESSAGE_STARTED_FERMENTER = 13; // Acknowledge of starting command
    public static final int MESSAGE_FERMENTER_DATA = 14; // Data incoming from Fermenting process
    public static final int MESSAGE_ADD_MANUAL_HOP = 15; // Added hop manually
    public static final int MESSAGE_ADD_MANUAL_DATA = 16; // Added data manually

    public static final int LOW_PRIORITY = 0; // Low priority for a message
    public static final int MEDIUM_PRIORITY = 1; // Medium priority for a message
    public static final int HIGH_PRIORITY = 2;  // High priority for a message

}