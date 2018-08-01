package com.iam725.kunal.gogonew.DataLoader;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by amit on 1/8/18.
 */

public class GsonObject {

    private List<Legs> routes;

    public static class Legs{
        private List<DistanceAndDuration> legs;

        public DistanceAndDuration getDistanceAndDuration() {
            return legs.get(0);
        }
    }

    public int getRouteSize(){
        return routes.size();
    }

    public DistanceAndDuration getDistanceAndDuration(){
        return routes.get(0).getDistanceAndDuration();
    }
}
