package com.redvolunteer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.calendar.CalendarFormatter;
import com.redvolunteer.utils.imagemarshalling.ImageBase64Marshaller;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public class UserDetailsActivity  extends AppCompatActivity {


    private UserViewModel mUserViewModel;

    private User mRetrievedUser;

    /**
     * Rx java subscribtions
     */
    private Subscription retrievedUserSubscription;


    /**
     * Layout components
     */
    private ImageView mUserPIc;
    private TextView mUserName;
    private TextView mUserSurname;
    private TextView mBirthDate;
    private TextView mUserBio;


    private ProgressDialog popuDialogProg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
                    showLayout();
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




    }

    /**
     * BInd each layout component to its own reference
     */
    private void bindActivityComponents(){

        mUserPIc = (ImageView) findViewById(R.id.requestor_image);
        mUserName = (TextView) findViewById(R.id.request_creator);
        mBirthDate = (TextView) findViewById(R.id);


    }
    private void filLActivity(){

        mUserPIc.setImageBitmap(ImageBase64Marshaller.decode64BitmapString(mRetrievedUser.getPhoto()));
        mUserName.setText(mRetrievedUser.getFullName());
        mUserSurname.setText(mRetrievedUser.getFullSurname());
        mBirthDate.setText(CalendarFormatter.getDate(mRetrievedUser.getBirthDate()));
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

        Context context;
        AlertDialog.Builder builder =new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton("GERAI", new DialogInterface.OnClickListener() {
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
