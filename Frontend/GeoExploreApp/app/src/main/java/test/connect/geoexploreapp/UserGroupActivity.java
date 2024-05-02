package test.connect.geoexploreapp;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.UserGroupApi;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.model.UserGroup;

public class UserGroupActivity extends Fragment {
    private RecyclerView recyclerView;
    private ImageButton searchUserGroup;

    private UserGroupAdapter adapter;
    private List<UserGroup> userGroups = new ArrayList<>();
    private TextView noUserGroupsText;
    private static Bundle args;
    private User user;
    public static UserGroupActivity newInstance(User user) {
        UserGroupActivity fragment = new UserGroupActivity();
        args = new Bundle();
        args.putSerializable("UserObject", user);

        fragment.setArguments(args);
            return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_groups, container, false);
        recyclerView = view.findViewById(R.id.userGroupsRecyclerView);
        noUserGroupsText = view.findViewById(R.id.noUserGroupsText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("UserObject");
        }
        searchUserGroup=view.findViewById(R.id.searchButton);
        if(user.getRole()== User.Role.ADMIN){
            searchUserGroup.setVisibility(View.VISIBLE);

            searchUserGroup.setOnClickListener(v -> {
                searchUserGroupPrompt();
            });
        }else{
            searchUserGroup.setVisibility(View.GONE);
        }
        fetchUserGroups();
        return view;
    }

    private void searchUserGroupPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter User Group's ID: ");

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
                fetchUserGroupById(id);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchUserGroupById(Long id) {
        UserGroupApi userGroupApi = ApiClientFactory.GetUserGroupApi();
        userGroupApi.getGroupById(id).enqueue(new Callback<UserGroup>() {
            @Override
            public void onResponse(Call<UserGroup> call, Response<UserGroup> response) {
                if (response.isSuccessful()&&response!=null ){
                    UserGroup userGroup = response.body();
                    Log.d("UserGroupFetch", "got user group: " + userGroup.getTitle());
                    showUserGroup(userGroup);

                } else {
                    Log.e("UserGroupFetch", "failed " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserGroup> call, Throwable t) {
                Log.e("UserGroupFetch", "Error", t);

            }
        });
    }

    private void showUserGroup(UserGroup userGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("User Group Details");

        String message = "User Group Name: " + userGroup.getTitle() + "\nUser Group ID: " +userGroup.getId() + "\nNumber of Members: " + userGroup.getMembers().size();
        builder.setMessage(message);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchUserGroups() {
            UserGroupApi userGroupApi = ApiClientFactory.GetUserGroupApi();
            userGroupApi.getAllGroups().enqueue(new Callback<List<UserGroup>>() {
                @Override
                public void onResponse(Call<List<UserGroup>> call, Response<List<UserGroup>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userGroups.clear();
                        userGroups.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        noUserGroupsText.setVisibility(userGroups.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        Log.e("UserGroupActivity", "Failed to fetch user groups: " + response.message());
                        noUserGroupsText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<UserGroup>> call, Throwable t) {
                    Log.e("UserGroupActivity", "Error fetching user groups", t);
                }
            });
            Log.d("user indo", user.getEmailId());
        adapter = new UserGroupAdapter(getContext(), userGroups, user);
        recyclerView.setAdapter(adapter);
    }

}