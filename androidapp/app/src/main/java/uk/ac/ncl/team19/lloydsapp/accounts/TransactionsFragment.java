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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;


/**
 * @author Yessengerey Bolatov (XML), Raffaello Perrotta
 * TODO : Not yet implemented.
 */
public class TransactionsFragment extends Fragment {
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final View transactionsView = inflater.inflate(R.layout.transaction_history_page, container, false);

        ExpandableListView elv = (ExpandableListView) transactionsView.findViewById(R.id.expandableListViewHistory);
        prepareListData();
        elv.setAdapter(new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild));

        return transactionsView;
    }
    /**
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
     **/


    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding group items data
        listDataHeader.add("January");
        listDataHeader.add("February");
        listDataHeader.add("March");
        listDataHeader.add("April");
        listDataHeader.add("May");
        listDataHeader.add("June");
        listDataHeader.add("July");
        listDataHeader.add("August");
        listDataHeader.add("September");
        listDataHeader.add("October");
        listDataHeader.add("November");
        listDataHeader.add("December");

        // Adding child data
        List<String> January = new ArrayList<String>();
        January.add("-£25.00 Withdrawn at Lloyds ATM");
        January.add("-£10.00 Payed to www.shoponline.co.uk");
        January.add("+£100.00 Transfer from John Smith 00-00-00");
        January.add("-£35 Payed to www.pizza.co.uk");

        List<String> February = new ArrayList<String>();
        February.add("-£5.00 Withdrawn at FastCash ATM");
        February.add("-£25.00 Payed to Virgin Media");
        February.add("-£10.00 Transferred to to Josh Whale 12345678");
        February.add("+£0.70 Tax return");

        List<String> March = new ArrayList<String>();
        March.add("+$0.80 Tax Return");
        March.add("-£25.00 Withdrawn at Lloyds ATM");

        List<String> April = new ArrayList<String>();
        April.add("-£30.00 Payed to www.helloworld.com");
        April.add("-£100.00 Payed to www.bestbubbles.co.uk");

        List<String> May = new ArrayList<String>();
        List<String> June = new ArrayList<String>();
        List<String> July = new ArrayList<String>();
        List<String> August = new ArrayList<String>();
        List<String> September = new ArrayList<String>();
        List<String> October = new ArrayList<String>();
        List<String> November = new ArrayList<String>();
        List<String> December = new ArrayList<String>();




        listDataChild.put(listDataHeader.get(0), January); // Header, Child data
        listDataChild.put(listDataHeader.get(1), February);
        listDataChild.put(listDataHeader.get(2), March);
        listDataChild.put(listDataHeader.get(3), April);
        listDataChild.put(listDataHeader.get(4), May);
        listDataChild.put(listDataHeader.get(5), June);
        listDataChild.put(listDataHeader.get(6), July);
        listDataChild.put(listDataHeader.get(7), August);
        listDataChild.put(listDataHeader.get(8), September);
        listDataChild.put(listDataHeader.get(9), October);
        listDataChild.put(listDataHeader.get(10), November);
        listDataChild.put(listDataHeader.get(11), December);

    }

}
