package com.redvolunteer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.redvolunteer.MessageActivity;
import com.redvolunteer.R;
import com.redvolunteer.UserDetailsActivity;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.ExtraLabels;

import java.util.List;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.ViewHolder> {


    private Context mContext;
    private List<User> mUserList;

    public BlockedUserAdapter(Context mContext, List<User> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list,parent, false);
        return new BlockedUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUserList.get(position);
        holder.Blockedusername.setText(user.getName());


        if(user.getPhoto().equals("default_photo")){
            holder.profile_photo_blocked.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(mContext).load(user.getPhoto()).into(holder.profile_photo_blocked);
        }

        View.OnClickListener showUserClicked = new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Intent showUserDetails = new Intent(mContext, UserDetailsActivity.class);
                showUserDetails.putExtra(ExtraLabels.USER_ID, user.getId());

                showUserDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(showUserDetails);

            }
        };
        holder.Blockedusername.setOnClickListener(showUserClicked);
        holder.profile_photo_blocked.setOnClickListener(showUserClicked);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Blockedusername;
        public ImageView profile_photo_blocked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Blockedusername = itemView.findViewById(R.id.Name);
            profile_photo_blocked = itemView.findViewById(R.id.profile_photo);

        }
    }
}