package com.redvolunteer.utils.persistence.firebasepersistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.RemoteUserDao;

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
    public void save(User userToStore) {
        //USer ALLWAYS has an a ID
        String userID = userToStore.getId();

        //save the request to store
        userID = this.dataRef.child(userID).getKey();
        //save in database;
        this.dataRef.child(userID).setValue(userToStore);
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
