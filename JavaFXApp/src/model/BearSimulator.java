package model;

import gui.FPTS;

import java.util.ArrayList;

/**
 * Created by Brockway on 3/12/16.
 */
public class BearSimulator implements Simulator {
    public static String name = "Bear Market Simulator";

    private ArrayList<Holding> holdings;//TODO: find out how to get the holdings******
    private String interval;
    private boolean hasSteps;
    private int numSteps;
    private double pricePerYear;
    private double currentPercentDecrease;
    private int stepNumber;

    /**
     * Bear Market Constructor. Input values are converted
     * to their appropriate types in the SimulationController and
     * then this constructor is called.
     *
     * @param numSteps
     * @param interval
     * @param hasSteps
     * @param pricePerYearPercentage
     */
    public BearSimulator(int numSteps, String interval, boolean hasSteps, double pricePerYearPercentage) {
        this.interval = interval;
        this.hasSteps = hasSteps;
        this.numSteps = numSteps;
        this.pricePerYear = pricePerYearPercentage;
        this.holdings = FPTS.getSelf().getPortfolio().getHoldings();
        this.stepNumber = 0;
    }


    //TODO: CHECK IF IT HAS STEPS.

    /**
     * DECREASE
     *
     * @return
     */
    @Override
    public double simulate(int numberOfSteps) {
        double valueCount = 0;
        if (interval.equals("Day")) {
            currentPercentDecrease = pricePerYear / 365;
        } else if (interval.equals("Month")) {
            currentPercentDecrease = pricePerYear / 12;
        } else {
            currentPercentDecrease = pricePerYear;
        }
        for (int i = 0; i < numberOfSteps; i++) {
            for (Holding h : holdings) {
                valueCount -= currentPercentDecrease * h.getValue();
            }
        }
        stepNumber += numberOfSteps;
        return valueCount;
    }

    @Override
    public int getCurrentStep() {
        return stepNumber;
    }

    @Override
    public int getTotalSteps() {
        return numSteps;
    }


}
