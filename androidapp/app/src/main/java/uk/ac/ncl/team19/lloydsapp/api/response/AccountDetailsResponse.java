package uk.ac.ncl.team19.lloydsapp.api.response;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;

/**
 * @author Dale Whinham
 *
 * A class to represent the response given by the backend when a list of accounts is returned.
 */
public class AccountDetailsResponse extends APIResponse {
    private List<BankAccount> accounts;

    public List<BankAccount> getAccounts() {
        return accounts;
    }
}
