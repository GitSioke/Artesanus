package nandroid.artesanus.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Chronometer;

import nandroid.artesanus.gui.MonitorActivity;


/**
 * This class is intended to handle the countdown process to inform MonitoringActivity and to launch alarm notifications
 */
public class TimerService extends Service {

    Thread mThread;

    Handler mHandler;

    Context mContext;

    public TimerService(Handler handler) {

        mHandler = handler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public TimerService(Context context, Handler handler) {
        mHandler = handler;
        mContext = context;

    }

    //TODO Averigurar como pasar datos a un servicio
    @Override
    public void onCreate()
    {
        //TODO Minutos jhardcoded
        final int minutes = 5;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //Countdown with value set by user
                    new CountDownTimer(minutes * 1000 * 60, 1000) {

                        public void onTick(long millisUntilFinished) {
                            long remainingSeconds = millisUntilFinished / 1000;
                            long remainingMinutes = millisUntilFinished / (1000 * 60);
                            int seconds = (int) remainingSeconds % 60;
                            int minutes = (int) remainingMinutes % 60;
                            //TODO Format time
                            byte[] buffer = new String(minutes + " : " + seconds).getBytes();
                            mHandler.obtainMessage(MonitorActivity.UPDATE_CLOCK, -1, -1, buffer)
                                    .sendToTarget();

                        }

                        public void onFinish() {
                            //StartCountUp();
                        }
                    }.start();
                }
            }


        });
    }

    public void start()
    {
        //TODO Minutos jhardcoded
        final int minutes = 1;

        //TODO Realizar la operacion en un hilo nuevo
        /*Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.getLooper().prepare();
                while (true) {
*/
                    //Countdown with value set by user
                    new CountDownTimer((long) (0.1 * 1000 * 60), 1000) {

                        public void onTick(long millisUntilFinished) {
                            long remainingSeconds = millisUntilFinished / 1000;
                            long remainingMinutes = millisUntilFinished / (1000 * 60);
                            int seconds = (int) remainingSeconds % 60;
                            int minutes = (int) remainingMinutes % 60;
                            //TODO Format time
                            byte[] buffer = new String(minutes + " : " + seconds).getBytes();

                            mHandler.obtainMessage(MonitorActivity.UPDATE_CLOCK, -1, -1, buffer)
                                    .sendToTarget();

                        }

                        public void onFinish()
                        {
                            countUp();
                        }
                    }.start();
       /*         }
            }
        });

        mThread.start();*/
    }

    public void countUp()
    {
        //TODO Realizar la operacion en un hilo nuevo
        /*Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.getLooper().prepare();
                while (true) {
*/

                Chronometer stopWatch = new Chronometer(mContext);
                long startTime = SystemClock.elapsedRealtime();

                stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;
                        long elapsedMinutes = elapsedSeconds/60;

                        String chronoStr = elapsedMinutes + ":" + elapsedSeconds;
                        mHandler.obtainMessage(MonitorActivity.FINISH_CLOCK, -1, -1, chronoStr.getBytes())
                                .sendToTarget();
                    }
                });

                stopWatch.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //TODO Probar que el Servicio recibe el comando y sigue su ejecucion
        return START_NOT_STICKY;
    }
}
