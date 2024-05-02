package test.connect.geoexploreapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.CommentApi;
import test.connect.geoexploreapp.api.EventMarkerApi;
import test.connect.geoexploreapp.api.ImageApi;
import test.connect.geoexploreapp.api.ObservationApi;
import test.connect.geoexploreapp.api.ReportMarkerApi;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.Comment;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.FeedItem;
import test.connect.geoexploreapp.model.Image;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.ReportMarker;
import test.connect.geoexploreapp.model.User;

public class FeedActivity extends Fragment implements FeedAdapter.OnShowAllImagesListener{
    private RecyclerView recyclerView;
    private TextView noItemsDisplay;
    private ImageButton viewAllCommentsButton, viewAllImagesButton;
    private ImageButton searchComment, searchImage;
    private List<FeedItem> allItems = new ArrayList<>();
    private List<Image> allImages = new ArrayList<>();
    private FeedAdapter adapter;
    private static User user;
    private List<Comment> allComments = new ArrayList<>();

    private static Bundle args;
    public FeedActivity() {
    }


    public static FeedActivity newInstance(User user) {
        FeedActivity fragment = new FeedActivity();
        args = new Bundle();
        args.putSerializable("UserObject", user);
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
        View view = inflater.inflate(R.layout.activity_feed, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFeedItems();
        fetchAllImages();
        viewAllCommentsButton=view.findViewById(R.id.viewAllComments);
        viewAllImagesButton =view.findViewById(R.id.viewAllImagesButton);
        searchComment=view.findViewById(R.id.searchComment);
        searchImage = view.findViewById(R.id.searchImage);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("UserObject");
        }

        if(user.getRole()== User.Role.ADMIN){
            viewAllCommentsButton.setVisibility(View.VISIBLE);
            searchComment.setVisibility(View.VISIBLE);
            searchImage.setVisibility(View.VISIBLE);
            viewAllCommentsButton.setOnClickListener(v -> {
                fetchAllComments();
            });
            viewAllImagesButton.setOnClickListener(v -> {
                if (adapter.listener != null) {
                    adapter.listener.onShowAllImages(allImages);
                }
            });
            searchComment.setOnClickListener(v -> {
                searchCommentPrompt();
            });
            searchImage.setOnClickListener(v->{
                searchImagePrompt();
            });
        }else{
            viewAllCommentsButton.setVisibility(View.GONE);
            searchComment.setVisibility(View.GONE);
            searchImage.setVisibility(View.GONE);
            viewAllImagesButton.setVisibility(View.GONE);
        }

         noItemsDisplay = view.findViewById(R.id.noItems);
         recyclerView = view.findViewById(R.id.recyclerViewFeed);

         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                adapter.setSelectedUri(uri);
                Toast.makeText(getContext(), "Image selected.", Toast.LENGTH_SHORT).show();

                Log.d("File URI", "Selected File URI: " + uri.toString());
            }
        });
         adapter = new FeedAdapter(allItems,allImages, user, getActivity(), mGetContent);
        adapter.setOnShowAllImagesListener(this);

        recyclerView.setAdapter(adapter);



