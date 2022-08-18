package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByUserId(Long userId) {
        String sql = "SELECT * FROM account WHERE user_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, userId);

        if(rows.next()){
            return mapRowToAccount(rows);
        }
        return null;
    }

    @Override
    public void updateAccount(Account updateAccount) {
        String sql = "UPDATE account SET user_id = ?, balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql, updateAccount.getUserId(), updateAccount.getBalance(), updateAccount.getAccountId());
    }

    @Override
    public Account findAccountByAccountId(Long accountId) {
        String sql ="SELECT * FROM account WHERE account_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, accountId);
        if(rows.next()){
            return mapRowToAccount(rows);
        }
        return null;
    }

    private Account mapRowToAccount(SqlRowSet row) {
        Long accountId = row.getLong("account_id");
        Long userId = row.getLong("user_id");
        BigDecimal balance = row.getBigDecimal("balance");
        Account account = new Account(accountId, userId, balance);
        return account;
    }
}
