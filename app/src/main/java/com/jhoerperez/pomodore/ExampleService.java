package com.jhoerperez.pomodore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.jhoerperez.pomodore.App.CHANNEL_ID;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_IMAGE;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_NPOMODORO;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_TIEMPO;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_TRABAJO;


public class ExampleService extends Service {

    private Chronometer cronoService;
    CountDownTimer timerServiceWork;
    CountDownTimer timerServiceRest;
    CountDownTimer timerServiceRest2;
    CountDownTimer timerServiceInicial;
    private long crono;
    private boolean trabajo;
    private int nPomodoros;
    private long tiempoTrabajo;
    private int image;
    private String titulo;

    private static final int ONGOING_NOTIFICATION_ID = 1;

    //Esta clase tambien se debe declarar en el manifest como service
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Pomodore","Servicio Creado");

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        crono = intent.getLongExtra(INTENT_KEY_TIEMPO,25*60*1000);
        trabajo = intent.getBooleanExtra(INTENT_KEY_TRABAJO,true);
        nPomodoros = intent.getIntExtra(INTENT_KEY_NPOMODORO,0);
        image = intent.getIntExtra(INTENT_KEY_IMAGE,R.drawable.working);
        Log.d("Pomodore","Creando notificación");
        if (trabajo){
            titulo = getString(R.string.trabajo);
        }else{
            titulo = getString(R.string.descanso);
        }
        cronometro();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Este metodo se debe crear pero no es necesario poner nada dentro a menos que se use un binder service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void cronometro(){
        Log.d("Pomodore","Cronometro iniciado");

        timerServiceInicial = new CountDownTimer(crono, 1000) {
            public void onTick(long millisUntilFinished) {
                updateNotificacion(millisUntilFinished);
            }
            public void onFinish() {
                logicaDeConteo();
            }
        }.start();

        timerServiceWork = new CountDownTimer(25*60*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                updateNotificacion(millisUntilFinished);
            }
            public void onFinish() {
                logicaDeConteo();
            }
        };

        timerServiceRest = new CountDownTimer(5*60*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                updateNotificacion(millisUntilFinished);
            }
            public void onFinish() {
                logicaDeConteo();
            }
        };

        timerServiceRest2 = new CountDownTimer(15*60*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                updateNotificacion(millisUntilFinished);
            }
            public void onFinish() {
                logicaDeConteo();
            }
        };
    }

    public void updateNotificacion(long millisUntilFinished){
        Intent notificationIntent = new Intent(getApplicationContext(), LecturaExtras.class);
        notificationIntent.putExtra(INTENT_KEY_TIEMPO,millisUntilFinished);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(INTENT_KEY_TRABAJO,trabajo);
        notificationIntent.putExtra(INTENT_KEY_NPOMODORO,nPomodoros);
        notificationIntent.putExtra(INTENT_KEY_IMAGE,image);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long minutos = millisUntilFinished/(1000*60);
        long segundos = (millisUntilFinished - minutos*1000*60)/1000;
        String segundosS;
        if (segundos<10){
            segundosS = "0" + segundos;
        }else{
            segundosS = "" + segundos;
        }
        Notification notification = new NotificationCompat.Builder(ExampleService.this, CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(minutos + ":" + segundosS)
                .setSmallIcon(R.drawable.tomate)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        //NotificationManager mNotificationManager =
        //        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //mNotificationManager.notify(ONGOING_NOTIFICATION_ID,notification);

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    public void logicaDeConteo(){

        if (trabajo == true){
            nPomodoros ++;
            image = R.drawable.rest;
            Log.d("Pomodore","cambiando a descanso");
            titulo = getString(R.string.descanso);
            trabajo = false;
            if (nPomodoros%4 == 0 && nPomodoros>0){
                timerServiceRest2.start();
            }else {
                timerServiceRest.start();
            }
            MediaPlayer mediaPlayer = MediaPlayer.create(ExampleService.this, R.raw.rest);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    mediaPlayer.release();
                    Log.d("Pomodore", "Media liberado");
                }
            }, 5000);

        }else{
            crono = (long) 25 * 60 * 1000;
            image = R.drawable.working;
            Log.d("Pomodore","cambiando a trabajo");
            titulo = getString(R.string.trabajo);
            trabajo = true;
            timerServiceWork.start();
            MediaPlayer mediaPlayer = MediaPlayer.create(ExampleService.this, R.raw.work);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    mediaPlayer.release();
                    Log.d("Pomodore", "Media liberado");
                }
            }, 5000);

        }
    }
}
