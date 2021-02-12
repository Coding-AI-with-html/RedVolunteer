package com.redvolunteer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.redvolunteer.viewmodels.MessageViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscriber;
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

    private MessageViewModel mMainViewModel;

    private Subscription MessageRetrievedSubscription;

    ImageButton send_message;
    EditText message_field;
    CircularImageView prof_image;
    TextView HelpUserName;


    MessageAdapter messageAdapter;
    List<Chat> mChating;
    List<Chat> mChat;

    RecyclerView recyclerView;
    private User mRetrievedUserCreator;
    private Subscription retrievedUserSubscription;
    DatabaseReference dataRef;

    private ProgressDialog popuDialogProg;

    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = ((RedVolunteerApplication)getApplication()).getUserViewModel();
        mMainViewModel = ((RedVolunteerApplication) getApplication()).getMessageViewModel();

        this.popuDialogProg = ProgressDialog.show(this, null,getString(R.string.loading_popup_message_spinner), true);

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

                    setLayout();
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });


            mMainViewModel.getUserMessages().subscribe(new Subscriber<List<Chat>>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1L);

                    if(MessageRetrievedSubscription !=null){
                        MessageRetrievedSubscription.cancel();
                    }
                    MessageRetrievedSubscription = subscription;
                }

                @Override
                public void onNext(List<Chat> chats) {
                    stopSpinner();
                    mChat = chats;
                    fillActivity();

                    //Log.d(TAG, "onNext: " + chats);
                }

                @Override
                public void onError(Throwable t) {
                    stopSpinner();
                    showRetrievedErrorPopupDialog();
                }

                @Override
                public void onComplete() {

                }
            });

        } else {
            Toast.makeText(MessageActivity.this, getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }



    }


    private void setLayout(){

        stopSpinner();
        setContentView(R.layout.activity_message_with_x);
        BindLayoutComponents();
        fillActivity();
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

    }

    private void InitiliazeMessageView(List<Chat> mChat){

    }

    private void fillActivity(){

        String userSenderUID = mUserViewModel.retrieveCachedUser().getId();
        String userID = mRetrievedUserCreator.getId();
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat cht = new Chat();
                String msg = message_field.getText().toString();
                if(!msg.equals("")){
                    cht.setMessage(msg);
                    cht.setSender(userSenderUID);
                    cht.setReceiver(userID);
                    mMainViewModel.StoreChat(cht);
                } else {
                    Toast.makeText(MessageActivity.this, "Negalima rasyti tuscia zinute!", Toast.LENGTH_SHORT).show();
                }
                message_field.setText("");
            }
        });

        HelpUserName.setText(mRetrievedUserCreator.getName());


        if(mRetrievedUserCreator.getPhoto().equals("default_photo")){
            prof_image.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(MessageActivity.this).load(mRetrievedUserCreator.getPhoto()).into(prof_image);
        }


        mChating = new ArrayList<>();
        for(Chat cht: mChat){
            if(cht.getReceiver().equals(userSenderUID) && cht.getSender().equals(userID) ||
                    cht.getReceiver().equals(userID) && cht.getSender().equals(userSenderUID)){
                mChating.add(cht);
            }
            messageAdapter = new MessageAdapter(MessageActivity.this, mChating, mRetrievedUserCreator.getPhoto());
            recyclerView.setAdapter(messageAdapter);

        }


        //readMessages(userSenderUID, userID, mRetrievedUserCreator.getPhoto());
            }


    private void readMessages(final String myID,final  String userID, final String photo){
        mChating = new ArrayList<>();


        for(Chat cht: mChat){
            if(cht.getReceiver().equals(myID) && cht.getSender().equals(userID) ||
                    cht.getReceiver().equals(userID) && cht.getSender().equals(myID)){
                mChating.add(cht);
            }
            messageAdapter = new MessageAdapter(MessageActivity.this, mChating, photo);
            recyclerView.setAdapter(messageAdapter);
        }
    }

    private void fillMessageActivity(){

        mChating = new ArrayList<>();


        for(Chat cht: mChat){
            if(cht.getReceiver().equals(myID) && cht.getSender().equals(userID) ||
                    cht.getReceiver().equals(userID) && cht.getSender().equals(myID)){
                mChating.add(cht);
            }
            messageAdapter = new MessageAdapter(MessageActivity.this, mChating, photo);
            recyclerView.setAdapter(messageAdapter);
        }
    }

    private void stopSpinner(){
        if (popuDialogProg!= null)
            popuDialogProg.dismiss();
        popuDialogProg = null;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(retrievedUserSubscription != null){
            retrievedUserSubscription.cancel();
        }
    }

    private void showRetrievedErrorPopupDialog(){

        this.popuDialogProg.dismiss();


        AlertDialog.Builder builder =new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    private void showNoInternetionConnectionPopup(){

        stopSpinner();

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go to the main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
