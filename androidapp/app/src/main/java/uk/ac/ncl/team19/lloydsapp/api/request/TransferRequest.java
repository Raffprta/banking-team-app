package uk.ac.ncl.team19.lloydsapp.api.request;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;

/**
 * Created by Dale Whinham on 03/04/15.
 */
public class TransferRequest {
    private final Long fromAccId;
    private final String toAccNo;
    private final String toSortCode;
    private final Long amount;
    private final String reference;
    private final Transaction.Tag tag;

    public TransferRequest(long fromAccId, String toAccNo, String toSortCode, long amount, String reference, Transaction.Tag tag) {
        this.fromAccId = fromAccId;
        this.toAccNo = toAccNo;
        this.toSortCode = toSortCode;
        this.amount = amount;
        this.reference = reference;
        this.tag = tag == null ? Transaction.Tag.UNTAGGED : tag;
    }
}