package test.connect.geoexploreapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.ReportMarkerApi;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.api.UserGroupApi;
import test.connect.geoexploreapp.model.LocationPrivacy;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.model.UserGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private User user;
    private boolean isUserSet;
    private UserApi userApi;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(boolean isAdmin) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean("IsAdmin", isAdmin); // get user admin status
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Bundle args = getArguments();

        boolean isAdmin = false;

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getLoggedInUser().observe(getViewLifecycleOwner(), loggedInUser -> {
            user = loggedInUser;
            if (loggedInUser != null && !isUserSet){
                Log.d("4/23","User: "+ user);
                isUserSet = true;
            }else {
                Log.e("WebSocket", "Logged in user is null. Cannot establish WebSocket connection.");
            }
        });

        if(args != null){
            isAdmin = args.getBoolean("IsAdmin",false);
            Log.d("SettingsActivity", "isAdmin: " + isAdmin);

            FrameLayout btnEmergencyDashboard = view.findViewById(R.id.emergencyDashButton);
            FrameLayout btnMarkerTagManagement = view.findViewById(R.id.markerTagMngmtBtn);
            FrameLayout btnCreateUserGroup = view.findViewById(R.id.createUserGroupButton);

            FrameLayout btnEditLocationPreference = view.findViewById(R.id.locationPreferenceButton);
            FrameLayout btnSignOut = view.findViewById(R.id.signOut);
            FrameLayout btnReportedUsers = view.findViewById(R.id.reportedUsers);

            if (isAdmin) {
                btnEmergencyDashboard.setVisibility(View.VISIBLE);
                btnMarkerTagManagement.setVisibility(View.VISIBLE);
                btnCreateUserGroup.setVisibility(View.VISIBLE);


                btnEmergencyDashboard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment emergencyDashboardFragment = new EmergencyDashboardFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame, emergencyDashboardFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });

                btnMarkerTagManagement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment markerTagManagementFragment = new MarkerTagManagementFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame, markerTagManagementFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });

                btnCreateUserGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addUserGroupPrompt();
                    }
                });

                btnReportedUsers.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        ReportedUserActivity reportedUserActivity = ReportedUserActivity.newInstance();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame,reportedUserActivity);
                        fragmentTransaction.commit();

                    }
                });
            } else {
                btnEmergencyDashboard.setVisibility(View.GONE);
                btnCreateUserGroup.setVisibility(View.GONE);
                btnReportedUsers.setVisibility(View.GONE);
            }

            btnEditLocationPreference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // update user with location preference
                    createLocationPreferenceDialog();
                }
            });

            btnSignOut.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });


        }

        return view;
    }

    private void addUserGroupPrompt(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create User Group");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Add the title of the user group");

        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = input.getText().toString();
                UserGroup userGrouptoAdd = new UserGroup();
                userGrouptoAdd.setTitle(groupName);
                addUserGroup(userGrouptoAdd);
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

    private void addUserGroup(UserGroup group) {
        UserGroupApi userGroupApi = ApiClientFactory.GetUserGroupApi();

        userGroupApi.addGroup(group).enqueue(new Callback<UserGroup>() {
            @Override
            public void onResponse(Call<UserGroup> call, Response<UserGroup> response) {
                if(response.isSuccessful()) {
                    UserGroup createdGroup = response.body();
                    Log.d("addUserGroup", "Group created successfully: " + createdGroup.getTitle());
                } else {
                    Log.e("addUserGroup", "Failed to create group: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserGroup> call, Throwable t) {
                Log.e("addUserGroup", "Error creating group", t);
            }
        });
    }

    private void createLocationPreferenceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Location Privacy");

        String[] options = {"Disabled", "Public", "Group", "Emergency"};

        // users current privacy
        int defaultSelection = getIndexForLocationPrivacy(user.getLocation_privacy());

        builder.setSingleChoiceItems(options, defaultSelection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                if (selected == defaultSelection) {

                    dialog.dismiss();
                    return;
                }
                LocationPrivacy selectedPrivacy = LocationPrivacy.values()[selected];
                updateUserLocationPrivacy(selectedPrivacy);  // update user with new privacy
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private int getIndexForLocationPrivacy(LocationPrivacy privacy) {
        switch (privacy) {
            case DISABLED:
                return 0;
            case PUBLIC:
                return 1;
            case GROUP:
                return 2;
            case EMERGENCY:
                return 3;
            default:
                return 0;  // user default is disabled
        }
    }

    private void updateUserLocationPrivacy(LocationPrivacy newPrivacy) {
        user.setLocation_privacy(newPrivacy);
        UserApi userApi = ApiClientFactory.GetUserApi();


        Call<User> call = userApi.updateUser(user.getId(), user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Location privacy updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update location privacy.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error updating location privacy.", Toast.LENGTH_SHORT).show();
                Log.e("MyFragment", "Error updating location privacy", t);
            }
        });
    }
}