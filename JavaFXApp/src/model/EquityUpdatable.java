/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author ericepstein
 */
public interface EquityUpdatable {
    public float getValuePerShare();
    public String getTickerSymbol();
    public String getEquityName();
    public ArrayList<String> getIndices();
    public ArrayList<String> getSectors();
}
