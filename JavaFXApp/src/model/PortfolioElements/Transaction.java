package model.PortfolioElements;

import java.util.Date;

public class Transaction {

//Previously implemented methods (Made 3 classes into 1)

//    /**
//     * Run operation on object
//     */
//    public void execute();
//
//    /**
//     * returns CashAccount unique to the realization
//     *
//     * @return CashAccount
//     */
//    public CashAccount getCashAccount();

    private double amount;
    private Date dateMade;//TODO: change type to date here and for readInTransFile(String userID) method in the User class.
    private String type;
    //TODO:Warning:(21, 20) [UnusedDeclaration] Private field 'type' is assigned but never accessed
    private String cashAccountName;
    private CashAccount cashAccount;
    //TODO: Warning:(23, 25) [UnusedDeclaration] Private field 'cashAccount' is never assigned

    public Transaction(double amount, Date dateMade, String type, CashAccount cashAccount) {
        this.amount = amount;
        this.dateMade = dateMade;
        this.type = type;
        this.cashAccountName = cashAccount.getAccountName();
        this.cashAccount = cashAccount;
    }


    //only used for reading in the user for a temporary transaction until the cash account object can be created
    //to be saved as a transaction attribute
    public Transaction(double amount, Date dateMade, String type, String cashAccountName) {
        this.amount = amount;
        this.dateMade = dateMade;
        this.type = type;
        this.cashAccountName = cashAccountName;
    }

    public void setCashAccount(CashAccount c){
        cashAccount = c;
    }

    /**
     * Runs the operation type on the object.
     */
    public void execute(CashAccount cashAccount, double amount, String type){
        if(type.equals("Deposit")){
            cashAccount.deposit(amount);
        } else if (type.equals("Withdraw")){
            cashAccount.withdraw(amount);
        }
    }

    public Double getAmount() {return this.amount;}

    public Date getDateMade(){
        return this.dateMade;
    }

    public String getType() {return this.type;}

    /**
     * returns a String representation for display
     *
     * @return String
     */
    public String toString() {
        //LocalDate localDate = theDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        String theDateString = (localDate.getMonthValue() + "/" +
//                localDate.getDayOfMonth() +
//                "/" + localDate.getYear());

        return "Deposited OR Withdrew " + amount + " to " + this.cashAccountName + " on " + this.getDateMade();
    }


    /**
     * returns associated CashAccount
     *
     * @return CashAccount
     */
    public CashAccount getCashAccount() {
        return cashAccount;
    }

    /**
     * returns String name of cashAccount
     *
     */
    public String getCashAccountName() {return cashAccountName;}
}
