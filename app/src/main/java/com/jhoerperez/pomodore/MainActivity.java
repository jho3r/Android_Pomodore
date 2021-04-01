package com.jhoerperez.pomodore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TRABAJO = "trabajo";
    public static final String INTENT_KEY_TIEMPO = "cronometro";
    public static final String INTENT_KEY_NPOMODORO = "npomodoros";
    public static final String INTENT_KEY_IMAGE = "image";
    public static final String INTENT_KEY_CONTANDO = "contando";
    Chronometer crono;
    Button btnIniciar;
    ImageView ivtomate;
    boolean contando = false;
    private boolean trabajo = true;
    private long tiempoTrabajo;
    private int nPomodoros = 0;
    private int image;

    // Se va a usar un foreground service para mantener la app activa
    //https://developer.android.com/guide/components/foreground-services

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Pomodore", "Inciando app oncreate");

        ivtomate = findViewById(R.id.ivTomate);
        btnIniciar = findViewById(R.id.btnIniciar);
        crono = findViewById(R.id.cronometer);
        crono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //Al comparar el tiempoactual menos el base puedo saber cuantos milisegundos han pasado
                if(SystemClock.elapsedRealtime() - crono.getBase() >= 0){
                    crono.stop();
                    //Si completa cuatro pomodoros el tiempo de descanso es mayor
                    if (trabajo){
                        nPomodoros ++;
                        if (nPomodoros%4 == 0 && nPomodoros>0){
                            tiempoTrabajo = 20 * 60 * 1000;
                        }else {
                            tiempoTrabajo = 5 * 60 * 1000;
                        }
                        image = R.drawable.rest;
                        ivtomate.setImageResource(R.drawable.rest);
                        Log.d("Pomodore","cambiando a descanso");
                        trabajo = false;
                        crono.setBase(SystemClock.elapsedRealtime()+tiempoTrabajo);
                        crono.start();
                        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.rest);
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
                        tiempoTrabajo = 25 * 60 * 1000;
                        image = R.drawable.working;
                        ivtomate.setImageResource(R.drawable.working);
                        Log.d("Pomodore","cambiando a trabajo");
                        trabajo = true;
                        crono.setBase(SystemClock.elapsedRealtime()+tiempoTrabajo);
                        crono.start();
                        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.work);
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
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            Log.d("Pomodore","extras diferente de null");
            recoverExtras(extras);
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void iniciar(View v){
        tiempoTrabajo = 25 * 60 * 1000;
        if(contando == false){
            image = R.drawable.working;
            ivtomate.setImageResource(R.drawable.working);
            btnIniciar.setVisibility(View.GONE);
            crono.setVisibility(View.VISIBLE);
            crono.setBase(SystemClock.elapsedRealtime()+tiempoTrabajo);
            crono.setCountDown(true);
            crono.start();
            contando = true;
        }else{
            //Pausando el cronometro
            long tiempoPausado = (crono.getBase() - SystemClock.elapsedRealtime());
            crono.stop();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.pausatexto)
                    .setTitle(R.string.pausatitulo);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked reanudar button
                    crono.setBase(SystemClock.elapsedRealtime() + tiempoPausado);
                    crono.start();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User reinicio the dialog
                    contando = false;
                    btnIniciar.setVisibility(View.VISIBLE);
                    crono.setVisibility(View.GONE);
                    ivtomate.setImageResource(R.drawable.tomate);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Pomodore","App pausada");
        if (contando){
            long tiempoPausado = (crono.getBase() - SystemClock.elapsedRealtime());
            Intent serviceIntent = new Intent(this, ExampleService.class);
            serviceIntent.putExtra(INTENT_KEY_TIEMPO,tiempoPausado);
            serviceIntent.putExtra(INTENT_KEY_TRABAJO,trabajo);
            serviceIntent.putExtra(INTENT_KEY_NPOMODORO,nPomodoros);
            serviceIntent.putExtra(INTENT_KEY_IMAGE,image);
            startService(serviceIntent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Pomodore","App resumida");

        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void recoverExtras(Bundle extras) {

        int contandoIntent = extras.getInt(INTENT_KEY_CONTANDO,0);

        if (contandoIntent == 0){
            return;
        }else{
            Log.d("Pomodore","Recuperando extras");
            long tiempoPausado = extras.getLong(INTENT_KEY_TIEMPO,0);
            trabajo = extras.getBoolean(INTENT_KEY_TRABAJO,true);
            nPomodoros = extras.getInt(INTENT_KEY_NPOMODORO,0);
            image = extras.getInt(INTENT_KEY_IMAGE,R.drawable.tomate);

            Log.d("Pomodore","extras: tiempo: " + tiempoPausado + "trabajo: " + trabajo + "npomodoros: " + nPomodoros);

            ivtomate.setImageResource(image);
            btnIniciar.setVisibility(View.GONE);
            crono.setVisibility(View.VISIBLE);
            crono.setBase(SystemClock.elapsedRealtime()+(tiempoPausado-1000));
            crono.setCountDown(true);
            crono.start();
            contando = true;
        }


    }
}