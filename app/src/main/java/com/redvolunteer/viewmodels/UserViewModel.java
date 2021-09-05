package com.redvolunteer.viewmodels;

import com.redvolunteer.dataModels.UserModel;
import com.redvolunteer.pojo.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public class UserViewModel {

    private UserModel mUserModel;

    public UserViewModel(UserModel mUserModel) {
        this.mUserModel = mUserModel;
    }

    public Flowable<User> retireveUSerFromRemoteStore() {
        return mUserModel.retrievedLoggedUSer();
    }
    public User retrieveCachedUser(){
        return mUserModel.GetLocalUser();
    }


    public boolean isAuth(){
        return mUserModel.isAuth();
    }

    public void signOut(){
         mUserModel.signOut();
    }

    public void UpdateUser(User userToSave){
        mUserModel.updateUSer(userToSave);
    }

    public Flowable<User> retrieveUserByID(String userID){
        return mUserModel.retrievedUserById(userID);
    }
    public Flowable<List<User>> retrieveUserForMEssages(){

        return mUserModel.LoadUserForMessages();
    }
    public void blockUser(User CurrentUSer, String BlockuserID){
        mUserModel.blockUserByID(CurrentUSer, BlockuserID);
    }

    public Flowable<List<User>> loadCurrUserBlockedUserList(String CurrentUserID){
       return mUserModel.retrieveCurrentUSerBlockedUser(CurrentUserID);
    }
    public Flowable<List<String>> LoadOtherUserBlockedList(String OtherUserID){
        return mUserModel.retrieveOtherUserBlockedList(OtherUserID);
    }



}
