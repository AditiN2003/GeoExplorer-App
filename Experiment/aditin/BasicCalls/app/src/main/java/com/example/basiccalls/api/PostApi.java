package com.example.basiccalls.api;

import com.example.basiccalls.model.Post;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PostApi{
        @GET("posts/1")
        Call<Post> getFirstPost();

}
