package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.RequestHelp;

import java.util.List;

import io.reactivex.Flowable;

public interface RemoteRequestDao {


    /**
     * it load's new Request
     * @return
     */
    Flowable<List<RequestHelp>> loadNewRequests(int NumResult, int anchorID);

    /**
     * it loads user created help request
     */
    Flowable<List<RequestHelp>> loadRequestByAdmin(String adminID);


    /**
     * Saves new help request
     */
    RequestHelp save(RequestHelp requestToStore);


}
