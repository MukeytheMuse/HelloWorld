/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import java.util.Date;
import model.Holding;
import model.Portfolio;
import model.Searchable;

/**
 *
 * @author ericepstein
 */
public class SellHoldingAlgorithm extends HoldingAlgorithm {
 
    private Portfolio p;
    private ArrayList<Searchable> toBeSearched;
    
    @Override
    public void establishContext() {
        //this.theFPTS = theFPTS;
        p = theFPTS.getPortfolio();
        toBeSearched = p.getHoldingSearchables();
        //System.out.println(""+theFPTS.getWidth());
    }
 
    public ArrayList<Searchable> getToBeSearched() {
        //Portfolio p = copyFPTS.getPortfolio();
        //return p.getLoadedSearchables();
        return toBeSearched;
    }
    
    /*
    
    PRECONDITION: CashAccountOfInterest would have already been identified,
    equityOfInterest already identified, and numOfShares & pricePerShare
    checked for valid numOfShares & pricePerShare
    */
    public void processInsideFPTS() {
       Holding e = (Holding) equityOfInterest; 
       
       double accountVal = cashAccountOfInterest.getValue();

       if ( (numOfShares * pricePerShare) > 0 && e.getNumOfShares() >= numOfShares ) {
           cashAccountOfInterest.deposit(numOfShares * pricePerShare);
           e.subtractShares(numOfShares);
           if (e.getNumOfShares() == 0) {
               p.removeHolding(e);
               theStage.setScene(getConfirmationScene());
           }  
       } else {
           mainInput.setText("INVALID");
       }
    }
     
    public void processOutsideFPTS() {
        Holding e = (Holding) equityOfInterest; 
        if (e.getNumOfShares() > numOfShares) {
            e.subtractShares(numOfShares);
        } else if (e.getNumOfShares() == numOfShares) {
            p.removeHolding(e);
            theStage.setScene(getConfirmationScene());
        } else {
            mainInput.setText("INVALID");
        }

       
   }
    
}