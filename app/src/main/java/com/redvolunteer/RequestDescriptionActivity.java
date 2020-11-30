package com.redvolunteer;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import java.util.List;
import java.util.Locale;

import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

public class RequestDescriptionActivity extends AppCompatActivity {

    /**
    * Showed request
     */
    private RequestHelp mRetrievedRequest;


    /**
     * RxJava listeners
     */
    private Subscription requestRetrieve;


    /**
     * View Components
     */
    private TextView mRequestName;
    private EditText mRequestDescription;
    private TextView mRequestPlace;
    private TextView mRequestCity;
    private LinearLayout mOpenMapButton;
    private Button mAcceptRequest;
    private Button mModifyRequest;
    private Button mAcceptModifyRequest;
    private Button mAcceptHelpRequest;
    private ProgressDialog popupDialogProgress;

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;

    /**
     * Help Request View Model
     */
    private HelpRequestViewModel mHelpRequestViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String requestID = this.getIntent().getStringExtra(ExtraLabels.REQUEST);



        this.popupDialogProgress = ProgressDialog.show(this, null, getString(R.string.loading_popup_message_spinner), true );

        this.mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();
        this.mHelpRequestViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();

        if(NetworkCheker.getInstance().isNetworkAvailable(this)){

            mHelpRequestViewModel.getRequest(requestID).subscribe(new FlowableSubscriber<RequestHelp>() {
                @Override
                public void onSubscribe(@NonNull Subscription subscription) {

                    subscription.request(Long.MAX_VALUE);
                    requestRetrieve = subscription;

                }

                @Override
                public void onNext(RequestHelp requestHelp) {

                    mRetrievedRequest = requestHelp;

                    showLayout();

                    fillActivityInformation(requestHelp);
                }

                @Override
                public void onError(Throwable throwable) {

                    showRetrievedErrorPopup();
                }

                @Override
                public void onComplete() {

                }
            });

        } else {
            showNoInternetConnectionPopup();
        }
    }


    private void showLayout(){

        popupDialogProgress.dismiss();

        setContentView(R.layout.activity_new_help_request);

        this.bindComponents();


    }

    private void bindComponents(){





    }


    private void fillActivityInformation(final RequestHelp retrievedRequest){

        //request infos filling
        this.mRequestName.setText(retrievedRequest.getName());
        this.mRequestDescription.setText(retrievedRequest.getDescription());
        this.mRequestPlace.setText(retrievedRequest.getRequestLocation().getName().trim());
        this.mRequestCity.setText(String.format(Locale.ENGLISH,
                "%4.5f, %4.5f",
                retrievedRequest.getRequestLocation().getLatitude(),
                retrievedRequest.getRequestLocation().getLongitude()));

        //show action buttons depending on the ownership of the request

        setRequestActionButton();

        showMapButton();




    }

 private void showMapButton(){

        this.mOpenMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                RequestLocation requestLocation = mRetrievedRequest.getRequestLocation();

                double latitude = requestLocation.getLatitude();
                double longitude = requestLocation.getLongitude();

                Uri googleMapsUri = Uri.parse("geo:" + latitude + "," + longitude + "7q=" + latitude + "," + longitude);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, googleMapsUri);

                mapIntent.setPackage(getString(R.string.android_map_request_name));


                try {

                    startActivity(mapIntent);
                } catch(ActivityNotFoundException exception) {
                    Toast.makeText(getApplicationContext(), "There is no application", Toast.LENGTH_SHORT).show();
                }

                
            }
        });
 }
 private void setRequestActionButtons(){


        mAcceptHelpRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // retrieves user(volunteer) id and moves to the message box with requestCreator
                mUserViewModel.retrieveCachedUser().getId();
            }
        });

        mModifyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modifButton) {
                modifButton.setVisibility(View.GONEs);

                //allow desctiption modification
                mRequestDescription.setEnabled(true);
                mRequestDescription.requestFocus();
                mRequestDescription.setSelection(mRequestDescription.getText().length());

                mAcceptHelpRequest.setVisibility(View.VISIBLE);
            }
        });

        mAcceptModifyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modifAcceptButton) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())) {

                    String newDescription = mAcceptHelpRequest.getTag().toString();
                    if (newDescription.length() != 0) {

                        mRetrievedRequest.setDescription(mRequestDescription.getText().toString());

                        //disable modification
                        mRequestDescription.setEnabled(false);

                        //update Request
                        mModifyRequest.setVisibility(View.VISIBLE);
                        modifAcceptButton.setVisibility(View.GONE);

                    }
                } else {
                    Toast.makeText(getApplicationContext(),R.string.no_internet_popup_label, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAcceptModifyRequest.setVisibility(View.GONE);

        if(checkIfUserIsAdmin()){

            mAcceptHelpRequest.setVisibility(View.GONE);

        } else {

            mModifyRequest.setVisibility(View.GONE);

            if(isLoggedUserIsVolunteer()){
                mAcceptHelpRequest.setVisibility(View.GONE);

            } else {
                mAcceptHelpRequest.setVisibility(View.VISIBLE);
            }
        }


 }

 private void isLoggedUserIsVolunteer(){

        String loggedUserId = mUserViewModel.retrieveCachedUser().getId();

        return true;


 }
 private void checkIfUserIsAdmin(){

 }


}
