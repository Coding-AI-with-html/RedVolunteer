package com.redvolunteer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.adapters.MessageAdapter;
import com.redvolunteer.fragments.UserMessageFragment;
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
import io.reactivex.functions.Consumer;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    /**
     * UserViewModel to recognize  who send message
    */
    private UserViewModel mUserViewModel;

    private MessageViewModel mMainViewModel;

    private Subscription MessageRetrievedSubscription;

    String CurrentUserID;
    FloatingActionButton send_message;
    EditText message_field;
    CircularImageView prof_image;
    TextView HelpUserName;
    ImageView blockUserFromMessage;
    ImageView goBackButton;
    TextView unBlockButton;
    TextView SomethingWrongButton;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");


    MessageAdapter messageAdapter;
    List<Chat> mChating;

    RecyclerView recyclerView;
    private User mCurUser;
    private User mRetrievedUserCreator;
    private Subscription retrievedUserSubscription;
    private Subscription mBlockedUserSub;
    private Subscription mUnblockUserSub;
    private RelativeLayout mBlockedUser;
    private RelativeLayout mTextField;
    private RelativeLayout mUnblockUser;

    private ProgressDialog popuDialogProg;

    ValueEventListener seenListener;

    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserViewModel = ((RedVolunteerApplication)getApplication()).getUserViewModel();
        mMainViewModel = ((RedVolunteerApplication) getApplication()).getMessageViewModel();
        mCurUser = mUserViewModel.retrieveCachedUser();
        CurrentUserID = mCurUser.getId();
        this.popuDialogProg = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true);
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
                    fillActivity(user);
                }

                @Override
                public void onError(Throwable t) {

                    showRetrievedErrorPopupDialog();
                }

                @Override
                public void onComplete() {

                }
            });



            mUserViewModel.LoadOtherUserBlockedList(userID).subscribe(new Subscriber<List<String>>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1L);
                    mBlockedUserSub = subscription;
                }

                @Override
                public void onNext(List<String> strings) {
                    checkIfUserBlockedCurUser(strings);
                    Log.d(TAG, "onNexting: " + strings);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });

            mUserViewModel.loadCurrUserBlockedUserList(CurrentUserID).subscribe(new Subscriber<List<User>>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(1L);
                    mUnblockUserSub = s;
                }

                @Override
                public void onNext(List<User> users) {
                    checkIfUserBlockedByCurrentUSer(users);

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });

        }

        else {
            showNoInternetionConnectionPopup();
        }



    }

    private void fillActivity(final User retrievedUser) {


        mChating = new ArrayList<>();

        HelpUserName.setText(retrievedUser.getName());

        if(retrievedUser.getPhoto().equals("default_photo")){
            prof_image.setImageResource(R.drawable.ic_default_profile);
        } else {
            Glide.with(MessageActivity.this).load(retrievedUser.getPhoto()).into(prof_image);
        }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                mChating.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Chat chatting = ds.getValue(Chat.class);

                    if(chatting.getReceiver().equals(mCurUser.getId()) && chatting.getSender().equals(retrievedUser.getId()) ||
                            chatting.getReceiver().equals(retrievedUser.getId()) && chatting.getSender().equals(mCurUser.getId())){
                        mChating.add(chatting);
                    }

                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mChating, retrievedUser.getPhoto());
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat cht = new Chat();
                String msg = message_field.getText().toString();
                if(!msg.equals("")){
                    cht.setMessage(msg);
                    cht.setSender(CurrentUserID);
                    cht.setReceiver(retrievedUser.getId());
                    mMainViewModel.StoreChat(cht);
                } else {
                    Toast.makeText(MessageActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                }
                message_field.setText("");
            }
        });


    }


    private void setLayout(){

        stopSpinner();
        setContentView(R.layout.activity_message_with_x);

        this.bindComponents();

    }

    private void bindComponents(){

        this.blockUserFromMessage = findViewById(R.id.block_user_message);
        this.mTextField = findViewById(R.id.bottom_thing);
        this.mBlockedUser = findViewById(R.id.blocked_user_layout);
        this.mUnblockUser = findViewById(R.id.unblocked_user_layout);
        this.prof_image = findViewById(R.id.profile_photo_msg_user);
        this.HelpUserName = findViewById(R.id.name_user);
        this.send_message = findViewById(R.id.btn_send_msg);
        this.message_field = findViewById(R.id.text_send_field);
        this.recyclerView = findViewById(R.id.recycler_viewer);
        this.goBackButton = findViewById(R.id.go_back_message);
        this.unBlockButton = findViewById(R.id.unblock_button);
        this.SomethingWrongButton = findViewById(R.id.something_wrong_button);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        blockUserFromMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBlockConfirmationUserDialog();
            }
        });
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        unBlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnblockConfirmationUserDialog();
            }
        });
    }


    private void checkIfUserBlockedByCurrentUSer(List<User> user){

        for(User usering: user){

            if(usering.getId().equals(mRetrievedUserCreator.getId())){

                mTextField.setVisibility(View.GONE);
                mUnblockUser.setVisibility(View.VISIBLE);
            }
        }
    }

    private void checkIfUserBlockedCurUser(List<String> mBlockedList){

  for(String IDBlockList: mBlockedList){
       if(mCurUser.getId().equals(IDBlockList)){

          mTextField.setVisibility(View.GONE);
         mBlockedUser.setVisibility(View.VISIBLE);
      }
  }
    }


    /**
     * Show's whait spinner
     */
    private void ShowWhaitSpinner() {

        this.popuDialogProg = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true);
    }

    private void stopSpinner(){
        if (popuDialogProg!= null)
            popuDialogProg.dismiss();
        popuDialogProg = null;

    }
    public void showUnblockConfirmationUserDialog(){


        AlertDialog.Builder blder = new AlertDialog.Builder(this);

        String titl = getString(R.string.unblock_user_confirmation);
        String messag = getString(R.string.ask_unblock_user);
        String positiv = getString(R.string.yes_delete_request);
        String negativ = getString(R.string.no_delete_request);

        blder.setTitle(titl);
        blder.setMessage(messag);

        blder.setPositiveButton(positiv, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int wicth) {

                mUserViewModel.unblockUser(mCurUser, mRetrievedUserCreator.getId());
                Intent goToMainActiv = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(goToMainActiv);
            }
        });

        blder.setNegativeButton(negativ, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        blder.show();
    }


    public void showBlockConfirmationUserDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String title = getString(R.string.block_user_confirmation);
        String message = getString(R.string.ask_block_user);
        String positive = getString(R.string.yes_delete_request);
        String negative = getString(R.string.no_delete_request);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mUserViewModel.blockUser(mCurUser, mRetrievedUserCreator.getId());
                Intent goToMain = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(goToMain);
            }
        });
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent goToMain = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(goToMain);
            }
        });

        builder.show();
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
