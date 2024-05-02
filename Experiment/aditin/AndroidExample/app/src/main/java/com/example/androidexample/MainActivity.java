package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

    }

    public void Add(View v){
        EditText input1 = (EditText)findViewById(R.id.editTextNumber);
        EditText input2 = (EditText)findViewById(R.id.editTextNumber2);
        EditText input3 = (EditText)findViewById(R.id.editTextNumber3);

        int num1 = Integer.parseInt(input1.getText().toString());
        int num2 = Integer.parseInt(input2.getText().toString());
        int num3 = num1+num2;

        input3.setText("Total Value " + num3);

    }

    public void Subtract(View v){
        EditText input1 = (EditText)findViewById(R.id.editTextNumber);
        EditText input2 = (EditText)findViewById(R.id.editTextNumber2);
        EditText input3 = (EditText)findViewById(R.id.editTextNumber3);

        int num1 = Integer.parseInt(input1.getText().toString());
        int num2 = Integer.parseInt(input2.getText().toString());
        int num3 = num1-num2;

        input3.setText("Subtract Value " + num3);

    }

    public void Multiply(View v){
        EditText input1 = (EditText)findViewById(R.id.editTextNumber);
        EditText input2 = (EditText)findViewById(R.id.editTextNumber2);
        EditText input3 = (EditText)findViewById(R.id.editTextNumber3);

        int num1 = Integer.parseInt(input1.getText().toString());
        int num2 = Integer.parseInt(input2.getText().toString());
        int num3 = num1*num2;

        input3.setText("Multiply Value " + num3);

    }

    public void Divide(View v){
        EditText input1 = (EditText)findViewById(R.id.editTextNumber);
        EditText input2 = (EditText)findViewById(R.id.editTextNumber2);
        EditText input3 = (EditText)findViewById(R.id.editTextNumber3);

        int num1 = Integer.parseInt(input1.getText().toString());
        int num2 = Integer.parseInt(input2.getText().toString());
        int num3 = num1/num2;

        input3.setText("Divide Value " + num3);

    }


}