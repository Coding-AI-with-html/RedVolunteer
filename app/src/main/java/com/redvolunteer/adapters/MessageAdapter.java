package com.redvolunteer.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.User;
import com.redvolunteer.viewmodels.UserViewModel;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    FirebaseUser Fuser;

    private UserViewModel mUserViewModel;
    private Context mContext;
    private List<Chat> Messages;
    private String photo;


    public MessageAdapter(Context mContext, List<Chat> messages, String photo) {
        this.mContext = mContext;
        this.Messages = messages;
        this.photo = photo;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = Messages.get(position);

        holder.showMessage.setText(chat.getMessage());

        if(photo.equals("default_photo")){
            holder.profile_picture.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(mContext).load(photo).into(holder.profile_picture);
        }

    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profile_picture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
            profile_picture = itemView.findViewById(R.id.profile_image_chat);

        }

    }

    @Override
    public int getItemViewType(int position) {
         Fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(Messages.get(position).getSender().equals(Fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }
}
