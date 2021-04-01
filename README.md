# Android_Pomodore

### App desarrollada en Java con Android Studio para utilizar la tecnica Pomodore de estudio

## Desarrollo
Se basa en un [Cronometro](https://developer.android.com/reference/android/widget/Chronometer) con cuenta regresiva que se implementa 
directamente en el archivo xml del main_activity y activa un [Foreground Service](https://developer.android.com/guide/components/foreground-services) 
y visualiza una notificacion cuando la app se cierra y el reloj aun esta corriendo.

En el control del service se implementa un [CountDonTimer](https://developer.android.com/reference/android/os/CountDownTimer) 
para mantener el temporizador en el texto de la notificación.

## Errores
Hace falta implementar codigo para recuperar los datos cuando la app no se abre directamente de la notificación.

## Interfaz
La interfaz gráfica es bastante simple y se implementan conceptos basicos de diseño para el color y las figuras.

<p float="left">
  <img src="https://github.com/jho3r/Android_Pomodore/blob/master/app/src/main/res/drawable-v24/1.jpg" width="400" />
  <img src="https://github.com/jho3r/Android_Pomodore/blob/master/app/src/main/res/drawable-v24/2.jpg" width="400" />
</p>
