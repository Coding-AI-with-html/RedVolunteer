package com.redvolunteer.viewmodels;

import com.redvolunteer.dataModels.RequestHelpModel;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;

import java.util.List;

import io.reactivex.Flowable;

public class HelpRequestViewModel {

    private RequestHelpModel mRequestHelpModel;

    public HelpRequestViewModel(RequestHelpModel requestHelpModel){
        this.mRequestHelpModel = requestHelpModel;
    }

    public Flowable<List<RequestHelp>> getUserHelpRequests(){
        return mRequestHelpModel.getUserHelpRequests();
    }
    public Flowable<List<Chat>> getUserChats(){
        return mRequestHelpModel.getUserMessages();
    }
    public Flowable<List<RequestHelp>> getRequests() {
        return mRequestHelpModel.getRequests();
    }
    public Flowable<RequestHelp> getRequest(String requestID){
        return mRequestHelpModel.getEventByID(requestID);
    }
    public void setLocation(RequestLocation location){
        mRequestHelpModel.setLocation(location);
    }
    public void createNewHelpRequest(RequestHelp requestHelp){
         mRequestHelpModel.storeHelpRequest(requestHelp);
    }

    public void updateHelpRequest(RequestHelp requestHelp){
        mRequestHelpModel.storeHelpRequest(requestHelp);

    }
    public Flowable<List<RequestHelp>> getNewRequests(){
        return mRequestHelpModel.getNewRequests();
    }
    public void deleteRequestFromStore(RequestHelp requestID){
        mRequestHelpModel.deleteRequest(requestID);
    }


    public RequestLocation getUserLocation(){
        return mRequestHelpModel.getUserLocation();
    }

}
