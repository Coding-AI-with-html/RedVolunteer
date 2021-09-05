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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.MessageActivity;
import com.redvolunteer.R;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.ExtraLabels;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private Context mContext;
    private List<User> mUserList;

    String lastMessage;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

        User user = mUserList.get(position);
        holder.username.setText(user.getName());


        if(user.getPhoto().equals("default_photo")){
            holder.profile_photo_list.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(mContext).load(user.getPhoto()).into(holder.profile_photo_list);
        }
        lastMessage(user.getId(), holder.last_msg);
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
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.Name);
            profile_photo_list = itemView.findViewById(R.id.profile_photo);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    //checking last message
    private void lastMessage(String userUId, TextView last_msg){

        lastMessage = "default";
        FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Chats");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = Fuser.getUid();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Chat cht = ds.getValue(Chat.class);
                    if(cht.getReceiver().equals(userId) && cht.getSender().equals(userUId) ||
                    cht.getSender().equals(userId) && cht.getReceiver().equals(userUId)){
                        lastMessage = cht.getMessage();
                    }
                }
                switch (lastMessage){

                    case "default":
                        last_msg.setText("Zinuciu Nera");
                        break;

                    default:
                        last_msg.setText(lastMessage);
                        break;
                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

