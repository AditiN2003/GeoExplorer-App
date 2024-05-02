package test.connect.geoexploreapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import test.connect.geoexploreapp.api.ApiClientFactory;
import test.connect.geoexploreapp.api.UserApi;
import test.connect.geoexploreapp.api.UserGroupApi;
import test.connect.geoexploreapp.model.User;
import test.connect.geoexploreapp.model.UserGroup;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private List<String> members;
    private UserGroup userGroup;
    private MemberCallback memberCallback;


    public MembersAdapter(UserGroup userGroup, List<String> members, MemberCallback memberCallback) {
        this.members = members;
        this.userGroup=userGroup;
        this.memberCallback = memberCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String memberEmailID = members.get(position);
        holder.memberEmailId.setText(memberEmailID);
        holder.deleteMemberButton.setOnClickListener(v -> findUser(v.getContext(), userGroup, memberEmailID, position));
    }

    private void findUser(Context context, UserGroup userGroup, String memberEmailID, int pos) {
        UserApi userApi = ApiClientFactory.GetUserApi();
        userApi.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    for(User user : response.body()) {
                        if(user.getEmailId().equalsIgnoreCase(memberEmailID)) {
                            Log.d("findUser", "User found: " + user.getName());
                            deleteMember(context, userGroup,user, pos);
                            return;
                        }
                    }
                    Log.d("findUser", "User not found with email ID: " + memberEmailID);
                } else {
                    Log.e("findUser", "Error fetching users: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("findUser", "API call failed: " + t.getMessage());
            }
        });
    }

    private void deleteMember(Context context, UserGroup userGroup, User user, int pos) {
        UserGroupApi userGroupApi= ApiClientFactory.GetUserGroupApi();
        userGroupApi.deleteUserFromGroup(userGroup.getId(), user.getId()).enqueue(new Callback<ResponseBody> (){

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response!=null&&response.isSuccessful()){
                    Toast.makeText(context, "Member successfully deleted from group.", Toast.LENGTH_SHORT).show();
                    Log.d("deleteMember", "Member successfully deleted: " + response.body());
                    members.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, members.size());

                        memberCallback.onMemberDeleted(userGroup.getId());

                } else {
                    Toast.makeText(context, "Failed to delete member from group.", Toast.LENGTH_SHORT).show();
                    Log.e("deleteMember", "Failed to delete member: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("deleteMember", "API call failed: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberEmailId;
        ImageButton deleteMemberButton;


        ViewHolder(View itemView) {
            super(itemView);
            memberEmailId = itemView.findViewById(R.id.memberName);
            deleteMemberButton = itemView.findViewById(R.id.deleteMemberButton);

        }
    }
}
