package com.jajmu.pushupgame;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.jajmu.pushupgame.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import com.jajmu.pushupgame.R;

public class SplashActivity extends Activity implements SplashFragment.OnFragmentInteractionListener{
    private boolean isResumed = false;
    private Fragment splashFrag = new SplashFragment();
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback loginStatusCallback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, loginStatusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(splashFrag);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void showSplash(boolean addToBackStack) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(splashFrag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void showMain(boolean addToBackStack) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(splashFrag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        isResumed = true;
        uiHelper.onResume();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment0
            System.out.println("show main");
            showMain(false);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showSplash(false);
            System.out.println("show splash");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {
            FragmentManager manager = getFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                System.out.println("show main 2");
                showMain(false);
            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                System.out.println("show splash 2");
                showSplash(false);
            }
        }
        if(state.isOpened()){
            Session.StatusCallback facebookCallback = new Session.StatusCallback() {

                // callback when session changes state
                @Override
                public void call(com.facebook.Session session, SessionState state, Exception exception) {
                    System.out.println("calling");
                    if (session.isOpened()) {
                        System.out.println("opening");
                        // make request to the /me API
                        com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {

                            //TODO: facebook errors

                            // callback after Graph API response with user object
                            @Override
                            public void onCompleted(GraphUser user, com.facebook.Response response) {
                                if (user != null) {
//		            	  TextView txtView = (TextView) ((Activity)context).findViewById(R.id.cardResult);
//		                  txtView.setText("Hello");
//
//		            	  TextView welcome = ((TextView) findViewById(R.id.cardResult));
//		                welcome.setText("Hello " + user.getName() + "!");
                                    ((PushUpApplication)getApplication()).setCurrentUser(user);
                                    ((PushUpApplication)getApplication()).setUpGCM();
                                    System.out.println(user.getName());
                                }
                            }
                        }).executeAsync();
                    }
                }
            };
            com.facebook.Session.openActiveSession(this, true, facebookCallback);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
