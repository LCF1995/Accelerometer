package br.com.layse.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager mSensorManager;
    private Button start, stop, send;
    private boolean isRunning = false;
    private Sensor mSensor;
    private long steps = 0;
    private TextView textViewX, textViewY, textViewZ;
    private String x, y, z;

    private boolean run;
    private int test=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    public void initViews() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        start = findViewById(R.id.buttonStart);
        stop = findViewById(R.id.buttonStop);
        send = findViewById(R.id.buttonSend);

        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);


        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mSensor , SensorManager.SENSOR_DELAY_NORMAL);



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                onResume();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                test=0;
                onPause();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(test == 0){

                    test=1;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            run=true;
                            while(run){

                                new MessageSender().execute(x+","+y+","+z);

                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e){

                                }

                            }


                        }
                    }).start();

                }



            }
        });

        onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        x = String.valueOf(sensorEvent.values[0]);
        y = String.valueOf(sensorEvent.values[1]);
        z = String.valueOf(sensorEvent.values[2]);


        textViewX.setText("X: "+x);
        textViewY.setText("Y: "+y);
        textViewZ.setText("Z: "+z);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("LOG", "onAccuracyChanged -> " +i);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        Log.i("LOG", "Sensor onPause");
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("LOG", "Sensor onResume");
    }

    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }



}
