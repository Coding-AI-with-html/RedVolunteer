package com.redvolunteer.LoginAndRegister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.redvolunteer.Utils.NetworkCheker;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.pojo.User;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

public class Login extends AppCompatActivity {

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(!NetworkCheker.getInstance().isNetworkAvailable(this)){
            Toast.makeText(Login.this, "Reikia interneto prieigos!", Toast.LENGTH_LONG).show();
        }

        this.loginModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        //setup firebase
        setupFirebaseAuth();
        setupFacebookLogin();


        setContentView(R.layout.login_activity);

        this.bindFacebookButton();
    }

    private void setupFirebaseAuth(){
        //Initialiazing Firebase authenticator;
        mAuth =FirebaseAuth.getInstance();
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

        mFacebookLogin = (Button) this.findViewById(R.id.login_facebook_button);

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
