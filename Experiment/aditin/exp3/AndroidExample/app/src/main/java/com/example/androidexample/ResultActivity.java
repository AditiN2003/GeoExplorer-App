package com.example.androidexample;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView randomNumberTextView = findViewById(R.id.randomNumberTextView);
        Button backButton = findViewById(R.id.backButton);

        // Get the random number from the intent
        Intent intent = getIntent();
        if (intent.hasExtra("randomNumber")) {
            int randomNumber = intent.getIntExtra("randomNumber", 0);
            randomNumberTextView.setText("Random Number: " + randomNumber);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the current activity and go back to the main activity
                finish();
            }
        });
    }
}
