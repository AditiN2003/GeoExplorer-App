package test.connect.geoexploreapp.api;



import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.distanceLocation;
import test.connect.geoexploreapp.model.LocationProximity;
import test.connect.geoexploreapp.model.LocationRange;
import test.connect.geoexploreapp.model.Range;

public interface EventMarkerApi {

    //[C]rudl - Add a new event to the database
    @POST("geomap/events")
    Call<EventMarker> addEvent(@Body EventMarker reportMarker);

    @POST("geomap/events/{id}/tags")
    Call<Void> addExistingTagToEvent(@Path("id") Long reportId, @Body Long tagId);

    // c[R]udl - Get an event from the database by its id
    @GET("geomap/events/{id}")
    Call<EventMarker> getEventById(@Path("id") Long id);

    // cr[U]dl - Update an event already in the database by it's id
    @PUT("geomap/events/{id}")
    Call<EventMarker> updateEventById(@Path("id") Long id, @Body EventMarker eventMarker);

    // cru[D]l - Delete an event in the database by it's id
    @DELETE("geomap/events/{id}")
    Call<Void> deleteEventById(@Path("id") Long id);

    // crud[L] - Get a list of all the events in the database
    @GET("geomap/events")
    Call<List<EventMarker>> GetAllEventMarker();


    @POST("geomap/events/within/proximity")
    Call<List<EventMarker>> getEventsWithinProximitySorted(@Body LocationProximity range);

    @POST("geomap/events/within/rect")
    Call<Set<EventMarker>> getEventsWithinRect(@Body Range range);

    @POST("geomap/events/within/rect/sorted")
    Call<List<EventMarker>> getProxSortedEventsWithinRect(@Body LocationRange range);

    @POST("geomap/events/{id}/distance")
    Call<Double> getDistanceToEventById(@Path("id") Long id, @Body distanceLocation src);
}
