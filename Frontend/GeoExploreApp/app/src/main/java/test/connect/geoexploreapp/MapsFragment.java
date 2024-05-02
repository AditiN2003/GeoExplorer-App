package test.connect.geoexploreapp;

import static org.json.JSONObject.NULL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.AlertMarkerApi;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.EventMarkerApi;
import test.connect.geoexploreapp.api.ImageApi;
import test.connect.geoexploreapp.api.MarkerTagApi;
import test.connect.geoexploreapp.api.ObservationApi;
import test.connect.geoexploreapp.api.ReportMarkerApi;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.Image;
import test.connect.geoexploreapp.model.LocationProximity;
import test.connect.geoexploreapp.model.MarkerTag;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.Range;
import test.connect.geoexploreapp.model.ReportMarker;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.model.distanceLocation;
import test.connect.geoexploreapp.websocket.AlertWebSocketManager;
import test.connect.geoexploreapp.websocket.LocationWebSocketManager;
import test.connect.geoexploreapp.websocket.WebSocketListener;
import android.Manifest;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private boolean isUpdateReportMode = false;
    private boolean isUpdateEventMode = false;
    private boolean isUpdateObservationMode = false;
    private boolean isCreateEmergencyNotification = false;
    private boolean isUserSet = false;
    private int reportIdStatus = 0; // For promptForReportID method. 1 to Read, 2 to Delete, 3 to Update
    private int eventIdStatus = 0; // For promptForEventID method. 1 to Read, 2 to Delete, 3 to Update
    private int observationIdStatus = 0; // For promptForReportID method. 1 to Read, 2 to Delete, 3 to Update
    private double usersCurrentLatitude;
    private double usersCurrentLongitude;
    private TextView reportUpdateTextView, statusMessage;
    private TextView eventUpdateTextView;
    private TextView observationUpdateTextView;
    private User loggedInUser;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private WebSocketListener alertWebSocketListener;
    private WebSocketListener locationWebSocketListener;
    private Map<Integer, Marker> userMarkersMap = new HashMap<>();
    private ActivityResultLauncher<String> mFilePickerLauncher;

    private Uri selectedUri;
    private static User user;
    private Button uploadImageObservation;
    private static Bundle args;


    public MapsFragment() {

    }
    public static MapsFragment newInstance(User user ) {
        MapsFragment fragment = new MapsFragment();
        args = new Bundle();
        args.putSerializable("UserObject", user);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedUri = uri;
                uploadImageObservation.setText("Image selected: "+ uri.getLastPathSegment());
                Log.d("File URI", "Selected File URI: " + uri.toString());

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        alertWebSocketListener = new WebSocketListener() {
            @Override
            public void onWebSocketOpen(ServerHandshake handshakedata) {
                Log.d("AlertWebSocket", "Connected");
            }

            @Override
            public void onWebSocketMessage(String message) {
                if (message.contains("io_latitude")) {
                    AlertMarker alertMarker = new Gson().fromJson(message, AlertMarker.class);
                    showEmergencyNotification(alertMarker.getTitle(), alertMarker.getDescription(),
                            alertMarker.getIo_latitude(), alertMarker.getIo_longitude());
                }
            }

            @Override
            public void onWebSocketClose(int code, String reason, boolean remote) {
                Log.d("AlertWebSocket", "Closed: " + reason);
            }

            @Override
            public void onWebSocketError(Exception ex) {
                Log.e("AlertWebSocket", "Error", ex);
            }
        };

        locationWebSocketListener = new WebSocketListener() {
            @Override
            public void onWebSocketOpen(ServerHandshake handshakedata) {
                Log.d("LocationWebSocket", "Connected");
            }

            @Override
            public void onWebSocketMessage(String message) {
                if (message.contains("latitude") && message.contains("emergency")) {
                    try {
                        JsonObject locationJson = JsonParser.parseString(message).getAsJsonObject();
                        int user_id = locationJson.get("user_id").getAsInt();
                        double latitude = locationJson.get("latitude").getAsDouble();
                        double longitude = locationJson.get("longitude").getAsDouble();

                        displayUserOnMap(user_id,latitude,longitude);

                        Log.d("LocationWebSocket", "Latitude: " + latitude + ", Longitude: " + longitude);
                    } catch (JsonSyntaxException e) {
                        Log.e("LocationWebSocket", "Error parsing location data", e);
                    }
                }
            }

            @Override
            public void onWebSocketClose(int code, String reason, boolean remote) {
                Log.d("LocationWebSocket", "Closed: " + reason);
            }

            @Override
            public void onWebSocketError(Exception ex) {
                Log.e("LocationWebSocket", "Error", ex);
            }
        };

        AlertWebSocketManager.getInstance().setWebSocketListener(alertWebSocketListener);
        LocationWebSocketManager.getInstance().setWebSocketListener(locationWebSocketListener);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());




        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)  // 10 seconds
                .setMinUpdateIntervalMillis(150000)
                .build();


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // websocket send here
                        JsonObject locationData = new JsonObject();
                        locationData.addProperty("latitude", latitude);
                        locationData.addProperty("longitude", longitude);

                        Gson gson = new Gson();
                        String jsonMessage = gson.toJson(locationData);

                        LocationWebSocketManager.getInstance().sendMessage(jsonMessage);
                        usersCurrentLatitude = latitude;
                        usersCurrentLongitude = longitude;

                        Log.d("MapsFragment", "Latitude: " + latitude + ", Longitude: " + longitude);
                    }
                }
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (getArguments() != null) {

            user = (User) getArguments().getSerializable("UserObject");
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getCreateEmergencyNotification().observe(getViewLifecycleOwner(), isCreateEmergency -> {
            isCreateEmergencyNotification = isCreateEmergency;
        });

        viewModel.getLoggedInUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            this.loggedInUser = loggedInUser;
            if (loggedInUser != null && !isUserSet){

                isUserSet = true;
            }else {
                Log.e("WebSocket", "Logged in user is null. Cannot establish WebSocket connection.");
            }
        });

        reportUpdateTextView = view.findViewById(R.id.activity_maps_report_update_text_view);
        eventUpdateTextView = view.findViewById(R.id.activity_maps_event_update_text_view);
        observationUpdateTextView = view.findViewById(R.id.activity_maps_observation_update_text_view);
        statusMessage = view.findViewById(R.id.statusMessage);

        FloatingActionButton markerOpFab = view.findViewById(R.id.MarkerOperationsFab);
        FloatingActionButton proximityMarkerFab = view.findViewById(R.id.MarkerProximityFab);
        markerOpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        proximityMarkerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximityMarkerPopup();
            }
        });



        startLocationUpdates();

        return view;
    }


    private void startLocationUpdates() {
        // check permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // start sending location if permission granted
            try {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } catch (SecurityException e) {
                Log.e("MapsFragment", "issue with permissions", e);
            }
        } else {
            // request perms if not granted
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);  // stop the updates
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();  // resume the locatyion updates
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();  // stop the location updates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();  // stops location when user exits
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_json));
            if (!success) {
                Log.e("MapsFragment", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsFragment", "Can't find style. Error: ", e);
        }


        LatLng ames = new LatLng(42.026224,-93.646256);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ames,14));

