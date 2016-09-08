package nandroid.artesanus.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * This service handle the dummy data created to simulate Arduino entrances.
 */
public class NotifierService extends Service
{
    private final Handler mHandler;

    private BluetoothMessageService mBTService;

    private Thread mThread;

    public NotifierService(Context context, Handler handler, BluetoothMessageService btService)
    {
        mHandler = handler;
        mBTService = btService;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    Random rand = new Random();

                    // Generate temperature between Max and Min range
                    List<Pair> dataForPump = new ArrayList<>();
                    int tempRangeMax = 35;
                    int tempRangeMin = 30;
                    int randomTemp = rand.nextInt((tempRangeMax - tempRangeMin) + 1) + tempRangeMin;
                    Pair<String, Integer> kvTemp = new Pair<>("Temperature", randomTemp);
                    dataForPump.add(kvTemp);
                    // Generate density between Max and Min range
                    int densRangeMax = 25;
                    int densRangeMin = 20;
                    int randomDens = rand.nextInt((densRangeMax - densRangeMin) + 1) + densRangeMin;
                    Pair<String, Integer> kvDens = new Pair<>("Density", randomDens);
                    dataForPump.add(kvDens);
                    //Generate timestamp
                    Pair<String, Date> kvTimestamp = new Pair<>("Date", new Date(System.currentTimeMillis()));
                    dataForPump.add(kvTimestamp);
                    // Check that we're actually connected before trying anything
                    if (mBTService.getState() != BluetoothMessageService.STATE_CONNECTED)
                    {
                        return;
                    }
                    else
                    {
                        mBTService.write(dataForPump);
                    }

                    try
                    {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void start()
    {

        mThread.run();

        //Integer minutes = intent.getIntExtra("Minutes", 0);
        /*if (minutes > 0)
        {
            /*long time = System.currentTimeMillis() + (minutes*1000);
            AlarmManager alarmManager = (AlarmManager)NotifierService.this.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pIntentAlarm = PendingIntent.getBroadcast(NotifierService.this, 123456789, new Intent(NotifierService.this, AlarmReceiver.class), 0);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, time, pIntentAlarm);
            NotificationManager notificationMan = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);

            // Set the icon, scrolling text and timestamp
            NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifierService.this);

            int notifyID = 001;
            Notification notification = new Notification(R.drawable.common_full_open_on_phone, "Test Alarm",
                    System.currentTimeMillis());
            //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, TestActivity.class), 0);
            notificationMan.notify(notifyID, builder.build());
        }*/

    }

    @Override
    public void onDestroy()
    {

    }
}
