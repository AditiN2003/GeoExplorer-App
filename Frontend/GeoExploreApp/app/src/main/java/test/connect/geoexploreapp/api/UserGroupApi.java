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
import test.connect.geoexploreapp.model.UserGroup;

public interface UserGroupApi {

    @POST("user/groups")
    Call<UserGroup> addGroup(@Body UserGroup group);

    @POST("user/groups/create")
    Call<UserGroup> createGroup(@Body String name);

    @GET("user/groups/{id}")
    Call<UserGroup> getGroupById(@Path("id") Long id);

    @PUT("user/groups/{id}")
    Call<UserGroup> updateGroupById(@Path("id") Long id, @Body UserGroup group);

    @DELETE("user/groups/{id}")
    Call<Void> deleteGroupById(@Path("id") Long id);

    @GET("user/groups")
    Call<List<UserGroup>> getAllGroups();

    @POST("user/groups/{group_id}/members")
    Call<UserGroup> addMemberToGroupById(@Path("group_id") Long group_id, @Body Long user_id);

    @DELETE("user/usergroups/{group_id}/{user_id}/")
    Call<ResponseBody> deleteUserFromGroup(@Path("group_id") Long group_id, @Path("user_id") Long user_id);

    @GET("user/usergroups/{group_id}/memberlist")
    Call<List<String>> listGroupMembersById(@Path("group_id") Long group_id);

    @PUT("user/userGroups/{group_id}/{observation_id}")
    Call<String> addObservationToGroupFilter(@Path("group_id") Long group_id, @Path("observation_id") Long observation_id);
    @GET("user/usergroup/{group_id}/num")
    Call<Integer> getMemberCount(@Path("group_id") Long group_id);

}