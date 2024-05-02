package com.example.basiccalls;

import static com.example.basiccalls.api.ApiClientFactory.GetPostApi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.basiccalls.api.SlimCallback;
import com.example.basiccalls.model.Post;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView apiText1 = findViewById(R.id.test);

//        Retrofit retroft = new Retrofit.Builder()
//                .baseUrl("https://jsonplaceholder.typicode.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

  //      PostApi apiClient = retroft.create(PostApi.class);

//        GetPostApi().getFirstPost().enqueue(new Callback<Post>(){
//
//            @Override
//            public void onResponse(Call<Post> call, Response<Post> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Post post = response.body();
//                    apiText1.setText(post.getBody());
//                } else {
//                    // Log an error message if the response is not successful
//                    Log.e("API_CALL", "Failed to get post. Code: " + response.code());
//                }

//
//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//
//            }
//        });
        GetPostApi().getFirstPost().enqueue(new SlimCallback<Post>(response->{
            String result = response.getId() + "\n Title: " + response.getTitle() + "\nBody: " + response.getBody();
            apiText1.setText(result);

        }, "CustomTagForFirstApi"));

    }

}
//
//interface PostApi{
//    @GET("posts/1")
//    Call<Post> getFirstPost();
//}
//class Post{
//    private int userId;
//    private int id;
//    private String title;
//    private String body;
//
//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//}
