package test.connect.geoexploreapp;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.MarkerTagApi;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.model.MarkerTag;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarkerTagManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkerTagManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tagInfoTextView;
    private EditText tagIdEditText, tagNameEditText;
    private ListView listView;

    public MarkerTagManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarkerTagManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerTagManagementFragment newInstance(String param1, String param2) {
        MarkerTagManagementFragment fragment = new MarkerTagManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_marker_tag_management, container, false);

        Button backButton = view.findViewById(R.id.markerTagBackButton);
        Button newTagBtn = view.findViewById(R.id.newTagBtn);
        listView = view.findViewById(R.id.tagListView);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        newTagBtn.setOnClickListener(v -> showNewTagDialog());

        getAllTags();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MarkerTag selectedTag = (MarkerTag) parent.getItemAtPosition(position);
                showTagDetailFragment(selectedTag);
            }
        });
        return view;
    }


    private void showNewTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Tag");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String tagName = input.getText().toString().trim();
            if (!tagName.isEmpty()) {
                createTag(tagName);
            } else {
                Toast.makeText(getContext(), "Tag name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }




    private void showTagDetailFragment(MarkerTag markerTag) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MarkerTagDetailedViewFragment detailFragment = MarkerTagDetailedViewFragment.newInstance(markerTag);
        fragmentTransaction.replace(R.id.frame, detailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }




    private void getAllTags() {
        MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();
        markerTagApi.getAllMarkerTags().enqueue(new SlimCallback<>(markerTags -> {
            ArrayAdapter<MarkerTag> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, markerTags);
            listView.setAdapter(adapter);
        }, "GetAllTags"));
    }

    private void createTag(String tagName) {

        if (!tagName.isEmpty()) {
            MarkerTagApi markerTagApi = ApiClientFactory.getMarkerTagApi();

            MarkerTag newTag = new MarkerTag();
            newTag.setName(tagName);
            markerTagApi.addMarkerTag(newTag).enqueue(new Callback<MarkerTag>() {
                @Override
                public void onResponse(Call<MarkerTag> call, Response<MarkerTag> response) {
                    if (response.isSuccessful()) {
                        MarkerTag createdTag = response.body();
                        String successMessage = "Tag created successfully with ID: " + createdTag.getId();
                        Toast.makeText(getActivity(), successMessage, Toast.LENGTH_SHORT).show();
                        getAllTags();
                    } else {
                        Toast.makeText(getActivity(), "Failed to create tag", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MarkerTag> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error creating tag: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Tag name cannot be empty", Toast.LENGTH_LONG).show();
        }
    }
}