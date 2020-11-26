package com.redvolunteer.utils.persistence.firebasepersistence;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.utils.persistence.RemoteRequestDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class FirebaseHelpRequestDao implements RemoteRequestDao {

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
                        .orderByChild("adminID")  //its necessary to access the childer
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
        public void subscribe(@NonNull FlowableEmitter<List<RequestHelp>> FlowEmitter) throws Exception {

            mRequestStore.orderByKey();

            if(mAnchor != 0){
                mRequestStore.limitToFirst(mAnchor);
            } else {
                mRequestStore.limitToFirst(mNumberResult);
            }

            mRequestStore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot Datasnapshot) {

                    List<RequestHelp> firebaseRequests = new ArrayList<>();

                    for(DataSnapshot snapshot: Datasnapshot.getChildren()){
                        RequestHelp wrapper = snapshot.getValue(RequestHelp.class);
                        firebaseRequests.add(wrapper);
                    }


                    if(mAnchor != 0){

                        if(mAnchor > firebaseRequests.size()){
                            mAnchor = firebaseRequests.size();
                        }

                        firebaseRequests = firebaseRequests.subList(mAnchor, firebaseRequests.size());
                    }
                    FlowEmitter.onNext(firebaseRequests);
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                    //required but not needed
                }
            });

        }
    }
}
