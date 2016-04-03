package model;

import gui.FPTS;
import model.PortfolioElements.CashAccount;
import model.PortfolioElements.Holding;
import model.PortfolioElements.Portfolio;
import model.PortfolioElements.Transaction;

import javax.swing.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String loginID;
    private String password;
    private Portfolio myPortfolio;
    private static Map<String, User> allUsersMap = new HashMap();
    //TODO: Warning:(17, 52) Unchecked assignment: 'java.util.HashMap' to 'java.util.Map<java.lang.String,model.User>'


    public Map<String, User> getAllUsersMap(){
        return allUsersMap;
    }
    
    public void setMyPortfolio(Portfolio p) {
        myPortfolio = p;
    }


    //private final String dateFormatPattern = "yyyy/MM/dd";

//    public User(String loginID, String password) {
//        this.loginID = loginID;
//        this.password = hash(password);
//    }


    /**
     * Acts as a temporary user for accessing static methods.
     *
     * @param uid
     *
     * Author(s): Kaitlin Brockway
     */
    public User(String uid) {
        this.loginID = uid;
        myPortfolio = new Portfolio();
    }

    /**
     * Used by fillUsers method.
     *
     * @param loginID
     * @param password
     * @param portfolio
     *
     * Author(s): Kaitlin Brockway
     */
    public User(String loginID, String password, Portfolio portfolio) {
        this.loginID = loginID;
        this.password = hash(password);
        this.myPortfolio = portfolio;
    }

    public Portfolio getMyPortfolio() {
        return this.myPortfolio;
    }


    /**
     * Static method allows "validateUser" to call this method
     * when checking if login input is valid.
     * <p>
     * Private in order to protect any outside sources from obtaining the
     * hashing method.
     *
     * @param password
     * @return
     */
    private static String hash(String password) {
        String encryptedPW = "";

        for(int i = 0; i < password.length(); ++i) {
            char encryptedChar = (char)(password.charAt(i) + 1);
            encryptedPW = encryptedPW + encryptedChar;
        }

        return encryptedPW;
    }

    private static String unHash(String password) {
        String textPass = "";

        for(int i = 0; i < password.length(); ++i) {
            char encryptedChar = (char)(password.charAt(i) - 1);
            textPass = textPass + encryptedChar;
        }

        return textPass;
    }

    public boolean equals(Object o) {
        if(!(o instanceof User)) {
            return false;
        } else {
            User cur_user = (User)o;
            return cur_user.getLoginID().equals(this.loginID) && cur_user.getPassword().equals(this.password);
        }
    }

    public String getLoginID() {
        return this.loginID;
    }

    private String getPassword() {
        return this.password;
    }


    /**
     * Checks to see if a username already exists in the system
     * when a user is registering with a new username and password.
     * <p>
     * TODO: ask if this should be case sensitive???****
     *
     * @param id
     * @return
     */
    public static boolean ValidLoginID(String id) {
        return !allUsersMap.containsKey(id);
    }

    /**
     * Upon clicking "Login" on the LoginPage, this method is called to validate
     * that the User has entered in a correct username and password combination
     * that exists in all the registered users. (in the allUsersMap)
     *
     * @param username: input field
     * @param password: input field
     * @return: true if the user and password combination exists in the system.
     */
    public static boolean validateUser(String username, String password) {
        if(allUsersMap.containsKey(username)) {
            String hashedPasswordMappedTo = allUsersMap.get(username).getPassword();
            String hashedPasswordEntered = hash(password);
            return hashedPasswordEntered.equals(hashedPasswordMappedTo);
        }
        return false;
    }


    public static void addToUserMap(User u) {
        allUsersMap.put(u.getLoginID(), u);
    }

    public String toString() {
        return this.loginID;
    }

    /**
     * Public method used to populate the users ArrayList<User>
     * from the UserData.csv file.
     */
    public static void fillUsers() {
        String user_csv = "JavaFXApp/src/model/DataBase/UserData.csv";
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(user_csv));
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                String un = split[0];
                String pwd = split[1];
                ArrayList<CashAccount> usersCashAccounts = readInCashFile(un);
                //line above also utilizes readInTransFile(String userID) method
                //ArrayList<Holding> usersHoldings = ReadHoldings.read();//THIS CAUSED ERRORS FOR SOME REASON
                ArrayList<Holding> usersHoldings = readInHoldingsFile(un);

                Portfolio userPortfolio = new Portfolio(usersHoldings, usersCashAccounts);
                User newUser = new User(un, unHash(pwd), userPortfolio);

                System.out.println(un);
                //have to unhash before creating a user because the password is hashed during creation
                allUsersMap.put(un, newUser);
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/UserData.csv not found! Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Reads Cash.csv and Trans.csv in order to created a populated list of Cash Accounts
     * with their corresponding transaction history.
     *
     * @param userID: Current User being created and populated with their information
     *              from the database when the system first starts up.
     * @return
     *
     * Author(s): Ian London and Kaitlin Brockway
     */
    private static ArrayList<CashAccount> readInCashFile(String userID){
        String cash_csv = "JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Cash.csv";
        String line;
        BufferedReader reader = null;
        String cashAccountName;
        String cashAccountTotalValue;
        String cashAccountDateAdded;
        ArrayList<CashAccount> usersCashAccounts = new ArrayList<>();
        Map<String, ArrayList<Transaction>> cashAccountNameTransactionsMap;

        try {
            reader = new BufferedReader(new FileReader(cash_csv));
            cashAccountNameTransactionsMap = readInTransFile(userID);//get the transactions from Trans.csv
            //GIVE TRANSACTIONS AN ASSOCIATED CASH ACCOUNT
            //Each line in the file is formatted: AccountName, currentValue, dateAdded
            //loops through while there are still lines with information left in the file
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                cashAccountName = split[0];
                cashAccountName = cashAccountName.substring(1, (cashAccountName.length() - 1));//strips the first @ last "
                cashAccountTotalValue = split[1];
                cashAccountTotalValue = cashAccountTotalValue.substring(1, (cashAccountTotalValue.length() - 1));//strips the first @ last "
                double doubleCashATotalValue = Double.parseDouble(cashAccountTotalValue);
                cashAccountDateAdded = split[2];
                cashAccountDateAdded = cashAccountDateAdded.substring(1, (cashAccountDateAdded.length() - 1));//strips the first @ last "
                //TODO: figure out why date conversion throws parsing errors and fix and change types in class constructor.
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
                //LocalDate parsedDate = LocalDate.parse(cashAccountDateAdded, formatter);

                CashAccount cashAccountToAdd = new CashAccount(cashAccountName, doubleCashATotalValue , cashAccountDateAdded, cashAccountNameTransactionsMap.get(cashAccountName));
                if( cashAccountNameTransactionsMap.containsKey(cashAccountToAdd.getAccountName())){
                    ArrayList<Transaction> newTransactions = new ArrayList<>();
                    ArrayList<Transaction> curTransactions = cashAccountNameTransactionsMap.get(cashAccountToAdd.getAccountName());
                    for(Transaction t: curTransactions){
                        t.setCashAccount(cashAccountToAdd);
                        newTransactions.add(t);
                    }
                    cashAccountToAdd.setTransactions(newTransactions);
                    usersCashAccounts.add(cashAccountToAdd);
                } else {
                    usersCashAccounts.add(cashAccountToAdd);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Cash.csv not found! Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return usersCashAccounts;
    }

    /**
     * Holdings.csv file is in the format:
     * "tickerSymbol","holdingName","valuePricePerShare","numOfShares","aquisitionDate","index1","sector1"
     * where there may be multiple indicies and sectors.
     *
     * @param userID
     * @return
     *
     * Author(s): Kaitlin Brockway
     */
    private static ArrayList<Holding> readInHoldingsFile(String userID) {
        String holdings_csv = "JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Holdings.csv";
        String line;
        BufferedReader reader = null;
        String tickerSymbol;
        String holdingName;
        String stringPricePerShare;
        String stringNumOfShares;
        Date acquisitionDate;
        ArrayList<String> indicies = new ArrayList<>();
        String cur_indexORsector;
        ArrayList<String> sectors = new ArrayList<>();
        ArrayList<Holding> allHoldings = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(holdings_csv));
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\",\"");
                int splitLength = split.length;
                System.out.println(split[0]);
                tickerSymbol = split[0];
                tickerSymbol = tickerSymbol.substring(1, (tickerSymbol.length() - 1));//strips the first @ last "
                holdingName = split[1];
                stringPricePerShare = split[2];
                double doublePricePerShareValue = Double.parseDouble(stringPricePerShare);
                stringNumOfShares = split[3];
                int intNumOfShares = Integer.parseInt(stringNumOfShares);
                acquisitionDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(split[4]);
                // WE DON'T NEED THE TOTAL VALUE BECAUSE IT IS CALCULATED IN THE CONSTRUCTOR.
                int counter = 5;
                while (counter < splitLength) {
                    cur_indexORsector = split[counter];
                    cur_indexORsector = cur_indexORsector.substring(1, cur_indexORsector.length() - 1);
                    if (FPTS.allIndicies.contains(cur_indexORsector)) {
                        indicies.add(cur_indexORsector);
                    } else if (FPTS.allSectors.contains(cur_indexORsector)) {
                        sectors.add(cur_indexORsector);
                    } else {
//                        System.out.println("The current index or sector being read is not included in the possibilities.");
//                        System.out.println("Check to make sure allSectors and allIndicies in the FPTS class have included all possibilities");
                        System.out.println("The current string being ignored is: " + cur_indexORsector);
                        System.out.println("This is being printed from the method readInHoldingsFile in the User Class.");
                    }
                    counter += 1;
                }
                Holding cur_holding = new Holding(tickerSymbol, holdingName, doublePricePerShareValue, intNumOfShares, acquisitionDate, indicies, sectors);
                allHoldings.add(cur_holding);
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Trans.csv not found! Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return allHoldings;
    }


    /**
     * BE AWARE: IF THE USER HAS TRANSACTIONS WITH A CASH ACCOUNT NAME THAT DOES NOT EXIST
     * IT WILL NOT BE SAVED IN THE PORTFOLIO. ALSO IF THE CASH ACCOUNT NAME DOES NOT HAVE ANY TRANSACTIONS
     * THE TRANSACTIONS WILL BE "null"
     * <p>
     * <p>
     * Reads the Trans.csv file for the userID specified as a parameter and returns
     * a map that contains a key represented as the CashAccountName with the values
     * represented as a list of the transactions associated with the cash account.
     * <p>
     * Called by "readInCashFile" method because the cash objects need to be created with
     * the preexisting transactions.
     * <p>
     * Private method for safety purposes.
     *
     * @param userID
     * @return Author(s): Kaitlin Brockway
     */
    private static Map<String, ArrayList<Transaction>> readInTransFile(String userID) {
        String transactions_csv = "JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Trans.csv";
        Map<String, ArrayList<Transaction>> cashAccountNameTransactionsMap = new HashMap<>();
        String line;
        BufferedReader reader = null;
        String stringCashAccountNameAssociatedWith;
        String stringAmount;
        Date dateMade;

        String stringType;
        // Line format:
        // cashAccountNameAssociatedWith, amount, year, month, day, typeWithdrawalTransferOrDeposit
        try {
            reader = new BufferedReader(new FileReader(transactions_csv));
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\",\"");
                System.out.println(split[0] + split[1] + split[2] + split[3]);
                stringCashAccountNameAssociatedWith = split[3];
                //stringCashAccountNameAssociatedWith = stringCashAccountNameAssociatedWith.substring(1, (stringCashAccountNameAssociatedWith.length() - 1));//strips the first @ last "
                stringAmount = split[0];
                stringAmount = stringAmount.substring(1, (stringAmount.length() - 1));//strips the first @ last "
                dateMade = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(split[1]);
                //System.out.println(stringDateMade);
                stringType = split[2];
                //stringType = stringType.substring(1, (stringType.length() - 1));//strips the first @ last "
                //convert certain fields to their appropriate types for the constructor.
                //TODO: ADD CHECK TO SEE IF "stringAmount" is in the format 90809890.99 with only numbers as parts of the string.
                double amount = Double.parseDouble(stringAmount);//need the amount in double format to use the transaction constructor
                //TODO: figure out why date conversion throws parsing errors and fix and change types in class constructor.
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
//                LocalDate parsedDate = LocalDate.parse(stringDateMade, formatter);
                Transaction newTransactionToAdd = new Transaction(amount, dateMade, stringType, stringCashAccountNameAssociatedWith);
                if (cashAccountNameTransactionsMap.containsKey(stringCashAccountNameAssociatedWith)) {
                    ArrayList<Transaction> newTransactionsList = cashAccountNameTransactionsMap.get(stringCashAccountNameAssociatedWith);
                    newTransactionsList.add(newTransactionToAdd);
                    cashAccountNameTransactionsMap.replace(stringCashAccountNameAssociatedWith, newTransactionsList);
                } else {
                    ArrayList<Transaction> transactionListToAdd = new ArrayList<>();
                    transactionListToAdd.add(newTransactionToAdd);
                    cashAccountNameTransactionsMap.put(stringCashAccountNameAssociatedWith, transactionListToAdd);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/Portfolios/" + userID + "/Trans.csv not found! Please try again.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
//                    System.out.print("Closed Successfully. ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cashAccountNameTransactionsMap;
    }

    /**
     * Adds the user to UserDate.csv that holds all the users usernames and associated passwords.
     *
     * @param usr
     * @param pw1
     *
     * Author(s): Kimberly Sookoo & Kaitlin Brockway & Ian London
     *
     */
    public void addUser(User usr, String pw1, ArrayList<Holding> holdings, ArrayList<Transaction> transactions) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        File newHoldingsFile;
        File newTransFile;
        File newCashFile;

        try {
            fileWriter = new FileWriter((new File("JavaFXApp/src/model/Database/UserData.csv")).getAbsolutePath(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(usr.getLoginID() + ",");
            bufferedWriter.write(hash(pw1));
            bufferedWriter.newLine();
            bufferedWriter.close();
            //creates portfolio directory to store all user information
            File portfolioDir = new File("JavaFXApp/src/model/Database/Portfolios/");
            if (!portfolioDir.exists()) {
                portfolioDir.mkdir();
            }
            //create a new user directory for the new user registering.
            File newUserDir = new File("JavaFXApp/src/model/Database/Portfolios/" + usr.loginID + "/");
            newUserDir.mkdir();
            //create 3 new files inside the newUserDir
            newTransFile = new File(newUserDir.getAbsolutePath() + "/Trans.csv");
            newCashFile = new File(newUserDir.getAbsolutePath() + "/Cash.csv");
            newHoldingsFile = new File(newUserDir.getAbsolutePath() + "/Holdings.csv");
            try {
                newCashFile.createNewFile();
                newTransFile.createNewFile();
                newHoldingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //if there are holdings to import
            if (!holdings.isEmpty()) {
                addHoldings(holdings, newHoldingsFile);
            }
            //if there are transactions to import
            if (!transactions.isEmpty()) {
                addCash(transactions, newTransFile);
            }

        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }


    /**
     * Writes the user's holding content to a file called Holdings.csv.
     * Written by: Kimberly Sookoo
     * @param holdings
     * @param importedHoldings
     */
    public void addHoldings(ArrayList<Holding> holdings, File importedHoldings) {
        try {
            FileWriter writerH = new FileWriter(importedHoldings, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writerH);
            String index = "";
            String sector = "";

            for (Holding holding : holdings) {
                for (String s : holding.getIndices()) {
                    index += s;
                }
                for (String se : holding.getSectors()) {
                    sector += se;
                }
                bufferedWriter.write("\"" + holding.getTickerSymbol() + "\",\"" +
                        holding.getDisplayName() + "\",\"" + holding.getPricePerShare() + "\",\""
                        + holding.getNumOfShares() + "\",\"" + holding.getAcquisitionDate() + "\",\""
                        + index + "\",\"" + sector + "\"");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Writes the users holding content to a file called Cash.csv in the form
     * "CashAccountForUser1","345.00","yyyy/mm/dd"
     *
     * @param transactions
     */
    private void addCash(ArrayList<Transaction> transactions, File importedTransactions) {
        FileWriter writerT = null;
        try {
            writerT = new FileWriter(importedTransactions, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writerT);

            for (Transaction transaction : transactions) {
                bufferedWriter.write("\"" + transaction.getAmount() + "\",\"" + transaction.getDateMade() +
                        "\",\"" + transaction.getType() + "\",\"" + transaction.getCashAccount() + "\"");
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
