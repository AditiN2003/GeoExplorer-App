package test.connect.geoexploreapp.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientFactory {
    static Retrofit apiClientSeed = null;
    static Retrofit GetApiClientSeed(){
        if(apiClientSeed == null){

            apiClientSeed = new Retrofit.Builder()
                    .baseUrl("http://coms-309-005.class.las.iastate.edu:8080/") // Server url here with / at the end http://coms-309-005.class.las.iastate.edu:8080
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return apiClientSeed;
    }

    public static ReportMarkerApi getReportMarkerApi(){ return GetApiClientSeed().create(ReportMarkerApi.class);}
    public static EventMarkerApi getEventMarkerApi(){ return GetApiClientSeed().create(EventMarkerApi.class);}
    public static ObservationApi GetObservationApi() { return GetApiClientSeed().create(ObservationApi.class);}
    public static CommentApi GetCommentApi() {
        return GetApiClientSeed().create(CommentApi.class);
    }
    public static AlertMarkerApi getAlertMarkerApi(){ return GetApiClientSeed().create(AlertMarkerApi.class);}
    public static MarkerTagApi getMarkerTagApi(){ return GetApiClientSeed().create(MarkerTagApi.class);}
    public static UserApi GetUserApi(){return GetApiClientSeed().create(UserApi.class);}
    public static UserGroupApi GetUserGroupApi(){return GetApiClientSeed().create(UserGroupApi.class);}

    public static ReportedUserApi GetReportedUserApi(){
        return GetApiClientSeed().create(ReportedUserApi.class);
    }
    public static ImageApi GetImageApi(){return GetApiClientSeed().create(ImageApi.class);}




}
