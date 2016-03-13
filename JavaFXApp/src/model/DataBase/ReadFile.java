package model.DataBase;

import gui.FPTS;
import model.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Ian London
 */
public class ReadFile{

    public static ArrayList<String[]> splitFile = new ArrayList<String[]>();
    public static ArrayList<EquityComponent> readEquity(){
        return ReadEquity.read();
    }
    public static ArrayList<Holding> readHoldings() {
        return ReadHoldings.read();
    }
    public static ArrayList<CashAccount> readCash() {
        return ReadCash.read();
    }


    // reads in CSV file
    public static ArrayList<String[]> readInFile() {
        String csv = "JavaFXApp/src/model/DataBase/equities.csv";
        splitFile = new ArrayList<String[]>();
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(csv));
            while ((line = reader.readLine()) != null) {
                line = line.substring(1,line.length()-1);
                String[] split = line.split("\",\"");
                splitFile.add(split);
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/equities.csv not found! Please try again.");
            //readInFile();
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

    // reads in CSV file
    public static ArrayList<String[]> readInUser(String file) {
        //System.out.println("CURRENT USER ID: " + FPTS.getCurrentUserID());
        String csv = "JavaFXApp/src/model/DataBase/Portfolios/" + FPTS.getCurrentUserID() + file;
        splitFile = new ArrayList<String[]>();
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(csv));
            while ((line = reader.readLine()) != null) {
                line = line.substring(1,line.length()-1);
                String[] split = line.split("\",\"");
                splitFile.add(split);
            }
        } catch (FileNotFoundException e) {
            System.out.println("JavaFXApp/src/model/DataBase/equities.csv not found! Please try again.");
            //readInFile();
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
