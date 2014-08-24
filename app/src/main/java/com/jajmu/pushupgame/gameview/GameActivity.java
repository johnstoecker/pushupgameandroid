package com.jajmu.pushupgame.gameview;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.jajmu.pushupgame.GameStateManager;
import com.jajmu.pushupgame.PushUpApplication;
import com.jajmu.pushupgame.R;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity implements FriendListFragment.OnFragmentInteractionListener, VersusFragment.OnFragmentInteractionListener{
    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final String GAME_PREFERENCES_FILE = "GameStateManager";
    private GameStateManager gameStateManager;
    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("received by game activity");
            gcmMessageHandler(intent.getExtras());
            abortBroadcast();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("game created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setGameStateManager(new GameStateManager(getSharedPreferences(GAME_PREFERENCES_FILE, Context.MODE_PRIVATE)));
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.gameContainer) != null) {
            GameStateManager.GameState gameState = getGameStateManager().getCurrentState();
            if (savedInstanceState != null) {
                if(savedInstanceState.containsKey("type")){
                    System.out.println(savedInstanceState.get("type").toString());
                    String type = savedInstanceState.get("type").toString();
                    String challengerId = savedInstanceState.get("challenger_id").toString();
                    if(type.equals("challenge")){
                        getGameStateManager().setCurrentState(GameStateManager.GameState.BEING_CHALLENGED);
//                    notificationIntent.putExtra("param1", getCurrentUser().getId());
//                    notificationIntent.putExtra("param2", challengerId);
                    } else if(type.equals("finishTurn")){
                        getGameStateManager().setCurrentState(GameStateManager.GameState.YOUR_TURN);
                    } else{
                        getGameStateManager().setCurrentState(GameStateManager.GameState.START);
                    }
                } else {
                    // if we're being restored from a previous state,
                    // then we don't need to do anything and should return or else
                    // we could end up with overlapping fragments.
                    return;
                }
            }
            // Create a new Fragment to be placed in the activity layout
            FriendListFragment firstFragment = new FriendListFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.gameContainer, firstFragment).commit();
            if(gameState == GameStateManager.GameState.START) {
            } else if(gameState == GameStateManager.GameState.BEING_CHALLENGED || gameState == GameStateManager.GameState.CHALLENGING_OPPONENT){
                VersusFragment vFrag = new VersusFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.gameContainer, vFrag);
                transaction.addToBackStack(null);
                transaction.commit();
            } else if(gameState == GameStateManager.GameState.BEING_CHALLENGED || gameState == GameStateManager.GameState.YOUR_TURN){
            }
        }
    }

//    @Override
    public void onFriendSelected(String link) {
//        DetailFragment fragment = (DetailFragment) getFragmentManager()
//                .findFragmentById(R.id.detailFragment);
//        if (fragment != null && fragment.isInLayout()) {
//            fragment.setText(link);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles fragment UI when receiving push notification
     * @param b
     */
    private void gcmMessageHandler(Bundle b){
        if (b != null) {
            if(b.containsKey("type")){
                System.out.println(b.get("type").toString());
                String type = b.get("type").toString();
                String challengerId = b.get("challenger_id").toString();
                if(type.equals("challenge")){
                    getGameStateManager().setCurrentState(GameStateManager.GameState.BEING_CHALLENGED);
                    List<GraphUser> opponents = new ArrayList<GraphUser>();
                    GraphUser u = GraphObject.Factory.create(GraphUser.class);
                    u.setId(challengerId);
                    u.setName("Random");
                    u.setFirstName("first");
                    u.setLastName("last");
                    opponents.add(u);
                    onFriendSelect(opponents);
                } else if(type.equals("finishTurn")){
                    getGameStateManager().setCurrentState(GameStateManager.GameState.YOUR_TURN);
                } else{
                    getGameStateManager().setCurrentState(GameStateManager.GameState.START);
                }
            } else {
                // if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                return;
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println(uri);
        if(getGameStateManager().getOpponents().size() > 0){
            VersusFragment vFrag = new VersusFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.gameContainer, vFrag);
            transaction.addToBackStack(null);

            transaction.commit();
        }
     }

    public void onFriendSelect(List<GraphUser> opponents){
        getGameStateManager().setOpponents(opponents);
        if(opponents.size() > 0){
            VersusFragment vFrag = new VersusFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.gameContainer, vFrag);
            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
    }
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
//                                player1ProfilePicture.setProfileId(user.getId());
                                System.out.println(user.getLocation());
                                // Set the Textview's text to the user's name.
//                                userNameView.setText(user.getName());
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                });
        request.executeAsync();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == Activity.RESULT_OK) {
            System.out.println("inside game activity");
            System.out.println(requestCode);
            System.out.println(resultCode);
            System.out.println(data);
        }
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter("com.jajmu.GOT_PUSH");
        filter.setPriority(2);
        registerReceiver(mBroadcastReceiver, filter);
        super.onResume();
        uiHelper.onResume();
        System.out.println("game resumed");
        Bundle savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey("type")){
                System.out.println(savedInstanceState.get("type").toString());
                String type = savedInstanceState.get("type").toString();
                String challengerId = savedInstanceState.get("challenger_id").toString();
                if(type.equals("challenge")){
                    getGameStateManager().setCurrentState(GameStateManager.GameState.BEING_CHALLENGED);
//                    notificationIntent.putExtra("param1", getCurrentUser().getId());
//                    notificationIntent.putExtra("param2", challengerId);
                } else if(type.equals("finishTurn")){
                    getGameStateManager().setCurrentState(GameStateManager.GameState.YOUR_TURN);
                } else{
                    getGameStateManager().setCurrentState(GameStateManager.GameState.START);
                }
            } else {
                // if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.
                return;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        unregisterReceiver(mBroadcastReceiver);
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        System.out.println("game destroyed");
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public void setGameStateManager(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
    }
}
