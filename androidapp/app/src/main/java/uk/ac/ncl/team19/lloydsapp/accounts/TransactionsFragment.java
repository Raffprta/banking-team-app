package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ExpandableListActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.app.Activity;


import uk.ac.ncl.team19.lloydsapp.R;


/**
 * @author Yessengerey Bolatov (XML), Raffaello Perrotta
 * TODO : Not yet implemented.
 */
public class TransactionsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final View transactionsView = inflater.inflate(R.layout.transaction_history_page, container, false);

        ExpandableListView elv = (ExpandableListView) transactionsView.findViewById(R.id.expandableListView);

        elv.setAdapter(new ExpandableListAdapter());

        return transactionsView;
    }

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


    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }

}
