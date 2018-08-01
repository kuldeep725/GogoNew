package com.iam725.kunal.gogonew.Utilities;

import android.graphics.Paint;

import com.google.android.gms.maps.model.LatLng;
import com.iam725.kunal.gogonew.DataLoader.DistanceAndDuration;

/**
 * Created by amit on 1/8/18.
 */

public class DistanceCalculationUtil {

    public DistanceAndDuration getDistanceAndDuration(LatLng mCurrentPosition, double latitudeBus,
            double longitudeBus){
        double dist = CalculationByDistance(mCurrentPosition,
                new LatLng(latitudeBus, longitudeBus)) * 1000;

        DistanceAndDuration distanceAndDuration = new DistanceAndDuration();

        long distLong = Math.round(dist);
        String timeStr;
        if (dist < 1000) {
            String distStr = String.valueOf(distLong) + " m";
//            //                                                long timeLong =Math.round(9 * distLong / 100);          //      40km/hr into m/s
            long timeLong = Math.round(12 * distLong / 100);          //      30km/hr into m/s
            if (timeLong > 60) {
                timeLong = Math.round(timeLong / 60);
                timeStr = String.valueOf(timeLong) + " min";
            } else {
                timeStr = String.valueOf(timeLong) + " s";
            }
            distanceAndDuration.setDistance(timeStr);
            distanceAndDuration.setDuration(distStr);
        } else {
            distLong = Math.round(distLong / 1000);
            String distStr = String.valueOf(distLong + " km");
            //                                                long timeLong = Math.round(3 * distLong / 2);           //      40km/hr into km/min
            long timeLong = Math.round(2 * distLong);           //      30km/hr into km/min
            if (timeLong > 60) {
                timeLong = Math.round(timeLong / 60);
                timeStr = String.valueOf(timeLong) + " hr";
            } else {
                timeStr = String.valueOf(timeLong) + " min";
            }

            distanceAndDuration.setDistance(timeStr);
            distanceAndDuration.setDuration(distStr);
        }

        return distanceAndDuration;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
//                double c = 2000* Math.asin(Math.sqrt(a));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * Radius;
    }
}
