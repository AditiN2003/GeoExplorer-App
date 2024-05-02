package test.connect.geoexploreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.ReportedUserApi;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.ReportedUser;
import test.connect.geoexploreapp.model.User;

public class ReportedUserActivity extends Fragment {

    private TextView noItemsDisplay;
    private ReportedUserAdapter reportedUserAdapter;
    private RecyclerView recyclerView;
    private List<ReportedUser> allReportedUsers = new ArrayList<>();
    private ImageButton searchReportedUser;



    public ReportedUserActivity() {
    }


    public static ReportedUserActivity newInstance() {
        ReportedUserActivity fragment = new ReportedUserActivity();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reported_users, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchReportedUsers();
        recyclerView = view.findViewById(R.id.reportedUsersRecyclerView);
        searchReportedUser = view.findViewById(R.id.searchReportedUser);
        searchReportedUser.setOnClickListener(v -> {
            searchReportedUserPrompt();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noItemsDisplay = view.findViewById(R.id.noItems);

        reportedUserAdapter = new ReportedUserAdapter(allReportedUsers, getActivity());
        recyclerView.setAdapter(reportedUserAdapter);
    }

    private void searchReportedUserPrompt() {
        Context context = getActivity();
        if (context == null) return;
        CharSequence[] options = {"Search by Report ID", "Search by Reported User's ID"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Search Method");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showIdInputPrompt("Report ID", true);
            } else {
                showIdInputPrompt("Report User ID", false);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void getUserById(ReportedUser reportedUser) {
        UserApi userApi = ApiClientFactory.GetUserApi();
        userApi.getUser(reportedUser.getReportedUserId()).enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    showReportedUsersDetails(user, reportedUser);


                    Log.d("getting a user",  "got  user");
                } else{


                    Log.d("getting a user",  "Failed to get user");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("getting a user",  "Failed");
            }
        });

    }
    private void showIdInputPrompt(String title, boolean isReportID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter " + title);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Long id = null;
            try {
                id = Long.parseLong(input.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid " + title + ".", Toast.LENGTH_SHORT).show();
                return;
            }

            if (id != null) {
                if (isReportID) {
                    fetchReportedUserByReportID(id);
                } else {
                    fetchReportedUserByUserID(id);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchReportedUserByReportID(Long id) {
        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.getReportedById(id).enqueue(new Callback<ReportedUser>() {
            @Override
            public void onResponse(Call<ReportedUser> call, Response<ReportedUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReportedUser reportedUser = response.body();
                    Log.d("fetchReportedUserByReportID", "Reported User fetched successfully for ID: " + id + response.body());
                    getUserById(reportedUser);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch reported user details", Toast.LENGTH_SHORT).show();
                    Log.e("fetchReportedUserByReportID", "Failed to fetch reported user. HTTP Status Code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReportedUser> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching reported user details: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("fetchReportedUserByReportID", "API call failed: " + t.getMessage(), t);
            }
        });

    }

    private void fetchReportedUserByUserID(Long id) {
        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.getReported(id).enqueue(new Callback<ReportedUser>() {
            @Override
            public void onResponse(Call<ReportedUser> call, Response<ReportedUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReportedUser reportedUser = response.body();
                    Log.d("fetchReportedUserByUserID", "Reported User fetched successfully for ID: " + id + response.body());
                    getUserById(reportedUser);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch reported user details", Toast.LENGTH_SHORT).show();
                    Log.e("fetchReportedUserByUserID", "Failed to fetch reported user. HTTP Status Code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReportedUser> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching reported user details: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("fetchReportedUserByUserID", "API call failed: " + t.getMessage(), t);
            }
        });

    }

    private void showReportedUsersDetails(User user, ReportedUser reportedUser) {
        Context context = getActivity();
        if (context == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reported Users Details");
        StringBuilder reasons = new StringBuilder();
        if (reportedUser.getHarassment() != null && reportedUser.getHarassment()) {
            reasons.append("Harassment, ");
        }
        if (reportedUser.getMisinformation() != null && reportedUser.getMisinformation()) {
            reasons.append("Misinformation, ");
        }
        if (reportedUser.getSpamming() != null && reportedUser.getSpamming()) {
            reasons.append("Spamming, ");
        }
        if (reportedUser.getInappropriateContent() != null && reportedUser.getInappropriateContent()) {
            reasons.append("Inappropriate Content, ");
        }
        if (reasons.length() > 0) {
            reasons.setLength(reasons.length() - 2);
        }

        String message = "User Name: " + user.getName() + "\nUser Email: " + user.getEmailId() + "\nReasons: " + reasons.toString();
        builder.setMessage(message);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void fetchReportedUsers() {
        Log.d("fetchReportedUsers", "Fetching reports for users ");

        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.ListOfReports().enqueue(new Callback<List<ReportedUser>>() {
            @Override
            public void onResponse(Call<List<ReportedUser>> call, Response<List<ReportedUser>> response) {
                if(response.isSuccessful()&& response.body() != null&&!response.body().isEmpty()){
                    allReportedUsers.clear();
                    allReportedUsers.addAll(response.body());
                    reportedUserAdapter.notifyDataSetChanged();
                    Log.d("fetchReportedUsers", "Fetching reports for users: Successful ");
                    updateUI(allReportedUsers);
                }
            }

            @Override
            public void onFailure(Call<List<ReportedUser>> call, Throwable t) {
                Log.d("fetchReportedUsers", "Fetching reports for users: Failed");
            }
        });

    }

    private void updateUI(List<ReportedUser> allReportedUsers) {
        if (allReportedUsers.isEmpty()) {
            noItemsDisplay.setVisibility(View.VISIBLE);
        } else {
            noItemsDisplay.setVisibility(View.GONE);
        }
    }
}
