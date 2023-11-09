package com.example.runningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ShowRunActivity extends AppCompatActivity {
    TextView metresTextView, caloriesTextView, dateTextView, timeTextView;
    Button returnButton;
    int steps, seconds;
    Date runDate = new Date();
    double calories, metres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_run);

        // Get text views and button
        metresTextView = findViewById(R.id.metresTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timerTextView);
        returnButton = findViewById(R.id.returnButton);

        // Get details from previous view
        Bundle runData = getIntent().getExtras();
        steps = runData.getInt("Steps");
        seconds = runData.getInt("Seconds");

        // Do calculation for the run data
        calories = steps * 0.04;
        metres = steps * 0.8;


        // Display data on screen
        metresTextView.setText(String.format("Metres:\n%.2fm", metres));
        caloriesTextView.setText(String.format("Calories Burned:\n%.2fkcal", calories));
        dateTextView.setText("Date of run:\n" + String.valueOf(new SimpleDateFormat("dd/MM/yyyy").format(runDate)));
        timeTextView.setText("Time ran:\n" + seconds + " seconds");

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}