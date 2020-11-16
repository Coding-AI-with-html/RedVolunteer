package com.redvolunteer.pojo;

import com.google.firebase.database.Exclude;

public class RequestHelp implements Comparable<RequestHelp> {

    private String id;


    /**
     * Request help characterizing info
     */
    private String name;
    private String description;
    private RequestLocation requestLocation;


    /**
     * User Who created help request
     */
    private User helpRequestCreator;

    /**
     * User ID of the admin
     */
    private String helpRequestCreatorID;

    public RequestHelp(){

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestLocation getRequestLocation() {
        return requestLocation;
    }

    public void setRequestLocation(RequestLocation requestLocation) {
        this.requestLocation = requestLocation;
    }

    @Exclude
    public User getHelpRequestCreator() {
        return helpRequestCreator;
    }

    @Exclude
    public void setHelpRequestCreator(User helpRequestCreator) {
        this.helpRequestCreator = helpRequestCreator;
    }

    public String getHelpRequestCreatorID() {
        return helpRequestCreatorID;
    }

    public void setHelpRequestCreatorID(String helpRequestCreatorID) {
        this.helpRequestCreatorID = helpRequestCreatorID;
    }

    @Override
    public int compareTo(RequestHelp requestHelp) {
        return 0;
    }
}
