package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.User;

import io.reactivex.Flowable;

public interface UserModel {

    /**
     * Retrieve logged user
     * @return
     */
    Flowable<User> retrievedLoggedUSer();


    /**
     * Retunrs true if user currently Logged,false otherwise
     */

    boolean isAuth();

    /**
     * it Sign out User
     */
    void signOut();

    /**
     * Get's stored user locally
     */
    User GetLocalUser();

    /**
     * Updates the current user
     */
    void updateUSer(User userToStore);

    /**
     * Returns volunteer who joined;
     */
    Flowable<User> retrievedUserById(String userID);


}
