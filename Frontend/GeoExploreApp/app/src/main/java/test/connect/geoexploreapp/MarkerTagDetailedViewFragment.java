package test.connect.geoexploreapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.MarkerTagApi;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.MarkerTag;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.ReportMarker;

public class MarkerTagDetailedViewFragment extends Fragment {

    private static final String ARG_MARKER_TAG = "marker_tag";
    private MarkerTag markerTag;
    private TextView nameTextView;
    private LinearLayout linearLayoutMarkers;


    public MarkerTagDetailedViewFragment() {
        // Required empty public constructor
    }

    public static MarkerTagDetailedViewFragment newInstance(MarkerTag markerTag) {
        MarkerTagDetailedViewFragment fragment = new MarkerTagDetailedViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MARKER_TAG, markerTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_MARKER_TAG)) {
            markerTag = (MarkerTag) getArguments().getSerializable(ARG_MARKER_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marker_tag_detailed_view, container, false);inflater.inflate(R.layout.fragment_marker_tag_detailed_view, container, false);
        Button backButton = view.findViewById(R.id.backButton);
        Button deleteButton = view.findViewById(R.id.tagDetailDeleteBtn);
        Button updateButton = view.findViewById(R.id.tagDetailUpdateBtn);


        TextView idTextView = view.findViewById(R.id.idTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        linearLayoutMarkers = view.findViewById(R.id.linearLayoutMarkers);


        if (markerTag != null) {
            idTextView.setText(String.format("Tag ID: %s", markerTag.getId()));
            nameTextView.setText(String.format("Tag Name: %s", markerTag.getName()));
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTagByID();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNameForTagDialog();
            }
        });

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        fetchMarkers();


        return view;
    }

    private void newNameForTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rename Tag");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tagName = input.getText().toString().trim();
            if (!tagName.isEmpty()) {
                updateTagByID(tagName);
            } else {
                Toast.makeText(getContext(), "Tag name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteTagByID() {
        String idString = "" + markerTag.getId();
        if (!idString.isEmpty()) {
            try {
                Long id = Long.parseLong(idString);
                MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
                markerTagApi.deleteMarkerTagById(id).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Tag deleted", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete tag", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getActivity(), "Error deleting tag: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Enter a valid ID", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateTagByID(String newName) {
        Log.d("MarkerTag", "" + markerTag.getId());
        String idString = "" + markerTag.getId();

        if (!idString.isEmpty() && !newName.isEmpty()) {
            try {
                Long id = Long.parseLong(idString);
                MarkerTag updatedTag = new MarkerTag();
                updatedTag.setName(newName);

                MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
                markerTagApi.updateMarkerTag(id, updatedTag).enqueue(new Callback<MarkerTag>() {
                    @Override
                    public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Tag Updated", Toast.LENGTH_SHORT).show();
                            nameTextView.setText(String.format("Name: %s", newName));
                        } else {
                            Toast.makeText(getActivity(), "Failed to update tag", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MarkerTag> call, Throwable t) {
                        Toast.makeText(getActivity(), "Error updating tag", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Enter a valid ID", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "ID and new name cannot be empty", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchMarkers() {
        MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
        Log.d("4/28", "Im inside fetchMarkers id is: " + markerTag.getId());
        Long tagId = markerTag.getId();

        markerTagApi.getReportsForTag(tagId).enqueue(new Callback<List<ReportMarker>>() {
            @Override
            public void onResponse(Call<List<ReportMarker>> call, Response<List<ReportMarker>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ReportMarker report : response.body()) {
                        addMarkerToLinearLayout("Report: " + report.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ReportMarker>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching reports", Toast.LENGTH_SHORT).show();
            }
        });

        markerTagApi.getEventsForTag(tagId).enqueue(new Callback<List<EventMarker>>() {
            @Override
            public void onResponse(Call<List<EventMarker>> call, Response<List<EventMarker>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (EventMarker event : response.body()) {
                        addMarkerToLinearLayout("Event: " + event.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EventMarker>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching events", Toast.LENGTH_SHORT).show();
            }
        });

        markerTagApi.getObservationsForTag(tagId).enqueue(new Callback<List<Observation>>() {
            @Override
            public void onResponse(Call<List<Observation>> call, Response<List<Observation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Observation observation : response.body()) {
                        addMarkerToLinearLayout("Observation: " + observation.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Observation>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching observations", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addMarkerToLinearLayout(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(21);

        linearLayoutMarkers.addView(textView);
    }

}