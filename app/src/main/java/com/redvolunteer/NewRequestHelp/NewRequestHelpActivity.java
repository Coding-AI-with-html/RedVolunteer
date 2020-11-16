package com.redvolunteer.NewRequestHelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;

import java.util.LinkedList;

public class NewRequestHelpActivity extends AppCompatActivity  {

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
     * PLACE PICKER CONSTANT
     * @param savedInstanceState
     */
    private static final int PLACE_PICKER_REQUEST = 1;

    /**
     * Layout
     */
    private EditText mHelpRequestName;
    private TextView mRetrievedPositionLabel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_help_request);
        MainViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();
        mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();
    }

    /**
     * Binds UI to the layout
     */
    private void bind(final View view){




    }

    public User getHelpRequestCreator(){

        return mUserViewModel.retrieveCachedUser();
    }

    public RequestLocation getUserLocation(){
        return MainViewModel.getUserLocation();
    }




    public void finish(RequestHelp newRequestHelp){
        this.newHelpRequest = newRequestHelp;

        MainViewModel.createNewHelpRequest(newRequestHelp);


        startActivity(new Intent(this, MainActivity.class));

    }


}
