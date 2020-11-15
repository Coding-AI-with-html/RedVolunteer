package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;

import java.util.List;

import io.reactivex.Flowable;

public interface RequestHelpModel {

    /**
    *Returns created help request, its for
     */
    Flowable<List<RequestHelp>> getUserHelpRequests();

    /**
     * set the location of the user
     */
    void setLocation(RequestLocation location);

    /**
     * Store new HelpRequest
     */
    void storeHelpRequest(RequestHelp requestHelp);

    /**
     * Retrieves the location of the user
     */
    RequestLocation getUserLocation();

    /**
     *
     */









}
