package com.jhoerperez.pomodore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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

    Chronometer crono;
    Button btnIniciar;
    ImageView ivtomate;
    boolean contando = false;
    boolean trabajo = true;
    long tiempoTrabajo;
    int nPomodoros = 0;

    // Se va a usar un foreground service para mantener la app activa
    //https://developer.android.com/guide/components/foreground-services

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    if (trabajo == true){
                        nPomodoros ++;
                        if (nPomodoros%4 == 0 && nPomodoros>0){
                            tiempoTrabajo = 20 * 60 * 1000;
                        }else {
                            tiempoTrabajo = 5 * 60 * 1000;
                        }
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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void iniciar(View v){
        tiempoTrabajo = 25 * 60 * 1000;
        if(contando == false){
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Pomodore","App parada");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Pomodore","App resumida");
    }
}