//Not using this java file
package com.iam725.kunal.gogonew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkChangeReceiver extends BroadcastReceiver {

        private static final String TAG = "LocationActivity";
        MapsActivity yourMain;

        public NetworkChangeReceiver() {
                super();
        }

        public NetworkChangeReceiver (MapsActivity main) {
                super();
                yourMain = main;
        }
//        protected void onNetworkChange(){}
        @Override
        public void onReceive(Context context, Intent intent) {

                Log.e(TAG, "BROADCAST RECIEVER onReceive fired...");
                try {

                        boolean isVisible = MapsActivity.isActivityVisible();// Check if
                        // activity
                        // is
                        // visible
                        // or not

                        Log.i(TAG, "Is activity visible : " + isVisible);
                        ConnectivityManager connec =
                                                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                        Log.e(TAG, "CONNEC="+connec.toString());
                        NetworkInfo networkInfo = connec.getActiveNetworkInfo();
//                        Log.e(TAG, "NETWORKINFO = "+ networkInfo.toString());
//                        Log.e(TAG, "NETWORKINFO.isConnected()" + networkInfo.isConnected());
                        // If it is visible then trigger the task else do nothing
                        if (isVisible) {
//                                onNetworkChange();
                                // Check for network connections
//                                new MapsActivity().showNetworkState();

                                // Check internet connection and accrding to state change the
                                // text of activity by calling method
                                if (networkInfo != null && networkInfo.isConnected()) {

//                                        Log.e(TAG, "ITS 1");
//                                        Toast toast = new Toast(context);
//                                        LinearLayout layout = new LinearLayout(context);
//                                        layout.setBackgroundColor(Color.parseColor("#cc0000"));
//                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                        layout.setLayoutParams(params);
//                                        TextView tv = new TextView(context);
//                                        tv.setTextColor(Color.WHITE);
//                                        tv.setTextSize(25);
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                                        }
//                                        tv.setGravity(Gravity.CENTER_VERTICAL);
//                                        tv.setText("Connected");
//                                        tv.setPadding(25, 25, 25, 25);
//                                        tv.setBackgroundColor(Color.parseColor("#08B34A"));
//                                        layout.addView(tv);
//                                        toast.setView(layout);
//                                        toast.setGravity(Gravity.BOTTOM, 0, 0);
//                                        toast.show();
                                        Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
//                                        yourMain.showInternetStatus();
//                                        new MapsActivity().isInternetOn();
//                                        Intent i = context.getPackageManager()
//                                                .getLaunchIntentForPackage( context.getPackageName() );
//                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        context.startActivity(i);
//                                        new MapsActivity().onStart();
//                                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) new View(context).findViewById(R.id.coordinatorLayout);
//                                        Log.e(TAG,"COORDINATORLAYOUT");
//                                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connected", Snackbar.LENGTH_LONG);
//                                        Log.e(TAG, "snackbar = "+snackbar.toString());
//                                        View sbView = snackbar.getView();
//                                        Log.e(TAG,"sbview="+sbView.toString());
//                                        sbView.setBackgroundColor(Color.parseColor("#08B34A"));
//                                        TextView tview = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                                        Log.e(TAG,"TVIEW="+tview.toString());
//                                        tview.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                                tview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                                        }
//                        snackbar.setActionTextColor(ColorStateList.valueOf(Color.parseColor("#08B34A")));
//                                        snackbar.show();
//                                        new MapsActivity().showNetworkState(1);
                                } else {
                                        Log.e(TAG, "ITS 0");
//                                        Toast toast = new Toast(context);
//                                        LinearLayout layout = new LinearLayout(context);
//                                        layout.setBackgroundColor(Color.parseColor("#cc0000"));
//                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                        layout.setLayoutParams(params);
//                                        TextView tv = new TextView(context);
//                                        tv.setTextColor(Color.WHITE);
//                                        tv.setTextSize(25);
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                                        }
//                                        tv.setPadding(25, 25, 25, 25);
//                                        tv.setGravity(Gravity.CENTER_VERTICAL);
//                                        tv.setText("No Internet Connection");
//                                        layout.addView(tv);
////                                        tv.setBackgroundColor(Color.parseColor("#cc0000"));
//                                        toast.setView(layout);
//                                        toast.setGravity(Gravity.BOTTOM, 0, 0);
//                                        toast.show();
                                        Toast.makeText(context,"No Internet Connection", Toast.LENGTH_LONG).show();
//                                        yourMain.showInternetStatus();
//                                        Intent i = context.getPackageManager()
//                                                .getLaunchIntentForPackage( context.getPackageName() );
//                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        context.startActivity(i);
//                                        new MapsActivity().onStart();
//                                        new MapsActivity().isInternetOn();
//                                        new MapsActivity().showNetworkState(0);
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }

        }
}