//        getFeedItems();
    }

    private void searchImagePrompt() {
        CharSequence[] options = {"Search by Image ID", "Search by Observation Post ID"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Search Method");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showIdInputPrompt("Image ID", true);
            } else {
                showIdInputPrompt("Post ID", false);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showIdInputPrompt(String title, boolean isImageId) {
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
                if (isImageId) {
                    fetchImageById(id, isImageId);
                } else {
                    fetchImageByPostId(id, isImageId);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchImageByPostId(Long postId, boolean isImageId) {
        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.getImageByPostId(postId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchImage", "Image fetched successfully for Post ID: " + postId);
                //  Image img =  response.body();
                    displayImage(response.body(), isImageId, postId);
                } else {
                    Log.e("SearchImage", "Failed to fetch image. HTTP Status Code: " + response.code() + " Message: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("SearchImage", "Error response body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("SearchImage", "Error parsing error body", e);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SearchImage", "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayImage(ResponseBody body, boolean isImageId, Long id) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(body.byteStream());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.show_image,null);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bmp);
            TextView descriptionView = view.findViewById(R.id.image_description);
            imageView.setImageBitmap(bmp);
            if(isImageId){
                descriptionView.setText("Image retrieved for image id: " + id);
            }else{
                descriptionView.setText("Image retrieved for post id: " + id);
            }
            descriptionView.setText("");
            new AlertDialog.Builder(getContext())
                    .setTitle("Found the Image")
                    .setView(imageView)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .show();
        } catch (Exception e) {
            Log.e("DisplayImage", "Error displaying image", e);
        }
    }


    private void fetchImageById(Long id, boolean isImageId) {
        ImageApi imageApi = ApiClientFactory.GetImageApi();
        Log.d("SearchImage", "Starting to fetch image with ID: " + id);
        imageApi.getImageById(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchImage", "Image fetched successfully for ID: " + id);
                    displayImage(response.body(), isImageId, id);
                } else {
                    Log.e("SearchImage", "Failed to fetch image. HTTP Status Code: " + response.code() + " Message: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("SearchImage", "Error response body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("SearchImage", "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SearchImage", "API call failed: " + t.getMessage(), t);
            }
        });
    }


    private void searchCommentPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Comment's ID: ");

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
                fetchCommentById(id);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchCommentById(Long id) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        Log.d("fetchCommentById", "Fetching comment with ID: " + id);
        commentApi.getComment(id).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.isSuccessful() &&response!=null){
                    Comment comment = response.body();
                    Log.d("fetchCommentById", "Fetch successful. Comment: " + comment.toString());

                    getUserById(comment, comment.getUserId());
                }else{


                    if (response.errorBody() != null) {
                        try {
                            Log.e("fetchCommentById", "Error fetching comment: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("fetchCommentById", "Error parsing error body", e);
                        }
                    } else {
                        Log.e("fetchCommentById", "Unsuccessful fetch, but no error body.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("fetchCommentById", "Fetch failed", t);

            }
        });
    }

    private void getUserById(Comment comment, Long userId) {
        UserApi userApi = ApiClientFactory.GetUserApi();
        userApi.getUser(userId).enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    showCommentDetails(comment, user);

                    Log.d("getting a user",  "got  user");
                } else{
                    Log.d("getting a user",  "Failed to get user");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("getting a user",  "Failed");
            }
        });}


    private void showCommentDetails(Comment comment, User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Comment Details");

        String message = "Comment: " + comment.getComment() + "\nUser name: " + user.getName() + "\nPost Id: " + comment.getPostid() + "\nPost Type: " + comment.getPostType();
        builder.setMessage(message);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void showAllCommentsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View popupView = getLayoutInflater().inflate(R.layout.all_comments, null);
        RecyclerView commentsRecyclerView = popupView.findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CommentAdapter adapter = new CommentAdapter(allComments, user, null, false);
        commentsRecyclerView.setAdapter(adapter);

        builder.setView(popupView)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void fetchAllComments() {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.getAllComments().enqueue(new Callback<List<Comment>>(){
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allComments.clear();
                    allComments.addAll(response.body());

                    showAllCommentsPopup();
                } else {
                    Log.e("fetchAllComments", "Failed to fetch comments: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e("fetchAllComments", "API call failed: " + t.getMessage());
            }
        });
    }

    private void fetchAllImages() {
        Log.d("fetchAllImages", "Fetching images from the server.");

        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.listImageEntities().enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("fetchAllImages", "Images fetched successfully.");
                    allImages.clear();
                    allImages.addAll(response.body());
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("fetchAllImages", "Failed to fetch images: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.e("fetchAllImages", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    private void getFeedItems() {
        allItems.clear();
        fetchReports();
        fetchEvents();
        fetchObservations();
    }

    private void fetchReports() {
        ReportMarkerApi reportMarkerApi = ApiClientFactory.getReportMarkerApi();
        reportMarkerApi.GetAllReportMarker().enqueue(new SlimCallback<>(reportMarkers -> {
            for (ReportMarker reportMarker : reportMarkers) {
                allItems.add(reportMarker);
                fetchCommentsForReport(reportMarker);
            }

            updateUI(adapter, allItems);
        }, "GetAllReports"));
    }

    private void fetchCommentsForReport(ReportMarker reportMarker) {
        Log.d("comment fetching", " fetching for " + reportMarker.getId());
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.getCommentsForReports(reportMarker.getId()).enqueue(new SlimCallback<>(comments -> {
            reportMarker.setComments(comments);
            Log.d("FeedActivity", "Fetched Comments for Report " + reportMarker.getId() + ": " + comments);


            updateUI(adapter, allItems);

        }, "GetCommentsForReport"));
    }

    private void fetchEvents() {
        EventMarkerApi eventMarkerApi = ApiClientFactory.getEventMarkerApi();

        // Fetch EventMarkers
        eventMarkerApi.GetAllEventMarker().enqueue(new SlimCallback<>(eventMarkers -> {
            for (EventMarker eventMarker : eventMarkers) {
                allItems.add(eventMarker);
                fetchCommentsForEvent(eventMarker);
            }
            updateUI(adapter, allItems);
        }, "GetAllEvents"));
    }

    private void fetchCommentsForEvent(EventMarker eventMarker) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.getCommentsForEvents(eventMarker.getId()).enqueue(new SlimCallback<>(comments -> {
            eventMarker.setComments(comments);
            updateUI(adapter, allItems);

        }, "GetCommentsForEvent"));
    }

    private void fetchObservations() {
        ObservationApi observationApi = ApiClientFactory.GetObservationApi();
        observationApi.getAllObs().enqueue(new SlimCallback<>(observations -> {
            for (Observation obs : observations) {
                allItems.add(obs);
                fetchCommentsForObservation(obs);
            }
            updateUI(adapter, allItems);
        }, "GetAllObservations"));
    }

    private void fetchCommentsForObservation(Observation obs) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();

        commentApi.getCommentsForObs(obs.getId()).enqueue(new SlimCallback<>(comments -> {
            obs.setComments(comments);
            updateUI(adapter, allItems);

        }, "GetCommentsForObs"));
    }

    private void updateUI(FeedAdapter adapter, List<FeedItem> allItems) {
        adapter.setItems(allItems);
        adapter.notifyDataSetChanged();

        if (allItems.isEmpty()) {
            noItemsDisplay.setVisibility(View.VISIBLE);
        } else {
            noItemsDisplay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShowAllImages(List<Image> images) {
        displayAllImagesDialog(images);
    }

    private void displayAllImagesDialog(List<Image> images) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.layout_for_all_images, null);
        RecyclerView recyclerView = view.findViewById(R.id.imagesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageAdapter imageAdapter = new ImageAdapter(getContext(), images);
        recyclerView.setAdapter(imageAdapter);

        builder.setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
