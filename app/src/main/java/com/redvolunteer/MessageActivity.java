package com.redvolunteer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.adapters.MessageAdapter;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    /**
     * UserViewModel to recognize  who send message
    */
    private UserViewModel mUserViewModel;

    ImageButton send_message;
    EditText message_field;
    CircularImageView prof_image;
    TextView HelpUserName;


    MessageAdapter messageAdapter;
    List<Chat> mChating;

    RecyclerView recyclerView;
    private User mRetrievedUserCreator;
    private Subscription retrievedUserSubscription;
    DatabaseReference dataRef;
    StorageReference storageReference;


    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = ((RedVolunteerApplication)getApplication()).getUserViewModel();

        setContentView(R.layout.activity_message_with_x);

        Intent receivedIntent = this.getIntent();
        final String userID = receivedIntent.getStringExtra(ExtraLabels.USER_ID);
        if(NetworkCheker.getInstance().isNetworkAvailable(this)) {
            mUserViewModel.retrieveUserByID(userID).subscribe(new FlowableSubscriber<User>() {
                @Override
                public void onSubscribe(@NonNull Subscription subscription) {

                    subscription.request(1L);
                    retrievedUserSubscription = subscription;

                }

                @Override
                public void onNext(User user) {

                    mRetrievedUserCreator = user;
                    BindLayoutComponents();
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });


        } else {
            Toast.makeText(MessageActivity.this, getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }



    }

    /**
     * Bind Layout Components for messaging
     */
    private void BindLayoutComponents(){

        prof_image = findViewById(R.id.profile_photo_msg_user);
        HelpUserName = findViewById(R.id.name_user);
        send_message = findViewById(R.id.btn_send_msg);
        message_field = findViewById(R.id.text_send_field);
        recyclerView = findViewById(R.id.recycler_viewer);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        String userSenderUID = mUserViewModel.retrieveCachedUser().getId();


        String userID = mRetrievedUserCreator.getId();
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message_field.getText().toString();
                if(!msg.equals("")){
                    sendMessage(userSenderUID, userID, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Negalima rasyti tuscia zinute!", Toast.LENGTH_SHORT).show();
                }
                message_field.setText("");
            }
        });

        HelpUserName.setText(mRetrievedUserCreator.getName());

        dataRef = FirebaseDatabase.getInstance().getReference("Help_Seekers").child(userID);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                User usr = snapshot.getValue(User.class);
                if(usr.getPhoto().equals("default_photo")){
                    prof_image.setImageResource(R.drawable.ic_default_profile);
                } else {

                    Glide.with(MessageActivity.this).load(usr.getPhoto()).into(prof_image);
                }
                readMessages(userSenderUID, userID, usr.getPhoto());
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


    }

    private void fillActivityWithUSerInfo(){

        HelpUserName.setText(mRetrievedUserCreator.getName());


    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference DatRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        DatRef.child("Chats").push().setValue(hashMap);

    }

    private void readMessages(final String myID,final  String userID, final String photo){
        mChating = new ArrayList<>();


        DatabaseReference DatRef = FirebaseDatabase.getInstance().getReference("Chats");
        DatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot Dsnapshot) {

                mChating.clear();
                for(DataSnapshot snapshot: Dsnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myID) && chat.getSender().equals(userID) ||
                        chat.getReceiver().equals(userID) && chat.getSender().equals(myID)){
                            mChating.add(chat);
                        }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChating, photo);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }


}
