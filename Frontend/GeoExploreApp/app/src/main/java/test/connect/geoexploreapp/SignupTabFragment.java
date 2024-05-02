package test.connect.geoexploreapp;

import static test.connect.geoexploreapp.api.ApiClientFactory.GetUserApi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.User;

public class SignupTabFragment extends Fragment {
    EditText UserEmail,UserPassword, FirstName, LastName, ConfirmPassword;
    CheckBox IsAdmin;
    Button signinSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirstName=view.findViewById(R.id.firstName);
        LastName=view.findViewById(R.id.lastName);
        UserEmail=view.findViewById(R.id.signup_email);
        UserPassword=view.findViewById(R.id.signup_password);
        ConfirmPassword=view.findViewById(R.id.signup_confirm);
        signinSubmit = view.findViewById(R.id.signup_button);
        IsAdmin = (CheckBox) view.findViewById(R.id.is_admin);
        signinSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = FirstName.getText().toString() + " " + LastName.getText().toString();
                String email = UserEmail.getText().toString().trim();
                String password = UserPassword.getText().toString().trim();
                String confirmPassword = ConfirmPassword.getText().toString().trim();
                boolean isAdmin = IsAdmin.isChecked();
                Log.d("Admin: ", "" + isAdmin);

                if (fullName.isEmpty() ||  email.isEmpty() ||  password.isEmpty() ||  confirmPassword.isEmpty()){
                    showAlert("Please fill out all information!");

                }else if (password.equals(confirmPassword)==false){
                    showAlert("Password does not match confirmPassword!");

                }else {
                    User newUser = new User();
                    newUser.setName(fullName);
                    newUser.setEmailId(email);
                    newUser.setPassword(password);
                    if(isAdmin){
                        newUser.setRole(User.Role.ADMIN);
                    }else{
                        newUser.setRole(User.Role.USER);

                    }

                    createUserIfEmailNotExists(newUser);
                }

            }
        });
    }
    private void showAlert(String message) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setTitle("Alert")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    public void createUserIfEmailNotExists(User newUser){
        Log.d("UserCreation", "Checking if user email exists: " + newUser.getEmailId());

        GetUserApi().getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    boolean emailExists = users.stream().anyMatch(user -> newUser.getEmailId().equalsIgnoreCase(user.getEmailId()));

                    if (!emailExists) {
                        createUser(newUser);
                    } else {
                        Log.d("UserCreation", "Email already exists: " + newUser.getEmailId());
                        showAlert("Email already exists!");
                    }
                } else {
                    Log.e("api fail", "Failed to retrieve users: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("api fail", "Call failed: " + t.getMessage(), t);
            }
        });
    }

    private void createUser(User newUser){
        GetUserApi().UserCreate(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User createdUser = response.body();
                    Log.d("API_SUCCESS", "User created: " + createdUser.toString());
                    startMainActivity(createdUser);
                } else {
                    Log.e("api fail", "Response not successful: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("api fail", "Call failed: " + t.getMessage(), t);
            }
        });
    }
    private void startMainActivity(User newUser) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("UserObject", newUser);
        startActivity(intent);
        getActivity().finish();
    }
}