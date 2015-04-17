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
 * This class provides an adapter to display transaction tags in a spinner view, including
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

        int iconResource = Transaction.Tag.getDrawableIdForTag(t);
        tagIcon.setImageDrawable(getContext().getResources().getDrawable(iconResource));
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}