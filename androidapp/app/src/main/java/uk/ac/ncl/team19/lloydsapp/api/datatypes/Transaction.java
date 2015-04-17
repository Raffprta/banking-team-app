package uk.ac.ncl.team19.lloydsapp.api.datatypes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;

/**
 * @author Dale Whinham
 *
 * A data type to represent a transaction.
 */
public class Transaction implements Serializable {
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

        // Returns the corresponding icon for the tag.
        //
        // NB. The following icons are taken from the Google Material Design icons pack,
        // available at: https://github.com/google/material-design-icons
        public static int getDrawableIdForTag(Transaction.Tag t) {
            switch (t) {
                case FOODDRINK: return R.drawable.ic_local_restaurant_black_48dp;
                case CLOTHES: return R.drawable.ic_local_mall_black_48dp;
                case WITHDRAWAL: return R.drawable.ic_attach_money_black_48dp;
                case ENTERTAINMENT: return R.drawable.ic_local_attraction_black_48dp;
                case UTILITY: return R.drawable.ic_whatshot_black_48dp;
                case TRANSPORT: return R.drawable.ic_directions_transit_black_48dp;
                case DONATION: return R.drawable.ic_favorite_black_48dp;
                case OTHER: return R.drawable.ic_star_black_48dp;
                case UNTAGGED: return R.drawable.out;
                default: return -1;
            }
        }

        // Returns the tag's color
        public static int getColorIdForTag(Transaction.Tag t) {
            switch (t) {
                case FOODDRINK: return R.color.tag_food_drink;
                case CLOTHES: return R.color.tag_clothes;
                case WITHDRAWAL: return R.color.tag_withdrawal;
                case ENTERTAINMENT: return R.color.tag_entertainment;
                case OTHER: return R.color.tag_other;
                case UTILITY: return R.color.tag_utility;
                case TRANSPORT: return R.color.tag_transport;
                case DONATION: return R.color.tag_donation;
                case UNTAGGED: return R.color.tag_untagged;
                default: return -1;
            }
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