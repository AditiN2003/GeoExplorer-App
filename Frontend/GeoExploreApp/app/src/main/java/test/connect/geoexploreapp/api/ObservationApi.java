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
import test.connect.geoexploreapp.model.distanceLocation;
import test.connect.geoexploreapp.model.LocationProximity;
import test.connect.geoexploreapp.model.LocationRange;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.Range;

public interface ObservationApi {

    @POST("geomap/observations")
    Call<Observation> saveObs(@Body Observation observation);

    @POST("geomap/observations/{id}/tags")
    Call<Void> addExistingTagToObservation(@Path("id") Long reportId, @Body Long tagId);

    @GET("geomap/observations/{id}")
    Call<Observation> getObs(@Path("id") Long id);

    @PUT("geomap/observations/{id}")
    Call<Observation> updateObs(@Path("id") Long id, @Body Observation observation);

    @DELETE("geomap/observations/{id}")
    Call<Observation> deleteObs(@Path("id") Long id);

    @GET("geomap/observations")
    Call<List<Observation>> getAllObs();

    @POST("geomap/observations/within/proximity")
    Call<List<Observation>> getObservationsWithinProximitySorted(@Body LocationProximity range);

    @POST("geomap/observations/within/rect")
    Call<Set<Observation>> getObservationsWithinRect(@Body Range range);

    @POST("geomap/observations/within/rect/sorted")
    Call<List<Observation>> getProxSortedObservationsWithinRect(@Body LocationRange range);

    @POST("geomap/observations/{id}/distance")
    Call<Double> getDistanceToObservationById(@Path("id") Long id, @Body distanceLocation src);



}