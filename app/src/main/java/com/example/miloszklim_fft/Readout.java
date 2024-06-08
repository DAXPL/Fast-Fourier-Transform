package com.example.miloszklim_fft;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class Readout extends Thread {

    // Konfiguracja kanału audio i format
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    // Rozmiar bloku i częstotliwość próbkowania
    int blocksize = 2048;
    int samplingFrequency = 6000; // FS

    // Wskaźniki do głównych komponentów
    android.app.Activity activity;
    double[] x;
    double[] y;
    double[] ampl;
    FFT myFFT;

    boolean shouldRun = true;

    MainActivity main;

    public Readout(MainActivity _main) {
        main = _main;
        blocksize = _main.blocksize;
        samplingFrequency = _main.samplingFrequency;
        activity = _main;
        x = _main.x;
        y = _main.y;
        ampl = _main.ampl;

        myFFT = new FFT(blocksize);
    }

    @Override
    public void run() {
        Log.d("Readout", "Readout thread " + Thread.currentThread().getId() + " is running");
        for (int i = 0; i < blocksize; i++) {
            y[i] = 0;
        }
        short[] audioBuffer = new short[blocksize];

        int bufferSize = AudioRecord.getMinBufferSize(samplingFrequency, channelConfiguration, audioEncoding);

        // Sprawdzanie uprawnień do nagrywania audio
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);
            return;
        }

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingFrequency, channelConfiguration, audioEncoding, bufferSize);
        audioRecord.startRecording();

        while (shouldRun) {
            // Odczytywanie danych audio
            int bufferReadResult = audioRecord.read(audioBuffer, 0, blocksize);
            Log.d("Audio", audioRecord.getActiveRecordingConfiguration().toString());

            for (int i = 0; i < blocksize && i < bufferReadResult; i++) {
                x[i] = (double) audioBuffer[i] / 32768.0; // konwersja 16-bitowych danych na double
            }

            // Obliczenia
            ComputeAmpl();
            main.temperature = ((ComputeAVG() * main.a) + main.b);

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

    /**
     * Obliczanie amplitudy sygnału.
     */
    private void ComputeAmpl() {
        myFFT.fft(x, y);

        main.ymax = 0;
        double maxValue = Double.MIN_VALUE;

        for (int i = 0; i < blocksize / 2; i++) {
            ampl[i] = x[i] * x[i] + y[i] * y[i];

            if (ampl[i] > maxValue) {
                maxValue = ampl[i];
                main.ymax = i;
            }
        }
        Log.d("temp", main.ymax + " " + maxValue);

        for (int i = 0; i < blocksize / 2; i++) {
            ampl[i] = (ampl[i] * 500) / maxValue;
        }
    }

    /**
     * Obliczanie średniej amplitudy z odczytów.
     *
     * @return Średnia amplituda.
     */
    private double ComputeAVG() {
        for (int i = 0; i < main.readings.length - 1; i++) {
            main.readings[i] = main.readings[i + 1];
        }
        main.readings[main.readings.length - 1] = main.ymax;

        double avg = 0;
        for (int i = 0; i < main.readings.length; i++) {
            avg += main.readings[i];
        }

        return (avg / main.readings.length);
    }
}
