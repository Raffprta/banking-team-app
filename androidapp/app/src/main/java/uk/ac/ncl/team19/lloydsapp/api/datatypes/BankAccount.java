package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class BankAccount {
    public enum Type {
        @SerializedName("0")
        CURRENT,
        @SerializedName("1")
        SAVINGS,
        @SerializedName("2")
        STUDENT
    }

    private long id;
    private Type type;
    private String nickname;
    private String accountNumber;
    private String sortCode;
    private double interest;
    private long overdraft;
    private long balance;

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public double getInterest() {
        return interest;
    }

    public long getOverdraft() {
        return overdraft;
    }

    public long getBalance() {
        return balance;
    }
}