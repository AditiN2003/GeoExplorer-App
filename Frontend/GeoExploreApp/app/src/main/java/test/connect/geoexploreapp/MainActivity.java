package test.connect.geoexploreapp;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import test.connect.geoexploreapp.databinding.ActivityMainBinding;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.websocket.AlertWebSocketManager;
import test.connect.geoexploreapp.websocket.CommentWebSocketManager;
import test.connect.geoexploreapp.websocket.LocationWebSocketManager;

import android.Manifest;
public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean hasLocationPermissions;
    ActivityMainBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        user = (User) getIntent().getSerializableExtra("UserObject");
        if (user == null) {
            throw new IllegalArgumentException("User object is null");
        }
        String userID = String.valueOf(user.getId());
        AlertWebSocketManager.getInstance().connectWebSocket("ws://coms-309-005.class.las.iastate.edu:8080/live/alerts/" + userID);
        LocationWebSocketManager.getInstance().connectWebSocket("ws://coms-309-005.class.las.iastate.edu:8080/live/location/" + userID);

        if(user!= null){
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            viewModel.setLoggedInUser(user);
            Log.d("MainActivity", "User: " + user);
        }

        if (hasLocationPermissions()) {
            Log.d("MainActivity", "Location permission is already granted.");
        } else {
            Log.d("MainActivity", "Requesting location permissions.");
            requestLocationPermissions();
        }


        binding.bottomNavigationView.setSelectedItemId(R.id.maps);
        replaceFragment(new MapsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.profile) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(user);
                replaceFragment(profileFragment);
            } else if (itemId == R.id.maps) {
                MapsFragment mapsFragment = MapsFragment.newInstance(user);
                replaceFragment(new MapsFragment());
            } else if(itemId == R.id.show_feed){
                CommentWebSocketManager.getInstance().connectWebSocket("ws://coms-309-005.class.las.iastate.edu:8080/comments/"+user.getId()); //URL ADD LATER
                FeedActivity feedActivity = FeedActivity.newInstance(user);
                replaceFragment(feedActivity);
            } else if (itemId == R.id.settings) {
                SettingsFragment settingsFragment = SettingsFragment.newInstance(user.getRole()== User.Role.ADMIN);
                replaceFragment(settingsFragment);
            }else if(itemId == R.id.usergroups){
                UserGroupActivity userGroupsFragment = UserGroupActivity.newInstance(user);
                replaceFragment(userGroupsFragment);
            }

            return true;
        });

    }

    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.d("MainActivity", "Location permissions granted.");
            } else {
                Log.d("MainActivity", "Location permissions denied.");
                handlePermissionDenied();
            }
        }
    }

    private void handlePermissionDenied() {
        Toast.makeText(this, "Location permissions are required for this feature.", Toast.LENGTH_SHORT).show();
    }
}

