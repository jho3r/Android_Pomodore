package com.jhoerperez.pomodore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_CONTANDO;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_IMAGE;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_NPOMODORO;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_TIEMPO;
import static com.jhoerperez.pomodore.MainActivity.INTENT_KEY_TRABAJO;

//Activity de paso para main idea de:
//https://developer.android.com/training/notify-user/navigation
public class LecturaExtras extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura__extras);

        Log.d("Pomodore","En lectura de extras");

        Bundle extras = getIntent().getExtras();
        long tiempoPausado = extras.getLong(INTENT_KEY_TIEMPO,0);
        boolean trabajo = extras.getBoolean(INTENT_KEY_TRABAJO,true);
        int nPomodoros = extras.getInt(INTENT_KEY_NPOMODORO,0);
        int image = extras.getInt(INTENT_KEY_IMAGE,R.drawable.tomate);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_TIEMPO,tiempoPausado);
        intent.putExtra(INTENT_KEY_TRABAJO,trabajo);
        intent.putExtra(INTENT_KEY_NPOMODORO,nPomodoros);
        intent.putExtra(INTENT_KEY_IMAGE,image);
        intent.putExtra(INTENT_KEY_CONTANDO,1);
        startActivity(intent);

    }
}