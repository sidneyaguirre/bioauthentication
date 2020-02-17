package com.example.biomeasures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private Button button = null;
    private TextView measures = null;


    private MotionEvent ev;
    private float x;
    private float y;
    private float press;
    private float toucharea;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        measures = findViewById(R.id.measures);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                x = ev.getX();
                y = ev.getY();
                System.out.println("COORDS: x-> " + x + ", y-> " + y );
                press = ev.getPressure();
                System.out.println("PRESSURE: " + press);
                toucharea = ev.getSize();
                System.out.println("TOUCH AREA: " + toucharea);
                message = "COORDS: x-> " + x + ", y-> " + y +"\nPressure: " + press + "\nTouch Area: " + toucharea;
                //message = String.valueOf(press);
                measures.setText(message);
                System.out.println(message);
                return false;
            }
        });


    }
}

