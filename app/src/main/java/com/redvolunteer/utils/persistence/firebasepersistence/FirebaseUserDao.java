package com.redvolunteer.utils.persistence.firebasepersistence;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.RemoteUserDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class FirebaseUserDao implements RemoteUserDao {

    private static final String TAG = "FirebaseUserDao";


    private static final String BLOCKED_USER_LIST_FIELD =  "blocked_users";
    private static final String BLOCKED_ID_FIELD = "blockID";
    /**
     * ref to firebase
     */
    private DatabaseReference dataRef;


    @Override
    public Flowable<User> loadById(String userID) {
        return Flowable.create(new UserLoaderProvider(userID), BackpressureStrategy.BUFFER);
    }


    @Override
    public Flowable<Map<String, User>> loadByIds(List<String> userIDs) {
        return Flowable.create(new FlowableOnSubscribe<Map<String, User>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Map<String, User>> FlowEmitter) throws Exception {

                final Map<String, User> retrievedUser = new HashMap<>();

                final Set<String> uniqueID = new HashSet<>(userIDs);


                for(String userID: uniqueID){


                    loadById(userID).subscribe(new Consumer<User>() {
                        @Override
                        public void accept(User user) throws Exception {
                            retrievedUser.put(user.getId(), user);

                            //if retrieved user number is equal to the requested ID, then the load is complected

                            if(retrievedUser.size() == uniqueID.size()){
                                FlowEmitter.onNext(retrievedUser);

                            }



                        }
                    });
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public void blockUser(User CurrentUser, String BlockUserID) {

        String userID = CurrentUser.getId();

        if(BlockUserID == null) {
            this.dataRef.child(userID).child(BLOCKED_USER_LIST_FIELD).child(BLOCKED_ID_FIELD).setValue(BlockUserID);
        }
    }


    @Override
    public void save(User userToStore) {
        //USer ALLWAYS has an a ID
        String userID = userToStore.getId();

        //save the request to store
       userID = this.dataRef.child(userID).getKey();
        //save in database;
        this.dataRef.child(userID).setValue(userToStore);
    }

    @Override
    public Flowable<List<User>> LoadUserForMessages() {
        return Flowable.create(new FlowableOnSubscribe<List<User>>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<List<User>> FlowE) throws Exception {

                        dataRef
                                .orderByChild("id")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                                        List<User> mUserList = new ArrayList<>();

                                        for(DataSnapshot ds: snapshot.getChildren()){

                                            User wrapper = ds.getValue(User.class);
                                            mUserList.add(wrapper);
                                        }
                                        FlowE.onNext(mUserList);

                                    }

                                    @Override
                                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                        //requiered but not needed
                                    }
                                });

                    }
                }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<User>> LoadBlockedList(String CurrentUserID) {
        return Flowable.create(new FlowableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<User>> FLOWe) throws Exception {

                dataRef.child(CurrentUserID);
                dataRef.child(BLOCKED_USER_LIST_FIELD);
                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                        List<User> userIds = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User userId = ds.getValue(User.class);
                            Log.d(TAG, "onDataChange: " + userId);
                            userIds.add(userId);
                        }

                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });
            }
        },BackpressureStrategy.BUFFER);
    }

    public FirebaseUserDao(FirebaseDatabase firebaseDatabase, String userStoreName){

        //access to remote the user store
        this.dataRef = firebaseDatabase.getReference(userStoreName);
    }


    private class UserLoaderProvider implements FlowableOnSubscribe<User>{

        private String userID;

    @Override
    public void subscribe(@NonNull FlowableEmitter<User> FlowEmiter) throws Exception {

        dataRef
                .orderByKey()
                .equalTo(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                        User retrieved;
                        try {

                            DataSnapshot userWrap = snapshot.getChildren().iterator().next();
                            retrieved = userWrap.getValue(User.class);
                        } catch (NoSuchElementException e){
                            retrieved = new User();
                        }
                        /**
                         * send request user retrieved
                         */
                        FlowEmiter.onNext(retrieved);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                        //requered, but not needed
                    }
                });
    }

    /**
     * Creation of the callback retrieve
     * @param userID
     */
    UserLoaderProvider(String userID){
        this.userID = userID;
    }
}



}
