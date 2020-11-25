package com.redvolunteer.utils.userhandling;

import android.content.Context;

import com.redvolunteer.R;
import com.redvolunteer.pojo.User;

public class DefaultUSerFiller {
    private static final DefaultUSerFiller ourInstance = new DefaultUSerFiller();

    private Context sysContext;


    public static DefaultUSerFiller getInstance(){
        return ourInstance;
    }

    public DefaultUSerFiller() {
    }



    public void init(Context appContext){
        this.sysContext = appContext;
    }



    public User fillNEwUserWithDefaultsValues(User newUser){

        String defaultImage = sysContext.getString(R.string.user_default_image);
        String defaultBio = sysContext.getString(R.string.user_bio_default);

        newUser.setPhoto(defaultImage);
        newUser.setBiography(defaultBio);

        return newUser;
    }
}
