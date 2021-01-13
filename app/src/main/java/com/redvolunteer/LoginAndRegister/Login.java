package com.redvolunteer.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.pojo.User;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";


    /**
     * For registering with google credentials
     */
    private static final int GOOGLE_REQ_LOGIN_CODE = 1;
    /**
     * Facebook register
     */
    private Button fbRegister;

    private Button GoogleRegister;

    /**
     * Handle callback
     */
    private CallbackManager fbCallBackManager;

    private GoogleApiClient mGoogleApiClient;

    /**
     * Firebase authentication manager
     */
    private FirebaseAuth mAuth;
    /**
     * Handle to register with WM
     */
    private UserViewModel mUserViewModel;


    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(Login.this, Login.this.getString(R.string.no_internet_popup_label), Toast.LENGTH_LONG).show();
        }
        this.mUserViewModel = ((RedVolunteerApplication) getApplicationContext()).getUserViewModel();

        setupFacebookRegister();
        setupFirebaseAuth();
        setupGoogleLogin();

        setContentView(R.layout.login_activity);


        this.bindFacebookButton();
        this.BindGoogleButton();


    }



    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register without Facebook
     */


    private void setupFacebookRegister(){

        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager  = CallbackManager.Factory.create();
    }



    private void bindFacebookButton(){

        fbRegister = (Button) findViewById(R.id.facebook_login_btn);


        LoginManager.getInstance().registerCallback(fbCallBackManager, new FacebookRegisterRequestCallBack());

        fbRegister.setOnClickListener(new View.OnClickListener() {

            final List<String> requestPermissions = Arrays.asList(
                    "public_profile",
                    "email"
            );

            @Override
            public void onClick(View view) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                    LoginManager.getInstance().logInWithReadPermissions(Login.this, requestPermissions);
                } else {
                    showNoInternetConnectionToast();
                }
            }
        });
    }
    private void setupGoogleLogin(){

        GoogleSignInOptions Goptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(Login.this,
                                "Prisijunkite prie interneto!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, Goptions)
                .build();
    }

    private void BindGoogleButton(){

        GoogleRegister = (Button) findViewById(R.id.google_login_btn);

        GoogleRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, GOOGLE_REQ_LOGIN_CODE);
                } else {
                    showNoInternetConnectionToast();
                }
            }
        });



    }

    private void handleGoogleAccesTokenForFireBase(GoogleSignInAccount Googleaccount){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(Googleaccount.getIdToken(), null);
        buildFirebaseUser(authCredential);
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
            Log.d(TAG, "onError: " + error);
            Toast.makeText(getApplicationContext(), R.string.facebook_login_error_string, Toast.LENGTH_LONG).show();
        }
    }

    private void handleFacebookAccesTokenForFirebase(AccessToken token){

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        buildFirebaseUser(credential);
    }

    private void googleLoginResultHandling(GoogleSignInResult resultGoogle){


        if(resultGoogle.isSuccess()){
            handleGoogleAccesTokenForFireBase(resultGoogle.getSignInAccount());
        }
        else {
            stopSpinner();
            Toast.makeText(this, R.string.facebook_login_error_string, Toast.LENGTH_LONG).show();
        }
    }

    private void buildFirebaseUser(final AuthCredential credential){

        mAuth.signInWithCredential(credential)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        showWhaitSpinner();
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_REQ_LOGIN_CODE) {
            GoogleSignInResult GoogleRes = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleLoginResultHandling(GoogleRes);
        } else {
            // facebook request has NO USER DEFINED CODEs
            fbCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
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



}
