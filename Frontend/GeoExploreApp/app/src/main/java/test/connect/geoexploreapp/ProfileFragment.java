package test.connect.geoexploreapp;

import static org.json.JSONObject.NULL;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.ImageApi;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ActivityResultLauncher<String> mFilePickerLauncher;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private static Bundle args;
    private String mParam2;
    private static User user;
    private Uri selectedUri;
    private Button uploadProfileImg;
    private Boolean uploaded=false;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User user ) {
        ProfileFragment fragment = new ProfileFragment();
        args = new Bundle();
        args.putSerializable("UserObject", user);
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
        mFilePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedUri = uri;
                uploadProfileImg.setText("Image selected: " + uri.getLastPathSegment());
                Log.d("File URI", "Selected File URI: " + uri.toString());
            } else {
                selectedUri = null;
                Log.e("File Picker", "No file was selected or an error occurred.");
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (getArguments() != null) {

            user = (User) getArguments().getSerializable("UserObject");

            Log.d("ProfileActivity", "isAdmin: " + (user.getRole()== User.Role.ADMIN));

            TextView userNameDisplay = view.findViewById(R.id.userNameDisplay);
            TextView userEmailDisplay = view.findViewById(R.id.userEmailDisplay);
            userNameDisplay.setText(user.getName());
            userEmailDisplay.setText(user.getEmailId());
            ImageView star = view.findViewById(R.id.star);
            ImageView banned = view.findViewById(R.id.banned);
            star.setVisibility(view.GONE);
            banned.setVisibility(view.GONE);

            if(user.getRole()== User.Role.ADMIN){
                star.setVisibility(view.VISIBLE);
                banned.setVisibility(view.GONE);
            }
            if(user.getRole()== User.Role.BANNED){
                star.setVisibility(view.GONE);
                banned.setVisibility(view.VISIBLE);
            }
        }

        Button buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(v -> showChangePasswordDialog(v.getContext()));

         uploadProfileImg = view.findViewById(R.id.uploadImageProfile);
        uploadProfileImg.setOnClickListener(v -> {
            UploadImagePrompt();

        });
        fetchAndSetProfileImage();
        return view;

    }

    private void UpdateImagePrompt() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_image, null);

        uploadProfileImg = view.findViewById(R.id.uploadImage);
        uploadProfileImg.setOnClickListener(v -> openFileExplorer());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Do you want to add a profile picture?")
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedUri!=NULL) {

                            uploadProfImage(selectedUri);
                        }else{
                            Toast.makeText(getActivity(), "No Uri Selected", Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });



        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void UploadImagePrompt() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_image, null);

        uploadProfileImg = view.findViewById(R.id.uploadImage);
        uploadProfileImg.setOnClickListener(v -> openFileExplorer());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Do you want to add a profile picture?")
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedUri!=NULL) {

                            uploadProfImage(selectedUri);
                        }else{
                            Toast.makeText(getActivity(), "No Uri Selected", Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });



        android.app.AlertDialog dialog = builder.create();
        dialog.show();

    }



    private void uploadProfImage(Uri selectedUri) {
        Log.d("UploadImage", "Attempting to upload image. URI: " + selectedUri);

        String filePath = FileUtils.createCopyFromUri(getContext(), selectedUri);

        if (filePath == null) {
            Log.e("UploadImage", "Failed to get file path from URI");
            return;
        }

        File file = new File(filePath);
        Log.d("UploadImage", "Starting upload for file: " + filePath);

        // Check if file is too large
        long fileSizeInMB = file.length() / (1024 * 1024);
        Log.e("UploadImage", "File size (" + fileSizeInMB + "MB)");

        if (fileSizeInMB > 1) {
            Log.e("UploadImage", "File size (" + fileSizeInMB + "MB) exceeds the maximum limit.");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.observationFileUpload(body, user.getId(), "PROFILE").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("UploadImage", "Success: " + response.body());
                    uploadProfileImg.setText("Update Image");
                    uploaded=true;
                    fetchAndSetProfileImage();
                } else {
                    Log.e("UploadImage", "Upload failed with response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("UploadImage", "Upload failed with error: " + errorBody);
                    } catch (IOException e) {
                        Log.e("UploadImage", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UploadImage", "Upload failed", t);
            }
        });
    }

    private void fetchAndSetProfileImage() {
        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.getImageByUserId(user.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    ImageView profileImage = getView().findViewById(R.id.profilePic);
                    profileImage.setImageBitmap(bitmap);
                    uploadProfileImg.setEnabled(FALSE);
                    uploadProfileImg.setText("Uploaded");

                } else {
                    Log.e("ProfileFragment", "Failed to load image");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ProfileFragment", "Error fetching image", t);
            }
        });
    }
    private void openFileExplorer() {
        mFilePickerLauncher.launch("image/*");
    }

    private void showChangePasswordDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Change Password");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText newPasswordInput = new EditText(context);
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setHint("New password");

        final EditText confirmPasswordInput = new EditText(context);
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setHint("Confirm password");

        layout.addView(newPasswordInput);
        layout.addView(confirmPasswordInput);

        builder.setView(layout);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String newPassword = newPasswordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }else{
                updatePassword(newPassword);
            }

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePassword(String newPassword) {
        user.setPassword(newPassword);
        UserApi userApi = ApiClientFactory.GetUserApi();

        userApi.updateUser(user.getId(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User userUpdates = response.body();
                    Toast.makeText(getContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Toast.makeText(getContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();

                        Log.e("UpdatePassword", "Failed to update password. Response: " + response.errorBody().string());
//                        Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("UpdatePassword", "Error reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UpdatePassword", "API call failed: ", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }


}