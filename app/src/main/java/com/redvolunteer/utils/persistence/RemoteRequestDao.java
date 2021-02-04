package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.Chat;
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
     * Loads requestHelp based on its id
     */
    Flowable<RequestHelp> LoadRequestById(String requestID);


    /**
     * Saves new help request
     */
    RequestHelp save(RequestHelp requestToStore);

    RequestHelp deleteRequest(RequestHelp requestHelpDeleteID);


    Flowable<List<RequestHelp>> LoadRequestByIds(List<String> requestID);
}
