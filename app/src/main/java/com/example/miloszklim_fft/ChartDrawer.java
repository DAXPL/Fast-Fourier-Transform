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
    TextView tempOutputFinal;
    TextView fsText;
    TextView fText;
    EditText aInput;
    EditText bInput;

    TextView chartMin;
    TextView chartMax;
    TextView audioDebug;
    MainActivity main;
    public ChartDrawer(MainActivity _main)
    {
        main = _main;

        tempOutputFinal = main.findViewById(R.id.tempOutputFinal);
        fsText = main.findViewById(R.id.textViewFS);
        fText = main.findViewById(R.id.textF);
        aInput = main.findViewById(R.id.aInput);
        bInput = main.findViewById(R.id.bInput);
        chartMin = main.findViewById(R.id.chartMin);
        chartMax = main.findViewById(R.id.chartMax);

        iv = main.findViewById(R.id.wykres);
        bitmap = Bitmap.createBitmap(main.blocksize / 2, 520, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        iv.setImageBitmap(bitmap);
        canvas.drawColor(Color.RED);

        Button buttonStart = main.findViewById(R.id.ButtonStart);
        Slider FSslider = main.findViewById(R.id.SliderFS);
        Slider Fslider = main.findViewById(R.id.SliderF);

        buttonStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(main.isRunning == false)
                {
                    try
                    {
                        main.a = Double.parseDouble(aInput.getText().toString());
                    }
                    catch(Exception e)
                    {
                        main.a = 1;
                    }

                    try
                    {
                        main.b = Double.parseDouble(bInput.getText().toString());
                    }
                    catch(Exception e)
                    {
                        main.b = 0;
                    }
                    buttonStart.setText("Stop");

                    main.readoutProcess = new Readout(main);
                    main.readoutProcess.start();

                }
                else
                {
                    main.readoutProcess.shouldRun = false;
                    canvas.drawColor(Color.BLACK);
                    buttonStart.setText("Start");
                }

                main.isRunning = !main.isRunning;
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

    }

    public void DrawChart()
    {
        canvas.drawColor(Color.BLACK);
        if(!main.isRunning) return;

        paint.setColor(Color.GREEN);
        for(int i=0; i<main.ampl.length;i++)
        {
            int downy = 510;
            int upy = 510 - (int) main.ampl[i];

            canvas.drawLine(i,downy,i,upy,paint);
        }

        paint.setColor(Color.RED);
        for(int i=0; i<main.ampl.length;i++){
            if(i%10 == 0)
            {
                canvas.drawLine(i,510,i,500,paint);
            }
            if(i%100 == 0)
            {
                canvas.drawLine(i,510,i,475,paint);
            }
        }

        chartMin.setText("0");
        chartMax.setText(main.ampl.length+"");

        tempOutputFinal.setText(String.format("%.2f",main.temperature)+" C");
    }

}
