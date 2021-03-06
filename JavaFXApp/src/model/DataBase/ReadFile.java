package model.DataBase;

import gui.FPTS;
import model.Equities.EquityComponent;
import model.PortfolioElements.CashAccount;
import model.PortfolioElements.Holding;
import model.PortfolioElements.Transaction;
import model.PortfolioElements.WatchedEquity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Ian London
 */
public class ReadFile {

    /**
     *
     */
    protected static ArrayList<String[]> splitFile = new ArrayList<String[]>();

    public static ArrayList<EquityComponent> readEquity() {
        return ReadEquity.read();
    }

    private static WriteFile writeFile = new WriteFile();

    public static ArrayList<Holding> readHoldings(String un) {
        return ReadHoldings.readDB(un);
    }

    public static ArrayList<WatchedEquity> readWatchedEquities(String user) {
        return ReadWatchedEquity.readDB(user);
    }

    public static ArrayList<CashAccount> readInCashFile(String un) {
        return ReadCash.readDB(un);
    }

    public static ArrayList<Transaction> readInTransFile(String un){
        return ReadTrans.readDB(un);
    }


    // reads in CSV file
    protected static ArrayList<String[]> readInEquities() {
        writeFile.makeDB();
        String csv = "JavaFXApp/src/model/DataBase/equities.csv";
        splitFile = readIn(csv);

        return splitFile;
    }

    // reads in specified CSV file corresponding to the current user from the database
    protected static ArrayList<String[]> readInUser(String file) {

        String csv = "/lilBase/Portfolios/" + file;
        try {
            csv = writeFile.getPath() + csv;
        } catch (UnsupportedEncodingException e) {
            System.out.println("readInUser threw an exception for the lilBase's filepath");
            e.printStackTrace();
        }
        ArrayList<String[]> result = readIn(csv);

        return result;
    }

    // reads in CSV file
    protected static ArrayList<String[]> readIn(String path) {

        splitFile = new ArrayList<String[]>();
        BufferedReader reader = null;
        String line;

        try {
            if (path.contains("/equities.c")) {
                InputStream is = FPTS.class.getClassLoader().getResourceAsStream("model/DataBase/equities.csv");
                reader = new BufferedReader(new InputStreamReader(is));
            } else {
                reader = new BufferedReader(new FileReader(path));
            }

            while ((line = reader.readLine()) != null) {
                line = line.substring(1, line.length() - 1);
                String[] split = line.split("\",\"");
                splitFile.add(split);
            }
        } catch (FileNotFoundException e) {
            System.out.println(path + " not found! Please try again.");
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
        return splitFile;
    }
}
