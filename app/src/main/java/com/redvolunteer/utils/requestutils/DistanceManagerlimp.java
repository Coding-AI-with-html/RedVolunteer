package com.redvolunteer.utils.requestutils;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;

import java.util.ArrayList;
import java.util.List;

public class DistanceManagerlimp implements DistanceManager {

    @Override
    public List<RequestHelp> sortByDistanceFromLocation(RequestLocation center, List<RequestHelp> requestHelpsToSort, int distanceKmFiller) {


        List<RequestHelp> orderedList = new ArrayList<>();
        List<DistanceHolder> wrapped = this.getSortableList(center, requestHelpsToSort);


        for(DistanceHolder holder: wrapped){

            if(!(holder.distance > distanceKmFiller)){
                orderedList.add(holder.requestHelp);
            }
        }
        return orderedList;
    }


    private List<DistanceHolder> getSortableList(RequestLocation center, List<RequestHelp> requestToSort){

        List<DistanceHolder> listWrappers = new ArrayList<>();
        for(RequestHelp requestHelp: requestToSort){
            listWrappers.add(this.wrap(center, requestHelp));
        }
        return listWrappers;

    }

    private DistanceHolder wrap(RequestLocation center, RequestHelp requestHelpToWrap){

        double distance = this.computerDistance(center, requestHelpToWrap.getRequestLocation());

        return  new DistanceHolder(requestHelpToWrap, distance);


    }

    private double computerDistance(RequestLocation center, RequestLocation requestLocation){

        final int R = 6371; //radius of the earth;

        double latDistance = Math.toRadians(requestLocation.getLatitude() - center.getLatitude());
        double lotDistance = Math.toRadians(requestLocation.getLongitude() - center.getLongitude());
        double a = Math.sin(latDistance/ 2) * Math.sin(latDistance/2)
                + Math.sin(lotDistance / 2) * Math.sin(lotDistance/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;

    }




    private class DistanceHolder implements Comparable<DistanceHolder>{

        /**
         * Wrapped requestHelp
         */
        RequestHelp requestHelp;


        /**
         * Distance from specified location
         */
        double distance;

        DistanceHolder(RequestHelp Helprequest, double distance){
            this.requestHelp = Helprequest;
            this.distance = distance;
        }




        @Override
        public int compareTo(DistanceHolder distanceHolder) {

            int compareResult = 0;

            if(this.distance > distanceHolder.distance){
                compareResult = -1;
            }
            if(this.distance < distanceHolder.distance){
                compareResult = 1;
            }

            return compareResult;
        }
    }
}
