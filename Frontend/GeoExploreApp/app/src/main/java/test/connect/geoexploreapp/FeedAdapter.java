package test.connect.geoexploreapp;
import androidx.activity.result.ActivityResultLauncher;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static org.json.JSONObject.NULL;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.CommentApi;
import test.connect.geoexploreapp.api.ImageApi;
import test.connect.geoexploreapp.api.SlimCallback;
import test.connect.geoexploreapp.model.Comment;
import test.connect.geoexploreapp.model.EventMarker;
import test.connect.geoexploreapp.model.FeedItem;
import test.connect.geoexploreapp.model.Image;
import test.connect.geoexploreapp.model.Observation;
import test.connect.geoexploreapp.model.ReportMarker;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.websocket.CommentWebSocketManager;
import test.connect.geoexploreapp.websocket.WebSocketListener;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> implements WebSocketListener, CommentActionListener{
    private List<FeedItem> items;
    private List<Image> allImages;
    private EditText commentEditText;
    private Button cancelCommentButton, uploadImageObservation, sendCommentButton;
    private User user;
    private  FeedItem feedItem;
    private Context context;
    private Uri selectedUri;
    private ActivityResultLauncher<String> mFilePickerLauncher;


    public FeedAdapter(List<FeedItem> items, List<Image> allImages, User user, Context context,  ActivityResultLauncher<String> mFilePickerLauncher) {
        this.items = items;
        this.user=user;
        this.context = context;
        this.allImages = allImages;
        this.mFilePickerLauncher = mFilePickerLauncher;
        CommentWebSocketManager.getInstance().setWebSocketListener(this);


    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);

        return new FeedViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        feedItem = items.get(position);

        holder.obsImage.setVisibility(View.GONE);
        holder.deleteImage.setVisibility(View.GONE);
        holder.updateImage.setVisibility(View.GONE);

        if (feedItem.getType().equals("Observation")) {
            fetchImageByPostId(holder, feedItem.getPostID());
        }
        if(feedItem.getType().equals("Observation")){
            for(int i = 0; i< allImages.size(); i++){
                if(allImages.get(i).getObservation()!=null){
                    if (allImages.get(i).getObservation().getId()==feedItem.getPostID()){
                        Image imgToShow = allImages.get(i);

                        if(allImages.get(i).getObservation().getCreator().getId()==user.getId()){//image owened by user
                            holder.deleteImage.setVisibility(View.VISIBLE);
                            holder.updateImage.setVisibility(View.VISIBLE);

                            holder.deleteImage.setOnClickListener(v -> {
                                deleteImagePrompt(v, imgToShow, position);
                            });
                            holder.updateImage.setOnClickListener(v -> {
                                UpdateImagePrompt(imgToShow, position, feedItem.getPostID());
                            });
                        }

                        break;
                    }
                }

            }

        }

        if (feedItem.getDescription() == null || feedItem.getDescription().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(feedItem.getDescription());
        }

        holder.title.setText(feedItem.getTitle());
        holder.type.setText(feedItem.getType());
        holder.date.setText(feedItem.getTime_created().toString());
        getLocation(holder, feedItem.getIo_latitude(),  feedItem.getIo_longitude());

        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        List<Comment> commentsForThisItem = feedItem.getComments();
        CommentAdapter commentAdapter = new CommentAdapter(commentsForThisItem, user, this, true);
        holder.commentsRecyclerView.setAdapter(commentAdapter);
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComments(holder.commentButton.getContext(),position);
                Log.d("item check", String.valueOf(position));
            }
        });


    }
    private void fetchImageByPostId(FeedViewHolder holder, Long postId) {
        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.getImageByPostId(postId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Read the InputStream from ResponseBody
                        InputStream inputStream = response.body().byteStream();
                        // Convert InputStream to Bitmap
                        Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                        if (bmp != null) {
                            holder.obsImage.setImageBitmap(bmp);
                            holder.obsImage.setVisibility(View.VISIBLE);
                            Log.d("SearchImage", "Image displayed successfully for Post ID: " + postId);
                        } else {
                            Log.e("SearchImage", "No image data found for Post ID: " + postId);
                            holder.obsImage.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Log.e("SearchImage", "Error processing image data", e);
                    }
                } else {
                    Log.e("SearchImage", "Failed to fetch image. HTTP Status Code: " + response.code() + " Message: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("SearchImage", "Error response body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e("SearchImage", "Error parsing error body", e);
                    }
                    holder.obsImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("SearchImage", "API call failed: " + t.getMessage());
                holder.obsImage.setVisibility(View.GONE);
            }
        });
    }

    private void UpdateImagePrompt(Image imgToShow, int position, Long postID) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_image, null);

        uploadImageObservation = view.findViewById(R.id.uploadImage);
        uploadImageObservation.setOnClickListener(v -> openFileExplorer());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view)
                .setTitle("Do you want to update the image?")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(selectedUri!=NULL) {

                            uploadImageObservation.setText("Image selected: "+ selectedUri.getLastPathSegment());

                            UpdateImage(selectedUri, imgToShow, position, postID);
                        }else{
                            Toast.makeText(context, "No Uri Selected", Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });



        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public interface OnShowAllImagesListener {
        void onShowAllImages(List<Image> images);
    }

    OnShowAllImagesListener listener;

    public void setOnShowAllImagesListener(OnShowAllImagesListener listener) {
        this.listener = listener;
    }

    private void UpdateImage(Uri selectedUri, Image imgToShow, int position, Long postID) {

        String filePath = FileUtils.createCopyFromUri(context, selectedUri);
        if (filePath == null) {
            Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ImageApi imageApi = ApiClientFactory.GetImageApi();
        imageApi.imageUpdate(body, imgToShow.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    notifyDataSetChanged();
                    Log.d("Image Update", "Image updated successfully");
                    Toast.makeText(context, "Image updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Image Update", "Failed to update image: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Image Update", "Error updating image", t);
            }
        });
    }

    public void openFileExplorer() {
        if (mFilePickerLauncher != null) {
            mFilePickerLauncher.launch("image/*");
        }

    }

    private void deleteImagePrompt(View v, Image imageToDelete, int adapterPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImage(imageToDelete.getId(), adapterPosition);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteImage(Long id, int adapterPosition) {
        ImageApi imageApi = ApiClientFactory.GetImageApi(); //
        imageApi.deleteImage(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                    allImages.removeIf(img -> img.getId().equals(id));
                    notifyItemRemoved(adapterPosition);
                    Log.d("FeedAdapter", "Image deletion succeeded for ID: " + id);
                } else {
                    Toast.makeText(context, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    Log.d("FeedAdapter", "Image deletion failed with HTTP status code: " + response.code());
                    try {
                        Log.d("FeedAdapter", "Failed response body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("FeedAdapter", "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FeedAdapter", "Image deletion failed", t);
            }
        });
    }



    private void getLocation(FeedViewHolder holder, double ioLatitude, double ioLongitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(ioLatitude, ioLongitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                holder.location.setText("Address: " + addressText);
            } else {
                holder.location.setText("Address not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            holder.location.setText("Error fetching address");
        }
    }

    private void showComments(Context context, int itemIndex) {
        Gson gson = new Gson();
        FeedItem item = items.get(itemIndex);
        if (user.getRole() == User.Role.BANNED) {
            Toast.makeText(context, "You are banned from commenting.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialog = inflater.inflate(R.layout.activity_comment, null);

        commentEditText = dialog.findViewById(R.id.editTextComment);
        sendCommentButton = dialog.findViewById(R.id.buttonSend);
        cancelCommentButton = dialog.findViewById(R.id.buttonCancel);

        builder.setView(dialog).setTitle("Comments");
        AlertDialog dialogView = builder.create();

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentEditText.getText().toString().trim();
                if(!commentText.isEmpty()) {
                    try {
                       Comment sendComment = new Comment();
                        sendComment.setComment(commentText);
                        sendComment.setPostId(item.getPostID());
                        sendComment.setUserId(user.getId());
                        sendComment.setPostType(item.getType());
                        String sendCommentJson = gson.toJson(sendComment);
                        Log.d("WebSocket", "Message sent testing: " + sendCommentJson);

                        CommentWebSocketManager.getInstance().sendMessage(sendCommentJson);
                        Log.d("WebSocket", "Message sent: " + sendCommentJson);
                        fetchComments(itemIndex, item, item.getType());
//                      item.getComments().add(sendComment);
                        notifyItemChanged(itemIndex);
                    } catch (Exception e) {
                        Log.e("WebSocket", "Error sending message: " + e.getMessage());
                    }
                }
            }
        });

        cancelCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialogView.dismiss();

            }
        });

        dialogView.show();
    }

    private void fetchComments(int itemIndex, FeedItem item, String type) {
        if(type.equals("Report")){
            ReportMarker rep = (ReportMarker) item;
            fetchCommentsForReport(itemIndex, rep);

        }else if(type.equals("Observation")){
            Observation obs = (Observation) item;
            fetchCommentsForObservation(itemIndex, obs);

        }else if(type.equals("Event")){
            EventMarker event = (EventMarker) item;
            fetchCommentsForEvent(itemIndex, event);

        }
    }

    private void fetchCommentsForEvent(int itemIndex, EventMarker event) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.getCommentsForEvents(event.getId()).enqueue(new SlimCallback<>(comments -> {
            event.setComments(comments);
            notifyItemChanged(itemIndex);

        }, "GetCommentsForEvent"));
    }

    private void fetchCommentsForObservation(int itemIndex, Observation obs) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();

        commentApi.getCommentsForObs(obs.getId()).enqueue(new SlimCallback<>(comments -> {
            obs.setComments(comments);
            notifyItemChanged(itemIndex);

        }, "GetCommentsForObs"));
    }

    private void fetchCommentsForReport(int itemIndex, ReportMarker rep) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.getCommentsForReports(rep.getId()).enqueue(new SlimCallback<>(comments -> {
            rep.setComments(comments);
            Log.d("FeedActivity", "Fetched Comments: " + comments.toString());
            notifyItemChanged(itemIndex);

        }, "GetCommentsForReport"));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<FeedItem> allItems) {
        items=allItems;
    }

    public List<FeedItem> getItems() {
        return items;
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connection opened");

    }

    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Received message: " + message);
        Activity activity = (Activity) context;

        if (message != null && !message.isEmpty()) {
            try {
                Comment receivedComment = new Gson().fromJson(message, Comment.class);

                activity.runOnUiThread(() -> {
                    setCommment(receivedComment);
                });
            } catch (JsonSyntaxException e) {
                Log.e("WebSocket", "Error parsing message to Comment object", e);
            }
        }
    }

    private void setCommment(Comment receivedComment) {
        Log.d("trying to set comment", "pks wor");
        for (FeedItem item : items) {
            if (item.getPostID().equals(receivedComment.getPostid()) && item.getType().equals(receivedComment.getPostType())) {
                item.getComments().add(receivedComment);
                notifyDataSetChanged();
                break;
            }
        }
    }


    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket Close", "Code: " + code + ", reason: " + reason);
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket Error", ex.toString());
    }


    @Override
    public void onEditComment(Comment updatedComment, String newCommentText, int position) {
        Log.d("checking if edit works",updatedComment.toString() );
        updatedComment.setComment(newCommentText);
//
        Gson gson = new Gson();
        String updatedCommentJson = gson.toJson(updatedComment);
        Log.d("UpdateComment", "Updated comment JSON: " + updatedCommentJson);
        updateCommentInBackend( updatedComment, position);

    }

    private void updateCommentInBackend(Comment updatedComment, int position) {
        CommentApi commentApi = ApiClientFactory.GetCommentApi();
        commentApi.updateComment(updatedComment.getId(), updatedComment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Comment updatedComment = response.body();
                    Log.d("UpdateComment", "Comment updated successfully: " + updatedComment.getComment());

                    for (FeedItem item : items) {
                        if (item.getPostID().equals(updatedComment.getPostid()) && item.getType().equals(updatedComment.getPostType())) {
                           List<Comment> comm= item.getComments();
                           Log.d("ddddd", "got comments");
                            for (Comment comment : comm) {
                                if (comment.getId().equals(updatedComment.getId())) {
                                    comment.setComment(updatedComment.getComment());
                                    notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }

                   Toast.makeText(context, "Comment updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("UpdateComment", "Failed to update comment. Response Code: " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("UpdateComment", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("UpdateComment", "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(context, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UpdateComment", "Error updating comment", t.getCause());
            }
        });
    }

    public void setSelectedUri(Uri uri) {
        this.selectedUri = uri;
    }


    static class FeedViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView commentsRecyclerView;
        TextView title, description, type, date, location;
        ImageButton commentButton, updateImage, deleteImage;
        ImageView obsImage;

        FeedViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            description = itemView.findViewById(R.id.itemDescription);
            type = itemView.findViewById(R.id.itemType);
            date = itemView.findViewById(R.id.itemDate);
            location = itemView.findViewById(R.id.itemLocation);
            commentButton = itemView.findViewById(R.id.commentButton);
            commentsRecyclerView = itemView.findViewById(R.id.commentsRecyclerView);
            obsImage = itemView.findViewById(R.id.itemImage);
            updateImage = itemView.findViewById(R.id.imageUpdate);
            deleteImage = itemView.findViewById(R.id.imageDelete);

        }
    }
}
