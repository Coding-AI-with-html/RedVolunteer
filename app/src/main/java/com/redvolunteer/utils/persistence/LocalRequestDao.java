package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.RequestHelp;

import java.util.List;

public interface LocalRequestDao {

    /**
     * delete every requestStored
     */
    void wipe();

    /**
     * it returns a list of requests
     */
    List<RequestHelp> getRequest(int numResults, int startOffest);

    /**
     * it saves new requst
     */
    RequestHelp save(RequestHelp requestHelpToStore);

    /**
     * load new request based on its ID
     */
    RequestHelp LoadRequestById(String requestID);
}
