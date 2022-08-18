package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.ExceedFundsException;
import com.techelevator.tenmo.exception.InvalidAmountTransferException;
import com.techelevator.tenmo.exception.InvalidTransferAccountException;
import com.techelevator.tenmo.exception.TransferException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Component
public class TransferService {

    private TransferDao transferDao;
    private AccountService accountService;

    private static final Long REQUEST_ID = 1L;
    private static final Long SEND_ID = 2L;

    private static final Long PENDING_ID = 1L;
    private static final Long APPROVED_ID = 2L;
    private static final Long REJECTED_ID = 3L;

    public TransferService(TransferDao transferDao, AccountService accountService) {
        this.transferDao = transferDao;
        this.accountService = accountService;
    }

    @Transactional
    public void handleTransfer(Transfer transfer) throws ExceedFundsException, InvalidTransferAccountException, InvalidAmountTransferException {
        try{
            if (accountService.findAccountByAccountId(transfer.getAccountFrom()).getBalance().compareTo(transfer.getAmount()) < 0) {
                throw new ExceedFundsException("Insufficient funds: can't perform transfer");
            } else if(transfer.getAccountFrom().equals(transfer.getAccountTo())){
                throw new InvalidTransferAccountException("Invalid account: cannot transfer to self");
            } else if(transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0){
                throw new InvalidAmountTransferException("Invalid amount: please enter a valid amount");
            }
            if (transfer.getTransferStatusId().equals(APPROVED_ID)) {
                accountService.withdraw(transfer.getAccountFrom(), transfer.getAmount());
                accountService.deposit(transfer.getAccountTo(), transfer.getAmount());
            }
            transferDao.createTransfer(transfer);
        } catch (Exception e){
            throw new TransferException(e.getMessage());
        }
    }

    public void handleRequestTransfer(Transfer transfer) throws InvalidTransferAccountException, InvalidAmountTransferException{
        if(transfer.getAccountFrom().equals(transfer.getAccountTo())){
            throw new InvalidTransferAccountException("Invalid account: cannot request from self");
        } else if(transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountTransferException("Invalid amount: please enter a valid amount");
        }
        transferDao.createTransfer(transfer);
    }

    public void approveTransfer(Long transferId) throws ExceedFundsException {
        Transfer transfer = transferDao.getTransferByTransferId(transferId);
        if (transfer != null && accountService.findAccountByAccountId(transfer.getAccountTo()).getBalance().compareTo(transfer.getAmount()) <= 0) {
            throw new ExceedFundsException("Insufficient funds: can't approve transfer");
        }
        transferDao.approveTransfer(transferId);
        accountService.withdraw(transfer.getAccountFrom(), transfer.getAmount());
        accountService.deposit(transfer.getAccountTo(), transfer.getAmount());
    }

    public void rejectTransfer(Long transferId) throws ExceedFundsException, InvalidTransferAccountException {
        Transfer transfer = transferDao.getTransferByTransferId(transferId);
        if (transfer == null) {
            throw new InvalidTransferAccountException("Invalid transfer id:  Please enter a valid Transfer Id");
        }
        else if (accountService.findAccountByAccountId(transfer.getAccountTo()).getBalance().compareTo(transfer.getAmount()) <= 0) {
            throw new ExceedFundsException("Insufficient funds: can't approve transfer");
        }
        transferDao.rejectTransfer(transferId);

    }

    public List<Transfer> getPendingTransfersByAccountId(Long accountId){
        return transferDao.getPendingTransfersByAccountId(accountId);
    }

    public List<Transfer> getTransferByAccountId(Long accountId) {
        return transferDao.getTransferByAccountId(accountId);
    }
}
