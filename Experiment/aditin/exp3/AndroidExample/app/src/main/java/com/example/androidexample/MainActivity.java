package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  //  private TextView messageText;   // define message textview variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        Button randomButton = findViewById(R.id.randomButton);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int randomNumber = new Random().nextInt(100);

                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("randomNumber", randomNumber);
                startActivity(intent);
            }
        });
    }
}