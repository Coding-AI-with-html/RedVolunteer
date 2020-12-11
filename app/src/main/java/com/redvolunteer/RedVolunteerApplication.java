package com.redvolunteer;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.dataModels.UserModeAsynclmlp;
import com.redvolunteer.utils.auth.Auth20FirebaseHandlerlmpl;
import com.redvolunteer.utils.auth.Auth20Handler;
import com.redvolunteer.utils.persistence.LocalUserDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseUserDao;
import com.redvolunteer.utils.persistence.sharedpreferencespersistence.LocalUserDaolmpl;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.dataModels.UserModel;

public class RedVolunteerApplication extends Application {

    /**
     * Handlers of the low level
     */
    private UserModel mUserModel;
    private RequestHelpModel mRequestHelpModel;


    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseApp.initializeApp(this);

        RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), getString(R.string.database_all_users));
        Auth20Handler loginHandler = new Auth20FirebaseHandlerlmpl(FirebaseAuth.getInstance(), remoteUserDao);

        LocalUserDao localUserDao = new LocalUserDaolmpl(this);


        mUserModel = new UserModeAsynclmlp(localUserDao, loginHandler, remoteUserDao);
    }


    /**
     * Retrieve the user ViewModel pf the system
     */

    public UserViewModel getUserViewModel(){
        return  new UserViewModel(getUserModel());
    }


    /**
     * USer Model getter
     */

    private UserModel getUserModel(){
        return mUserModel;
    }

    /**
     * Retrieve Help Request ViewModel on the system
     */

    public HelpRequestViewModel getHelpRequestViewModel(){
        return  new HelpRequestViewModel(getHelpRequestModel());
    }


    /**
     * HelpRequest Model getter
     */
    private RequestHelpModel getHelpRequestModel(){
        return mRequestHelpModel;
    }
}
