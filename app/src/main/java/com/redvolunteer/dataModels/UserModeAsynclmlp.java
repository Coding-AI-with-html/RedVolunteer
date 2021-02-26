package com.redvolunteer.dataModels;

import android.util.Log;

import androidx.constraintlayout.helper.widget.Flow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.auth.Auth20Handler;
import com.redvolunteer.utils.persistence.LocalUserDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class UserModeAsynclmlp implements UserModel {


    private static final String TAG = "UserModeAsynclmlp";

    /**
     * Dao to operates on the Users stored locally
     */
    private final LocalUserDao localUserDao;

    /**
     * Login handler
     */
    private Auth20Handler loginHandler;
    /**
     * Dao to operates on the Users stored on the remote database
     */
    private final RemoteUserDao remoteUserStore;
    private UserModel mUserModel;



    @Override
    public Flowable<User> retrievedLoggedUSer() {

        return Flowable.create(new FlowableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<User> e) throws Exception {

                loginHandler.retrieveUser().subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        localUserDao.save(user);

                        e.onNext(user);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public boolean isAuth() {
        return  loginHandler.isAuth();
    }

    @Override
    public void signOut() {

        localUserDao.wipe();

        loginHandler.signOut();
    }

    @Override
    public User GetLocalUser() {

        return localUserDao.load();
    }

    @Override
    public void updateUSer(User userToStore) {

        //update local cache
        localUserDao.save(userToStore);

        remoteUserStore.save(userToStore);
    }

    @Override
    public void blockUserByID(User CurrUser, String userID) {

        remoteUserStore.blockUser(CurrUser, userID);
    }

    @Override
    public Flowable<List<User>> retrieveCurrentUSerBlockedUser(String CurrentUserID) {

        return Flowable.create(new FlowableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<User>> FLowE) throws Exception {
                String userID = localUserDao.load().getId();
                remoteUserStore
                        .LoadBlockedList(userID).subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {

                        for(String id: strings){
                            Log.d(TAG, "acceptss: " + id);
                        }
                    }
                });

            }
        }, BackpressureStrategy.BUFFER);
    }


    @Override
    public Flowable<User> retrievedUserById(String userID) {
        return remoteUserStore.loadById(userID);
    }

    @Override
    public Flowable<List<User>> LoadUserForMessages() {
        return remoteUserStore.LoadUserForMessages();
    }

    public UserModeAsynclmlp(LocalUserDao localUserDao, Auth20Handler loginHandler, RemoteUserDao remoteUserStore) {
        this.localUserDao = localUserDao;
        this.loginHandler = loginHandler;
        this.remoteUserStore = remoteUserStore;
    }

}
