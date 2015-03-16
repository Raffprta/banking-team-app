package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        return null;

    }

    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }

}
