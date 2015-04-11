package uk.ac.ncl.team19.lloydsapp.api.response;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class TransactionsResponse extends APIResponse {
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
