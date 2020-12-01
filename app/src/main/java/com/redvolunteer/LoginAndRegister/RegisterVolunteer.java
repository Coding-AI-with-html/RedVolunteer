package com.redvolunteer.LoginAndRegister;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.redvolunteer.R;
import com.redvolunteer.utils.ValidateUtils;
import com.redvolunteer.viewmodels.UserViewModel;

public class RegisterVolunteer extends AppCompatActivity {

    /**
     * Facebook register
     */
    private Button fbRegister;

    private CallbackManager fbCallBackManager;

    /**
     * Handle to register with WM
     */
    private UserViewModel mUserViewModel;

    private FirebaseAuth mFirebaseAuth;

    /**
     * Checking inputs
     */
    ValidateUtils inputChecker;

    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_volunteer);




        this.bindFacebookButton();
    }

    private void setupFirebaseAuth(){
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void setupFacebookRegister(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager  = CallbackManager.Factory.create();
    }

    private void bindFacebookButton(){

        fbRegister = (Button) findViewById(R.id.register_button);
    }
}
