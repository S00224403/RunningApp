package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private int seconds = 0;
    private int initialStepCount = 0;
    private TextView timerTextView;
    private TextView stepsTextView;
    private Button startButton, stopButton, resetButton, showRunButton;
    private Handler handler = new Handler();
    private Runnable timerRunnable; // Runnable task
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private int stepCount = 0;
    private boolean isRunActive = false;
    private float rmsLimit = 15;
    private float[] acceleration = new float[3]; // Co-ordinates of phone moving for calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        stepsTextView = findViewById(R.id.stepsTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);
        showRunButton = findViewById(R.id.showRunButton);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                seconds++;
                timerTextView.setText(seconds + " seconds");
                handler.postDelayed(this, 1000); // Update every 1 second
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunActive) { // Check if no run is active
                    initialStepCount = stepCount; // Store the initial step count
                    isRunActive = true; // Mark the run as active
                    startRun(); // Start the run
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunActive) { // Check if a run is active
                    stopRun(); // Stop the run
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRun(); // Reset the run
            }
        });
        showRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showRun();
            }
        });
    }

    private void startRun() {
        isRunActive = true;
        handler.post(timerRunnable); // Start execution of the timer runnable
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void stopRun() {
        isRunActive = false;
        handler.removeCallbacks(timerRunnable); // Stop execution of the timer runnable
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }

    private void resetRun() {
        stopRun();
        initialStepCount = stepCount; // Store the new initial step count
        stepCount = 0;
        seconds = 0;
        stepsTextView.setText(stepCount + " steps");
        timerTextView.setText(seconds + " seconds");
    }
    private void showRun(){
        stopRun();
        Intent ShowRunActivity = new Intent(getApplicationContext(), com.example.runningapp.ShowRunActivity.class);
        ShowRunActivity.putExtra("Steps", stepCount);
        ShowRunActivity.putExtra("Seconds", seconds);
        startActivity(ShowRunActivity);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stepDetectorSensor != null) {
            sensorManager.unregisterListener(this, stepDetectorSensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensor accuracy change event handler (not used here)
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isRunActive) {
            acceleration[0] = event.values[0]; //x
            acceleration[1] = event.values[1]; //y
            acceleration[2] = event.values[2]; //z

            // Calculating if a step is taken
            double rms = Math.sqrt(acceleration[0] * acceleration[0] + acceleration[1] * acceleration[1] + acceleration[2] * acceleration[2]);

            // If the rms is greater than the limit it is considered a step
            if (rms > rmsLimit) {
                // Step goes up by 1
                stepCount++;

                stepDetected(stepCount);
            }
        }
    }
    // Update the steps
    public void stepDetected(int stepCount) {
        runOnUiThread(() -> stepsTextView.setText(stepCount + " steps")); // Used to update steps when one has been detected.
    }
}



