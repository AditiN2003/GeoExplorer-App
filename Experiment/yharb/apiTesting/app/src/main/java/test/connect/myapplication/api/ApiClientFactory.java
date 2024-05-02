package test.connect.myapplication.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import test.connect.myapplication.api.PostApi;

public class ApiClientFactory {

        static Retrofit apiCLientSeed;

        static Retrofit GetApiClientSeed() {

            if(apiCLientSeed == null) {
                apiCLientSeed = new Retrofit.Builder()
                        .baseUrl("https://jsonplaceholder.typicode.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return apiCLientSeed;
        }

        public static PostApi GetPostApi(){
            return GetApiClientSeed().create(PostApi.class);
        }

        public static PhotoApi GetPhotoApi(){
        return GetApiClientSeed().create(PhotoApi.class);
    }
}
