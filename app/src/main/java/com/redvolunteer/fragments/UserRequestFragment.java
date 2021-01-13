package com.redvolunteer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.RuntimeExecutionException;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RequestDescriptionActivity;
import com.redvolunteer.adapters.UserRequestHelpWallAdapter;
import com.redvolunteer.newrequesthelp.NewRequestHelpActivity;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableSubscriber;

public class UserRequestFragment extends Fragment {


    private Subscription requestRetrievedSubscription;

    private FragmentInteractionListener mActivity;

    private HelpRequestViewModel mRequestHelpViewModel;

    private UserViewModel mUserViewModel;

    private LinearLayout mNoRequestLayout;


    private ListView mRequestListView;

    private ProgressDialog popupProgDialog;

    public UserRequestFragment() {
        //empty constructor required
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindLayoutComponents(view);
        InitializeView(new ArrayList<RequestHelp>());
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestHelpViewModel = mActivity.getHelpRequestViewModel();
        mUserViewModel = mActivity.getUserViewModel();
    }


    private void InitializeView(final List<RequestHelp> requestHelpList){
        UserRequestHelpWallAdapter userRequestHelpWallAdapter = new UserRequestHelpWallAdapter(requestHelpList, getActivity(), mUserViewModel.retrieveCachedUser());
        mRequestListView.setAdapter(userRequestHelpWallAdapter);
        mRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                RequestHelp selectedRequest = requestHelpList.get(position);

                Intent  intent = new Intent(getActivity(), RequestDescriptionActivity.class);
                intent.putExtra(ExtraLabels.REQUEST, selectedRequest.getId());

                startActivity(intent);
            }
        });


    }

    private void bindLayoutComponents(View view){

        setupTopbar(view);
        this.mRequestListView = (ListView) view.findViewById(R.id.myrequests_listview);
        this.mNoRequestLayout = (LinearLayout) view.findViewById(R.id.baggar_all_requests);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_request, container, false);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if(!hidden){

        showWaitSpinner();

    if(NetworkCheker.getInstance().isNetworkAvailable(getContext())){

    mRequestHelpViewModel.getUserHelpRequests().subscribe(new FlowableSubscriber<List<RequestHelp>>() {
    @Override
    public void onSubscribe(@io.reactivex.annotations.NonNull Subscription s) {

    s.request(1L);


    if(requestRetrievedSubscription != null){
        requestRetrievedSubscription.cancel();
    }

    requestRetrievedSubscription = s;
    }

    @Override
    public void onNext(List<RequestHelp> requestHelps) {

    StopWhaitSpinner();

    if(requestHelps.size() != 0){
        InitializeView(requestHelps);
    } else {
        mRequestListView.setVisibility(View.GONE);
        mNoRequestLayout.setVisibility(View.VISIBLE);
    }



    }

    @Override
    public void onError(Throwable t) {

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

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentInteractionListener){
            mActivity = (FragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(requestRetrievedSubscription != null){
            requestRetrievedSubscription.cancel();
        }
    }

    private void setupTopbar(View Vlayout){
        Vlayout.findViewById(R.id.myrequest_new_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewRequestHelpActivity.class));
            }
        });

    }

    private void showWaitSpinner() {
        // creation of the popup spinner
        // it will be shown until the event is fully loaded
        this.popupProgDialog = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    private void StopWhaitSpinner(){
        if(this.popupProgDialog != null) {
            this.popupProgDialog.dismiss();
        }
    }

    /**
     * It shows a Dialog containing an error to the user
     */
    private void ShowRetrievedErrorPopupDialog(){
        showWaitSpinner();
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

    private void ShowNoInternetConnection(){
        StopWhaitSpinner();
        //There was an error show error message
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
