package nandroid.artesanus.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Chronometer;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import nandroid.artesanus.common.BrewProcess;
import nandroid.artesanus.common.Event;
import nandroid.artesanus.gui.MonitoringActivity;
import nandroid.artesanus.gui.R;
import nandroid.artesanus.http.IAsyncHttpResponse;
import nandroid.artesanus.http.PostController;


/**
 * This class is intended to handle the countdown process to inform MonitoringActivity and to launch alarm notifications
 */
public class TimerService extends IntentService implements IAsyncHttpResponse {

    Context _context;
    private final int _notificationID = 000000;

    public TimerService() {
        super("TimerService");
    }

    @Override
    public void onCreate() {

        new Thread(new Runnable(){

            @Override
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (isAppIsInBackground(getApplicationContext()))
                    {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                        Gson gson = builder.create();
                        String json = gson.toJson("");
                        PostController controller = new PostController();
                        controller.execute("/retrieve/last_anomalous_event/", json);
                    }
                }
            }
        }).start();
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void ProcessFinish(String output)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // String format in database
        Gson gson = builder.create();
        try {
            Event lastEvent = gson.fromJson(output, Event.class);
            Intent notificationIntent = new Intent(getApplicationContext(), MonitoringActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.dark_yellow_beer)
                            .setContentTitle(getResources().getString(R.string.event_background) + lastEvent.getSource() )
                            .setContentText(lastEvent.getMessage())
                            .setContentIntent(intent);
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Gets an instance of the NotificationManager service//

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(_notificationID, notification);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {}
}
