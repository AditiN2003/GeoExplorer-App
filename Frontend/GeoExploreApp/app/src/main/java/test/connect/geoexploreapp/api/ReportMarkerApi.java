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
import test.connect.geoexploreapp.model.Range;
import test.connect.geoexploreapp.model.ReportMarker;

public interface ReportMarkerApi {

    //[C]rudl - Add a new report to the database
    @POST("geomap/reports")
    Call<ReportMarker> addReport(@Body ReportMarker reportMarker);

    @POST("geomap/reports/{id}/tags")
    Call<Void> addExistingTagToReport(@Path("id") Long reportId, @Body Long tagId);

    // c[R]udl - Get an report from the database by its id
    @GET("geomap/reports/{id}")
    Call<ReportMarker> getReportById(@Path("id") Long id);

    // cr[U]dl - Update an report already in the database by it's id
    @PUT("geomap/reports/{id}")
    Call<ReportMarker> updateReportById(@Path("id") Long id, @Body ReportMarker reportMarker);

    // cru[D]l - Delete an report in the database by it's id
    @DELETE("geomap/reports/{id}")
    Call<Void> deleteReportById(@Path("id") Long id);

    // crud[L] - Get a list of all the reports in the database
    @GET("geomap/reports")
    Call<List<ReportMarker>> GetAllReportMarker();

    @POST("geomap/reports/within/proximity")
    Call<List<ReportMarker>> getReportsWithinProximitySorted(@Body LocationProximity range);

    @POST("geomap/reports/within/rect")
    Call<Set<ReportMarker>> getReportsWithinRect(@Body Range range);

    @POST("geomap/reports/within/rect/sorted")
    Call<List<ReportMarker>> getProxSortedReportsWithinRect(@Body LocationRange range);

    @POST("geomap/reports/{id}/distance")
    Call<Double> getDistanceToReportById(@Path("id") Long id, @Body distanceLocation src);
}
