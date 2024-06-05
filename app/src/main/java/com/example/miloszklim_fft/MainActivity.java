package com.example.miloszklim_fft;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MainActivity extends AppCompatActivity {

    int samplingFrequency = 6000;//FS
    int f = 2800;//F
    int blocksize = 2048;
    double[] x;
    double[] y;
    double[] ampl;
    double ymax = 0;

    int window = 50;
    double[] readings = new double[window];
    double temperature = 0;
    double a = 1;
    double b = 0;
    Readout readoutProcess;
    ChartDrawer drawer;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new double[blocksize];
        y = new double[blocksize];
        ampl = new double[blocksize / 2];

        drawer = new ChartDrawer(this);

        readoutProcess = new Readout(this);
        readoutProcess.start();

        DrawChart();
    }

    public void DrawChart()
    {
        drawer.DrawChart();
        Refresh(500);
    }

    public void Refresh(int millis)
    {
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