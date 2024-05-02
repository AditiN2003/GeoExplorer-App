package test.connect.geoexploreapp.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.Comment;
import test.connect.geoexploreapp.model.ReportedUser;

public interface ReportedUserApi {
    //c of crudl
    //@Operation(summary = "Add a new report of a user to the database")
    @POST("user/report")
    Call<ReportedUser> ReportUser(@Body ReportedUser newUser);

    //@Operation(summary = "Gets a reported user based on userId")
    @GET("user/report/{id}")
    Call<ReportedUser> getReported(@Path("id") Long id);

    // @Operation(summary = "Gets a reported user based on ReportedUserId")
    @GET("user/report/{id}/mod")
    Call<ReportedUser> getReportedById(@Path("id") Long id);

    //@Operation(summary = "Updates a report on a user using the id of the report")
    @PUT("user/report/update")
    Call<ReportedUser> updateReportedUser(@Body ReportedUser updated);

    //@Operation(summary = "deletes report but not user uses id of report not of user, basically if a user is innocent")
    @DELETE("user/report/deletereport/{id}")
    Call<ResponseBody> deleteUserReport(@Path("id") Long id);

    //@Operation(summary = "deletes report and user uses id of report not of user, basically if a user is guilty or has been reported enough times")
    @DELETE("user/report/delete/{id}")
    Call<ResponseBody> deleteUser(@Path("id") Long id);

    // @Operation(summary = "Lists all userReports")
    @GET("user/report/list")
    Call<List<ReportedUser>> ListOfReports();
}
