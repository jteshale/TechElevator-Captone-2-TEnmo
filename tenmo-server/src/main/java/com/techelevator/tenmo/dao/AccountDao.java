package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    Account findAccountByUserId(Long userId);

    void updateAccount (Account updateAccount);

    Account findAccountByAccountId (Long accountId);

}
