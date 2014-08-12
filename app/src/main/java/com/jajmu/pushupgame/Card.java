package com.jajmu.pushupgame;

public class Card {

    public final static int SPADES = 0;   // Codes for the 4 suits, plus Joker.
    public final static int HEARTS = 1;
    public final static int DIAMONDS = 2;
    public final static int CLUBS = 3;

    public final static int JACK = 11;    //   Cards 2 through 10 have their
    public final static int QUEEN = 12;   //   numerical values for their codes.
    public final static int KING = 13;
    public final static int ACE = 14;


    private String name;
    private int index;
    private String imageResource;
    private int value;
    private int suit;
    private boolean drawn;

    public Card(int suit, int value){
        this.suit = suit;
        this.value = value;
        this.name = getNameFromSuitValue(suit, value);
        drawn = false;
    }

    private String getNameFromSuitValue(int suit, int value){
        return getNameFromValue(value) + getNameFromSuit(suit);
    }

    private String getNameFromSuit(int suit){
        switch (suit){
            case SPADES: return "Spades";
            case HEARTS: return "Hearts";
            case DIAMONDS: return "Diamonds";
            case CLUBS: return "Clubs";
        }
        return "";
    }

    private String getNameFromValue(int value){
        switch (value){
            case 2: return "Two of ";
            case 3: return "Three of ";
            case 4: return "Four of ";
            case 5: return "Five of ";
            case 6: return "Six of ";
            case 7: return "Seven of ";
            case 8: return "Eight of ";
            case 9: return "Nine of ";
            case 10: return "Ten of ";
            case JACK: return "Jack of ";
            case QUEEN: return "Queen of ";
            case KING: return "King of ";
            case ACE: return "Ace of ";
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void drawCard() {
        drawn = true;
    }

    public boolean isDrawn() {
        return drawn;
    }
}
