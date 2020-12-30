package com.redvolunteer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.newrequesthelp.NewRequestHelpActivity;
import com.redvolunteer.R;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.pojo.RequestHelp;

import org.reactivestreams.Subscription;

import java.util.List;

public class RequestWallFragment extends Fragment {

    /**
     * firebase
     */
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    /**
     * RxAndroid subscription
     */

    private Subscription requestRetrieveSubscription;

    /**
     * Reference to the Activity
     *
     */
    private FragmentInteractionListener mActivity;

    /**
     * User Created Help Requests
     */
    private ListView mUserRequestsList;

    /**
     * Main View Model
     */
    private HelpRequestViewModel mHelpRequestViewModel;

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;

    /**
     * Popup to show progress
     */
    private ProgressDialog popupDialogProgress;

    /**
     * LinearLayout if User don't Created HelpRequest
     */
    private LinearLayout mNoRequestShow;

    /**
     * Retrieved User HelpRequest
     */
    private RequestHelpModel RetrievedUserHelpRequest;

    public RequestWallFragment(){
        //Requires empty public constructor


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);

    }

    private void bind(View view){
        this.SetupToolbar(view);

    }
    /**
     * Checking if user is HelpSeeker, or Volunteer, if Volunteer, needs New_request_Button be not visible
     */
    private void ifUserIsVolunteer(){
        ImageView newRequestBut;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = mFirebaseDatabase.getReference(getString(R.string.database_Volunteers));
        if(mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();

            DatabaseReference userInfo = dataRef.child(userID);
            userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }



    }

    /**
     * Setup Toolbar and /**
     *      * Checking if user is HelpSeeker, or Volunteer, if Volunteer, needs New_request_Button be not visible
     */
    private void SetupToolbar(View layout){
        layout.findViewById(R.id.new_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewRequestHelpActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = mFirebaseDatabase.getReference(getString(R.string.database_Volunteers));
        if(mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
            DatabaseReference userInfo = dataRef.child(userID);
            userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){

                        layout.findViewById(R.id.new_request_button).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_wall, container, false);
    }

    /**
     * Initialize the ListView with helpRequest
     */

    private void initializeListView(final List<RequestHelp> helpList){



    }

    /**@Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){

            ShowWhaitSpinner();

            if(NetworkCheker.getInstance().isNetworkAvailable(getContext())){

                mHelpRequestViewModel.getUserHelpRequests().subscribe(new FlowableSubscriber<List<RequestHelp>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Subscription s) {

                        s.request(1L);


                        if(requestRetrieveSubscription != null){
                            requestRetrieveSubscription.cancel();
                        }

                        requestRetrieveSubscription = s;
                    }

                    @Override
                    public void onNext(List<RequestHelp> requestHelps) {

                        StopWhaitSpinner();

                        if(requestHelps.size() != 0){

                        }


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
    }
*/
    /**
     * Show's whait spinner
     */
    private void ShowWhaitSpinner() {

        this.popupDialogProgress = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    /**
     * Stopping whaitSpinner
     */
    private void StopWhaitSpinner(){
        if(this.popupDialogProgress != null) {
            this.popupDialogProgress.dismiss();
        }

    }


    /**
     * It shows a Dialog containing an error to the user
     */
    private void ShowRetrievedErrorPopupDialog(){
        ShowWhaitSpinner();
        //there is an error, show popup message
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * It show's a no internet connection popup
     */
    private void ShowNoInternetConnection(){
        StopWhaitSpinner();
        //There was an error show error message
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }








}
