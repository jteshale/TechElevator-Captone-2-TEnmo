package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techelevator.tenmo.dao.AccountDao;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

    @JsonProperty("account_id")
    @NotNull
    private Long accountId;

    @JsonProperty("user_id")
    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal balance;

    public Account(Long accountId, Long userId, BigDecimal balance){
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
