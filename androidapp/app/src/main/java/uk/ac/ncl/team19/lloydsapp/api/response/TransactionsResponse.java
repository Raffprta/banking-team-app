package uk.ac.ncl.team19.lloydsapp.api.response;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;

/**
 * @author Dale Whinham
 *
 * A class to represent the response given by the backend when a list of transactions is returned.
 */
public class TransactionsResponse extends APIResponse {
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
