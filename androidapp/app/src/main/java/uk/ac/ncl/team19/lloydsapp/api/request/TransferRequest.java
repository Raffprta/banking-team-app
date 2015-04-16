package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * @author Dale Whinham
 *
 * A class to pass a transfer request to the backend, via the API Connector. Transfers may contain
 * a reference indicating what the transfer is, or can contain a tag indicating what type of transaction it is.
 *
 */

public class TransferRequest {
    private Long fromAccId;
    private String toAccNo;
    private String toSortCode;
    private Long amount;
    private String reference;
    private Long tag;

    public TransferRequest(long fromAccId, String toAccNo, String toSortCode, long amount, String reference) {
        this.fromAccId = fromAccId;
        this.toAccNo = toAccNo;
        this.toSortCode = toSortCode;
        this.amount = amount;
        this.reference = reference;
    }

    public TransferRequest(long fromAccId, String toAccNo, String toSortCode, long amount, long tag) {
        this(fromAccId, toAccNo, toSortCode, amount, null);
        this.tag = tag;
    }
}