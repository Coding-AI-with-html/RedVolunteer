package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;

import java.util.List;

import io.reactivex.Flowable;

public interface RequestHelpModel {
    /**
     * Returns help request where volunteer might been accepted, it based on volunteer location
     */
    Flowable<List<RequestHelp>> getRequests();
    /**
    *Returns created help request, its for
     */
    Flowable<List<RequestHelp>> getUserHelpRequests();
    /**
     * Returns request the user may subscribe. It sorts them based on the user location
     */
    Flowable<List<RequestHelp>> getNewRequests();

    Flowable<List<Chat>> getUserMessages();

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
     *Retunrs the requesst based by id
     */
    Flowable<RequestHelp> getEventByID(String requestID);


    /**
     * Delete request Method
     */
    void deleteRequest(RequestHelp requestID);
}
