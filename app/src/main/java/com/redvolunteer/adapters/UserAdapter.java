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
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.ExtraLabels;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private Context mContext;
    private List<User> mUserList;

    public UserAdapter(Context mContext, List<User> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_list, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUserList.get(position);
        holder.username.setText(user.getName());

        if(user.getPhoto().equals("default_photo")){
            holder.profile_photo_list.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(mContext).load(user.getPhoto()).centerCrop().into(holder.profile_photo_list);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentingToMsg = new Intent(mContext, MessageActivity.class);
                intentingToMsg.putExtra(ExtraLabels.USER_ID, user.getId());
                mContext.startActivity(intentingToMsg);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_photo_list;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.Name);
            profile_photo_list = itemView.findViewById(R.id.profile_photo);
        }
    }
}

