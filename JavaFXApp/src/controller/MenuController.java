package controller;

import controller.CashAccountCtrl.*;
import gui.FPTS;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DataBase.ReadImports;
import model.DataBase.WriteFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import model.PortfolioElements.CashAccount;
import model.PortfolioElements.Holding;
import model.PortfolioElements.Portfolio;
import model.PortfolioElements.Transaction;
import model.UndoRedo.UndoRedoManager;

public abstract class MenuController implements Initializable {
    @FXML
    public MenuBar myMenuBar;

    FPTS fpts = FPTS.getSelf();

    /**
     * @param event
     * @throws IOException
     */
    public void handleLogoutMenuItemPressed(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent parent = FXMLLoader.load(this.getClass().getResource("/LogoutPage.fxml"));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    public void handleExitMenuItemPressed(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void handleSaveMenuItemPressed(ActionEvent event) {
        WriteFile writeFile = new WriteFile();
        writeFile.updatePortfolioForUser(FPTS.getCurrentUser());
    }

    /**
     * Handler for when the About button is pressed in the Menu Bar
     *
     * @param event - ActionEvent - Event that caused this function to be called.
     */
    public void handleAboutMenuItemPressed(ActionEvent event) {
        //TODO Add an about page
    }

    public void handleHomeMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/HomePage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * @param event
     * @throws IOException
     */
    public void handlePortfolioMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/PortfolioPage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void handleBuyEquitiesMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/SearchPage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleSellEquitiesMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/SellHoldingPage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleWatchlistMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/Watchlist/WatchlistPage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleWithdrawMenuItemPressed(ActionEvent event) {
        CashAccountAlgorithm cashAcctAlgor = new WithdrawCashAccountAlgorithm();
        cashAcctAlgor.process(FPTS.getSelf());
    }

    public void handleDepositMenuItemPressed(ActionEvent event) {
        CashAccountAlgorithm cashAcctAlgor = new DepositCashAccountAlgorithm();
        cashAcctAlgor.process(FPTS.getSelf());
    }

    public void handleCreateMenuItemPressed(ActionEvent event) {
        CashAccountCreator cashAcctAlgor = new CashAccountCreator(FPTS.getSelf());
        cashAcctAlgor.getCashAccountCreatorScene();
    }

    public void handleTransferMenuItemPressed(ActionEvent event) {
        CashAccountAlgorithm cashAcctAlgor = new TransferCashAccountAlgorithm();
        cashAcctAlgor.process(FPTS.getSelf());
    }

    public void handleRemoveMenuItemPressed(ActionEvent event) throws IOException {
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("/CADeletePage.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleWatchMenuItemPressed(ActionEvent event) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(this.getClass().getResource("/Watchlist/WatchlistPage.fxml")));
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleImportMenuItemPressed(ActionEvent event) {
        HashMap<String, ArrayList> importedEquities;
        ArrayList<Holding> userHoldingsToImport;
        ArrayList<Transaction> userTransactionsToImport;
        ArrayList<CashAccount> userCashAccountsToImport;
        Portfolio newPortfolio;

        Stage stage = new Stage();
        FileChooser fd = new FileChooser();
        fd.setTitle("Select file to upload");
        fd.setInitialDirectory(new File(System.getProperty("user.home")));
        fd.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fd.showOpenDialog(stage);

        if (file != null) {
            importedEquities = ReadImports.readInImports(file);
            //They are being checked for accuracy within ReadImports file.
            userHoldingsToImport = importedEquities.get("Holdings");
            userTransactionsToImport = importedEquities.get("Transactions");
            userCashAccountsToImport = importedEquities.get("Cash Accounts");

            ArrayList<CashAccount> cashAccountArrayList = FPTS.getCurrentUser().getMyPortfolio().getCashAccounts();

            for (CashAccount cashAccount : cashAccountArrayList) {
                for (CashAccount importedCashAccount : userCashAccountsToImport) {
                    if (cashAccount.getAccountName().equals(importedCashAccount.getAccountName())) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ImportPopUp.fxml"));
                            Parent root1 = fxmlLoader.load();
                            ImportCashAccountPopUpController controller = fxmlLoader.getController();
                            controller.setAccounts(cashAccount, importedCashAccount);
                            Stage stg = new Stage();
                            stg.setScene(new Scene(root1));
                            stg.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void handleUndoMenuItemPressed(ActionEvent event) throws IOException {
        FPTS fpts = FPTS.getSelf();
        UndoRedoManager undoRedoManager = fpts.getUndoRedoManager();
        undoRedoManager.undo();

        Scene scene = new Scene(FXMLLoader.load(this.getClass().getResource("/HomePage.fxml")));
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void handleRedoMenuItemPressed(ActionEvent event) throws IOException {
        FPTS fpts = FPTS.getSelf();
        UndoRedoManager undoRedoManager = fpts.getUndoRedoManager();
        undoRedoManager.redo();

        Scene scene = new Scene(FXMLLoader.load(this.getClass().getClassLoader().getResource("/HomePage.fxml")));
        Stage stage = (Stage) this.myMenuBar.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToLoginPage(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/LoginPage.fxml"));
        Stage stage;
        try {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } catch (ClassCastException var5) {
            stage = (Stage) this.myMenuBar.getScene().getWindow();
        }

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    public void goToSimulation(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SimulatePage.fxml"));
            Parent root1 = fxmlLoader.load();
            SimulationController controller = fxmlLoader.getController();
            controller.setHoldings(fpts.getPortfolio().getHoldings());
            Stage stage;
            try {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } catch (ClassCastException var5) {
                stage = (Stage) this.myMenuBar.getScene().getWindow();
            }

            Scene scene = new Scene(root1);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

