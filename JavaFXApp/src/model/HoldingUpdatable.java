/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 * @author ericepstein & Kaitlin Brockway
 */
public interface HoldingUpdatable {

    public String getTickerSymbol();

    public String getName();//may be an equity or a holding

    public double getPricePerShare();

    public ArrayList<String> getIndices();

    public ArrayList<String> getSectors();
}
