package com.redvolunteer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

                
            }
        });




 }


}
