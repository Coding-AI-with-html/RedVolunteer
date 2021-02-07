package com.redvolunteer;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.dataModels.HelpRequestModellmpl;
import com.redvolunteer.dataModels.MessageModel;
import com.redvolunteer.dataModels.MessageModellimp;
import com.redvolunteer.dataModels.UserModeAsynclmlp;
import com.redvolunteer.utils.LocalMessageDao;
import com.redvolunteer.utils.auth.Auth20FirebaseHandlerlmpl;
import com.redvolunteer.utils.auth.Auth20Handler;
import com.redvolunteer.utils.persistence.LocalRequestDao;
import com.redvolunteer.utils.persistence.LocalUserDao;
import com.redvolunteer.utils.persistence.RemoteMessageDao;
import com.redvolunteer.utils.persistence.RemoteRequestDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseHelpRequestDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseMessageDao;
import com.redvolunteer.utils.persistence.firebasepersistence.FirebaseUserDao;
import com.redvolunteer.utils.persistence.sharedpreferencespersistence.LocalUserDaolmpl;
import com.redvolunteer.utils.persistence.sqlitelocalpersistence.LocalSQLiteMessageDao;
import com.redvolunteer.utils.persistence.sqlitelocalpersistence.LocalSQLiteRequestDao;
import com.redvolunteer.utils.requestutils.DistanceManager;
import com.redvolunteer.utils.requestutils.DistanceManagerlimp;
import com.redvolunteer.utils.userhandling.DefaultUSerFiller;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.MessageViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.dataModels.UserModel;

public class RedVolunteerApplication extends Application {

    /**
     * Handlers of the low level
     */
    private UserModel mUserModel;
    private RequestHelpModel mRequestHelpModel;
    private MessageModel mMessageModel;

    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseApp.initializeApp(this);

        RemoteUserDao remoteUserDao = new FirebaseUserDao(FirebaseDatabase.getInstance(), getString(R.string.database_Help_seekers));
        RemoteRequestDao remoteRequestDao = new FirebaseHelpRequestDao(FirebaseDatabase.getInstance(), getString(R.string.firebase_request_store_name));
        RemoteMessageDao remoteMessageDao = new FirebaseMessageDao(FirebaseDatabase.getInstance(), getString(R.string.firebase_message_store_name));
        Auth20Handler loginHandler = new Auth20FirebaseHandlerlmpl(FirebaseAuth.getInstance(), remoteUserDao);

        LocalUserDao localUserDao = new LocalUserDaolmpl(this);
        LocalRequestDao localRequestDao = new LocalSQLiteRequestDao(this);
        LocalMessageDao localMessageDao = new LocalSQLiteMessageDao(this);

        //Utilities
        DefaultUSerFiller.getInstance().init(this);
        DistanceManager distanceManager = new DistanceManagerlimp();
        mUserModel = new UserModeAsynclmlp(localUserDao, loginHandler, remoteUserDao);
        mMessageModel = new MessageModellimp(remoteMessageDao,remoteRequestDao, remoteUserDao,mUserModel, localMessageDao);
        mRequestHelpModel = new HelpRequestModellmpl(remoteRequestDao, remoteUserDao, mUserModel, localRequestDao, distanceManager);
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

    /**
     * Message Model getter
     */
    private MessageModel getMessageModel(){
        return mMessageModel;
    }

    /**
     * Retrieve Message ViewModel on the system
     */
    public MessageViewModel getMessageViewModel(){
        return  new MessageViewModel(getMessageModel());
    }

}
