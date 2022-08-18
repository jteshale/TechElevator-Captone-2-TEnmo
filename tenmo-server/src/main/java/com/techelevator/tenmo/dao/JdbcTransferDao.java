package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    private static final Long REQUEST_ID = 1L;
    private static final Long SEND_ID = 2L;

    private static final Long PENDING_ID = 1L;
    private static final Long APPROVED_ID = 2L;
    private static final Long REJECTED_ID = 3L;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Transfer createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer VALUES (DEFAULT,?,?,?,?,?) RETURNING transfer_id ";
        Long transferId = jdbcTemplate.queryForObject(sql, Long.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount());
        transfer.setTransferId(transferId);
        return transfer;
    }
    public List<Transfer> getTransferByAccountId (Long accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while (row.next()) {
                Transfer transfer = mapRowToTransfer(row);
                transfers.add(transfer);
        }
        return transfers;

    }

    @Override
    public void approveTransfer(Long transferId) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        jdbcTemplate.update(sql, APPROVED_ID, transferId);
    }

    @Override
    public void rejectTransfer(Long transferId) {
        String sql = "DELETE FROM transfer WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferId);
    }

    @Override
    public Transfer getTransferByTransferId(Long transferId) {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, transferId);
        if (row.next()) {
            return mapRowToTransfer(row);

        }
        return null;
    }

    @Override
    public List<Transfer> getPendingTransfersByAccountId(Long accountId){
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? AND transfer_type_id = ? AND transfer_status_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId, REQUEST_ID, PENDING_ID);

        while(row.next()){
            Transfer transfer = mapRowToTransfer(row);
            transfers.add(transfer);
        }
        return transfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet row) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(row.getLong("transfer_id"));
        transfer.setTransferTypeId(row.getLong("transfer_type_id"));
        transfer.setTransferStatusId(row.getLong("transfer_status_id"));
        transfer.setAccountFrom(row.getLong("account_from"));
        transfer.setAccountTo(row.getLong("account_to"));
        transfer.setAmount(row.getBigDecimal("amount"));
        return transfer;
    }



}
