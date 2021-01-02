package com.redvolunteer.utils.persistence.firebasepersistence;

import android.service.autofill.Dataset;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.utils.persistence.RemoteRequestDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class FirebaseHelpRequestDao implements RemoteRequestDao {

    private static final String TAG = "FirebaseHelpRequestDao";
    /**
     * Description field in the Request Store
     */
    private static final String DESCRIPTION_FIELD =  "description";


    /**
     * Holds the ref of the data store
     */
    private DatabaseReference mRequestStore;

    public FirebaseHelpRequestDao(FirebaseDatabase FireDatabase, String requestStoreName) {
        //access to the remote user store
        this.mRequestStore = FireDatabase.getReference(requestStoreName);
    }



    @Override
    public Flowable<List<RequestHelp>> loadNewRequests(int numResult, int anchorID) {

        return Flowable.create(new LoadRequestAsync(numResult, anchorID), BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<RequestHelp>> loadRequestByAdmin(String adminID) {
        return Flowable.create(new FlowableOnSubscribe<List<RequestHelp>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<RequestHelp>> FlowEmitter) throws Exception {

                mRequestStore
                        .orderByChild("adminID")  //its necessary to access the children
                        .equalTo(adminID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot Datsnapshot) {

                                List<RequestHelp> retrieved = new ArrayList<>();
                                //retrieve data of the request from the DATABASE;
                                for(DataSnapshot snapshot: Datsnapshot.getChildren()){
                                    RequestHelp wrapper = snapshot.getValue(RequestHelp.class);
                                    retrieved.add(wrapper);
                                }
                                FlowEmitter.onNext(retrieved);
                            }

                            @Override
                            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                //requeired but not needed
                            }
                        });

            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<RequestHelp> LoadRequestById(final String requestID) {
        return Flowable.create(new FlowableOnSubscribe<RequestHelp>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<RequestHelp> FlowableEmitter) throws Exception {
                mRequestStore
                        .orderByKey()
                        .equalTo(requestID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                                RequestHelp retrieved;
                                try {
                                    DataSnapshot requestWrapper = snapshot.getChildren().iterator().next();
                                    retrieved = requestWrapper.getValue(RequestHelp.class);

                                } catch (NoSuchElementException e){
                                    retrieved = new RequestHelp();
                                }
                                FlowableEmitter.onNext(retrieved);
                            }

                            @Override
                            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                                //required thing, but it cannot happen;
                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public RequestHelp save(RequestHelp requestToStore) {

        String requestID;

        requestID = requestToStore.getId();
        if(requestID == null){

            // get the key from the store and assign to the request
            requestID = this.mRequestStore.push().getKey();
            requestToStore.setId(requestID);

            //save in firebase
            this.mRequestStore.child(requestID).setValue(requestToStore);
        } else {
            this.updateRequest(requestToStore);
        }


        return requestToStore;
    }

    private void updateRequest(RequestHelp requestToUpdate){
        // you can just edit the description of the request
        this.mRequestStore.child(requestToUpdate.getId()).child(DESCRIPTION_FIELD).setValue(requestToUpdate.getDescription());
    }

    @Override
    public Flowable<List<RequestHelp>> LoadRequestByIds(final List<String> requestID) {
        return Flowable.create(new FlowableOnSubscribe<List<RequestHelp>>() {
            @Override
            public void subscribe(final FlowableEmitter<List<RequestHelp>> FlowEmitter) throws Exception {

                final List<RequestHelp> retrievedRequestHelps = new ArrayList<>();

                final Set<String> uniqueID = new HashSet<>(requestID);

                for(String requestID: uniqueID){

                    LoadRequestById(requestID).subscribe(new Consumer<RequestHelp>() {
                        @Override
                        public void accept(RequestHelp requestHelp) throws Exception {

                            retrievedRequestHelps.add(requestHelp);


                            if(retrievedRequestHelps.size() == uniqueID.size()){
                                FlowEmitter.onNext(retrievedRequestHelps);
                            }
                        }
                    });
                }
            }
        }, BackpressureStrategy.BUFFER);
    }


    private class LoadRequestAsync implements FlowableOnSubscribe<List<RequestHelp>>{

        /**
         * Number of request result
         */
        private int mNumberResult = 0;


        /**
         * value of anchor
         */

        private int mAnchor;

        /**
         * Creation of the data retrieve Back
         */
        LoadRequestAsync(int numResult, int anchor){
            this.mNumberResult = numResult;
            this.mAnchor = anchor;
        }

        @Override
        public void subscribe(final FlowableEmitter<List<RequestHelp>> e) throws Exception {

            mRequestStore.orderByKey();
            if(mAnchor != 0){
                mRequestStore.limitToFirst(mAnchor);
            } else {
                mRequestStore.limitToFirst(mNumberResult);
            }

            mRequestStore
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                            List<RequestHelp> firebaseRequests = new ArrayList<>();

                            for(DataSnapshot ds: snapshot.getChildren()){
                                RequestHelp wrapper = ds.getValue(RequestHelp.class);
                                firebaseRequests.add(wrapper);
                            }

                            if(mAnchor != 0){

                                if(mAnchor > firebaseRequests.size()){
                                    mAnchor = firebaseRequests.size();
                                }
                                firebaseRequests = firebaseRequests.subList(mAnchor, firebaseRequests.size());
                            }

                            e.onNext(firebaseRequests);
                            Log.d(TAG, "onDataChange: " + firebaseRequests);

                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                            //not needed, but required
                        }
                    });
        }
    }
}
