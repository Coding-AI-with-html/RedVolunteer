package com.redvolunteer.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
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
     * Facebook login button
     */
    private Button mFacebookLogin;

    private CallbackManager fbCallBackManager;

    /**
     * Handle to log in with WM
     */
    private UserViewModel loginModel;

    private FirebaseAuth mAuth;

    /**
     * it simple is the popup spinner
     */
    private ProgressDialog popupProgDialog;

    /**
     * Login layout
     **/
    private EditText mEmail, mPassword;
    private ProgressBar mProgBar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(Login.this, R.string.no_internet_popup_label, Toast.LENGTH_LONG).show();
        }

        this.loginModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        //setup firebase
        setupFirebaseAuth();
        setupFacebookLogin();


        setContentView(R.layout.login_activity);

        this.bindFacebookButton();
        bindLoginComponents();
    }

    private void bindLoginComponents(){
        mEmail = (EditText) findViewById(R.id.LoginEmailAddress);
        mPassword = (EditText) findViewById(R.id.PasswordLogin);
        mProgBar = (ProgressBar) findViewById(R.id.ProgBar);

        mProgBar.setVisibility(View.GONE);

        Button mLoginBtn = (Button) findViewById(R.id.btnLogin);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(email.equals("") && password.equals("")){
                    Toast.makeText(getApplicationContext(), "Slaptazodis arba e-pastas neirasytas!", Toast.LENGTH_SHORT).show();
                } else {
                    mProgBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser Fuser = mAuth.getCurrentUser();

                                    if(task.isSuccessful()){
                                        try {
                                            startActivity(new Intent(Login.this, MainActivity.class));

                                        } catch (Exception e){
                                            Log.d(TAG, "onComplete: NullPointerException" + e.getMessage());
                                        }
                                    } else {
                                        Log.d(TAG, "onComplete: failure" + task.getException());
                                        Toast.makeText(Login.this, "Slaptazodis arba e-pastas netinka!", Toast.LENGTH_SHORT).show();
                                        mProgBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                }
            }
        });
       TextView  mRegisterTextView = (TextView) findViewById(R.id.go_to_register_helpseeker);
        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inttent = new Intent(Login.this, RegisterX.class);
                startActivity(inttent);
            }
        });


    }

    private void setupFirebaseAuth(){
        //Initialiazing Firebase authenticator;
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * FB login service instaliazation
     */
    private void setupFacebookLogin(){
        //Iniatiazling FacebookSDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallBackManager = CallbackManager.Factory.create();

    }

    /**
     * Connet the button layout of the facebook to login
     */

    private void bindFacebookButton(){

        mFacebookLogin = (Button) this.findViewById(R.id.facebook_login_btn);

        LoginManager.getInstance().registerCallback(fbCallBackManager, new FacebookLoginRequestCallBack());

        mFacebookLogin.setOnClickListener(new View.OnClickListener() {

            final List<String> requestPermisions = Arrays.asList(
                    "public_profile",
                    "email"
            );
            @Override
            public void onClick(View view) {

                if(NetworkCheker.getInstance().isNetworkAvailable(getApplicationContext())){
                    LoginManager.getInstance().logInWithReadPermissions(Login.this, requestPermisions);
                } else {
                    showNoInternetConnection();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        showWhaitSpiner();
        super.onActivityResult(requestCode, resultCode, data);

        fbCallBackManager.onActivityResult(requestCode,resultCode,data);
    }

    private class FacebookLoginRequestCallBack implements FacebookCallback<LoginResult>{
       @Override
       public void onSuccess(LoginResult loginResult) {
           showWhaitSpiner();
           handleFacebookTokenAccesFromFirebase(loginResult.getAccessToken());
       }

       @Override
       public void onCancel() {
           stopSpinner();
           Toast.makeText(getApplicationContext(),"Prasome nesidroveti",Toast.LENGTH_LONG).show();
       }

       @Override
       public void onError(FacebookException error) {

           stopSpinner();

           Toast.makeText(getApplicationContext(), "Ops, klaida su Facebook", Toast.LENGTH_SHORT).show();
       }
   }


   private void handleFacebookTokenAccesFromFirebase(AccessToken account) {
        AuthCredential credential = FacebookAuthProvider.getCredential(account.getToken());
        buildFirebaseUser(credential);
   }


   private void buildFirebaseUser(final AuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            loginModel.retireveUSerFromRemoteStore().subscribe(new Consumer<User>() {
                                @Override
                                public void accept(User user) throws Exception {
                                    startMainActivity();
                                }
                            });
                        } else {
                            stopSpinner();
                            Toast.makeText(getApplicationContext(), "Klaida su Programeles serveriu",Toast.LENGTH_LONG).show();
                        }
                    }
                });
   }



   private void startMainActivity(){

        stopSpinner();
        //check if auth has been succesfull
       if(this.loginModel.isAuth()){
           startActivity(new Intent(this, MainActivity.class));
           finish();
       } else {
           Toast.makeText(this, "Jusu e-pastas nepatvirtintas", Toast.LENGTH_LONG).show();
       }

   }
   private void showWhaitSpiner(){
       popupProgDialog = ProgressDialog.show(this, "",
               getString(R.string.loading_text), true);
   }

   private void stopSpinner(){
        if(popupProgDialog != null){
            popupProgDialog.dismiss();
            popupProgDialog = null;
        }
   }

   private void showNoInternetConnection(){

        stopSpinner();
       Toast.makeText(getApplicationContext(), "Nera prisijungta prie interneto", Toast.LENGTH_SHORT).show();
    }



}
