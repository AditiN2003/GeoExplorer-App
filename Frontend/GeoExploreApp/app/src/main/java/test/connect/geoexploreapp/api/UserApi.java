package test.connect.geoexploreapp.api;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.User;

public interface UserApi {

    @POST("user/create")
    Call<User> UserCreate(@Body User newUser);

    @GET("user/{id}")
    Call<User> getUser(@Path("id") Long id);

    @PUT("user/{id}/update")
    Call<User> updateUser(@Path("id") Long id, @Body User updated);
    @DELETE("user/{id}/delete")
    Call<String> deleteUser(@Path("id") Long Id);

    @DELETE("user/delete/all")
    Call<String> deleteAll();
    @GET("userinfo")
    Call<List<User>> getAllUsers();
}
