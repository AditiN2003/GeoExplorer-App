package test.connect.geoexploreapp.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.Comment;

public interface CommentApi {

    //create
    @POST("comment/store/{postType}")
    Call<Comment> commentStore(@Body Comment newComment, @Path("postType") String postType);

    @GET("comment/{id}")
    Call<Comment> getComment(@Path("id") Long id);

    @PUT("comment/{id}/update")
    Call<Comment> updateComment(@Path("id") Long id, @Body Comment updatedComment);

    @DELETE("comment/{id}/delete")
    Call<ResponseBody> deleteComment(@Path("id") Long id);

    @GET("comment/list")
    Call<List<Comment>> getAllComments();

    @GET("observation/comments/{postId}")
    Call<List<Comment>> getCommentsForObs(@Path("postId") Long postId);

    @GET("event/comments/{postId}")
    Call<List<Comment>> getCommentsForEvents(@Path("postId") Long postId);

    @GET("user/comments/{user_table_Id}")
    Call<List<Comment>> getCommentsForUser(@Path("user_table_Id") Long user_table_Id);

    @GET("report/comments/{postId}")
    Call<List<Comment>> getCommentsForReports(@Path("postId") Long postId);

}

