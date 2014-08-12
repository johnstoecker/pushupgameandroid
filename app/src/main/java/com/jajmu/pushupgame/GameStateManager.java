package com.jajmu.pushupgame;

import java.util.Deque;
import java.util.LinkedList;

public class GameStateManager {
    private enum GameState {
        START, FIND_OPPONENT, CHALLENGE_OPPONENT, YOUR_TURN, OPPONENT_TURN, LOSE, WIN
    };

    private GameState currentState;

    private Deque<Turn> turns;

    public GameStateManager(){
        currentState = GameState.START;
        turns = new LinkedList<Turn>();
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
