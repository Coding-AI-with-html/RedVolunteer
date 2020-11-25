package com.redvolunteer.newrequesthelp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;

import java.util.LinkedList;

public class NewRequestHelpActivity extends AppCompatActivity implements NewHelpRequestFragmentListener {
    private static final String TAG = "NewRequestHelpActivity";



    /**
     * Request being craited
     */
    private RequestHelp newHelpRequest;
    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * Request View Model
     */
    private HelpRequestViewModel MainViewModel;

    /**
     * Fragments
     */
    private NewHelpRequestFragmentFirst fragmentFirst = new NewHelpRequestFragmentFirst();
    private NewHelpRequestFragmentSecond fragmentSecond = new NewHelpRequestFragmentSecond();
    /**
     * Stack that manages the back button
     */
    private LinkedList<Fragment> stack = new LinkedList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_help_request);
        MainViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();
        mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.new_help_request_fragment, fragmentFirst)
                .add(R.id.new_help_request_fragment, fragmentSecond)
                .hide(fragmentSecond)
                .commit();
        stack.push(fragmentFirst);

    }


    /**
     * Shows Second Fragment
     */
    private void attachFragment(Fragment fragment){

        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentFirst)
                .hide(fragmentSecond)
                .show(fragment)
                .commit();
        stack.push(fragment);

    }


    @Override
    public void secondFragment(RequestHelp HelpRequest) {
        newHelpRequest = HelpRequest;
        attachFragment(fragmentSecond);
    }

    @Override
    public RequestHelp getHelpRequest() {
        return newHelpRequest;
    }

    @Override
    public void finish(RequestHelp newHelpRequest) {
        MainViewModel.createNewHelpRequest(newHelpRequest);

        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public User getHelpRequestCreator() {
        return mUserViewModel.retrieveCachedUser();
    }

    @Override
    public RequestLocation getHelpRequestCreatorLocation() {
        return MainViewModel.getUserLocation();

    }

    @Override
    public void onBackPressed() {
        if(stack.isEmpty() || stack.pop() instanceof NewHelpRequestFragmentFirst){
            super.onBackPressed();
        }
        else {
            attachFragment(stack.pop());
        }

    }
}

