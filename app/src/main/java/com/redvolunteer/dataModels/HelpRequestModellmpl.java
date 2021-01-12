package com.redvolunteer.dataModels;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.LocalRequestDao;
import com.redvolunteer.utils.persistence.RemoteRequestDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;
import com.redvolunteer.utils.requestutils.DistanceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class HelpRequestModellmpl implements RequestHelpModel {

    private static final String TAG = "HelpRequestModellmpl";
    /**
     * Firebase
     */
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;


    private static final int DISTANCE_FILTER = 10;

    /**
     * Numbers of HelRequest requested
     */
    private static final int NUMBER_HELP_REQUESTED = 20;

    /**
     * Coordinates of Kaunas
     */
    private static final RequestLocation  KAUNAS_POSITION_CENTER = new RequestLocation(54.8985, 23.9036);

    /**
     * Location of the user
     */
    private RequestLocation currentLocation;

    /**
     * DAO operates on the Requests stored on the remote database
     */
    private RemoteRequestDao remoteRequestDao;

    /**
     * Dao operates on the User on the remote database
     */
    private RemoteUserDao remoteUserDao;

    /**
     * Model to operate the Users
     */
    private UserModel userModel;

    /**
     * Dao operates on the Request stored locally
     */
    private LocalRequestDao localRequestDao;

    /***
     * object responsible to sort request based on the distance
     */
    private DistanceManager distanceManager;


    /**
     * anchorID of the current list of Requests
     */
    private int anchorID = 0;

    public HelpRequestModellmpl(RemoteRequestDao remoteRequestDao, RemoteUserDao remoteUserDao, UserModel userModel, LocalRequestDao localRequestDao, DistanceManager distanceManager) {
        this.remoteRequestDao = remoteRequestDao;
        this.remoteUserDao = remoteUserDao;
        this.userModel = userModel;
        this.localRequestDao = localRequestDao;
        this.distanceManager = distanceManager;
    }

    @Override
    public Flowable<List<RequestHelp>> getRequests() {

        anchorID = 0;
        return Flowable.create(new FillRequestDetails(), BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<RequestHelp>> getNewRequests() {
        return Flowable.create(new FillRequestDetails(), BackpressureStrategy.BUFFER);
    }
    @Override
    public Flowable<List<RequestHelp>> getUserHelpRequests() {

        return Flowable.create(new FlowableOnSubscribe<List<RequestHelp>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<RequestHelp>> FlowEmitter) throws Exception {


                remoteRequestDao
                        .loadRequestByAdmin(userID)
                        .subscribe(new Consumer<List<RequestHelp>>() {
                            @Override
                            public void accept(List<RequestHelp> requestHelps) throws Exception {

                                Collections.reverse(requestHelps);
                                FlowEmitter.onNext(requestHelps);
                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public synchronized void setLocation(RequestLocation location) {
        this.currentLocation = location;
    }

    @Override
    public void storeHelpRequest(RequestHelp requestHelp) {

        remoteRequestDao.save(requestHelp);
    }

    @Override
    public synchronized RequestLocation getUserLocation() {

        RequestLocation location = currentLocation;

        if(currentLocation == null)
            location = KAUNAS_POSITION_CENTER;
        return location;
    }

    @Override
    public Flowable<RequestHelp> getEventByID(String requestID) {

        return remoteRequestDao.LoadRequestById(requestID);
    }


    private class FillRequestDetails implements FlowableOnSubscribe<List<RequestHelp>> {


        @Override
        public void subscribe(final FlowableEmitter<List<RequestHelp>> Flowemitter) throws Exception {
            remoteRequestDao
                    .loadNewRequests(NUMBER_HELP_REQUESTED, anchorID)
                    .subscribe(new Consumer<List<RequestHelp>>() {
                        @Override
                        public void accept(List<RequestHelp> requestHelps) throws Exception {


                            //if theres no events, simple send a empty list
                            if(requestHelps.size() !=0){

                                //retrieve anchor to perform future calls
                                anchorID += NUMBER_HELP_REQUESTED;


                                requestHelps = distanceManager.sortByDistanceFromLocation(getUserLocation(), requestHelps, DISTANCE_FILTER);


                                if(requestHelps.size() != 0){

                                    List<String> creatorIDs = new ArrayList<>();

                                    for(RequestHelp retrievedRequests: requestHelps){
                                        creatorIDs.add(retrievedRequests.getHelpRequestCreatorID());

                                    }


                                    List<RequestHelp> finalRequestHelps = requestHelps;
                                    remoteUserDao.loadByIds(creatorIDs).subscribe(new Consumer<Map<String, User>>() {
                                        @Override
                                        public void accept(Map< String, User> stringUserMap) throws Exception {

                                            for (RequestHelp retrievedRequestHelps: finalRequestHelps) {
                                                retrievedRequestHelps.setHelpRequestCreator(stringUserMap.get(retrievedRequestHelps.getHelpRequestCreatorID()));
                                            }

                                            Flowemitter.onNext(finalRequestHelps);
                                        }
                                    });



                                } else {
                                    Flowemitter.onNext(requestHelps);
                                }

                            } else {
                                Flowemitter.onNext(requestHelps);
                            }
                        }
                    });
        }
    }

}
