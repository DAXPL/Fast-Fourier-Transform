package com.example.miloszklim_fft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Częstotliwość próbkowania (Hz)
    int samplingFrequency = 12000;
    // Częstotliwość sygnału (Hz)
    int f = 2800;
    // Rozmiar bloku
    int blocksize = 2048;

    // Tablice do przechowywania danych
    double[] x;
    double[] y;
    double[] ampl;

    // Parametry związane z odczytem temperatury
    int window = 50;
    double[] readings = new double[window];
    double temperature = 0;
    double a = 1;
    double b = 0;

    // Flaga oznaczająca stan działania programu
    boolean isRunning = false;


    // Elementy interfejsu użytkownika
    ImageView iv;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    TextView tempOutputFinal;
    TextView chartMin;
    TextView chartMax;

    //Wielowątkowość
    MainActivity mainThread;
    Readout readoutProcess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Znajdź elementy interfejsu w układzie
        tempOutputFinal = findViewById(R.id.tempOutputFinal);
        chartMin = findViewById(R.id.chartMin);
        chartMax = findViewById(R.id.chartMax);

        iv = findViewById(R.id.wykres);
        bitmap = Bitmap.createBitmap(blocksize / 2, 520, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        iv.setImageBitmap(bitmap);
        canvas.drawColor(Color.RED);

        Button buttonStart = findViewById(R.id.ButtonStart);

        mainThread = this;

        // Ustawienie listenerów dla elementów interfejsu
        buttonStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!isRunning) {
                    buttonStart.setText("Stop");

                    readoutProcess = new Readout(mainThread);
                    readoutProcess.start();
                } else {
                    readoutProcess.shouldRun = false;
                    canvas.drawColor(Color.BLACK);
                    buttonStart.setText("Start");
                }

                isRunning = !isRunning;

            }
        });

        // Inicjalizacja tablic
        x = new double[blocksize];
        y = new double[blocksize];
        ampl = new double[blocksize / 2];


        // Rysowanie wykresu
        DrawChart();
    }

    // Metoda rysująca wykres.
    public void DrawChart()
    {
        canvas.drawColor(Color.BLACK);

        // Rysowanie danych amplitudy na zielono
        paint.setColor(Color.GREEN);
        for (int i = 0; i < ampl.length; i++) {
            int downy = 510;
            int upy = 510 - (int) ampl[i];

            canvas.drawLine(i, downy, i, upy, paint);
        }

        // Rysowanie linii siatki na czerwono
        paint.setColor(Color.RED);
        for (int i = 0; i < ampl.length; i++) {
            if (i % 10 == 0) {
                canvas.drawLine(i, 510, i, 500, paint);
            }
            if (i % 100 == 0) {
                canvas.drawLine(i, 510, i, 475, paint);
            }
        }

        // Aktualizacja etykiet wykresu
        chartMin.setText("0");
        chartMax.setText(String.valueOf(ampl.length));

        // Aktualizacja wyświetlania temperatury
        tempOutputFinal.setText(String.format("%.2f", temperature) + " C");
        Refresh(500); // Odświeżanie wykresu co 500 ms
    }

    // Metoda odświeżająca wykres po określonym czasie.
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