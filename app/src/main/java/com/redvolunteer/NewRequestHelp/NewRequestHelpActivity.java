package com.redvolunteer.NewRequestHelp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private EditText mRequestDescription;


    /**
     * Location Retrieved
     */
    private RequestLocation mRetrievedLocation;


    /**
     * If Fields were be empty
     */
    private boolean nameSelected = false;
    private boolean positionSelected = false;
    private boolean mDescriptionWrited = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_help_request);
        MainViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();
        mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();
        bind();
    }

    /**
     * Binds UI to the layout
     */
    private void bind(){
        mHelpRequestName = (EditText) findViewById(R.id.new_request_name);
        mRequestDescription = (EditText) findViewById(R.id.new_request_description);
        LinearLayout choseMap = (LinearLayout) findViewById(R.id.new_help_request_map);

        ImageView mBackButton = (ImageView) findViewById(R.id.new_request_cancel_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mHelpRequestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameSelected = mHelpRequestName.getText().length() > 0;
            }
        });

        choseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



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
