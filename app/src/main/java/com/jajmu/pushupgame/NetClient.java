package com.jajmu.pushupgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class NetClient {

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;

    String SENDER_ID = "871518716184";
    public static final String HOST = "http://107.170.43.64:4000/finishTurn";
//	public static final String HOST = "http://192.168.2.14:4000/finishTurn";

    NetClient(Context c){
        gcm = GoogleCloudMessaging.getInstance(c);
//        registerDevice(c);
        completeTurn();
    }

    private void registerDevice(Context c){
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(context);
        }
        try{
            regid = gcm.register(SENDER_ID);
        }
        catch(IOException e){
            //TODO: handle error here
        }
//        msg = "Device registered, registration ID=" + regid;
    }


    public void completeTurn(){
        // Instantiate the RequestQueue.

        //		try{
//			URL url = new URL(HOST);
//	    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//	    	try {
//	//    	    urlConnection.setDoOutput(true);
//	//    	    urlConnection.setChunkedStreamingMode(0);
//	
//	//    	    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
//	    	    //writeStream(out);
//	    	    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//	    	    readStream(in);
//	    	}
//		    finally {
//		    	urlConnection.disconnect();
//		    }
//		} catch (Exception e){
//			e.printStackTrace();
//			return;
//		}
    }

    private static String readStream(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("RESTClient", "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("RESTClient", "IOException", e);
            }
        }
        return sb.toString();
    }
}
