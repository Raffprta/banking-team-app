package uk.ac.ncl.team19.lloydsapp.accounts;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

/**
 * Created by Yessengerey on 31/03/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private String[] groups = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    private String[][] children = {
            { "-£25.00 Withdrawn at Lloyds ATM", "-£10.00 Payed to www.shoponline.co.uk", "+£100.00 Transfer from John Smith 00-00-00", "-£35 Payed to www.pizza.co.uk" },
            { "-£5.00 Withdrawn at FastCash ATM", "-£25.00 Payed to Virgin Media", "-£10.00 Payed to Josh Whale 12345678", "+£0.70 Tax return" },
            { "+$0.80 Tax Return", "-£25.00 Withdrawn at Lloyds ATM" },
            { "-£30.00 Payed to www.helloworld.com", "-£100.00 Payed to www.bestbubbles.co.uk" },
            {"--"},
            {"--"},
            {"--"},
            {"--"},
            {"--"},
            {"--"},
            {"--"},
            {"--"}

    };

    @Override
    public int getGroupCount() {
        return groups.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return children[i].length;
    }

    @Override
    public Object getGroup(int i) {
        return groups[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return children[i][i1];
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(TransactionsFragment.this.getActivity());
        textView.setText(getGroup(i).toString());

        return textView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(TransactionsFragment.this.getActivity());
        textView.setText(getChild(i, i1).toString());

        return textView;
    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
