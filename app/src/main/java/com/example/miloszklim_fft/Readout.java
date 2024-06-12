package com.example.miloszklim_fft;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Readout extends Thread {

    // Konfiguracja kanału audio
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    // Wskaźniki do głównych komponentów

    double[] x;
    double[] y;
    double[] ampl;
    android.app.Activity activity;
    FFT myFFT;
    MainActivity main;

    //Flaga wyznaczająca czas życia wątku
    boolean shouldRun = true;

    //Konstruktor klasy
    public Readout(MainActivity _main)
    {
        main = _main;

        activity = _main;
        x = _main.x;
        y = _main.y;
        ampl = _main.ampl;

        myFFT = new FFT(_main.blocksize);
    }

    @Override
    public void run()
    {
        for (int i = 0; i < main.blocksize; i++) {
            y[i] = 0;
        }

        short[] audioBuffer = new short[main.blocksize];
        int bufferSize = AudioRecord.getMinBufferSize(main.samplingFrequency,
                                                        channelConfiguration,
                                                        audioEncoding);

        // Sprawdzanie uprawnień do nagrywania audio
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity,
                                            new String[]{android.Manifest.permission.RECORD_AUDIO},
                                            0);
            return;
        }

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                                                    main.samplingFrequency,
                                                    channelConfiguration,
                                                    audioEncoding,
                                                    bufferSize);
        audioRecord.startRecording();

        while (shouldRun) {
            // Odczytywanie danych audio
            int bufferReadResult = audioRecord.read(audioBuffer, 0, main.blocksize);

            for (int i = 0; i < main.blocksize && i < bufferReadResult; i++) {
                x[i] = (double) audioBuffer[i] / 32768.0; // konwersja 16-bitowych danych na double
            }

            // Obliczenia
            int newReading = ComputeFFT();
            main.temperature = (main.a * ComputeAVG(newReading)) + main.b;//y = ax + b

            // Uśpienie wątku
            try {
                Thread.sleep(200); // 5 razy na sekundę
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        audioRecord.stop();
    }

    //Obliczanie amplitudy sygnału
    //Zwraca miejsce w którym osiągnięto "spike"
    private int ComputeFFT() {
        myFFT.fft(x, y);

        int ymax = 0;
        double maxValue = Double.MIN_VALUE;

        for (int i = 0; i < main.blocksize / 2; i++)
        {
            ampl[i] = x[i] * x[i] + y[i] * y[i];

            if (ampl[i] > maxValue) {
                maxValue = ampl[i];
                ymax = i;
            }
        }

        for (int i = 0; i < main.blocksize / 2; i++)
        {
            ampl[i] = (ampl[i] * 500) / maxValue;
        }

        return ymax;
    }

    // Obliczanie średniej amplitudy z odczytów.
    private double ComputeAVG(int newReading)
    {
        //Przesuwanie pomiarów - średnia pełzająca
        for (int i = 0; i < main.readings.length - 1; i++)
        {
            main.readings[i] = main.readings[i + 1];
        }
        main.readings[main.readings.length - 1] = newReading;

        //Wyznaczanie średniej
        double avg = 0;
        for (int i = 0; i < main.readings.length; i++)
        {
            avg += main.readings[i];
        }

        return (avg / main.readings.length);
    }
}
