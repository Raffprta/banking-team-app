package uk.ac.ncl.team19.lloydsapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;

/**
 * @author Dale Whinham
 *
 * This class provides an adaptor to display transaction tags in a spiner view, including
 * an appropriate icon to indicate the nature of the tag.
 */
public class TagSpinnerAdapter extends ArrayAdapter<String> {
    public TagSpinnerAdapter(Context ctx) {
        super(ctx, R.layout.tag_spinner_item, R.id.tagDescription, ctx.getResources().getStringArray(R.array.tags));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView tagIcon = (ImageView) v.findViewById(R.id.tagIcon);

        Transaction.Tag t = Transaction.Tag.getTag(position);

        tagIcon.setVisibility(t == Transaction.Tag.UNTAGGED ? View.INVISIBLE : View.VISIBLE);

        /**
         * The following icons are taken from the Google Material Design icons pack,
         * available at: https://github.com/google/material-design-icons
         */
        int iconResource;
        switch (t) {
            case FOODDRINK:
                iconResource = R.drawable.ic_local_restaurant_black_48dp;
                break;

            case CLOTHES:
                iconResource = R.drawable.ic_local_mall_black_48dp;
                break;

            case WITHDRAWAL:
                iconResource = R.drawable.ic_attach_money_black_48dp;
                break;

            case ENTERTAINMENT:
                iconResource = R.drawable.ic_local_attraction_black_48dp;
                break;

            case UTILITY:
                iconResource = R.drawable.ic_whatshot_black_48dp;
                break;

            case TRANSPORT:
                iconResource = R.drawable.ic_directions_transit_black_48dp;
                break;

            case DONATION:
                iconResource = R.drawable.ic_favorite_black_48dp;
                break;

            case OTHER:
            default:
                iconResource = R.drawable.ic_star_black_48dp;
                break;
        }

        tagIcon.setImageDrawable(getContext().getResources().getDrawable(iconResource));
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}