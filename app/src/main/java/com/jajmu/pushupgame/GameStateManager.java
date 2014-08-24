package com.jajmu.pushupgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.jajmu.pushupgame.gameview.GameActivity;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class GameStateManager {
    public enum GameState {
        START, FIND_OPPONENT, CHALLENGING_OPPONENT, BEING_CHALLENGED, YOUR_TURN, OPPONENT_TURN, LOSE, WIN
    };
    private SharedPreferences prefs;
    private List<GraphUser> opponents;
    private GameState currentState;
    private Deque<Turn> turns;
    public Deck deck;

    public GameStateManager(SharedPreferences p){
        setCurrentState(GameState.START);
        turns = new LinkedList<Turn>();
        deck = new Deck();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

    private String getUserId(GraphUser u){
        if(u!= null){
            return u.getId();
        }
        return null;
    }

    private String getUserLongName(GraphUser u){
        if(u!= null){
            String name = "";
            name += u.getFirstName();
            if(u.getLastName()!= null && u.getLastName().length()>0){
                name += " "+u.getLastName().substring(0,1)+".";
            }
            return name;
        }
        return null;
    }

    private String getUserName(GraphUser u){
        if(u!= null){
            return u.getFirstName();
        }
        return null;
    }

    public String getPlayer2UserId(){
        if(opponents!=null && opponents.size()>0){
            return getUserId(opponents.get(0));
        }
        return null;
    }

    public String getPlayer2LongName(){
        if(opponents!=null && opponents.size()>0){
            return getUserLongName(opponents.get(0));
        }
        return null;
    }

    public String getPlayer2Name(){
        if(opponents!=null && opponents.size()>0){
            return getUserName(opponents.get(0));
        }
        return null;
    }

    /**
     * Gets the activity to start based on what message we received from GCM
     * @param
     * @return
     */
    public Intent gameStateMessageHandler(Bundle b, Context c){
        String type = b.get("type").toString();
        String challengerId = b.get("challenger_id").toString();
        Intent notificationIntent = new Intent(c, GameActivity.class);
        if(type.equals("challenge")){
            setCurrentState(GameState.BEING_CHALLENGED);
//            notificationIntent.putExtra("param1", getCurrentUser().getId());
            notificationIntent.putExtra("param2", challengerId);
        } else if(type.equals("finishTurn")){
            setCurrentState(GameState.YOUR_TURN);
        } else{
            setCurrentState(GameState.START);
        }

        return notificationIntent;
    }


    public List<GraphUser> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<GraphUser> users) {
        opponents = users;
    }

    public void setCurrentState(int state){

    }

    public void finishedTurn(Turn turn){
        //if we get a 'finishedTurn' signal out of order, just ignore! duplicate network message shenanigans.
        if((getCurrentState() == GameState.YOUR_TURN && !turn.isYours) || (getCurrentState() == GameState.OPPONENT_TURN && turn.isYours)){
            return;
        }
        //if not first turn and previous turn didn't complete, win!
        if(!turns.isEmpty() && !turns.getLast().didAllPushups){
            if(turn.isYours){
                win();
            }else{
                lose();
            }
        }
        else{
            turns.add(turn);
            if(turn.isYours){
                setCurrentState(GameState.OPPONENT_TURN);
            }else{
                setCurrentState(GameState.YOUR_TURN);
            }
        }
    }

    public void win(){
        setCurrentState(GameState.WIN);
    }

    public void lose(){
        setCurrentState(GameState.LOSE);
    }
}
