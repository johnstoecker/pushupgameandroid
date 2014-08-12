package com.jajmu.pushupgame;

public class Deck {
    private Card[] deck;
    private int cardsLeft;
    public final static int NUMCARDS = 52;

    public Deck() {
        deck = new Card[NUMCARDS];
        int cardCt = 0; // How many cards have been created so far.
        for ( int suit = 0; suit <= 3; suit++ ) {
            for ( int value = 2; value <= 14; value++ ) {
                deck[cardCt] = new Card(suit,value);
                cardCt++;
            }
        }
        shuffle();
    }

    public Card drawCard(){
        if (cardsLeft <=0){
            shuffle();
        }
        cardsLeft--;
        return deck[cardsLeft];
    }

    public int getCardsLeft() {
        return cardsLeft;
    }

    public void shuffle() {
        for ( int i = deck.length-1; i > 0; i-- ) {
            int rand = (int)(Math.random()*(i+1));
            Card temp = deck[i];
            deck[i] = deck[rand];
            deck[rand] = temp;
        }
        cardsLeft = NUMCARDS;
    }


}
