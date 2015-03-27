package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class Transaction {
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
    private long fromAccount;
    private long toAccount;
    private long amount;
    private String reference;
    private Tag tag;

    public long getId() {
        return id;
    }

    public Date getDate() {
        return new Date(time);
    }

    public long getFromAccountId() {
        return fromAccount;
    }

    public long getToAccountId() {
        return toAccount;
    }

    public long getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public Tag getTag() {
        return tag;
    }
}