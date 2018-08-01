package com.iam725.kunal.gogonew.DataLoader;

import com.google.gson.annotations.SerializedName;

/**
 * Created by amit on 1/8/18.
 */

public class DistanceAndDuration {

    private Distance distance;
    private Duration duration;

    public void setDistance(String distance) {
        Distance distance1 = new Distance();
        distance1.setDistance(distance);
        this.distance = distance1;
    }

    public void setDuration(String duration) {
        Duration duration1 = new Duration();
        duration1.setDuration(duration);
        this.duration = duration1;
    }

    public String getDistance() {
        return distance.getDistance();
    }

    public String getDuration() {
        return duration.getDuration();
    }

    public static class Distance{
        @SerializedName("text")
        String distance;

        public String getDistance(){
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }

    public static class Duration{
        @SerializedName("text")
        String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
