package test.connect.geoexploreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.AlertMarkerApi;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.MarkerTagApi;
import test.connect.geoexploreapp.api.ReportMarkerApi;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.model.AlertMarker;
import test.connect.geoexploreapp.model.MarkerTag;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyDashboardFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    private TextView alertInfoTextView;
    private EditText alertIdEditText;
    private ListView listView;

    public EmergencyDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyDashboardFragment newInstance(String param1, String param2) {
        EmergencyDashboardFragment fragment = new EmergencyDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_emergency_dashboard, container, false);
        Button backButton = view.findViewById(R.id.alertBackButton);
        Button newAlertButton = view.findViewById(R.id.newAlertBtn);

        listView = view.findViewById(R.id.alertListView);

        getAllAlerts();


        newAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment emergencySendFragment = new EmergencySendFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, emergencySendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertMarker selectedAlert = (AlertMarker) parent.getItemAtPosition(position);
                showAlertDetailFragment(selectedAlert);
            }
        });

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());


        return view;
    }


    private void showAlertDetailFragment(AlertMarker alertMarker) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AlertDetailedViewFragment detailFragment = AlertDetailedViewFragment.newInstance(alertMarker);
        fragmentTransaction.replace(R.id.frame, detailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getAllAlerts() {
        AlertMarkerApi alertMarkerApi = ApiClientFactory.getAlertMarkerApi();
        alertMarkerApi.getAllAlertMarker().enqueue(new SlimCallback<>(alertMarkers -> {
            ArrayAdapter<AlertMarker> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, alertMarkers);
            listView.setAdapter(adapter);
        }, "GetAllAlerts"));
    }

}