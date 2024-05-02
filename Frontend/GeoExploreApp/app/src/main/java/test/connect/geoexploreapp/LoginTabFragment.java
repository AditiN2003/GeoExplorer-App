package test.connect.geoexploreapp;

import static android.content.Context.MODE_PRIVATE;
import static test.connect.geoexploreapp.api.ApiClientFactory.GetUserApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.util.List;

import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.model.User;


public class LoginTabFragment extends Fragment {
    EditText UserEmail,UserPassword;
    Button loginSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_tab, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserEmail=view.findViewById(R.id.login_email);
        UserPassword=view.findViewById(R.id.login_password);
         loginSubmit = view.findViewById(R.id.login_button);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UserEmail.getText().toString().toLowerCase().trim();
                String passcode = UserPassword.getText().toString();


                isValidCredentials(email, passcode, new CredentialsCallback() {
                    @Override
                    public void onResult(boolean isValid) {
                        if (!isValid) {
                            showAlert("Invalid Credentials!");
                        }
                    }
                });
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

    private void isValidCredentials(String email, String password, CredentialsCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onResult(false);
            return;
        }

        GetUserApi().getAllUsers().enqueue(new SlimCallback<List<User>>(users->{

            for(int i = 0; i<users.size(); i++){
                User temp = users.get(i);
                Log.d("LoginCheck", "Checking user: " + temp.getEmailId() + ", " + temp.getPassword());
                boolean emailMatch = email != null && email.equals(temp.getEmailId());
                boolean passwordMatch = password != null && password.equals(temp.getPassword());
                if (emailMatch && passwordMatch) {
                    Log.d("LoginCheck", "Match found");
                    startMainActivity(temp);
                    callback.onResult(true);
                    return;
                }
            }
            Log.d("LoginCheck", "No match found");
            callback.onResult(false);

        }, "getAllUsers"));
    }
    private void startMainActivity(User newUser) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("UserObject", newUser);
        startActivity(intent);
        getActivity().finish();
    }
}

