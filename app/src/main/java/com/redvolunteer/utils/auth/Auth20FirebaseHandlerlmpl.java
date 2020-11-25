package com.redvolunteer.utils.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.RemoteUserDao;
import com.redvolunteer.utils.userhandling.DefaultUSerFiller;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class Auth20FirebaseHandlerlmpl implements Auth20Handler {


    private FirebaseAuth mFireAuth;


    /**
     * Firebase DAO
     */
    private RemoteUserDao mUserDao;

    @Override
    public boolean isAuth() {
        return mFireAuth.getCurrentUser() != null;
    }

    @Override
    public void signOut() {
        mFireAuth.signOut();
    }

    @Override
    public Flowable<User> retrieveUser() {
        return Flowable.create(new FlowableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<User> FlowEmiter) throws Exception {


                final FirebaseUser FireUSer= mFireAuth.getCurrentUser();

                final String userID = FireUSer.getUid();

                mUserDao.loadById(userID).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User userLogged) throws Exception {
                        if(userLogged.getId() == null){

                            FireUSer.sendEmailVerification();

                            userLogged.setId(FireUSer.getUid());
                            userLogged.setFullName(FireUSer.getDisplayName());
                            userLogged.setEmail(FireUSer.getEmail());

                            userLogged = DefaultUSerFiller.getInstance().fillNEwUserWithDefaultsValues(userLogged);

                            mUserDao.save(userLogged);


                        }
                        FlowEmiter.onNext(userLogged);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }



    public Auth20FirebaseHandlerlmpl(FirebaseAuth firebaseA,RemoteUserDao userDao){
        this.mFireAuth = firebaseA;
        this.mUserDao = userDao;
    }


    /**
     * get the contact of the user
     */
    private String getContact(){

        FirebaseUser mFireUser = mFireAuth.getCurrentUser();


        String contact = mFireUser != null ? mFireUser.getEmail() : null;
        if(contact == null){
            assert mFireUser != null;
            contact = mFireUser.getPhoneNumber();

        }
        return contact;
    }
}
