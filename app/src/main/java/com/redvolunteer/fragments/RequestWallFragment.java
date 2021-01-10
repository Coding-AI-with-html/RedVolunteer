package com.redvolunteer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.RequestDescriptionActivity;
import com.redvolunteer.adapters.HelpRequestWallAdapter;
import com.redvolunteer.newrequesthelp.NewRequestHelpActivity;
import com.redvolunteer.R;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.pojo.RequestHelp;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;

public class RequestWallFragment extends Fragment {

    private static final String TAG = "RequestWallFragment";
    /**
     * firebase
     */
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    /**
     * RxAndroid subscription
     */

    private Subscription requestRetrieveSubscription;

    /**
     * Rx java subcription
     */
    private Subscription mMotherRequestHelpSubscription;

    /**
     * Reference to the Activity
     *
     */
    private FragmentInteractionListener mListener;

    /**
     * User Created Help Requests
     */
    private ListView mUserRequestsList;

    /**
     * ListView for Volunteer
     */
    private ListView mVolunteerListView;
    /**
     * Main View Model
     */
    private HelpRequestViewModel mMainViewModel;

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
     * Request Wall Adapter
     */
    HelpRequestWallAdapter mAdapter;

    /**
     * retrieved Request
     */
    private List<RequestHelp> retrievedRequests;

    public RequestWallFragment(){
        //Requires empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    mMainViewModel = mListener.getHelpRequestViewModel();
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


    private void FetchHelpRequests(){

        if(NetworkCheker.getInstance().isNetworkAvailable(getContext())) {

            ShowWhaitSpinner();
            mMainViewModel
                    .getRequests()
                    .subscribe(new FlowableSubscriber<List<RequestHelp>>() {
                        @Override
                        public void onSubscribe(Subscription subscription) {
                            subscription.request(Long.MAX_VALUE);

                            requestRetrieveSubscription = subscription;
                        }

                        @Override
                        public void onNext(List<RequestHelp> requestHelps) {
                            StopWhaitSpinner();
                            handleAdapter(requestHelps);
                        }

                        @Override
                        public void onError(Throwable t) {
                            StopWhaitSpinner();
                            ShowRetrievedErrorPopupDialog();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });


        } else {
            ShowNoInternetConnection();
        }

    }

    private void bind(View view){
        this.SetupToolbar(view);

        mNoRequestShow = (LinearLayout) view.findViewById(R.id.no_available_request_text);

        mVolunteerListView = (ListView) view.findViewById(R.id.request_list);
        mVolunteerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                RequestHelp selected = mAdapter.getItem(i);

                // send the requestID
                Intent intent = new Intent(getActivity(), RequestDescriptionActivity.class);
                intent.putExtra(ExtraLabels.REQUEST, selected.getId());
                startActivity(intent);

            }
        });
        view.findViewById(R.id.new_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), NewRequestHelpActivity.class));
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            FetchHelpRequests();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_wall, container, false);
    }


    private void handleAdapter(List<RequestHelp> requestsForHelp){
        if(requestsForHelp.size() !=0) {
            Log.d(TAG, "handleAdapter: " + requestsForHelp);
            retrievedRequests = requestsForHelp;
            mVolunteerListView.setVisibility(View.VISIBLE);
            mNoRequestShow.setVisibility(View.GONE);
            if(mAdapter == null){
                mAdapter = new HelpRequestWallAdapter(retrievedRequests, getActivity());
                mVolunteerListView.setAdapter(mAdapter);
            } else
                mAdapter.setHelpList(retrievedRequests);
                mAdapter.notifyDataSetChanged();

                mVolunteerListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView absListView, int i) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int visibleElementCount = firstVisibleItem + visibleItemCount;
                        if (visibleElementCount == totalItemCount && totalItemCount != 0) {
                            mMainViewModel.getNewRequests().subscribe(new FlowableSubscriber<List<RequestHelp>>() {
                                @Override
                                public void onSubscribe(Subscription subscription) {
                                    subscription.request(1L);
                                    if (mMotherRequestHelpSubscription != null) {
                                        mMotherRequestHelpSubscription.cancel();
                                    }
                                    mMotherRequestHelpSubscription = subscription;
                                }

                                @Override
                                public void onNext(List<RequestHelp> requestsH) {
                                    if (requestsH.size() != 0) {
                                        retrievedRequests.addAll(requestsH);
                                        Log.d(TAG, "onNext: " + retrievedRequests);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }
                                @Override
                                public void onComplete() {

                                }
                            });
                        }
                    }

                });
        } else {
            mVolunteerListView.setVisibility(View.GONE);
            mNoRequestShow.setVisibility(View.VISIBLE);

        }
    }



    private void initializeListView(final List<RequestHelp> requestHelps){

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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentInteractionListener){
            mListener = (FragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + "must implement FragmentInteractionListener");
        }
    }

    /**
     * Show's whait spinner
     */
    private void ShowWhaitSpinner() {

        this.popupDialogProgress = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(requestRetrieveSubscription != null)
            requestRetrieveSubscription.cancel();
        if(mMotherRequestHelpSubscription != null){
            mMotherRequestHelpSubscription.cancel();
        }
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
