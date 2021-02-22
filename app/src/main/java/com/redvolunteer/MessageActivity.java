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
    ImageButton send_message;
    EditText message_field;
    CircularImageView prof_image;
    TextView HelpUserName;
    ImageView blockUserFromMessage;


    MessageAdapter messageAdapter;
    List<Chat> mChating;

    RecyclerView recyclerView;
    private User mCurUser;
    private User mRetrievedUserCreator;
    private Subscription retrievedUserSubscription;;

    private ProgressDialog popuDialogProg;

    /**
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mChating = new ArrayList<>();
        super.onCreate(savedInstanceState);
        this.mUserViewModel = ((RedVolunteerApplication)getApplication()).getUserViewModel();
        mMainViewModel = ((RedVolunteerApplication) getApplication()).getMessageViewModel();
        mCurUser = mUserViewModel.retrieveCachedUser();
        CurrentUserID = mCurUser.getId();
        ShowWhaitSpinner();
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

                    showRetrievedErrorPopupDialog();
                }

                @Override
                public void onComplete() {

                }
            });


        } else {
            showNoInternetionConnectionPopup();
        }



    }



    private void setLayout(){

        stopSpinner();
        setContentView(R.layout.activity_message_with_x);
        blockUserFromMessage = findViewById(R.id.block_user_message);
        prof_image = findViewById(R.id.profile_photo_msg_user);
        HelpUserName = findViewById(R.id.name_user);
        send_message = findViewById(R.id.btn_send_msg);
        message_field = findViewById(R.id.text_send_field);
        recyclerView = findViewById(R.id.recycler_viewer);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        String userID = mRetrievedUserCreator.getId();
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat cht = new Chat();
                String msg = message_field.getText().toString();
                if(!msg.equals("")){
                    cht.setMessage(msg);
                    cht.setSender(CurrentUserID);
                    cht.setReceiver(userID);
                    mMainViewModel.StoreChat(cht);
                } else {
                    Toast.makeText(MessageActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
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

        readMessages(CurrentUserID, userID, mRetrievedUserCreator.getPhoto());

        blockUserFromMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBlockConfirmationUserDialog();
            }
        });

    }


    private void readMessages(final String myID,final  String userID, final String photo){

        mMainViewModel.getMessagesByUSerID().subscribe(new Consumer<Chat>() {
            @Override
            public void accept(Chat chat) throws Exception {
                if(chat.getReceiver().equals(myID) && chat.getSender().equals(userID) ||
                        chat.getReceiver().equals(userID) && chat.getSender().equals(myID)){
                    mChating.add(chat);
                }
            }
        });

        messageAdapter = new MessageAdapter(MessageActivity.this, mChating, photo);
        recyclerView.setAdapter(messageAdapter);
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
