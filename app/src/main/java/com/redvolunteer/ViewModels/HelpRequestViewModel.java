package com.redvolunteer.ViewModels;

import com.redvolunteer.dataModels.RequestHelpModel;
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
    public void setLocation(RequestLocation location){
        mRequestHelpModel.setLocation(location);
    }
    public void createNewHelpRequest(RequestHelp requestHelp){
         mRequestHelpModel.storeHelpRequest(requestHelp);
    }

    public void updateHelpRequest(RequestHelp requestHelp){
        mRequestHelpModel.storeHelpRequest(requestHelp);
    }
}
