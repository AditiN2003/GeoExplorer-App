package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast; // for pop up messages

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner sp; // for drop down
    boolean invalid = false;
    String Colector="";
    TextView txtalertName;
    EditText UserName,UserPassword,UserContact,UserComment;
    Button SubmitSave;
    RadioButton Malebtn,Femalbtn;
    CheckBox html,css,java1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp=findViewById(R.id.SpCountry);
        UserName=findViewById(R.id.userName);
        UserPassword=findViewById(R.id.userPassword);
        UserContact=findViewById(R.id.userContact);
        UserComment=findViewById(R.id.usercomment);
        txtalertName=findViewById(R.id.userAlert);
        Malebtn =findViewById(R.id.Male);
        Femalbtn=findViewById(R.id.Female);
        html=findViewById(R.id.HTML);
        css=findViewById(R.id.CSS);
        java1=findViewById(R.id.jav);
        SubmitSave=findViewById(R.id.btnSubmit);
        SubmitSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = UserName.getText().toString();
                String Passcode=UserPassword.getText().toString();
                String contact=UserContact.getText().toString();
                String comment=UserComment.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(MainActivity.this,"Pleas fill the password field",Toast.LENGTH_SHORT).show();
                }
                else if (name.equals("Aditi") ||name.equals("Nachnani")){
                    invalid=true;
                    txtalertName.setText("Name Already exist");
                }

                else if(Passcode.isEmpty()){
                    Toast.makeText(MainActivity.this,"Pleas fill the password field",Toast.LENGTH_SHORT).show();
                }


                else if (contact.isEmpty()){
                    Toast.makeText(MainActivity.this,"Pleas fill the Contact field",Toast.LENGTH_SHORT).show();
                }

                else if (comment.isEmpty()){
                    Toast.makeText(MainActivity.this,"Pleas fill the Comment field",Toast.LENGTH_SHORT).show();
                }



                else{

                    Colector+=name+"\n";
                    Colector+=Passcode+"\n";
                    Colector+=contact+"\n";
                    Colector+=comment+"\n";
                    if (html.isChecked()){
                        Colector+="HTML"+"\n";
                        if (css.isChecked()){
                            Colector+="CSS"+"\n";
                        }
                        if (java1.isChecked()){
                            Colector+="Java"+"\n";
                        }
                    }
                    Toast.makeText(MainActivity.this,"User Info \n:"+Colector,Toast.LENGTH_SHORT).show();
                }

            }
        });

        List<String> categoryCountry=new ArrayList<>();
        categoryCountry.add("Select Country");
        categoryCountry.add("United States");
        categoryCountry.add("Canada");
        categoryCountry.add("UAE");
        categoryCountry.add("India");
        categoryCountry.add("Germany");
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,categoryCountry);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(arrayAdapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if(!parent.getItemAtPosition(position).equals("Select Country")){
                    //Do Nothing
                    String item=parent.getItemAtPosition(position).toString();
                    Colector+=item+"\n";
                    Toast.makeText(MainActivity.this, "Selected Country: "+item, Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}