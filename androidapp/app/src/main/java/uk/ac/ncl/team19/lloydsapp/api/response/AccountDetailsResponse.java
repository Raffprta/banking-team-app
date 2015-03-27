package uk.ac.ncl.team19.lloydsapp.api.response;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;

/**
 * Created by Dale Whinham on 27/03/15.
 */
public class AccountDetailsResponse extends APIResponse {
    private List<BankAccount> accounts;

    public List<BankAccount> getAccounts() {
        return accounts;
    }
}
