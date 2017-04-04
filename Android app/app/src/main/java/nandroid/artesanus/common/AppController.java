package nandroid.artesanus.common;

import android.app.Application;
import android.os.Handler;

import nandroid.artesanus.services.BluetoothMessageService;

/**
 * Created by Nando on 29/11/2016.
 */
public class AppController extends Application {

    private static AppController mInstance;
    private BluetoothMessageService service;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
    }

    /*public void startBTService()
    {
        service = new BluetoothMessageService(getApplicationContext());
    }*/

    public void stopBTService()
    {

    }

    public BluetoothMessageService getBTService(Handler mHandler)
    {
        //service.setHandler(mHandler);
        return service;
    }

}

