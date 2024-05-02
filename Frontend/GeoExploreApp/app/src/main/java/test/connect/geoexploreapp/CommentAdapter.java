package test.connect.geoexploreapp;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.CommentApi;
import test.connect.geoexploreapp.api.ReportedUserApi;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.Comment;
import test.connect.geoexploreapp.model.FeedItem;
import test.connect.geoexploreapp.model.ReportedUser;
import test.connect.geoexploreapp.model.User;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private User user;
    private CommentActionListener listener;
    private boolean showFeatures;

    public CommentAdapter(List<Comment> comments, User user, CommentActionListener listener, boolean showFeatures) {
        this.comments = comments;
       this.user = user;
        this.listener = listener;
        this.showFeatures = showFeatures;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment item = comments.get(position);
        Log.d("getting user", "getting user for" + item.getUserId());
         getUserById(holder, item.getUserId());;

       // holder.commentUser.setText(commentUser.getName());
        holder.comment.setText(item.getComment());

        boolean isUserCommenter = item.getUserId().equals(user.getId());
        boolean isAdmin =  user.getRole()== User.Role.ADMIN;

        holder.reportButton.setVisibility(!isUserCommenter && showFeatures ? View.VISIBLE : View.GONE);
        holder.editButton.setVisibility(isUserCommenter && showFeatures? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility((isUserCommenter || isAdmin ) &&showFeatures ? View.VISIBLE : View.GONE);
        holder.commentPostType.setVisibility(!showFeatures?View.VISIBLE : View.GONE);
        holder.commentPostType.setText("Comment made for a " + item.getPostType());
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION) {
                    editCommentPrompt(v.getContext(), comments.get(pos), position);
                }
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                int pos = holder.getAdapterPosition();
                Log.d("delete test", String.valueOf(pos));
                if(pos != RecyclerView.NO_POSITION) {
                    deleteCommentPrompt(v.getContext(), pos);
                }
            }
        });

        holder.reportButton.setOnClickListener(v -> {
            User taggedUser = (User) holder.commentUser.getTag();
            if (listener != null&&taggedUser!=null) {
                int pos = holder.getAdapterPosition();
                Log.d("report test", String.valueOf(pos));
                if(pos != RecyclerView.NO_POSITION) {
                    reportCommentPrompt(v.getContext(),taggedUser, pos);
                }
            }
        });

    }

    private void reportCommentPrompt(Context context, User commentUser, int position) {
        Comment comment = comments.get(position);
       // Log.d("testtttttttttttttt",commentUser.getName() );
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report User");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_report_user, null);
        CheckBox harassmentCheck = dialogView.findViewById(R.id.harassment);
        CheckBox missingInformationCheck = dialogView.findViewById(R.id.missingInformation);
        CheckBox spammingCheck = dialogView.findViewById(R.id.spamming);
        CheckBox inappropriateContentCheck = dialogView.findViewById(R.id.inappropriateContent);
        builder.setView(dialogView);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReportedUser reportedUser = new ReportedUser();

                reportedUser.setReportedUserId(comment.getUserId());
                reportedUser.setHarassment(harassmentCheck.isChecked());
                reportedUser.setSpamming(spammingCheck.isChecked());
                reportedUser.setMisinformation(missingInformationCheck.isChecked());
                reportedUser.setInappropriateContent(inappropriateContentCheck.isChecked());
                createReport(context,reportedUser);
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



    private void createReport(Context context, ReportedUser reportedUser) {

        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.ReportUser(reportedUser).enqueue(new Callback<ReportedUser>() {
            @Override
            public void onResponse(Call<ReportedUser> call, Response<ReportedUser> response) {
                if (response.isSuccessful()&&response!=null) {
                    ReportedUser reportedUser = response.body();
                    String successMessage = "Report created successfully with ID: " + reportedUser.getId();
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        Log.e("ReportUser", "Failed to create report: " + response.code() + " - " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportedUser> call, Throwable t) {
                Toast.makeText(context, "Error creating report: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserById(@NonNull CommentViewHolder holder, Long userId) {
        UserApi userApi = ApiClientFactory.GetUserApi();
        userApi.getUser(userId).enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    holder.commentUser.setText(user.getName());
                    holder.commentUser.setTag(user);
                    Log.d("getting a user",  "got  user");
                } else{
                    holder.commentUser.setText("Anonymous");

                    Log.d("getting a user",  "Failed to get user");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("getting a user",  "Failed");
            }
        });

    }

    private void deleteCommentPrompt(Context context, int position) {
        Comment commentDel = comments.get(position);
        Log.d("delete", commentDel.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    onDeleteComment(context, commentDel.getId(), position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();


    }

    private void onDeleteComment(Context context, Long id, int position) {
            CommentApi commentApi = ApiClientFactory.GetCommentApi();
            Log.d("checkkk",id.toString());
            // Toast.makeText(context, "Fai delete comment", Toast.LENGTH_SHORT).show();

            commentApi.deleteComment(id).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //  JsonReader.setLenient(true);
                    Log.d("DeleteComment",  response.body().toString());

                    if (response.isSuccessful()) {
                        ResponseBody responseMessage = response.body();
                        comments.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, comments.size());
                        Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Toast.makeText(context, "Failed to delete comment: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(context, "Error parsing error body", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("DeleteComment", "failedt: " + t.getMessage());

                }
            });
        }


    private void editCommentPrompt(Context context,  Comment comment, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(comment.getComment());
        builder.setTitle("Edit Comment")
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String editedCommentText = input.getText().toString();
                    if(editedCommentText.length()!=0) {
                      //  edit();
                        listener.onEditComment(comment, editedCommentText, position);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();



    }


    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setItems(List<Comment> allItems) {
        comments=allItems;
    }

    public List<Comment> getItems() {
        return comments;
    }



    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUser, comment, commentPostType;
        ImageButton editButton, deleteButton, reportButton;
        CommentViewHolder(View itemView) {
            super(itemView);
            commentUser = itemView.findViewById(R.id.commentUser);
            comment = itemView.findViewById(R.id.comment);
            editButton = itemView.findViewById(R.id.commentEdit); 
            deleteButton = itemView.findViewById(R.id.commentDelete);
            reportButton = itemView.findViewById(R.id.commentReport);
            commentPostType = itemView.findViewById(R.id.commentPostType);

        }
    }
}
