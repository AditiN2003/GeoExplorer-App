package test.connect.geoexploreapp.api;

import org.slf4j.Marker;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.MarkerTag;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.ReportMarker;
import test.connect.geoexploreapp.model.User;

public interface MarkerTagApi {

    // Gets created with a markerTag object
    @POST("marker_tags")
    Call<MarkerTag> addMarkerTag(@Body MarkerTag newMarkerTag);

    //Gest created with a string
    @POST("marker_tags/create")
    Call<MarkerTag> createTagWithName(@Body String tagName);

    @POST("/marker_tags/create")
    Call<MarkerTag> createTag(@Body MarkerTag tag);

    @PUT("marker_tags/{id}")
    Call<MarkerTag> updateMarkerTag(@Path("id") Long id, @Body MarkerTag updated);

    @DELETE("marker_tags/{id}")
    Call<Void> deleteMarkerTagById(@Path("id") Long Id);

    @GET("marker_tags")
    Call<List<MarkerTag>> getAllMarkerTags();

    @GET("marker_tags/{id}")
    Call<MarkerTag> getMarkerTagById(@Path("id") Long id);

    @GET("marker_tags/{id}/reports")
    Call<List<ReportMarker>> getReportsForTag(@Path("id") Long tagId);

    @GET("marker_tags/{id}/observations")
    Call<List<Observation>> getObservationsForTag(@Path("id") Long tagId);

    @GET("marker_tags/{id}/events")
    Call<List<EventMarker>> getEventsForTag(@Path("id") Long tagId);

    @GET("marker_tags/{id}/alerts")
    Call<List<AlertMarker>> getAlertsForTag(@Path("id") Long tagId);

    @GET("marker_tags/search/{name}")
    Call<MarkerTag> searchTagByName(@Path("name") String tagName);

}
