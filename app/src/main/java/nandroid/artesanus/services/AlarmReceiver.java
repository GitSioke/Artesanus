package nandroid.artesanus.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import nandroid.artesanus.gui.R;

/**
 * Created by nando on 11/08/2016.
 */
public class AlarmReceiver extends BroadcastReceiver
{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "NO JODAS", Toast.LENGTH_LONG).show();
            NotificationManager notificationMan = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);

            // Set the icon, scrolling text and timestamp
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            int notifyID = 001;
            Notification notification = new Notification(R.drawable.common_full_open_on_phone, "Test Alarm",
                    System.currentTimeMillis());
            //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, TestActivity.class), 0);
            notificationMan.notify(notifyID, builder.build());

            // Set the info for the views that show in the notification panel.
            /*notification.setLatestEventInfo(context, context.getText(R.string.alarm_service_label), "This is a Test Alarm", contentIntent);

            // Send the notification.
            // We use a layout id because it is a unique number. We use it later to cancel.
            notificationMan.notify(R.string.alarm_service_label, notification);*/
        }

}
