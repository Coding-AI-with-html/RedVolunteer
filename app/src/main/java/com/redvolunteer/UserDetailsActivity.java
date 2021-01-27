package com.redvolunteer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.redvolunteer.adapters.UserAdapter;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.calendar.CalendarFormatter;
import com.redvolunteer.utils.imagemarshalling.ImageBase64Marshaller;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import java.util.Locale;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public class UserDetailsActivity  extends AppCompatActivity {


    private UserViewModel mUserViewModel;

    private User mRetrievedUser;
    UserAdapter mAdapter;

    /**
     * Rx java subscribtions
     */
    private Subscription retrievedUserSubscription;


    /**
     * Layout components
     */
    private ImageView mUserPIc;
    private TextView mUserName;
    private TextView mBirthDate;
    private TextView mUserBio;
    DatabaseReference DataReference;
    StorageReference storageReference;


    private ProgressDialog popuDialogProg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        this.popuDialogProg = ProgressDialog.show(this, null,getString(R.string.loading_popup_message_spinner), true);

        this.mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        Intent receivedIntent = this.getIntent();

        String userID = receivedIntent.getStringExtra(ExtraLabels.USER_ID);

        if(NetworkCheker.getInstance().isNetworkAvailable(this)){
            mUserViewModel.retrieveUserByID(userID).subscribe(new FlowableSubscriber<User>() {
                @Override
                public void onSubscribe(@NonNull Subscription subscription) {

                    subscription.request(1L);
                retrievedUserSubscription = subscription;
                }

                @Override
                public void onNext(User user) {
                    //save retrieved user
                    mRetrievedUser = user;
                    //show layout content with user info
                    //this is an different operation in order to download info first
                    setLayout();
                }

                @Override
                public void onError(Throwable throwable) {
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
        setContentView(R.layout.activity_user_details);
        bindActivityComponents();
        filLActivity(); //user info
    }

    /**
     * BInd each layout component to its own reference
     */
    private void bindActivityComponents(){

        mUserPIc = (ImageView) findViewById(R.id.retrieved_profile_user_pic);
        mUserName = (TextView) findViewById(R.id.retrieved_user_name);
        mBirthDate = (TextView) findViewById(R.id.retrieved_birth_date);
        mUserBio = (TextView) findViewById(R.id.retrieved_user_bio);

        ImageView mBackButtonToMain = (ImageView) findViewById(R.id.user_details_backbutton);
        mBackButtonToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //contact button on top-bar
        ImageView mContactButton = (ImageView) findViewById(R.id.user_contact_button);
        if(mRetrievedUser.getId().equals(mUserViewModel.retrieveCachedUser().getId())){

            mContactButton.setVisibility(View.GONE);
        }
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                //send email to the user
                String[] TO = {mRetrievedUser.getEmail()};


                //create an email intent specifying the header
                // not possible to specify the sender
                emailIntent.setData(Uri.parse(getString(R.string.mail_uri_protocol)));
                emailIntent.setType(getString(R.string.email_content_type));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_email_object) );

                @SuppressLint("StringFormatMatches") String fillMailBody = String.format(
                        Locale.ENGLISH,
                        getString(R.string.message_mail_user),
                        mRetrievedUser.getName(),
                        mUserViewModel.retrieveCachedUser().getName());
                emailIntent.putExtra(Intent.EXTRA_TEXT, fillMailBody);

                try {
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_picker_title)));
                } catch (ActivityNotFoundException ex){
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_email_user_agent_not_installed), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void filLActivity(){


        DataReference = FirebaseDatabase.getInstance().getReference("Help_Seekers").child(mRetrievedUser.getId());

        DataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                User usr = snapshot.getValue(User.class);
                if(usr.getPhoto().equals("default_photo")){
                    mUserPIc.setImageResource(R.drawable.ic_default_profile);
                } else {
                    Glide.with(getBaseContext()).load(usr.getPhoto()).into(mUserPIc);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        mUserName.setText(mRetrievedUser.getName());
       mBirthDate.setText(CalendarFormatter.getDate(mRetrievedUser.getBirthDay()));
        mUserBio.setText(mRetrievedUser.getBiography());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(retrievedUserSubscription != null){
            retrievedUserSubscription.cancel();
        }
    }

    private void stopSpinner(){
        if (popuDialogProg!= null)
            popuDialogProg.dismiss();
        popuDialogProg = null;

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
