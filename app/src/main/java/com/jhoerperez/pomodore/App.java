package com.jhoerperez.pomodore;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

//Clase creada para crear el canal de notificaciones, se debe declarar de forma especial en el manifest
public class App extends Application {
    public static final String CHANNEL_ID = "serviceChannel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationCahnnel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationCahnnel(){
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
