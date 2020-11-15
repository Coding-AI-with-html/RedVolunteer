package com.redvolunteer;

import android.app.Application;

import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;
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
