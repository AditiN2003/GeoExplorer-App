package test.connect.geoexploreapp;

import static android.app.PendingIntent.getActivity;
import static android.view.View.GONE;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.ReportedUserApi;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.model.ReportedUser;
import test.connect.geoexploreapp.model.User;

public class ReportedUserAdapter extends RecyclerView.Adapter<ReportedUserAdapter.ReportedUsersViewHolder> {
    private List<ReportedUser> allReportedUsers;
    private Context context;

    public ReportedUserAdapter(List<ReportedUser> allReportedUsers, Context context) {
        this.allReportedUsers = allReportedUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reported_user_item, parent, false);

        return new ReportedUsersViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReportedUsersViewHolder holder, int position) {
        ReportedUser reportedUser = allReportedUsers.get(position);
        Log.d("getting report for user", "getting user for" + reportedUser.getReportedUserId());
        getUserById(holder,reportedUser.getReportedUserId());

        holder.report.setText("Reported for: ");

        if(reportedUser.getMisinformation()!=null&&reportedUser.getMisinformation()){
            holder.report.append("Missing Information, ");
        }

        if(reportedUser.getHarassment()!=null&&reportedUser.getHarassment()){
            holder.report.append("Harassment, ");
        }

        if(reportedUser.getSpamming()!=null&&reportedUser.getSpamming()){
            holder.report.append("Spam, ");
        }


        if(reportedUser.getInappropriateContent()!=null&&reportedUser.getInappropriateContent()){
            holder.report.append("Inappropriate Content, ");
        }
        if (holder.report.length() > 0) {
           // holder.report = new StringBuilder(holder.report.substring(0, reasons.length() - 2));

        }

        holder.reportEdit.setOnClickListener(v->{
            editReportPrompt(holder.itemView.getContext(), reportedUser);


        });
        holder.reportDelete.setOnClickListener(v->{
            int pos = holder.getAdapterPosition();
            deleteReportPrompt(holder.itemView.getContext(), reportedUser, pos);


        });

        holder.userBan.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            banReportedUserPrompt(holder, v.getContext(), reportedUser, pos);
        });


    }

    private void banReportedUserPrompt(ReportedUsersViewHolder holder, Context context, ReportedUser reportedUser, int pos) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Ban");
        builder.setMessage("Are you sure you want to ban this user?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                banUser(holder, reportedUser, pos);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void banUser(ReportedUsersViewHolder holder, ReportedUser reportedUser, int pos) {
        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.deleteUser(reportedUser.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseMessage = response.body();
                   // allReportedUsers.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, allReportedUsers.size());
                    Toast.makeText(context, " User banned successfully", Toast.LENGTH_SHORT).show();
                    holder.userBan.setText("BANNED");
                    holder.userBan.setEnabled(FALSE);


                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(context, "Failed to ban user-: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(context, "Error parsing error body", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("DeleteReport", "failed: " + t.getMessage());
            }
        });
    }

    private void deleteReportPrompt(Context context, ReportedUser reportedUser, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this report?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteReport(reportedUser.getId(), pos);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteReport(Long id, int pos) {
        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.deleteUserReport(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseMessage = response.body();
                    allReportedUsers.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, allReportedUsers.size());
                    Toast.makeText(context, "Report deleted successfully", Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(context, "Failed to delete report: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(context, "Error parsing error body", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("DeleteReport", "failed: " + t.getMessage());
            }
        });
    }

    private void editReportPrompt(Context context, ReportedUser reportedUser) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.activity_report_user, null);
        CheckBox harassmentCheck = dialogView.findViewById(R.id.harassment);
        CheckBox misinformationCheck = dialogView.findViewById(R.id.missingInformation);
        CheckBox spammingCheck = dialogView.findViewById(R.id.spamming);
        CheckBox inappropriateContentCheck = dialogView.findViewById(R.id.inappropriateContent);

        harassmentCheck.setChecked(reportedUser.getHarassment() != null && reportedUser.getHarassment());
        misinformationCheck.setChecked(reportedUser.getMisinformation() != null && reportedUser.getMisinformation());
        spammingCheck.setChecked(reportedUser.getSpamming() != null && reportedUser.getSpamming());
        inappropriateContentCheck.setChecked(reportedUser.getInappropriateContent() != null && reportedUser.getInappropriateContent());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setTitle("Edit Report");
        builder.setPositiveButton("Update", (dialog, which) -> {
            reportedUser.setHarassment(harassmentCheck.isChecked());
            reportedUser.setMisinformation(misinformationCheck.isChecked());
            reportedUser.setSpamming(spammingCheck.isChecked());
            reportedUser.setInappropriateContent(inappropriateContentCheck.isChecked());
            updateReportedUser(reportedUser);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void updateReportedUser(ReportedUser updatedReportedUser) {
        ReportedUserApi reportedUserApi = ApiClientFactory.GetReportedUserApi();
        reportedUserApi.updateReportedUser(updatedReportedUser).enqueue(new Callback<ReportedUser>() {
            @Override
            public void onResponse(Call<ReportedUser> call, Response<ReportedUser> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d("UpdateReportedUser", "Report updated successfully. Response: " + response.body());
                        Toast.makeText(context, "Report updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("UpdateReportedUser", "Report updated successfully but no content returned.");
                        Toast.makeText(context, "Report updated but no data returned.", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                } else {
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Unknown error body";
                        Log.e("UpdateReportedUser", "Failed to update report: " + errorResponse);
                        Toast.makeText(context, "Failed to update report: " + errorResponse, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("UpdateReportedUser", "Error parsing error body", e);
                        Toast.makeText(context, "Error parsing error body", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ReportedUser> call, Throwable t) {
                Log.e("UpdateReportedUser", "Error updating report: " + t.getMessage(), t);
                notifyDataSetChanged();
               // Toast.makeText(context, "Error updating report: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getUserById(@NonNull ReportedUsersViewHolder holder, Long userId) {
        UserApi userApi = ApiClientFactory.GetUserApi();
        userApi.getUser(userId).enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    holder.reportedUserName.setText("Reported User: " + user.getName());
                holder.reportedUserEmailId.setText("Email: " + user.getEmailId());
                    Log.d("getting a user",  "got  user");
                    if(user.getRole()== User.Role.BANNED){
                        holder.userBan.setText("BANNED");
                        holder.userBan.setEnabled(FALSE);
                    }
                } else{
                    holder.reportedUserName.setText("Anonymous");
                    holder.reportedUserEmailId.setText("Anonymous");


                    Log.d("getting a user",  "Failed to get user");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("getting a user",  "Failed");
            }
        });

    }
    @Override
    public int getItemCount() {
        return allReportedUsers.size();
    }

    public void setItems(List<ReportedUser> allReportedUsers) {
        this.allReportedUsers =allReportedUsers;
        notifyDataSetChanged();
    }


    static class ReportedUsersViewHolder extends RecyclerView.ViewHolder {
        TextView reportedUserName, report, reportedUserEmailId;
        ImageButton reportEdit, reportDelete;
        Button userBan;
        ReportedUsersViewHolder(View itemView) {
            super(itemView);
            reportedUserName = itemView.findViewById(R.id.reportedUserName);
            reportedUserEmailId = itemView.findViewById(R.id.reportedUserEmailId);
            report = itemView.findViewById(R.id.report);
            reportEdit = itemView.findViewById(R.id.reportEdit);
            reportDelete = itemView.findViewById(R.id.reportDelete);
            userBan = itemView.findViewById(R.id.userBan);

        }
    }
}
