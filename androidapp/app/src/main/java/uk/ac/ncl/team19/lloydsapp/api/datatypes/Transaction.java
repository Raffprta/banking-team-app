package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;

/**
 * @author Dale Whinham
 *
 * A data type to represent a transaction.
 */
public class Transaction {
    public enum Tag {
        @SerializedName("0")
        UNTAGGED(0),
        @SerializedName("1")
        FOODDRINK(1),
        @SerializedName("2")
        CLOTHES(2),
        @SerializedName("3")
        WITHDRAWAL(3),
        @SerializedName("4")
        ENTERTAINMENT(4),
        @SerializedName("5")
        OTHER(5),
        @SerializedName("6")
        UTILITY(6),
        @SerializedName("7")
        TRANSPORT(7),
        @SerializedName("8")
        DONATION(8);

        private long id;

        Tag(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public static Tag getTag(long id) {
            // Look up tag
            for (Transaction.Tag t: Transaction.Tag.values()) {
                if (t.getId() == id) {
                    return t;
                }
            }

            // We should never reach here
            assert false;
            return null;
        }
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
        return CurrencyMangler.integerToSterlingString(amount);
    }

    public String getReference() {
        return reference;
    }

    public Tag getTag() {
        return tag;
    }
}