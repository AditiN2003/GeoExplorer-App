package test.connect.myapplication;

import static test.connect.myapplication.api.ApiClientFactory.GetPhotoApi;
import static test.connect.myapplication.api.ApiClientFactory.GetPostApi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import test.connect.myapplication.api.SlimCallback;
import test.connect.myapplication.model.Photo;
import test.connect.myapplication.model.Post;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView apiText1 = findViewById(R.id.activity_main_testView1);
        apiText1.setMovementMethod(new ScrollingMovementMethod());

        Button photoButton = findViewById(R.id.activity_main_button_for_photo);
        EditText photoNumInput = findViewById(R.id.activity_main_photoNum_input);

        //Gets first photo from remote website and prints the values on display

//        GetPhotoApi().getFirstPhoto().enqueue(new SlimCallback<Photo>(responsePhoto -> {
//            apiText1.setText(responsePhoto.printable());
//        }));





//        GetPhotoApi().getAllPhoto().enqueue(new SlimCallback<List<Photo>>(photos ->{
//
//          apiText1.setText("");
//            for(int i = 0;i < photos.size(); i++){
//                apiText1.append(photos.get(i).printable());
//            }
//
//        },"multiplePhotosApi"));


        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPhotoApi().getPhotoByNum(photoNumInput.getText().toString()).enqueue(new SlimCallback<Photo>(photo ->{
                    apiText1.setText(photo.printable());
                }));
            }
        });

    }
}


