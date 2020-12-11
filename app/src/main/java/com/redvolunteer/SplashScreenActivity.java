package com.redvolunteer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.redvolunteer.LoginAndRegister.Login;
import com.redvolunteer.LoginAndRegister.RegisterX;
import com.redvolunteer.utils.StyleUtils;
import com.redvolunteer.viewmodels.UserViewModel;

public class SplashScreenActivity extends AppCompatActivity {

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
        Intent intent;
        UserViewModel mLoginModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        if(mLoginModel.isAuth()){

            mLoginModel.retireveUSerFromRemoteStore();

            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);



        } else {
            intent = new Intent(this, Login.class);
        }
        startActivity(intent);

        finish();

    }





}
