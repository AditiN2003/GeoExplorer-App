package test.connect.geoexploreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.AlertMarkerApi;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.MarkerTag;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertDetailedViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertDetailedViewFragment extends Fragment {

    private static final String ARG_ALERT_MARKER = "alert_marker";
    private AlertMarker alertMarker;

    public AlertDetailedViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertDetailedViewFragment newInstance(AlertMarker alertMarker) {
        AlertDetailedViewFragment fragment = new AlertDetailedViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALERT_MARKER, alertMarker);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_ALERT_MARKER)) {
            alertMarker = (AlertMarker) getArguments().getSerializable(ARG_ALERT_MARKER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_detailed_view, container, false);inflater.inflate(R.layout.fragment_alert_detailed_view, container, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        TextView latitudeTextView = view.findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = view.findViewById(R.id.longitudeTextView);

        Button backButton = view.findViewById(R.id.alertDetailBackButton);
        Button deleteButton = view.findViewById(R.id.alertDetailDeleteBtn);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert();
            }
        });

        if (alertMarker != null) {
            titleTextView.setText(String.format("Title: %s", alertMarker.getTitle()));
            descriptionTextView.setText(String.format("Description: %s", alertMarker.getDescription()));
            latitudeTextView.setText(String.format("Latitude: %s", alertMarker.getIo_latitude()));
            longitudeTextView.setText(String.format("Longitude: %s", alertMarker.getIo_longitude()));
        }

        return view;
    }

    private void deleteAlert(){
        AlertMarkerApi alertMarkerApi = ApiClientFactory.getAlertMarkerApi();
        alertMarkerApi.deleteAlertById(alertMarker.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(), "Alert deleted", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else{
                    Toast.makeText(getActivity(), "Failed to delete alert", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error deleting alert", Toast.LENGTH_SHORT).show();
            }
        });

    }
}