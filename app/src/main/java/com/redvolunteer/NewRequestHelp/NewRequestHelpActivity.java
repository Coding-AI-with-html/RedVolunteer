package com.redvolunteer.NewRequestHelp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;

import java.util.LinkedList;

public class NewRequestHelpActivity extends AppCompatActivity implements FragmentInteractionListener {

    /**
     * Fragments
     */
    private NewHelpRequestFragmentFirstPage fragmentFirstPage = new NewHelpRequestFragmentFirstPage();
    private  NewHelpRequestFragmentSecondPage fragmentSecondPage = new NewHelpRequestFragmentSecondPage();

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * Request View Model
     */
    private HelpRequestViewModel MainViewModel;

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
                .add(R.id.new_request_fragment, fragmentFirstPage)
                .add(R.id.new_request_fragment, fragmentSecondPage)
                .hide(fragmentSecondPage)
                .commit();
        stack.push(fragmentFirstPage);






    }
}
