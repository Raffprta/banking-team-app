package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * @author Dale Whinham
 *
 * A class to pass a transfer request to the backend, via the API Connector. Transfers may contain
 * a reference indicating what the transfer is, or can contain a tag indicating what type of transaction it is.
 *
 */

public class TransferRequest {
    private final Long fromAccId;
    private final String toAccNo;
    private final String toSortCode;
    private final Long amount;
    private final String reference;
    private final Long tag;

    public TransferRequest(long fromAccId, String toAccNo, String toSortCode, long amount, String reference, long tag) {
        this.fromAccId = fromAccId;
        this.toAccNo = toAccNo;
        this.toSortCode = toSortCode;
        this.amount = amount;
        this.reference = reference;
        this.tag = tag;
    }
}