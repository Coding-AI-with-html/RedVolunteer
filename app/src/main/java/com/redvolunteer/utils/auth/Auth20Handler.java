package com.redvolunteer.utils.auth;

import com.redvolunteer.pojo.User;

import io.reactivex.Flowable;

public interface Auth20Handler {


    /**
     * check if the user authenticated
     */
    boolean isAuth();

    /**
     * signing out user
     */
    void signOut();


    Flowable<User> retrieveUser();
}
