package com.redvolunteer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

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
            ShowNoInternetPopup();
        }
    }


    private void showLayout(){

        //remove spinner dialog as when download is complected
        popupDialogProgress.dismiss();

        setContentView(R.layout.activity_request_help_description);

        this.bindComponents();


    }

    private void bindComponents(){
        this.mRequestName = (TextView) findViewById(R.id.request_title_txt);
        this.mRequestDescription = (EditText) findViewById(R.id.request_description_txt);
        this.mRequestPlace = (TextView) findViewById(R.id.place_name_txt);
        this.mRequestCity = (TextView) findViewById(R.id.city_name_txt);
        this.mOpenMapButton = (LinearLayout) findViewById(R.id.open_map_btn);
        this.mAcceptHelpRequest  = (Button) findViewById(R.id.accept_request_btn);
        this.mModifyRequest = (Button) findViewById(R.id.modify_request_btn);
        this.mAcceptModifyRequest = (Button) findViewById(R.id.accept_modification_btn);

        ImageView mBackButton = (ImageView) findViewById(R.id.request_description_backbutton);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


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
        setRequestActionButtons();

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

                // retrieves user who wants help id and moves to the message box with requestCreator

                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra(ExtraLabels.USER_ID, mRetrievedRequest.getHelpRequestCreatorID());
                startActivity(intent);
            }
        });

        mModifyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modifButton) {
                modifButton.setVisibility(View.GONE);

                //allow desctiption modification
                mRequestDescription.setEnabled(true);
                mRequestDescription.requestFocus();
                mRequestDescription.setSelection(mRequestDescription.getText().length());

                mAcceptModifyRequest.setVisibility(View.VISIBLE);
            }
        });

        mAcceptModifyRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View modifAcceptButton) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())) {

                    String newDescription = mRequestDescription.getText().toString();
                    if (newDescription.length() != 0) {

                        mRetrievedRequest.setDescription(mRequestDescription.getText().toString());

                        //disable modification
                        mRequestDescription.setEnabled(false);

                        //update request
                        mHelpRequestViewModel.updateHelpRequest(mRetrievedRequest);


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

        }


 }



 private boolean checkIfUserIsAdmin(){

        boolean isAdmin = false;

        if(mRetrievedRequest.getHelpRequestCreatorID().equals(this.mUserViewModel.retrieveCachedUser().getId())) {
            isAdmin = true;
        }

        return isAdmin;
 }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    private void refresh(){

        Intent refresh = new Intent(this, RequestDescriptionActivity.class);
        Bundle oldExtras = getIntent().getExtras();
        if(oldExtras != null){
            refresh.putExtras(getIntent().getExtras());

            startActivity(refresh);
        }
    }


    public void onBackPressed(){

        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(requestRetrieve != null){
            requestRetrieve.cancel();
        }
    }

    private void showRetrievedErrorPopup(){

        if(this.popupDialogProgress != null){
            this.popupDialogProgress.dismiss();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void ShowNoInternetPopup(){

        if(this.popupDialogProgress != null){
            this.popupDialogProgress.dismiss();
        }

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });

        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
