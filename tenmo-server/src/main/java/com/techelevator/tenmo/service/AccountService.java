package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountService {

    private UserService userService;
    private AccountDao accountDao;


    public AccountService(UserService userService, AccountDao accountDao){
        this.userService = userService;
        this.accountDao = accountDao;
    }

    public Account findAccountByUserId(Long userId){
        return accountDao.findAccountByUserId(userId);
    }

    public Account withdraw (Long accountId, BigDecimal amount) {
        Account account = findAccountByAccountId(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        accountDao.updateAccount(account);
        return account;
    }

    public Account deposit (Long accountId, BigDecimal amount) {
        Account account = findAccountByAccountId(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountDao.updateAccount(account);
        return account;
    }

    public Account findAccountByAccountId(Long accountId) {
        return accountDao.findAccountByAccountId(accountId);
    }




}
