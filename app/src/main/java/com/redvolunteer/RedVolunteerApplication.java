package com.redvolunteer;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.dataModels.HelpRequestModellmpl;
import com.redvolunteer.dataModels.UserModeAsynclmlp;
import com.redvolunteer.utils.auth.Auth20FirebaseHandlerlmpl;
import com.redvolunteer.utils.auth.Auth20Handler;
import com.redvolunteer.utils.persistence.LocalRequestDao;
import com.redvolunteer.utils.persistence.LocalUserDao;
import com.redvolunteer.utils.persistence.RemoteRequestDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseHelpRequestDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseUserDao;
import com.redvolunteer.utils.persistence.sharedpreferencespersistence.LocalUserDaolmpl;
import com.redvolunteer.utils.persistence.sqlitelocalpersistence.LocalSQLiteRequestDao;
import com.redvolunteer.utils.requestutils.DistanceManager;
import com.redvolunteer.utils.requestutils.DistanceManagerlimp;
import com.redvolunteer.utils.userhandling.DefaultUSerFiller;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.dataModels.UserModel;

public class RedVolunteerApplication extends Application {

    /**
     * Handlers of the low level
     */
    private UserModel mUserModel,mUserModelVol;
    private RequestHelpModel mRequestHelpModel, RequestHelpModelVolunteer;


    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseApp.initializeApp(this);

        RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), getString(R.string.database_Help_seekers));
        RemoteUserDao remoteUserVolunteer = new FirebaseUserDao(FirebaseDatabase.getInstance(), getString(R.string.database_Volunteers));
        RemoteRequestDao remoteRequestDao = new FirebaseHelpRequestDao(FirebaseDatabase.getInstance(), getString(R.string.firebase_request_store_name));
        Auth20Handler loginHandler = new Auth20FirebaseHandlerlmpl(FirebaseAuth.getInstance(), remoteUserDao);
        Auth20Handler loginHandlerVol = new Auth20FirebaseHandlerlmpl(FirebaseAuth.getInstance(), remoteUserVolunteer);

        LocalUserDao localUserDao = new LocalUserDaolmpl(this);
        LocalRequestDao localRequestDao = new LocalSQLiteRequestDao(this);

        //Utilities
        DefaultUSerFiller.getInstance().init(this);
        DistanceManager distanceManager = new DistanceManagerlimp();
        mUserModel = new UserModeAsynclmlp(localUserDao, loginHandler, remoteUserDao);
        mUserModelVol = new UserModeAsynclmlp(localUserDao, loginHandlerVol, remoteUserVolunteer);
        mRequestHelpModel = new HelpRequestModellmpl(remoteRequestDao, remoteUserDao, mUserModel, localRequestDao, distanceManager);
        RequestHelpModelVolunteer = new HelpRequestModellmpl(remoteRequestDao, remoteUserVolunteer, mUserModelVol, localRequestDao, distanceManager);
    }


    /**
     * Retrieve the user ViewModel pf the system
     */

    public UserViewModel getUserViewModel(){
        return  new UserViewModel(getUserModel());
    }
    /**
     * Retrieve the user ViewModelVolunterr pf system
     */
    public UserViewModel getUserViewModelVolunteer(){
        return new UserViewModel(getmUserModelVol());
    }


    /**
     * USer Model getter
     */

    private UserModel getUserModel(){
        return mUserModel;
    }

    private UserModel getmUserModelVol(){
        return mUserModelVol;
    }

    /**
     * Retrieve Help Request ViewModel on the system
     */

    public HelpRequestViewModel getHelpRequestViewModel(){
        return  new HelpRequestViewModel(getHelpRequestModel());
    }
    /**
     * Retrieve Help Request ViewModel
     */
    public HelpRequestViewModel getHelpRequestViewModelVolunteer(){
        return new HelpRequestViewModel(getRequestHelpModelVolunteer());
    }

    /**
     * HelpRequest Model getter
     */
    private RequestHelpModel getHelpRequestModel(){
        return mRequestHelpModel;
    }

    private RequestHelpModel getRequestHelpModelVolunteer(){
        return RequestHelpModelVolunteer;
    }

}
