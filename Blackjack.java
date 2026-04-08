/**
 * Blackjack class with embedded card class to play
 * a game of blackjack.
 * 
 * @author Charles Clark
 * 
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Blackjack {

    //card class
    private class Card {
        String value;
        String type;
        //constructor
        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        //make values of cards strings for reading
        public String toString() {
            return value + "-" + type;
        }

        //get value of card
        public int getValue() {
            if("JQKA".contains(value)) {
                //for face cards
                if(value == "A") {
                    return 11;
                }
                return 10;
            }
            // for numbers 2-10
            return Integer.parseInt(value);
        } 

        public boolean isAce() {
            return value == "A";
        }

        public String getImage() {
            return "./cards/" + toString() + ".png";
        }
  
    } //end card class

    //deck of cards instaination
    ArrayList<Card> deck;
    //to shuffle deck
    Random random = new Random();

    //dealer cards
    Card faceDown;
    ArrayList<Card> dealerCards;
    int dealerCardsSum;
    //for changing value of ace 
    int dealerAceCount;

    //player cards
    ArrayList<Card> playerCards;
    int playerCardsSum;
    int playerAceCount;

    //message for who wins
    String win = "";

    //game interface
    int height = 800;
    int width = 800;
    int cardWidth = 100;
    int cardHeight = 140;
    JFrame frame = new JFrame("Blackjack");
    JPanel gamePanel = new JPanel() {
        //draw cards on game interface
        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);

            try {
                //dealers cards
                Image downCard = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                //turns over card when dealers plays 
                if(!stay.isEnabled()) {
                    downCard = new ImageIcon(getClass().getResource(faceDown.getImage())).getImage();
                }
                g.drawImage(downCard, 20, 20, cardWidth, cardHeight, null);

                for(int i = 0; i < dealerCards.size(); i++) {
                   Card card = dealerCards.get(i); 
                   Image cardImage = new ImageIcon(getClass().getResource(card.getImage())).getImage();
                   g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);  
                }    

                //players cards  
                for(int i = 0; i < playerCards.size(); i++) {
                    Card card = playerCards.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImage())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 5)*i, 450, cardWidth, cardHeight, null);
                }    

                //determines who wins      
                if(!stay.isEnabled()) {
                    dealerCardsSum = changeDealerAce();
                    playerCardsSum = changeAce();

                    if(playerCardsSum > 21) {
                        win = "You Lose";
                    } else if(dealerCardsSum > 21) {
                        win = "You Win!";
                    } else if(dealerCardsSum == playerCardsSum) {
                        win = "Draw";
                    } else if(playerCardsSum > dealerCardsSum) {
                        win = "You Win!";
                    } else if(playerCardsSum < dealerCardsSum) {
                        win = "You Lose";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.black);
                    g.drawString(win, 250,250);
                } 

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    // buttons
    JPanel buttonPanel = new JPanel();
    JButton hit = new JButton("Hit");
    JButton stay = new JButton("Stay");
    JButton reset = new JButton("New Game");
    

    //starts game of blackjack
    Blackjack() {
        Game();
        frame.setVisible(true);
        frame.setSize(width, height);
        //ends program when click x
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(55, 100, 75));
        frame.add(gamePanel);

        //add hit button
        hit.setFocusable(false);
        buttonPanel.add(hit);

        //add stay button
        stay.setFocusable(false);
        buttonPanel.add(stay);

        //add reset button
        reset.setFocusable(false);
        buttonPanel.add(reset);
 
        //put buttons at bottom 
        frame.add(buttonPanel, BorderLayout.SOUTH); 
        
        //hit button action
        hit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerCardsSum += card.getValue();
                playerAceCount += card.isAce()? 1 :0;
                playerCards.add(card);
                //disables hit button if player sum over 21
                if(changeAce() > 21) {
                    hit.setEnabled(false);
                }
                gamePanel.repaint();
                System.out.println("dealer " + dealerCardsSum);
                System.out.println("player " + playerCardsSum);
            }
        });

        //stay button action
        stay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hit.setEnabled(false);
                stay.setEnabled(false);
            
                //dealer draws until 17 or over
                while(dealerCardsSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerCardsSum += card.getValue();
                    dealerAceCount += card.isAce()? 1 : 0;
                    dealerCards.add(card);
                    dealerCardsSum = changeDealerAce();
                }
                gamePanel.repaint();
                System.out.println("dealer " + dealerCardsSum);
                System.out.println("player " + playerCardsSum);
            }
        });

        //reset button
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //clear cards
                playerCards.clear();
                dealerCards.clear();
                playerCardsSum = 0;
                dealerCardsSum = 0;
        
                //reshuffle deck
                createDeck();
                shuffleDeck();
        
                // renable gameplay buttons
                hit.setEnabled(true);
                stay.setEnabled(true);
        
                //Refresh the GUI
                gamePanel.repaint();
                Game();
            }
        });

        gamePanel.repaint();
    }
    
    //game method 
    public void Game() {
        //build and shuffle deck
        createDeck();
        shuffleDeck();

        //deal cards
        //dealer cards
        dealerCards = new ArrayList<Card>();
        dealerCardsSum = 0;
        dealerAceCount = 0;
        faceDown = deck.remove(deck.size() - 1);
        dealerCardsSum += faceDown.getValue();
        dealerAceCount += faceDown.isAce() ? 1 : 0;
        Card faceUp = deck.remove(deck.size() -1);
        dealerCardsSum += faceUp.getValue();
        dealerAceCount += faceUp.isAce() ? 1 : 0;
        dealerCards.add(faceUp);

        //player cards 
        playerCards = new ArrayList<Card>();
        playerCardsSum = 0;
        playerAceCount = 0;
        for (int i = 0; i < 2; i++) {
            Card card = deck.remove(deck.size() - 1);
            playerCardsSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerCards.add(card);
        }

    } // end game method 

    //create a deck of cards
    public void createDeck() {
        //create deck
        deck = new ArrayList<Card>();
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] types = {"S", "C", "D", "H"};
        
        //create cards and insert into deck
        for(int i = 0; i < types.length; i++) {
            for(int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }

    } // end build deck method
    
    //shuffle deck
    public void shuffleDeck() {
        for(int i = 0; i < deck.size(); i++) {
            int randomNumber = random.nextInt(deck.size());
            Card card = deck.get(i);
            Card randomCard = deck.get(randomNumber);
            deck.set(i, randomCard);
            deck.set(randomNumber, card);
        }
     
    } // end shuffle deck method 
 
    //change ace from 11 to 1 for player 
    public int changeAce() {
        while(playerCardsSum > 21 && playerAceCount > 0) {
            playerCardsSum -= 10;
            playerAceCount -= 1;
        }
        return playerCardsSum;
    } // end change ace method

    //change ace from 11 to 1 for dealer
    public int changeDealerAce() {
        while(dealerCardsSum > 21 && dealerAceCount > 0) {
            dealerCardsSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerCardsSum;  
    } // end change ace for dealer method

} //end blackjack class
