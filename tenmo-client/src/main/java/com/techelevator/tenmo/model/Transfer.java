package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transfer {
    @JsonProperty("transfer_id")
    private Long transferId;

    @JsonProperty("transfer_type_id")
    private Long transferTypeId;

    @JsonProperty("transfer_status_id")
    private Long transferStatusId;

    @JsonProperty("account_from")
    private Long accountFrom;

    @JsonProperty("account_to")
    private Long accountTo;

    private BigDecimal amount;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public Long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Long accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

//    @Override
//    public String toString() {
//        return "Transfer Details\n" +
//                "\nId: " + transferId +
//                "\nType: " + transferTypeId +
//                "\nStatus: " + transferStatusId +
//                "\nFrom: " + accountFrom +
//                "\nTo: " + accountTo +
//                "\nAmount: $" + amount ;
//    }
}
