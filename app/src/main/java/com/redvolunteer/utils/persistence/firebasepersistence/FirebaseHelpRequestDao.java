package com.redvolunteer.utils.persistence.firebasepersistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.utils.persistence.RemoteRequestDao;

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
    public Flowable<RequestHelp> loadNewRequests(int NumResult, int anchorID) {

        return Flowable.create(new LoadRequestAsync(NumResult, anchorID), BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<RequestHelp>> loadRequestByAdmin(String adminID) {
        return null;
    }

    @Override
    public RequestHelp save(RequestHelp requestToStore) {
        return null;
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
        LoadRequestAsync(int numResult)

        @Override
        public void subscribe(@NonNull FlowableEmitter<List<RequestHelp>> e) throws Exception {

        }
    }
}
