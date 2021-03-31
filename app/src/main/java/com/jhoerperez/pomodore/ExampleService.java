package com.jhoerperez.pomodore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ExampleService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
}
