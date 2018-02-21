package com.iam725.kunal.gogonew.Utilities;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser extends FragmentActivity {

        private static final String TAG = "DirectionsJSONParser";

        public DirectionsJSONParser() {
                Log.d(TAG, "OBJECT IS  CREATED SUCCESSFULLY.");
        }

        public List<List<HashMap<String,String>>> parse(JSONObject jObject) {

                Log.i(TAG,"DIRECTIONSJSONParser class has started.");
                List<List<HashMap<String, String>>> routes = new ArrayList<>();
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;
                JSONObject jDistance = null;
                JSONObject jDuration = null;

                try {

                        if (jObject == null)            return null;
                        jRoutes = jObject.getJSONArray("routes");
                      Log.d(TAG, "routes = " + jRoutes.toString());


                        /** Traversing all routes */
                        for (int i = 0; i < jRoutes.length(); i++) {
                                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                                Log.d(TAG, "legs = " + jLegs.toString());
                                List<HashMap<String, String>> path = new ArrayList<>();

                                /** Traversing all legs */
                                for (int j = 0; j < jLegs.length(); j++) {

                                        /** Getting distance from the json data */
                                        jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                                        HashMap<String, String> hmDistance = new HashMap<>();
                                        hmDistance.put("distance", jDistance.getString("text"));
                                        Log.d(TAG, "hmDistance = " + hmDistance.toString());

                                        /** Getting duration from the json data */
                                        jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                                        HashMap<String, String> hmDuration = new HashMap<>();
                                        hmDuration.put("duration", jDuration.getString("text"));
                                        Log.d(TAG, "hmDuration = " + hmDuration.toString());

                                        /** Adding distance object to the path */
                                        path.add(hmDistance);

                                        /** Adding duration object to the path */
                                        path.add(hmDuration);
                                        Log.d(TAG, "PATH = " + path.toString());

                                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                                        //Traversing all steps
                                        for(int k=0;k<jSteps.length();k++){
                                                String polyline = "";
                                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                                List<LatLng> list = decodePoly(polyline);

                                                /// Traversing all points
                                                for(int l=0;l<list.size();l++){
                                                        HashMap<String, String> hm = new HashMap<String, String>();
                                                        hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                                        hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                                        path.add(hm);
                                                }
                                        }
                                }
                                routes.add(path);
                                }
                                Log.d(TAG, "routes in DirectionJSONParser = " + routes.toString());
                        }
                catch(JSONException e){
                                e.printStackTrace();
                }
                        return routes;
                }
                /*
         * Method to decode polyline points
         * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
       private List<LatLng> decodePoly(String encoded) {

                List<LatLng> poly = new ArrayList<LatLng>();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                        int b, shift = 0, result = 0;
                        do {
                                b = encoded.charAt(index++) - 63;
                                result |= (b & 0x1f) << shift;
                                shift += 5;
                        } while (b >= 0x20);
                        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lat += dlat;

                        shift = 0;
                        result = 0;
                        do {
                                b = encoded.charAt(index++) - 63;
                                result |= (b & 0x1f) << shift;
                                shift += 5;
                        } while (b >= 0x20);
                        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                        lng += dlng;

                        LatLng p = new LatLng((((double) lat / 1E5)),
                                (((double) lng / 1E5)));
                        poly.add(p);
                }
                return poly;
        }
}
