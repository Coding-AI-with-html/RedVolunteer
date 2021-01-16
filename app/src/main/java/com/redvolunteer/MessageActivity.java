package com.redvolunteer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.GenericLifecycleObserver;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import java.util.HashMap;

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

    private User mRetrievedUserCreator;
    private Subscription retrievedUserSubscription;


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
                    Log.d(TAG, "onNext: " + mRetrievedUserCreator);

                    setLayout();
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });


        }



    }
    private void setLayout(){


        BindLayoutComponents();
        fillActivityWithUSerInfo();

    }

    /**
     * Bind Layout Components for messaging
     */
    private void BindLayoutComponents(){

        prof_image = findViewById(R.id.profile_photo_msg_user);
        HelpUserName = findViewById(R.id.name_user);
        send_message = findViewById(R.id.btn_send_msg);
        message_field = findViewById(R.id.text_send_field);


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


    }

    private void fillActivityWithUSerInfo(){

        HelpUserName.setText(mRetrievedUserCreator.getName());

            prof_image.setImageResource(R.drawable.ic_default_profile);



    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference DatRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        DatRef.child("Chats").push().setValue(hashMap);

    }


}
