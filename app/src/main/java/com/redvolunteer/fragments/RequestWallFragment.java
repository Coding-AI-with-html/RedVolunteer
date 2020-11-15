package com.redvolunteer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.NewRequestHelp.NewReguestHelpActivity;
import com.redvolunteer.R;
import com.redvolunteer.Utils.NetworkCheker;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.pojo.RequestHelp;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;

public class RequestWallFragment extends Fragment {

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
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Setup Toolbar
     */
    private void SetupToolbar(View layout){
        layout.findViewById(R.id.new_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewReguestHelpActivity.class));
            }
        });
    }


    /**
     * Initialize the ListView with helpRequest
     */

    private void initializeListView(final List<RequestHelp> helpList){





    }

    @Override
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
                            initia
                        }


                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })



            }




        }
    }

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