//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                fetchMarkersWithinScreenBounds(true,true,true,true);
//            }
//        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(isUpdateReportMode){
                    isUpdateReportMode = false;
                    reportIdStatus = 3;
                    promptForReportId(latLng);
                    reportUpdateTextView.setVisibility(View.GONE);
                }else if(isUpdateEventMode){
                    isUpdateEventMode = false;
                    eventIdStatus = 3;
                    promptForEventId(latLng);
                    eventUpdateTextView.setVisibility(View.GONE);
                }else if(isUpdateObservationMode){
                    isUpdateObservationMode = false;
                    observationIdStatus = 3;
                    promptForObservationId(latLng);
                    observationUpdateTextView.setVisibility(View.GONE);
                }else if(isCreateEmergencyNotification){
                    Log.d("MapsActivity", "Entering isCreateEmergencyNotification mode");
                    isCreateEmergencyNotification = false;
                    SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                    viewModel.setLocation(latLng.latitude, latLng.longitude);
                    viewModel.setCreateEmergencyNotification(false);
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showCreatePrompt(latLng);
            }
        });



    }


    private void fetchMarkersWithinScreenBounds(boolean showAlerts, boolean showEvents, boolean showObservations, boolean showReports){
        AlertMarkerApi alertApi = ApiClientFactory.getAlertMarkerApi();
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();
        mMap.clear();


        LatLngBounds currentScreenBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Log.d("4/29", "Im inside screen bouynds" + currentScreenBounds);

        Range range = new Range();
        range.setMin_latitude(currentScreenBounds.southwest.latitude);
        range.setMin_longitude(currentScreenBounds.southwest.longitude);
        range.setMax_latitude(currentScreenBounds.northeast.latitude);
        range.setMax_longitude(currentScreenBounds.northeast.longitude);

        if(showAlerts){
            alertApi.getAlertsWithinRect(range).enqueue(new Callback<Set<AlertMarker>>() {
                @Override
                public void onResponse(Call<Set<AlertMarker>> call, Response<Set<AlertMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Set<AlertMarker> alertSet = response.body();
                        Log.d("4/29", "Alerts has a body response" + alertSet);
                        List<AlertMarker> alertList = new ArrayList<>(alertSet);
                        displayAlertsOnMap(alertList);
                    }else{
                        Toast.makeText(getContext(), "Failed to fetch alerts", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Set<AlertMarker>> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to fetch alerts", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(showEvents){
            eventMarkerApi.getEventsWithinRect(range).enqueue(new Callback<Set<EventMarker>>() {
                @Override
                public void onResponse(Call<Set<EventMarker>> call, Response<Set<EventMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Set<EventMarker> eventSet = response.body();
                        List<EventMarker> eventList = new ArrayList<>(eventSet);
                        displayEventsOnMap(eventList);
                    }else{
                        Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Set<EventMarker>> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                }
            });
        }


        if(showObservations){
            observationApi.getObservationsWithinRect(range).enqueue(new Callback<Set<Observation>>() {
                @Override
                public void onResponse(Call<Set<Observation>> call, Response<Set<Observation>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Set<Observation> observationSet = response.body();
                        List<Observation> observationList = new ArrayList<>(observationSet);
                        displayObservationsOnMap(observationList);
                    }else{
                        Toast.makeText(getContext(), "Failed to fetch observations", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Set<Observation>> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to fetch observations", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(showReports){
            reportMarkerApi.getReportsWithinRect(range).enqueue(new Callback<Set<ReportMarker>>() {
                @Override
                public void onResponse(Call<Set<ReportMarker>> call, Response<Set<ReportMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Set<ReportMarker> reportSet = response.body();
                        List<ReportMarker> reportList = new ArrayList<>(reportSet);
                        displayReportsOnMap(reportList);
                    }else{
                        Toast.makeText(getContext(), "Failed to fetch reports", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Set<ReportMarker>> call, Throwable t) {
                    Toast.makeText(getContext(), "Failed to fetch reports", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchMarkersWithinProximity(double latitude, double longitude, double range, boolean showAlerts, boolean showEvents, boolean showObservations, boolean showReports) {
        AlertMarkerApi alertApi = ApiClientFactory.getAlertMarkerApi();
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();

        mMap.clear();

        LocationProximity locationProximity = new LocationProximity(latitude, longitude, range);

        if(showAlerts) {
            alertApi.getAlertsWithinProximitySorted(locationProximity).enqueue(new Callback<List<AlertMarker>>() {
                @Override
                public void onResponse(Call<List<AlertMarker>> call, Response<List<AlertMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<AlertMarker> alerts = response.body();
                        displayAlertsOnMap(alerts);
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch alerts", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<AlertMarker>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error fetching alerts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(showEvents) {
            eventMarkerApi.getEventsWithinProximitySorted(locationProximity).enqueue(new Callback<List<EventMarker>>() {
                @Override
                public void onResponse(Call<List<EventMarker>> call, Response<List<EventMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<EventMarker> events = response.body();
                        displayEventsOnMap(events);
                    } else {
                        Log.d("FetchEventsProximity", "Error fetching events");
                        Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<EventMarker>> call, Throwable t) {
                    Log.d("FetchEventsProximity", "Error fetching events");
                    Toast.makeText(getContext(), "Error fetching Events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(showObservations) {
            observationApi.getObservationsWithinProximitySorted(locationProximity).enqueue(new Callback<List<Observation>>() {
                @Override
                public void onResponse(Call<List<Observation>> call, Response<List<Observation>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Observation> observations = response.body();
                        displayObservationsOnMap(observations);
                    } else {
                        Log.d("FetchEventsProximity", "Error fetching events");
                        Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Observation>> call, Throwable t) {
                    Log.d("FetchObservationsProximity", "Error fetching observation");
                    Toast.makeText(getContext(), "Error fetching Observation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(showReports) {
            reportMarkerApi.getReportsWithinProximitySorted(locationProximity).enqueue(new Callback<List<ReportMarker>>() {
                @Override
                public void onResponse(Call<List<ReportMarker>> call, Response<List<ReportMarker>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ReportMarker> reports = response.body();
                        displayReportsOnMap(reports);
                    } else {
                        Log.d("FetchEventsProximity", "Error fetching events");
                        Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ReportMarker>> call, Throwable t) {
                    Log.d("FetchReportsProximity", "Error fetching report");
                    Toast.makeText(getContext(), "Error fetching Report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void displayAlertsOnMap(List<AlertMarker> alerts) {
        AlertMarkerApi alertApi = ApiClientFactory.getAlertMarkerApi();

        if (mMap != null) {
            for (AlertMarker alert : alerts) {
                LatLng alertLocation = new LatLng(alert.getIo_latitude(), alert.getIo_longitude());

                alertApi.getDistanceToAlertById(alert.getId(), new distanceLocation(usersCurrentLatitude, usersCurrentLongitude)).enqueue(new Callback<Double>() {
                    @Override
                    public void onResponse(Call<Double> call, Response<Double> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double distance = response.body();
                            String snippetWithDistance = alert.getDescription() + " | " + String.format("%.2f", distance) + " miles";
                            mMap.addMarker(new MarkerOptions()
                                    .position(alertLocation)
                                    .title(alert.getTitle())
                                    .snippet(snippetWithDistance)
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_crisis_alert_24)));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch distance for alert", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Double> call, Throwable t) {
                        Toast.makeText(getContext(), "Error fetching distance for alert: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void displayEventsOnMap(List<EventMarker> events) {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();
        if (mMap != null) {
            for (EventMarker event : events) {
                LatLng eventLocation = new LatLng(event.getIo_latitude(), event.getIo_longitude());

                eventMarkerApi.getDistanceToEventById(event.getId(), new distanceLocation(usersCurrentLatitude, usersCurrentLongitude)).enqueue(new Callback<Double>() {
                    @Override
                    public void onResponse(Call<Double> call, Response<Double> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double distance = response.body();
                            String snippetWithDistance =String.format("%.2f", distance) + " miles";
                            mMap.addMarker(new MarkerOptions()
                                    .position(eventLocation)
                                    .title(event.getTitle())
                                    .snippet(snippetWithDistance)
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_celebration_24)));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch distance for event", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Double> call, Throwable t) {
                        Toast.makeText(getContext(), "Error fetching distance for event: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void displayObservationsOnMap(List<Observation> observations) {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        if (mMap != null) {
            for (Observation observation : observations) {
                LatLng observationLocation = new LatLng(observation.getIo_latitude(), observation.getIo_longitude());

                observationApi.getDistanceToObservationById(observation.getId(), new distanceLocation(usersCurrentLatitude, usersCurrentLongitude)).enqueue(new Callback<Double>() {
                    @Override
                    public void onResponse(Call<Double> call, Response<Double> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double distance = response.body();
                            String snippetWithDistance = observation.getDescription() + " | " + String.format("%.2f", distance) + " miles";
                            mMap.addMarker(new MarkerOptions()
                                    .position(observationLocation)
                                    .title(observation.getTitle())
                                    .snippet(snippetWithDistance)
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_photo_camera_24)));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch distance for observation", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Double> call, Throwable t) {
                        Toast.makeText(getContext(), "Error fetching distance for observation: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void displayReportsOnMap(List<ReportMarker> reports) {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();
        if (mMap != null) {
            for (ReportMarker report : reports) {
                LatLng eventLocation = new LatLng(report.getIo_latitude(), report.getIo_longitude());

                reportMarkerApi.getDistanceToReportById(report.getId(), new distanceLocation(usersCurrentLatitude, usersCurrentLongitude)).enqueue(new Callback<Double>() {
                    @Override
                    public void onResponse(Call<Double> call, Response<Double> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            double distance = response.body();
                            String snippetWithDistance = String.format("%.2f", distance) + " miles";
                            mMap.addMarker(new MarkerOptions()
                                    .position(eventLocation)
                                    .title(report.getTitle())
                                    .snippet(snippetWithDistance)
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_report_24)));
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch distance for report", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Double> call, Throwable t) {
                        Toast.makeText(getContext(), "Error fetching distance for report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    private void proximityMarkerPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Show markers near me");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        CheckBox alertsCheckBox = new CheckBox(getContext());
        alertsCheckBox.setText("Alerts");
        alertsCheckBox.setChecked(true);
        layout.addView(alertsCheckBox);

        CheckBox eventsCheckBox = new CheckBox(getContext());
        eventsCheckBox.setText("Events");
        eventsCheckBox.setChecked(true);
        layout.addView(eventsCheckBox);

        CheckBox observationsCheckBox = new CheckBox(getContext());
        observationsCheckBox.setText("Observations");
        observationsCheckBox.setChecked(true);
        layout.addView(observationsCheckBox);

        CheckBox reportsCheckBox = new CheckBox(getContext());
        reportsCheckBox.setText("Reports");
        reportsCheckBox.setChecked(true);
        layout.addView(reportsCheckBox);

        EditText rangeInput = new EditText(getContext());
        rangeInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        rangeInput.setHint("Enter range (0.5 - 10 miles)");
        layout.addView(rangeInput);

        RadioGroup searchModeGroup = new RadioGroup(getContext());
        searchModeGroup.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton useRange = new RadioButton(getContext());
        useRange.setText("Use Range");
        useRange.setId(View.generateViewId());
        useRange.setChecked(true);

        RadioButton useScreenBounds = new RadioButton(getContext());
        useScreenBounds.setText("Use Screen Bounds");
        useScreenBounds.setId(View.generateViewId());

        searchModeGroup.addView(useRange);
        searchModeGroup.addView(useScreenBounds);
        layout.addView(searchModeGroup);

        builder.setView(layout);

        builder.setPositiveButton("Show", (dialog, which) -> {
            boolean useRangeSelected = useRange.isChecked();
            String inputText = rangeInput.getText().toString();
            float range = 0.5f;

            if (useRangeSelected) {
                try {
                    range = Float.parseFloat(inputText);
                    if (range < 0.5f || range > 10f) {
                        throw new NumberFormatException("Range out of bounds");
                    }
                } catch (NumberFormatException ex) {
                    Toast.makeText(getContext(), "Please enter a valid range between 0.5 and 10 miles", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean showAlerts = alertsCheckBox.isChecked();
            boolean showEvents = eventsCheckBox.isChecked();
            boolean showObservations = observationsCheckBox.isChecked();
            boolean showReports = reportsCheckBox.isChecked();

            if(useRangeSelected){
                // show all marrkers from the users current gps position
                fetchMarkersWithinProximity(usersCurrentLatitude, usersCurrentLongitude, range,showAlerts,showEvents,showObservations,showReports);
            } else{
                fetchMarkersWithinScreenBounds(showAlerts,showEvents,showObservations,showReports);
            }

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }




    public void showEmergencyNotification(String title, String message, double latitude, double longitude) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Show on map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        displayEmergencyOnMap(latitude, longitude, title, message);
                    }
                })
                .setNegativeButton("Dismiss", null)
                .setIcon(R.drawable.baseline_crisis_alert_24);

        // ui thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    public void displayEmergencyOnMap(double latitude, double longitude, String emergencyTitle, String emergencyMessage){

        LatLng emergencyLocation = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(emergencyLocation)
                .title(emergencyTitle)
                .snippet(emergencyMessage)
                .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_crisis_alert_24)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emergencyLocation, 14));
    }

    public void displayUserOnMap(int userId, double latitude, double longitude) {
        LatLng userLocation = new LatLng(latitude, longitude);

        getActivity().runOnUiThread(() -> {
            Marker existingMarker = userMarkersMap.get(userId);

            if (existingMarker != null) {
                // update the position
                existingMarker.setPosition(userLocation);
            } else {
                // create new user marker if new
                Marker newMarker = mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_purple_person_24)));

                userMarkersMap.put(userId, newMarker);
            }
        });

        UserApi userApi = ApiClientFactory.GetUserApi();

        Call<User> call = userApi.getUser((long) userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String userName = user.getName();

                    // attatch users name to markerr
                    getActivity().runOnUiThread(() -> {
                        Marker marker = userMarkersMap.get(userId);
                        if (marker != null) {
                            marker.setTitle(userName);
                        }
                    });
                } else {
                    Log.e("MapsFragment", "Failed to get user details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("MapsFragment", "Error fetching user details", t);
            }
        });
    }


    private void showCreatePrompt(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What do you want to create?");
        CharSequence[] options = {"Observation", "Report", "Event"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0: // Observation
                        showAddDialog(latLng, "Observation");
                        break;
                    case 1: // Report
                        showAddDialog(latLng, "Report");
                        break;
                    case 2: // Event
                        showAddDialog(latLng, "Event");
                        break;
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAddDialog(LatLng latLng, String type) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_forms, null);

        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        EditText editTextMarkerTag = view.findViewById(R.id.editTextMarkerTag);

        if ("Report".equals(type)) {
            editTextDescription.setVisibility(View.GONE);
        } else if ("Event".equals(type)) {
            editTextDescription.setVisibility(View.GONE);
        } else {
            editTextDescription.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(type)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = editTextTitle.getText().toString().trim();
                        String description = editTextDescription.getText().toString().trim();
                        String markerTagsInput = editTextMarkerTag.getText().toString().trim();

                        List<String> markerTags = parseMarkerTags(markerTagsInput);

                        if("Report".equals(type)){
                            createNewReport(latLng, title, markerTags);

                        }else if("Event".equals(type)){

                            createNewEvent(latLng, title, markerTags);


                        }else{
                            createNewObservation(latLng, title,description, markerTags);
                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openFileExplorer() {
        mFilePickerLauncher.launch("image/*");

    }

    private List<String> parseMarkerTags(String input) {
        List<String> markerTags = new ArrayList<>();
        if (input != null && !input.isEmpty()) {
            String[] tags = input.split(",");
            for (String tag : tags) {
                String trimmedTag = tag.trim();
                if (!trimmedTag.isEmpty()) {
                    markerTags.add(trimmedTag);
                }
            }
        }
        return markerTags;
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_menu);

        Button btnReportRead = bottomSheetDialog.findViewById(R.id.btn_report_read);
        Button btnReportUpdate = bottomSheetDialog.findViewById(R.id.btn_report_update);
        Button btnReportDelete = bottomSheetDialog.findViewById(R.id.btn_report_delete);
        Button btnReportList = bottomSheetDialog.findViewById(R.id.btn_report_list);
        Button btnEventRead = bottomSheetDialog.findViewById(R.id.btn_event_read);
        Button btnEventUpdate = bottomSheetDialog.findViewById(R.id.btn_event_update);
        Button btnEventDelete = bottomSheetDialog.findViewById(R.id.btn_event_delete);
        Button btnEventList = bottomSheetDialog.findViewById(R.id.btn_event_list);
        Button btnObservationRead = bottomSheetDialog.findViewById(R.id.btn_observation_read);
        Button btnObservationUpdate = bottomSheetDialog.findViewById(R.id.btn_observation_update); //not updating
        Button btnObservationDelete = bottomSheetDialog.findViewById(R.id.btn_observation_delete);
        Button btnObservationList = bottomSheetDialog.findViewById(R.id.btn_observation_all);


        btnReportRead.setOnClickListener(v -> {
            reportIdStatus = 1;
            promptForReportId();
            bottomSheetDialog.dismiss();
        });
        btnReportUpdate.setOnClickListener(v -> {
            reportUpdateTextView.setVisibility(View.VISIBLE);
            isUpdateReportMode = true;
            bottomSheetDialog.dismiss();
        });
        btnReportDelete.setOnClickListener(v -> {
            reportIdStatus = 2;
            promptForReportId();
            bottomSheetDialog.dismiss();
        });
        btnReportList.setOnClickListener(v -> {
            displayAllReports();
            bottomSheetDialog.dismiss();
        });
        btnEventRead.setOnClickListener(v -> {
            eventIdStatus = 1;
            promptForEventId();
            bottomSheetDialog.dismiss();
        });
        btnEventUpdate.setOnClickListener(v -> {
            eventUpdateTextView.setVisibility(View.VISIBLE);
            isUpdateEventMode = true;
            bottomSheetDialog.dismiss();
        });
        btnEventDelete.setOnClickListener(v -> {
            eventIdStatus = 2;
            promptForEventId();
            bottomSheetDialog.dismiss();
        });
        btnEventList.setOnClickListener(v -> {
            displayAllEvents();
            bottomSheetDialog.dismiss();
        });
        //done
        btnObservationRead.setOnClickListener(v -> {
            observationIdStatus = 1;
            promptForObservationId();
            bottomSheetDialog.dismiss();
        });
        btnObservationUpdate.setOnClickListener(v -> {
            observationUpdateTextView.setVisibility(View.VISIBLE);
            isUpdateObservationMode = true;
            bottomSheetDialog.dismiss();
        });
        btnObservationDelete.setOnClickListener(v -> {
            observationIdStatus = 2;
            promptForObservationId();
            bottomSheetDialog.dismiss();
        });
        btnObservationList.setOnClickListener(v -> {
            displayAllObservations();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    public String getLocation(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            return address.toString();
        }
        return "No address found!";
    }

    private void addTagsToReport(Long reportId, List<String> tagNames) {
        MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();

        for (String tagName : tagNames) {
            markerTagApi.searchTagByName(tagName).enqueue(new Callback<MarkerTag>() {
                @Override
                public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long tagId = response.body().getId();
                        reportMarkerApi.addExistingTagToReport(reportId, tagId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d("TEST","tagAddedToReport");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("addTagsToReport", "Failed");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<MarkerTag> call, Throwable t) {
                    markerTagApi.createTagWithName(tagName).enqueue(new Callback<MarkerTag>() {
                        @Override
                        public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Long newTagId = response.body().getId();
                                Log.d("TEST","New tag");
                                reportMarkerApi.addExistingTagToReport(reportId, newTagId).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("TEST","tagAddedToReport");
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("addTagsToReport", "Failed", t);
                                    }

                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<MarkerTag> call, Throwable t) {
                            Log.d("createNewTag", "Failed", t);
                        }
                    });
                }
            });
        }
    }

    private void addTagsToEvent(Long eventId, List<String> tagNames) {
        MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        for (String tagName : tagNames) {
            markerTagApi.searchTagByName(tagName).enqueue(new Callback<MarkerTag>() {
                @Override
                public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long tagId = response.body().getId();
                        eventMarkerApi.addExistingTagToEvent(eventId, tagId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d("TEST","tagAddedToEvent");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("addTagsToEvent", "Failed");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<MarkerTag> call, Throwable t) {
                    markerTagApi.createTagWithName(tagName).enqueue(new Callback<MarkerTag>() {
                        @Override
                        public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Long newTagId = response.body().getId();
                                Log.d("TEST","New tag");
                                eventMarkerApi.addExistingTagToEvent(eventId, newTagId).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("TEST","tagAddedToEvent");
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("addTagsToEvent", "Failed", t);
                                    }

                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<MarkerTag> call, Throwable t) {
                            Log.d("createNewTag", "Failed", t);
                        }
                    });
                }
            });
        }
    }

    private void addTagsToObservation(Long observationId, List<String> tagNames) {
        MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
        ObservationApi observationMarkerApi = ApiClientFactory.GetObservationApi();

        for (String tagName : tagNames) {
            markerTagApi.searchTagByName(tagName).enqueue(new Callback<MarkerTag>() {
                @Override
                public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long tagId = response.body().getId();
                        observationMarkerApi.addExistingTagToObservation(observationId, tagId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Log.d("TEST","tagAddedToObservation");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("addTagsToObservation", "Failed");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<MarkerTag> call, Throwable t) {
                    markerTagApi.createTagWithName(tagName).enqueue(new Callback<MarkerTag>() {
                        @Override
                        public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Long newTagId = response.body().getId();
                                Log.d("TEST","New tag");
                                observationMarkerApi.addExistingTagToObservation(observationId, newTagId).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("TEST","tagAddedToObservation");
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.d("addTagsToObservation", "Failed", t);
                                    }

                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<MarkerTag> call, Throwable t) {
                            Log.d("createNewTag", "Failed", t);
                        }
                    });
                }
            });
        }
    }


    // Report CRUDL
    private void createNewReport(final LatLng latLng, String reportTitle, List<String> markerTags) {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();
        if (reportTitle == null || reportTitle.trim().isEmpty()) {
            showStatus("Need Title for Report.");
            return;
        }
        ReportMarker newReportMarker = new ReportMarker();
        newReportMarker.setIo_latitude(latLng.latitude);
        newReportMarker.setIo_longitude(latLng.longitude);
        newReportMarker.setTitle(reportTitle);
        newReportMarker.setCreator(loggedInUser);
        reportMarkerApi.addReport(newReportMarker).enqueue(new Callback<ReportMarker>() {
            @Override
            public void onResponse(Call<ReportMarker> call, Response<ReportMarker> response) {
                if (response.isSuccessful()) {
                    ReportMarker createdReportMarker = response.body();
                    addTagsToReport(createdReportMarker.getId(), markerTags);
                    LatLng position = new LatLng(createdReportMarker.getIo_latitude(), createdReportMarker.getIo_longitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(createdReportMarker.getId() + " " + createdReportMarker.getTitle())
                            .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_report_24)));
                    showStatus("Report created successfully!");
                } else {
                    Log.d("save report fail", "failed " + response.errorBody());

                }

            }

            @Override
            public void onFailure(Call<ReportMarker> call, Throwable t) {
                Toast.makeText(getContext(),"Error: " + t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    private void displayReportByID(Long id) {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();

        reportMarkerApi.getReportById(id).enqueue(new SlimCallback<>(reportMarker -> {
            if (reportMarker != null) {
                LatLng position = new LatLng(reportMarker.getIo_latitude(), reportMarker.getIo_longitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(reportMarker.getId() + " " + reportMarker.getTitle())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_report_24)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            }
        }, "getReportByID"));
    }
    private void updateExistingReportByID(Long id, String newTitle, LatLng latLng) {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();

        ReportMarker updatedReportMarker = new ReportMarker();
        updatedReportMarker.setTitle(newTitle);
        //updatedReportMarker.setTime_updated(new Date());
        updatedReportMarker.setIo_latitude(latLng.latitude);
        updatedReportMarker.setIo_longitude(latLng.longitude);

        reportMarkerApi.updateReportById(id, updatedReportMarker).enqueue(new SlimCallback<>(updatedReport -> {
            if (updatedReport != null) {

                mMap.clear();
                displayAllReports();

                Toast.makeText(getActivity(), "Report updated successfully", Toast.LENGTH_SHORT).show();
            }
        }));
    }
    private void deleteReportByID(Long id){
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();
        reportMarkerApi.deleteReportById(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    mMap.clear();
                    displayAllReports();

                    Toast.makeText(getActivity(), "Report deleted successfully",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getActivity(), "Failed to delete report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error deleting report", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayAllReports() {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();

        reportMarkerApi.GetAllReportMarker().enqueue(new SlimCallback<>(reportMarkers -> {
            mMap.clear();
            for (ReportMarker reportMarker : reportMarkers) {
                LatLng position = new LatLng(reportMarker.getIo_latitude(), reportMarker.getIo_longitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(reportMarker.getId() + " " + reportMarker.getTitle())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_report_24)));
            }
        }, "GetAllReports"));
    }

    // Observation CRUDL
    private void createNewObservation(final LatLng latLng, String observationTitle, String observationDescription, List<String> markerTags) {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        if (observationTitle == null || observationTitle.trim().isEmpty()) {
            showStatus("Need Title for Observation.");
            return;
        }
        Observation observation = new Observation();
        observation.setIo_latitude(latLng.latitude);
        observation.setIo_longitude(latLng.longitude);
        observation.setCreator(loggedInUser);
        observation.setTitle(observationTitle);
        observation.setDescription(observationDescription);

        observationApi.saveObs(observation).enqueue(new Callback<Observation>() {
            @Override
            public void onResponse(Call<Observation> call, Response<Observation> response) {
                if (response.isSuccessful()) {
                    Observation obs = response.body();
                    addTagsToObservation(obs.getId(), markerTags);
                    LatLng position = new LatLng(obs.getIo_latitude(), obs.getIo_longitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(obs.getId() + " " + obs.getTitle())
                            .snippet(obs.getDescription())
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.baseline_photo_camera_24)));
                    showStatus("Observation created successfully!");
                    UploadImagePrompt(obs);
                } else {
                    Log.d("save obs fail", "failed " + response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<Observation> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void UploadImagePrompt(Observation obs) {
        Log.d("OBS ID", String.valueOf(obs.getPostID()));
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_image, null);

             uploadImageObservation = view.findViewById(R.id.uploadImage);
            uploadImageObservation.setOnClickListener(v -> openFileExplorer());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view)
                    .setTitle("Observation created! Do you want to add an image?")
                    .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(selectedUri!=NULL) {

                                UploadImage(selectedUri, obs);
                            }else{
                                Toast.makeText(getActivity(), "No Uri Selected", Toast.LENGTH_SHORT).show();

                            }


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });



            AlertDialog dialog = builder.create();
            dialog.show();


    }

    private void UploadImage(Uri fileUri, Observation imgForObs) {
        String filePath = FileUtils.createCopyFromUri(getContext(), fileUri);

        if (filePath == null) {
            Log.e("UploadImage", "Failed to get file path from URI");
            return;
        }

        File file = new File(filePath);
        Log.d("UploadImage", "Starting upload for file: " + filePath);

        // Check if file is too large
        long fileSizeInMB = file.length() / (1024 * 1024);
        Log.e("UploadImage", "File size (" + fileSizeInMB + "MB)");

        if (fileSizeInMB > 1) {
            Log.e("UploadImage", "File size (" + fileSizeInMB + "MB) exceeds the maximum limit.");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.observationFileUpload(body, imgForObs.getPostID(), "OBSERVATION").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("UploadImage", "Success: " + response.body());
                } else {
                    Log.e("UploadImage", "Upload failed with response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("UploadImage", "Upload failed with error: " + errorBody);
                    } catch (IOException e) {
                        Log.e("UploadImage", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UploadImage", "Upload failed", t);
            }
        });
    }

    private void showStatus(String s) {
        statusMessage.setText(s);
        statusMessage.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (statusMessage != null) {
                    statusMessage.setVisibility(View.GONE);
                }
            }
        }, 1000);
    }

    private void displayObservationByID(Long id) {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();

        observationApi.getObs(id).enqueue(new SlimCallback<>(obj -> {
            if (obj != null) {
                LatLng position = new LatLng(obj.getIo_latitude(), obj.getIo_longitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(obj.getId() + " " + obj.getTitle())
                        .snippet(obj.getDescription())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_photo_camera_24)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
                showStatus("Observation found successfully!");
            }
        }, "getObservationByID"));
    }

    private void updateExistingObservationByID(Long id, String newTitle, LatLng latLng, String newDescription) {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();

        Observation updatedObservation = new Observation();
        updatedObservation.setId(id);
        updatedObservation.setTitle(newTitle);
        updatedObservation.setIo_latitude(latLng.latitude);
        updatedObservation.setIo_longitude(latLng.longitude);

        Log.d("Updating...", updatedObservation.getTitle() + " "+ updatedObservation.getId()+" " +updatedObservation.getDescription());
        observationApi.updateObs(id, updatedObservation).enqueue(new SlimCallback<>(obs -> {
            Log.d("Update 1", "Update check");
            if (obs != null) {

                mMap.clear();
                displayAllObservations();
                Log.d("Update", "Updated correctly");

                Toast.makeText(getActivity(), "Report updated successfully", Toast.LENGTH_SHORT).show();
            }
            Log.d("Update 2", "Update failed");

        }));
    }
    private void deleteObservationByID(Long id){
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        observationApi.deleteObs(id).enqueue(new Callback<Observation>() {
            @Override
            public void onResponse(Call<Observation> call, Response<Observation> response) {
                if(response.isSuccessful()){
                    mMap.clear();
                    displayAllObservations();
                    Toast.makeText(getActivity(), "Report deleted successfully",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getActivity(), "Failed to delete report", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Observation> call, Throwable t) {
                Toast.makeText(getActivity(), "Error deleting report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAllObservations() {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();

        observationApi.getAllObs().enqueue(new SlimCallback<>(obs -> {
            mMap.clear();
            for (Observation ob : obs) {
                LatLng position = new LatLng(ob.getIo_latitude(), ob.getIo_longitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(ob.getId() + " " + ob.getTitle())
                        .snippet(ob.getDescription())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_photo_camera_24)));
            }
        }, "GetAllObservations"));
    }

    // Event CRUDL
    private void createNewEvent(final LatLng latLng, String eventTitle, List<String> markerTags) {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();
        if (eventTitle == null || eventTitle.trim().isEmpty()) {
            showStatus("Need Title for Event.");
            return;
        }
        EventMarker newEventMarker = new EventMarker();
        newEventMarker.setIo_latitude(latLng.latitude);
        newEventMarker.setIo_longitude(latLng.longitude);
        newEventMarker.setCreator(loggedInUser);
        newEventMarker.setTitle(eventTitle);
        eventMarkerApi.addEvent(newEventMarker).enqueue(new Callback<EventMarker>() {
            @Override
            public void onResponse(Call<EventMarker> call, Response<EventMarker> response) {
                if (response.isSuccessful()) {
                    EventMarker createdEventMarker = response.body();
                    addTagsToEvent(createdEventMarker.getId(), markerTags);
                    LatLng position = new LatLng(createdEventMarker.getIo_latitude(), createdEventMarker.getIo_longitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(createdEventMarker.getId() + " " + createdEventMarker.getTitle())
                            .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_celebration_24)));
                    showStatus("Event created successfully!");

                }  else {
                    showStatus("Failed to create event: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<EventMarker> call, Throwable t) {
                showStatus("Error: " + t.getMessage());

            }
        });

    }
    private void displayEventByID(Long id) {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        eventMarkerApi.getEventById(id).enqueue(new SlimCallback<>(eventMarker -> {
            if (eventMarker != null) {
                LatLng position = new LatLng(eventMarker.getIo_latitude(), eventMarker.getIo_longitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(eventMarker.getId() + " " + eventMarker.getTitle())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_celebration_24)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            }
        }, "getEventByID"));
    }
    private void updateExistingEventByID(Long id, String newTitle, LatLng latLng) {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        EventMarker updatedEventMarker = new EventMarker();
        updatedEventMarker.setTitle(newTitle);
//        updatedEventMarker.setCity_department(newCityDepartment);
//        updatedEventMarker.setTime_updated(new Date());
        updatedEventMarker.setIo_latitude(latLng.latitude);
        updatedEventMarker.setIo_longitude(latLng.longitude);


        eventMarkerApi.updateEventById(id, updatedEventMarker).enqueue(new SlimCallback<>(updatedEvent -> {
            if (updatedEvent != null) {

                mMap.clear();
                displayAllEvents();

                Toast.makeText(getActivity(), "Event updated successfully", Toast.LENGTH_SHORT).show();
            }
        }));
    }
    private void deleteEventByID(Long id) {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        eventMarkerApi.deleteEventById(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    mMap.clear();
                    displayAllEvents();

                    Toast.makeText(getActivity(), "Event deleted successfully",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getActivity(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error deleting event", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void displayAllEvents() {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        eventMarkerApi.GetAllEventMarker().enqueue(new SlimCallback<>(eventMarkers -> {
            mMap.clear();
            for (EventMarker eventMarker : eventMarkers) {
                LatLng position = new LatLng(eventMarker.getIo_latitude(), eventMarker.getIo_longitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(eventMarker.getId() + " " + eventMarker.getTitle())
                        .icon(bitmapDescriptorFromVector(getContext(),R.drawable.baseline_celebration_24)));
            }
        }, "GetAllEvents"));
    }

    // Methods for collecting CRUDL info from user
    private void promptForReportId(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Report ID");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(reportIdStatus == 3){
                    promptToUpdateReportTitle(id,latLng);
                    reportIdStatus = 0;
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void promptForObservationId(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Observation ID");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(observationIdStatus == 3){
                    promptToUpdateObservationTitle(id,latLng);
                    observationIdStatus = 0;
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void promptForReportId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Report ID");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(reportIdStatus == 1){
                    displayReportByID(id);
                    reportIdStatus = 0;
                }else if(reportIdStatus == 2){
                    deleteReportByID(id);
                    reportIdStatus = 0;
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void promptForObservationId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Observation ID");

        final EditText idInput = new EditText(getActivity());
        idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(idInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(idInput.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(observationIdStatus == 1){
                    displayObservationByID(id);
                    observationIdStatus = 0;
                }else if(observationIdStatus == 2){
                    deleteObservationByID(id);
                    observationIdStatus = 0;
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void promptForEventId(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Event ID");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(eventIdStatus == 3){
                    promptUserToUpdateEvent(id,latLng);
                    eventIdStatus = 0;
                }

            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void promptForEventId() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Event ID");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid ID.", Toast.LENGTH_SHORT).show();
            }

            if (id != null) {
                if(eventIdStatus == 1){
                    displayEventByID(id);
                    eventIdStatus = 0;
                }else if(eventIdStatus == 2){
                    deleteEventByID(id);
                    eventIdStatus = 0;
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void promptUserToUpdateEvent(Long Id,LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Event");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText titleInput = new EditText(getActivity());
        titleInput.setHint("New Title");
        layout.addView(titleInput);

        builder.setView(layout);


        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = titleInput.getText().toString();
//                String newCityDepartment = cityDepartmentInput.getText().toString();
                updateExistingEventByID(Id, newTitle, latLng);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void promptToUpdateReportTitle(Long Id, LatLng latLng) {
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("New Title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Report Title")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTitle = input.getText().toString();
                    updateExistingReportByID(Id, newTitle, latLng);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }
    private void promptToUpdateObservationTitle(Long Id, LatLng latLng) {
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("New Title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Observation Title")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newTitle = input.getText().toString();
                    promptToUpdateObservationDescription(Id, latLng, newTitle);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }
    private void promptToUpdateObservationDescription(Long Id, LatLng latLng, String newTitle) {
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        descriptionInput.setHint("New Description");
        descriptionInput.setLines(5);
        descriptionInput.setMaxLines(10);
        descriptionInput.setGravity(Gravity.START | Gravity.TOP);

        AlertDialog.Builder descriptionDialogBuilder = new AlertDialog.Builder(getActivity());
        descriptionDialogBuilder.setTitle("Update Observation Description")
                .setView(descriptionInput)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newDescription = descriptionInput.getText().toString();
                    updateExistingObservationByID(Id, newTitle, latLng, newDescription);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicHeight(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String getFormattedTime(long timestamp){
        Date date = new Date(timestamp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        String formattedDate = dateFormat.format(date);


        return formattedDate;
    }



}




