package controller;

import gui.FPTS;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.PortfolioElements.Holding;
import model.Simulators.BearSimulator;
import model.Simulators.BullSimulator;
import model.Simulators.NoGrowthSimulator;
import model.Simulators.Simulator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * authors: Kaitlin Brockway & Luke
 */
public class SimulationController extends MenuController {
    @FXML
    private Label error;
    @FXML
    private TextField numSteps;
    @FXML
    private ChoiceBox<String> interval;
    @FXML
    private Button stepButton;
    @FXML
    private TextField priceAnnum;
    private Simulator currentSimulator;
    @FXML
    private Label pValue;
    @FXML
    private Label totalValue;
    @FXML
    private Label stepNumber;
    private String simulation = "NOGROWTH";
    private boolean steps = false;

    @FXML
    protected void handleBearSimulateRadioButtonPressed(ActionEvent event) {
        simulation = "BEAR";
    }

    @FXML
    protected void handleStepYesRadioButtonPressed(ActionEvent event) {
        steps = true;
    }

    @FXML
    protected void handleStepNoRadioButtonPressed(ActionEvent event) {
        steps = false;
    }

    @FXML
    protected void handleBullSimulateRadioButtonPressed(ActionEvent event) {
        simulation = "BULL";
    }

    @FXML
    protected void handleNoSimulateRadioButtonPressed(ActionEvent event) {
        simulation = "NOGROWTH";
    }

    /**
     * Checks to make sure the number of steps entered is valid.
     * If the simulation is no growth then the simulation will be called,
     * but if the simulation is a bull or bear market simulation the user
     * will be asked to input a percentage for price increase or decrease
     * per year.
     *
     * @param event - ActionEvent - The event that is created when Simulate button is pressed.
     * @throws java.io.IOException - Exception thrown if the SimulationPage.fxml is not found.
     */
    public void handleSimulateButtonPressed(ActionEvent event) throws IOException {
//        MementoCareTaker careTaker = new MementoCareTaker();
//        Portfolio portfolio = new Portfolio();
//        Memento cur_state = FPTS.getSelf().getPortfolio().createMemento();
//
//        portfolio.setMemento(portfolio);
        //Portfolio cur_portfolio = FPTS.getSelf().getPortfolio();
        if (numSteps.getText().length() != 0 && priceAnnum.getText().length() != 0) {
            try {
                int numberOfSteps = Integer.parseInt(numSteps.getText());
                String curInterval = interval.getValue();
                Boolean hasSteps;
                hasSteps = steps;
                if (simulation.equals("NOGROWTH")) {
                    currentSimulator = new NoGrowthSimulator(numberOfSteps);
                } else {
                    if (priceAnnum.getText().length() != 0) {
                        String pricePerAnum = priceAnnum.getText();
                        try {
                            double pricePerYearAsDouble = Double.parseDouble(pricePerAnum);
                            if (pricePerYearAsDouble < 1.00 && pricePerYearAsDouble > 0) {
                                ArrayList<Holding> holdings = FPTS.getSelf().getPortfolio().getHoldings();
                                if (simulation.equals("BEAR")) {
                                    currentSimulator = new BearSimulator(numberOfSteps, curInterval, pricePerYearAsDouble, holdings);
                                } else {
                                    currentSimulator = new BullSimulator(numberOfSteps, curInterval, pricePerYearAsDouble, holdings);
                                }
                            }
                        } catch (NumberFormatException x) {
                            error.setText("Invalid Format. Please enter a percent value for the number of steps.");
                        }
                    } else {
                        error.setText("Please enter a percent value for the Price Per Annum.");
                    }
                }
                try {
                    FPTS.setCurrentSimulator(currentSimulator);
                    if (hasSteps) {
                        FPTS.setSimulationValue(currentSimulator.simulate(1));
                    } else {
                        FPTS.setSimulationValue(currentSimulator.simulate(numberOfSteps));
                    }
                    Parent parent = FXMLLoader.load(getClass().getResource("/SimulationPage.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (NullPointerException n) {
                    error.setText("Please Enter a value between 0 and 1 for the Price per Annum.");
                }
            } catch (NumberFormatException x) {
                error.setText("Invalid Format. Please enter an integer for the number of steps.");
            }
        } else if (numSteps.getText().length() != 0) {
            error.setText("Please enter a decimal value for the Price Per Annum.");
        } else {
            error.setText("Please enter an integer for the number of steps.");
        }
    }

    /**
     * @param event - Event that caused this class to be called.
     * @throws IOException - Throws IOException if the SimulatorPage is not found.
     */
    @FXML
    protected void handleStepButtonPressed(ActionEvent event) throws IOException {
        currentSimulator = FPTS.getCurrentSimulator();
        if (currentSimulator.getCurrentStep() < currentSimulator.getTotalSteps()) {
            FPTS.setSimulationValue(FPTS.getSimulationValue() + currentSimulator.simulate(1));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/SimulationPage.fxml"))));
            stage.show();
        }
    }

    /**
     * @param event - Event that caused this class to be called.
     * @throws IOException - Throws IOException if the SimulatePage is not found.
     */
    @FXML
    protected void handleResetToStartButtonPressed(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/SimulatePage.fxml"))));
        stage.show();
    }

    /**
     * @param event authors: Kaitlin
     */
    @FXML
    protected void handleResetToCurrentPricesButtonPressed(ActionEvent event) {
        //TODO Memento portfolio restoration
        //currentPortfolioState.restoreFromMemento();
    }

    /**
     * Method used to initialize the choiceBox on the SimulatePage and the simulation value on the SimulationPage.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (interval != null) {
            interval.setItems(FXCollections.observableArrayList(
                    "Day", "Month", "Year"
            ));
        }
        if (pValue != null) {
            pValue.setText("$" + FPTS.getSimulationValue());
            stepNumber.setText("" + FPTS.getCurrentSimulator().getCurrentStep());
            currentSimulator = FPTS.getCurrentSimulator();
            if (currentSimulator.getCurrentStep() >= currentSimulator.getTotalSteps()) {
                stepButton.setDisable(true);
            }
            double value = 0;
            for (Holding h : FPTS.getSelf().getPortfolio().getHoldings()) {
                value += h.getTotalValue();
            }
            totalValue.setText("$" + (value + FPTS.getSimulationValue()));

        }

    }
}

