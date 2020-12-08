package com.redvolunteer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.redvolunteer.LoginAndRegister.Login;
import com.redvolunteer.LoginAndRegister.RegisterX;
import com.redvolunteer.utils.StyleUtils;

public class SplashScreenActivity extends Activity {

    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
                mContext = getApplicationContext();
        StyleUtils styleUtils = new StyleUtils();

        styleUtils.setStatusBarColor(getWindow(), getColor(R.color.mainColorRed));
        styleUtils.setNavigationBarColor(getWindow(),getColor(R.color.mainColorRed));
                loading();

    }
    private void loading()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gatekeeper();
            }
        },2000);
    }


    private void gatekeeper()
    {

            startActivity(new Intent(SplashScreenActivity.this, Login.class));

    }
    //check if user logged in or not
    private void checkIsUserLoggedIn(FirebaseUser Fuser){

        if (Fuser == null){

        }
    }


}
