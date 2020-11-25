package com.redvolunteer.utils.requestutils;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;

import java.util.List;

public interface DistanceManager {



    List<RequestHelp> sortByDistanceFromLocation(RequestLocation center, List<RequestHelp> requestHelpsToSort, int distanceKmFiller);
}
