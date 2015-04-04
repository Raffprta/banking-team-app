package uk.ac.ncl.team19.lloydsapp.api.request;

/**
 * Created by Dale Whinham on 03/04/15.
 */
public class TransferRequest {
    private long fromAccId;
    private String toAccNo;
    private String toSortCode;
    private int amount;
    private String reference;

    public TransferRequest(long fromAccId, String toAccNo, String toSortCode, int amount, String reference) {
        this.fromAccId = fromAccId;
        this.toAccNo = toAccNo;
        this.toSortCode = toSortCode;
        this.amount = amount;
        this.reference = reference;
    }
}