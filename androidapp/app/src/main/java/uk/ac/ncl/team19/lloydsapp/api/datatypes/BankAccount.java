package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class BankAccount implements Serializable {
    private static final String STERLING_FORMATTING_STRING = "£%.2f";

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

    public String getAccountTypeString(Context ctx) {
        String accountTypeString = "";
        switch (type) {
            case CURRENT:
                accountTypeString = ctx.getString(R.string.acc_current);
                break;
            case STUDENT:
                accountTypeString = ctx.getString(R.string.acc_student);
                break;
            case SAVINGS:
                accountTypeString = ctx.getString(R.string.acc_savings);
                break;
        }
        return accountTypeString;
    }

    public String getFormattedSortCode() {
        return sortCode == null || sortCode.length() != 6 ? null : String.format("%s-%s-%s", sortCode.substring(0,2), sortCode.substring(2,4), sortCode.substring(4,6));
    }

    public String getFormattedBalance() {
        return String.format(STERLING_FORMATTING_STRING, balance / 100.0);
    }

    public String getFormattedAvailableFunds() {
        return String.format(STERLING_FORMATTING_STRING, (balance + overdraft) / 100.0);
    }
}