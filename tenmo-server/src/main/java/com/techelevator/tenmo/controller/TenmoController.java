package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.ExceedFundsException;
import com.techelevator.tenmo.exception.InvalidAmountTransferException;
import com.techelevator.tenmo.exception.InvalidTransferAccountException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.AccountService;
import com.techelevator.tenmo.service.TransferService;
import com.techelevator.tenmo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    private AccountService accountService;
    private AccountDao accountDao;
    private UserService userService;
    private TransferService transferService;

    public TenmoController(AccountService accountService, AccountDao accountDao, UserService userService, TransferService transferService){
        this.accountService = accountService;
        this.accountDao = accountDao;
        this.userService = userService;
        this.transferService = transferService;
    }

    @GetMapping(path = "/account/balance")
    public BigDecimal findAccountBalanceByUserId(Principal principal){
        Long userId = userService.findIdByUsername(principal.getName());
        Account account = accountDao.findAccountByUserId(userId);

        return account.getBalance();
    }

    @GetMapping(path = "/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "/account/{id}")
    public Account getAccountByUserId(@PathVariable Long id){
        return accountService.findAccountByUserId(id);
    }

    @PostMapping(path = "/transfer")
    public void performTransfer(@RequestBody Transfer transfer) throws InvalidTransferAccountException, ExceedFundsException, InvalidAmountTransferException {
        transferService.handleTransfer(transfer);
    }

    @PostMapping(path = "/transfer/request")
    public void requestTransfer(@RequestBody Transfer transfer) throws InvalidTransferAccountException, InvalidAmountTransferException {
        transferService.handleRequestTransfer(transfer);
    }

    @GetMapping(path = "/transfer/{userId}")
    public List<Transfer> getTransferByUserId(@PathVariable Long userId) {
        Account account = accountService.findAccountByUserId(userId);
        return transferService.getTransferByAccountId(account.getAccountId());
    }

    @PutMapping(path = "/transfer/approve")
    public void approveTransfer(@RequestBody Long transferId) throws ExceedFundsException {
        transferService.approveTransfer(transferId);
    }

    @PutMapping(path = "/transfer/reject")
    public void rejectTransfer(@RequestBody Long transferId) throws ExceedFundsException, InvalidTransferAccountException {
        transferService.rejectTransfer(transferId);
    }

    @GetMapping(path = "/transfer/pending")
    public List<Transfer> getPendingTransfersByAccountId(Principal principal){
        Long userId = userService.findIdByUsername(principal.getName());
        Account account = accountDao.findAccountByUserId(userId);
        return transferService.getPendingTransfersByAccountId(account.getAccountId());
    }

    @GetMapping(path = "/account/{accountId}/user")
    public User getUserByAccountId(@PathVariable Long accountId){
        return userService.findUserByAccountId(accountId);
    }

}
