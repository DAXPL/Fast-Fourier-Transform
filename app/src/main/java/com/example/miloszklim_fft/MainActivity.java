package com.example.miloszklim_fft;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

// Importy związane z nagrywaniem dźwięku
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MainActivity extends AppCompatActivity {

    // Częstotliwość próbkowania (Hz)
    int samplingFrequency = 6000;
    // Częstotliwość sygnału (Hz)
    int f = 2800;
    // Rozmiar bloku
    int blocksize = 2048;

    // Tablice do przechowywania danych
    double[] x;
    double[] y;
    double[] ampl;
    //double ymax = 0;

    // Parametry związane z odczytem temperatury
    int window = 25;
    double[] readings = new double[window];
    double temperature = 0;
    double a = 1;
    double b = 0;
    public Readout readoutProcess;

    // Obiekt do rysowania wykresu
    ChartDrawer drawer;
    // Flaga oznaczająca stan działania programu
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja tablic
        x = new double[blocksize];
        y = new double[blocksize];
        ampl = new double[blocksize / 2];

        // Inicjalizacja obiektu rysującego wykres
        drawer = new ChartDrawer(this);

        // Rysowanie wykresu
        DrawChart();
    }

    /**
     * Metoda rysująca wykres.
     */
    public void DrawChart() {
        drawer.DrawChart();
        Refresh(500); // Odświeżanie wykresu co 500 ms
    }

    /**
     * Metoda odświeżająca wykres po określonym czasie.
     *
     * @param millis czas w milisekundach
     */
    public void Refresh(int millis) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DrawChart();
            }
        };
        handler.postDelayed(runnable, millis);
    }
}