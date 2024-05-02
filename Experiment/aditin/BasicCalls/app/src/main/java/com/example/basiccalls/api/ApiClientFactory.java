package com.example.basiccalls.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientFactory {

    static Retrofit apiClientSeed=null;

    static Retrofit GetApiClientSeed() {

        if(apiClientSeed==null) {
            apiClientSeed = new Retrofit.Builder()
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return apiClientSeed;
    }

    public static PostApi GetPostApi(){
        return GetApiClientSeed().create(PostApi.class);
    }


}
