package com.redvolunteer.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.ValidateUtils;
import com.redvolunteer.viewmodels.UserViewModel;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

public class RegisterX extends AppCompatActivity {


    /**
     * Facebook register
     */
    private Button fbRegister;

    /**
     * Handle callback
     */
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
     * layout
     */
    private Button register;
    private EditText mName;
    private EditText mSurname;
    private Spinner mGender;
    private EditText mEmail;
    private EditText mBirthday;

    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        bindLayoutComponents();
        super.onCreate(savedInstanceState);

        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(RegisterX.this, RegisterX.this.getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }
        this.mUserViewModel = ((RedVolunteerApplication) getApplicationContext()).getUserViewModel();

        setupFacebookRegister();
        setupFirebaseAuth();

    setContentView(R.layout.register_helpseeker);

    this.bindFacebookButton();

    }

    /**
     * Binding layout components
     */
    private void bindLayoutComponents(){

        mName = (EditText) findViewById(R.id.Name_register);
        mSurname = (EditText) findViewById(R.id.Surname_register);
        mEmail = (EditText) findViewById(R.id.Email_register);
        mGender = (Spinner) findViewById(R.id.RegisterX_gender);
        mBirthday = (EditText) findViewById(R.id.RegisterX_birth_date);
        register = (Button) findViewById(R.id.register_helpseeker);

    }

    private void setupFirebaseAuth(){
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register without Facebook
     */
























    private void setupFacebookRegister(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager  = CallbackManager.Factory.create();
    }

    private void bindFacebookButton(){

        fbRegister = (Button) findViewById(R.id.facebook_register_btnSecond);


        LoginManager.getInstance().registerCallback(fbCallBackManager, new FacebookRegisterRequestCallBack());

        fbRegister.setOnClickListener(new View.OnClickListener() {

            final List<String> requestPermissions = Arrays.asList(
                    "public_profile",
                    "email"
            );

            @Override
            public void onClick(View view) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                    LoginManager.getInstance().logInWithReadPermissions(RegisterX.this, requestPermissions);
                } else {
                    showNoInternetConnectionToast();
                }
            }
        });
    }


    private class FacebookRegisterRequestCallBack implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult registerResult) {
            showWhaitSpinner();
            handleFacebookAccesTokenForFirebase(registerResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            stopSpinner();
            Toast.makeText(getApplicationContext(), R.string.facebook_cancel_string, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException error) {
            stopSpinner();
            Toast.makeText(getApplicationContext(), R.string.facebook_login_error_string, Toast.LENGTH_LONG).show();
        }
    }

    private void handleFacebookAccesTokenForFirebase(AccessToken token){

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        buildFirebaseUser(credential);
    }

    private void buildFirebaseUser(final AuthCredential credential){

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mUserViewModel.retireveUSerFromRemoteStore().subscribe(new Consumer<User>() {
                                @Override
                                public void accept(User user) throws Exception {

                                    startMainActivity();
                                }
                            });
                        } else {
                            stopSpinner();
                            Toast.makeText(getApplicationContext(), R.string.facebook_error_credential_string, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void startMainActivity(){

        stopSpinner();

        if(this.mUserViewModel.isAuth()){
            startActivity(new Intent(this, MainActivity.class));

        } else {
            Toast.makeText(this, "E-pasto klaida", Toast.LENGTH_LONG).show();
        }
    }

    private void showWhaitSpinner(){

        popupProgDialog = ProgressDialog.show(this, "", getString(R.string.loading_text), true);
    }

    private void stopSpinner(){
        if(popupProgDialog != null)

            popupProgDialog.dismiss();
        popupProgDialog = null;

    }

    private void showNoInternetConnectionToast(){

        stopSpinner();

        Toast.makeText(getApplicationContext(), R.string.no_internet_popup_label, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        showWhaitSpinner();
        super.onActivityResult(requestCode, resultCode, data);
        // facebook request has NO USER DEFINED CODEs
        fbCallBackManager.onActivityResult(requestCode, resultCode, data);
    }
}
