package test.connect.geoexploreapp.api;


import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.distanceLocation;
import test.connect.geoexploreapp.model.LocationProximity;
import test.connect.geoexploreapp.model.LocationRange;
import test.connect.geoexploreapp.model.Range;

public interface AlertMarkerApi {

    // c[R]udl - Get an alert from the database by its id
    @GET("geomap/alerts/{id}")
    Call<AlertMarker> getAlertById(@Path("id") Long id);

    // crud[L] - Get a list of all the alerts in the database
    @GET("geomap/alerts")
    Call<List<AlertMarker>> getAllAlertMarker();

    // cru[D]l - Delete an alert in the database by it's id
    @DELETE("geomap/alerts/{id}")
    Call<Void> deleteAlertById(@Path("id") Long id);

    @GET("geomap/alerts/within/poly")
    Call<Set<AlertMarker>> getAlertsWithinPoly(@Body String wkt_bounds_geom);

    @POST("geomap/alerts/within/proximity")
    Call<List<AlertMarker>> getAlertsWithinProximitySorted(@Body LocationProximity range);

    @POST("geomap/alerts/within/rect")
    Call<Set<AlertMarker>> getAlertsWithinRect(@Body Range range);

    @POST("geomap/alerts/within/rect/sorted")
    Call<List<AlertMarker>> getProxSortedAlertsWithinRect(@Body LocationRange range);
    @POST("geomap/alerts/{id}/distance")
    Call<Double> getDistanceToAlertById(@Path("id") Long id, @Body distanceLocation src);
}
