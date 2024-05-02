package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class secondPage extends AppCompatActivity {

    private TextView professorText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_page);

        professorText = findViewById(R.id.second_page_prof_text);
        backButton = findViewById(R.id.second_page_back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(secondPage.this,MainActivity.class);
                startActivity(intent);
            }
        });


        professorText.setText("Professor: Abraham Aldaco\n"+
                                "\nMWF 2:15-3:05");


    }
}