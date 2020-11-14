package com.redvolunteer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.NewRequestHelp.NewReguestHelpActivity;
import com.redvolunteer.R;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;

import org.reactivestreams.Subscription;

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
    private ListView mUserRequests;

    /**
     * Main View Model
     */
    private HelpRequestViewModel mHelpRequestViewModel;

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
}
