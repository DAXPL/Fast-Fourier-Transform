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

    ImageView iv;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    FFT myFFT = new FFT(blocksize);
    /*---AUDIO---*/
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO; //CHANNEL_CONFIGURATION_MONO
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    /*---UI---*/
    TextView maxYtext;
    TextView fsText;
    TextView fText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new double[blocksize];
        y = new double[blocksize];
        ampl = new double[blocksize / 2];

        iv = this.findViewById(R.id.wykres);
        bitmap = Bitmap.createBitmap(blocksize / 2, 520, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        iv.setImageBitmap(bitmap);
        canvas.drawColor(Color.RED);

        Button buttonStart = findViewById(R.id.ButtonStart);
        Button buttonStop = findViewById(R.id.ButtonStop);
        Slider FSslider = findViewById(R.id.SliderFS);
        Slider Fslider = findViewById(R.id.SliderF);
        maxYtext = findViewById(R.id.textMaxY);
        fsText = findViewById(R.id.textViewFS);
        fText = findViewById(R.id.textF);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ComputeAmpl();
                DrawChart();
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                canvas.drawColor(Color.BLACK);
            }
        });

        FSslider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                samplingFrequency = (int) value;
                fsText.setText("samplingFrequency: " + samplingFrequency);
            }
        });

        Fslider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                f = (int) value;
                fText.setText("F: " + f);
            }
        });
    }

    public void GenerateSignal() {
        for (int i = 0; i < blocksize; i++) {
            x[i] = Math.sin(2 * Math.PI * f * ((double) i / samplingFrequency));
            y[i] = 0;
        }
    }

    public void ComputeAmpl() {
        //GenerateSignal();
        Odczyt();
        myFFT.fft(x, y);
        ymax = 0;
        for (int i = 0; i < blocksize / 2; i++) {
            ampl[i] = x[i] * x[i] + y[i] * y[i];
            if (ampl[i] > ymax) ymax = ampl[i];
        }
        for (int i = 0; i < blocksize / 2; i++) {
            ampl[i] = ampl[i] * 500 / ymax;
        }
        maxYtext.setText("Wartość: " + ymax);
    }

    private void Odczyt() {
        for (int i = 0; i < blocksize; i++) {
            y[i] = 0;
        }
        short[] audioBuffer = new short[blocksize];

        int bufferSize = AudioRecord.getMinBufferSize(samplingFrequency,
                channelConfiguration, audioEncoding);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO},0);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, samplingFrequency,
                channelConfiguration, audioEncoding, bufferSize);

        audioRecord.startRecording();
        //while (started) {
        int bufferReadResult = audioRecord.read(audioBuffer, 0, blocksize);

        for (int i = 0; i < blocksize && i < bufferReadResult; i++) {
            x[i] = (double) audioBuffer[i] / 32768.0; // signed 16 bit
        }
        //}
        audioRecord.stop();

        //return null;
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