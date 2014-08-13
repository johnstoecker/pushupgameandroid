package com.jajmu.pushupgame;

import com.facebook.model.GraphUser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class GameStateManager {
    private enum GameState {
        START, FIND_OPPONENT, CHALLENGE_OPPONENT, YOUR_TURN, OPPONENT_TURN, LOSE, WIN
    };
    private List<GraphUser> selectedUsers;

    public GraphUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(GraphUser currentUser) {
        this.currentUser = currentUser;
    }

    private GraphUser currentUser;

    private GameState currentState;

    private Deque<Turn> turns;

    public GameStateManager(){
        currentState = GameState.START;
        turns = new LinkedList<Turn>();
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

    public String getPlayer1UserId(){
        return getUserId(currentUser);
    }

    public String getPlayer1LongName(){
        return getUserLongName(currentUser);
    }

    public String getPlayer1Name(){
        return getUserName(currentUser);
    }

    public String getPlayer2UserId(){
        if(selectedUsers.size()>0){
            return getUserId(selectedUsers.get(0));
        }
        return null;
    }

    public String getPlayer2LongName(){
        if(selectedUsers.size()>0){
            return getUserLongName(selectedUsers.get(0));
        }
        return null;
    }

    public String getPlayer2Name(){
        if(selectedUsers.size()>0){
            return getUserName(selectedUsers.get(0));
        }
        return null;
    }


    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> users) {
        selectedUsers = users;
    }

    public void setCurrentState(int state){

    }

    public void finishedTurn(Turn turn){
        //if we get a 'finishedTurn' signal out of order, just ignore! duplicate network message shenanigans.
        if((currentState == GameState.YOUR_TURN && !turn.isYours) || (currentState == GameState.OPPONENT_TURN && turn.isYours)){
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
                currentState = GameState.OPPONENT_TURN;
            }else{
                currentState = GameState.YOUR_TURN;
            }
        }
    }

    public void win(){
        currentState = GameState.WIN;
    }

    public void lose(){
        currentState = GameState.LOSE;
    }
}
