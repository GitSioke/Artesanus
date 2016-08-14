package services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.example.nando.arti.R;

public class NotifierService extends Service
{
    public NotifierService()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Integer minutes = intent.getIntExtra("Minutes", 0);
        if (minutes > 0)
        {
            /*long time = System.currentTimeMillis() + (minutes*1000);
            AlarmManager alarmManager = (AlarmManager)NotifierService.this.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pIntentAlarm = PendingIntent.getBroadcast(NotifierService.this, 123456789, new Intent(NotifierService.this, AlarmReceiver.class), 0);
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, time, pIntentAlarm);*/
            NotificationManager notificationMan = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);

            // Set the icon, scrolling text and timestamp
            NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifierService.this);

            int notifyID = 001;
            Notification notification = new Notification(R.drawable.common_full_open_on_phone, "Test Alarm",
                    System.currentTimeMillis());
            //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, TestActivity.class), 0);
            notificationMan.notify(notifyID, builder.build());
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {

    }
}
