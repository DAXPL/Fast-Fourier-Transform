package com.example.miloszklim_fft;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity
{

    int samplingFrequency = 6000;//FS
    int f = 2800;//F
    int blocksize = 2048;
    double[] x;
    double[] y;
    double[] ampl;
    double ymax = 0;

    ImageView iv;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    FFT myFFT = new FFT(blocksize);

    TextView maxYtext;
    TextView fsText;
    TextView fText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new double[blocksize];
        y = new double[blocksize];
        ampl = new double[blocksize/2];

        iv = this.findViewById(R.id.wykres);
        bitmap = Bitmap.createBitmap(blocksize/2,520,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        iv.setImageBitmap(bitmap);
        canvas.drawColor(Color.RED);

        Button buttonStart = findViewById(R.id.ButtonStart);
        Button buttonStop = findViewById(R.id.ButtonStop);
        Slider FSslider = findViewById(R.id.SliderFS);
        Slider Fslider = findViewById(R.id.SliderF);
        maxYtext = findViewById(R.id.textMaxY);
        fsText= findViewById(R.id.textViewFS);
        fText = findViewById(R.id.textF);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ComputeAmpl();
                DrawChart();
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                canvas.drawColor(Color.BLACK);
            }
        });

        FSslider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                samplingFrequency = (int)value;
                fsText.setText("samplingFrequency: " + samplingFrequency);
            }
        });

        Fslider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                f = (int)value;
                fText.setText("F: " + f);
            }
        });


    }

    public void GenerateSignal()
    {
        for(int i=0;i<blocksize;i++)
        {
            x[i] = Math.sin(2*Math.PI*f*((double)i/samplingFrequency));
            y[i] = 0;
        }
    }

    public void ComputeAmpl()
    {
        GenerateSignal();
        myFFT.fft(x,y);
        ymax=0;
        for(int i = 0; i<blocksize/2;i++)
        {
            ampl[i]=x[i]*x[i]+y[i]*y[i];
            if(ampl[i]>ymax)ymax=ampl[i];
        }
        for(int i=0;i<blocksize/2;i++)
        {
            ampl[i]=ampl[i]*500/ymax;
        }
        maxYtext.setText("Wartość: " + ymax);
    }
    public void DrawChart()
    {
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.GREEN);
        for(int i=0; i<ampl.length;i++)
        {
            int downy = 510;
            int upy = 510 - (int) ampl[i];
            canvas.drawLine(i,downy,i,upy,paint);
        }
    }
}