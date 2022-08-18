package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer createTransfer(Transfer transfer);

    List<Transfer> getTransferByAccountId (Long accountId);

    List<Transfer> getPendingTransfersByAccountId(Long accountId);

    void approveTransfer(Long transferId);

    void rejectTransfer(Long transferId);

    Transfer getTransferByTransferId(Long transferId);


}
