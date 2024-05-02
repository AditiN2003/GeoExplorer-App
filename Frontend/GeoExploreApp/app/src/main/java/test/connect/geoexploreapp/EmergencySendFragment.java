package test.connect.geoexploreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.websocket.AlertWebSocketManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencySendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencySendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText titleText, messageText, latitudeText, longitudeText;
    private User loggedInUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmergencySendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmergencyDashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencySendFragment newInstance(String param1, String param2) {
        EmergencySendFragment fragment = new EmergencySendFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_send, container, false);


        latitudeText = view.findViewById(R.id.latitudeText);
        longitudeText = view.findViewById(R.id.longitudeText);
        titleText = view.findViewById(R.id.titleText);
        messageText = view.findViewById(R.id.messageText);


        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        viewModel.getLatitude().observe(getViewLifecycleOwner(), latitude -> {
            latitudeText.setText(latitude != null ? String.valueOf(latitude) : "N/A");
        });


        viewModel.getLongitude().observe(getViewLifecycleOwner(), longitude -> {
            longitudeText.setText(longitude != null ? String.valueOf(longitude) : "N/A");
        });


        viewModel.getLoggedInUser().observe(getViewLifecycleOwner(), loggedUser -> {
            loggedInUser = loggedUser;

        });


        Button backButton = view.findViewById(R.id.backButton);
        Button setLocationButton = view.findViewById(R.id.setLocationButton);
        Button sendEmergencyButton = view.findViewById(R.id.sendEmergencyButton);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        setLocationButton.setOnClickListener(v -> {
            viewModel.setCreateEmergencyNotification(true);
            Log.d("EmergencyDashFragment","Emergency was set to true");
            MapsFragment mapsFragment = new MapsFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame, mapsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        sendEmergencyButton.setOnClickListener(v -> {

            String title = titleText.getText().toString();
            String message = messageText.getText().toString();
            String latitude = latitudeText.getText().toString();
            String longitude = longitudeText.getText().toString();

            if(title.isEmpty() || message.isEmpty() || latitude.isEmpty() || longitude.isEmpty()) {

                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            AlertMarker alertMarker = new AlertMarker();
            alertMarker.setTitle(title);
            alertMarker.setDescription(message);
            alertMarker.setIo_latitude(Double.parseDouble(latitude));
            alertMarker.setIo_longitude(Double.parseDouble(longitude));
            alertMarker.setCreator(loggedInUser);

            Gson gson = new Gson();
            String jsonMessage = gson.toJson(alertMarker);

            AlertWebSocketManager.getInstance().sendMessage(jsonMessage);

        });
        return view;
    }
}