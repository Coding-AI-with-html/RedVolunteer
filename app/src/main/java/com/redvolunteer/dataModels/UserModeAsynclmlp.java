package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.auth.Auth20Handler;
import com.redvolunteer.utils.persistence.LocalUserDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class UserModeAsynclmlp implements UserModel {


    private final LocalUserDao localUserDao;

    private Auth20Handler loginHandler;

    private final RemoteUserDao remoteUserStore;



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
    public Flowable<User> retrievedUserById(String userID) {
        return remoteUserStore.loadById(userID);
    }

    public UserModeAsynclmlp(LocalUserDao localUserDao, Auth20Handler loginHandler, RemoteUserDao remoteUserStore) {
        this.localUserDao = localUserDao;
        this.loginHandler = loginHandler;
        this.remoteUserStore = remoteUserStore;
    }
}