package com.jajmu.pushupgame;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.service.textservice.SpellCheckerService.Session;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class PushUpApplication extends Application {
    private NetClient netClient;

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    private GameStateManager gameStateManager;
    private RequestQueue requestQueue;
    private GoogleCloudMessaging gcm;
    private String regid;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private int appVersion;
    private static final String SENDER_ID = "871518716184";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(){
        super.onCreate();
        requestQueue = createRequestQueue();
        setUpGCM();
        try{
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch(NameNotFoundException e){
            appVersion = 1;
        }
        gameStateManager = new GameStateManager();
    }


    private void setUpGCM(){
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getApplicationContext());

            if (regid.isEmpty()) {
                new registerGCMTask().execute(getApplicationContext());
            }
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Google Play Service Required!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private RequestQueue createRequestQueue() {
        RequestQueue queue = Volley.newRequestQueue(this);
        return queue;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public NetClient getNetClient(){
        return netClient;
    }

    public void setNetClient(NetClient netClient){
        this.netClient = netClient;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
//	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
////	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
////	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
//	        } else {
            //TODO: error handling
            Context context = getApplicationContext();
            CharSequence text = "This device is not supported";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
//	            Log.i(TAG, "This device is not supported.");
//	            finish();
//	        }
            return false;
        }
        return true;
    }


    private String getRegistrationId(Context context) {
        if (regid == null || regid.isEmpty()) {
            CharSequence text = "Registration id not found";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int currentVersion = getAppVersion(context);
        if (appVersion != currentVersion) {
            CharSequence text = "App version changed";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return "";
        }
        return regid;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private class registerGCMTask extends AsyncTask<Context, Void, String>{
        protected String doInBackground(Context...context) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context[0]);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                String url ="http://107.170.43.64:4000/registerGCM?device="+regid;
                System.out.println(url);

                // Request a string response from the provided URL.
                StringRequest registerRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
//        		        mTextView.setText("Response is: "+ response.substring(0,500));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//        		        mTextView.setText("That didn't work!");
                    }});


                requestQueue.add(registerRequest);

//                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
//			int duration = Toast.LENGTH_SHORT;
//
//			Toast toast = Toast.makeText(context, msg, duration);
//			toast.show();
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
            toast.show();
        }
    }

}