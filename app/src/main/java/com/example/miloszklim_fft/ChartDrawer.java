package com.example.miloszklim_fft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class ChartDrawer
{
    ImageView iv;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    /*---UI---*/
    TextView maxYtext;
    TextView tempOutputFinal;
    TextView fsText;
    TextView fText;
    EditText multiplierInput;

    MainActivity main;
    public ChartDrawer(MainActivity _main)
    {
        main = _main;

        maxYtext = main.findViewById(R.id.tempOutput);
        tempOutputFinal = main.findViewById(R.id.tempOutputFinal);
        fsText = main.findViewById(R.id.textViewFS);
        fText = main.findViewById(R.id.textF);
        multiplierInput = main.findViewById(R.id.multipilerInput);

        iv = main.findViewById(R.id.wykres);
        bitmap = Bitmap.createBitmap(main.blocksize / 2, 520, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        iv.setImageBitmap(bitmap);
        canvas.drawColor(Color.RED);

        Button buttonStart = main.findViewById(R.id.ButtonStart);
        Button buttonStop = main.findViewById(R.id.ButtonStop);
        Slider FSslider = main.findViewById(R.id.SliderFS);
        Slider Fslider = main.findViewById(R.id.SliderF);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                main.samplingFrequency = (int) value;
                fsText.setText("samplingFrequency: " + main.samplingFrequency);
            }
        });

        Fslider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                main.f = (int) value;
                fText.setText("F: " + main.f);
            }
        });

        multiplierInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) main.multiplier = Double.parseDouble(multiplierInput.getText().toString());
            }
        });

    }

    public void DrawChart()
    {
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.GREEN);
        for(int i=0; i<main.ampl.length;i++)
        {
            int downy = 510;
            int upy = 510 - (int) main.ampl[i];
            canvas.drawLine(i,downy,i,upy,paint);
        }

        maxYtext.setText("Wartosc: " + main.ymax);
        tempOutputFinal.setText(main.temperature+" C");;
    }

}
