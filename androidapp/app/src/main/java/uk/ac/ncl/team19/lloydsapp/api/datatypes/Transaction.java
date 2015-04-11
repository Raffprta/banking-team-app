package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class Transaction {
    private static final String STERLING_FORMATTING_STRING = "£%.2f";

    public enum Tag {
        @SerializedName("0")
        UNTAGGED,
        @SerializedName("1")
        FOODDRINK,
        @SerializedName("2")
        CLOTHES,
        @SerializedName("3")
        WITHDRAWAL,
        @SerializedName("4")
        ENTERTAINMENT,
        @SerializedName("5")
        OTHER,
        @SerializedName("6")
        UTILITY,
        @SerializedName("7")
        TRANSPORT,
        @SerializedName("8")
        DONATION
    }

    private long id;
    private long time;
    private long fromAccountId;
    private long toAccountId;
    private long amount;
    private String reference;
    private Tag tag;

    public long getId() {
        return id;
    }

    public Date getDate() {
        return new Date(time * 1000);
    }

    public long getFromAccountId() {
        return fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public long getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return String.format(STERLING_FORMATTING_STRING, amount / 100.0);
    }

    public String getReference() {
        return reference;
    }

    public Tag getTag() {
        return tag;
    }
}