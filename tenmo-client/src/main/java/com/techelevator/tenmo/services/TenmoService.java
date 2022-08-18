package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import com.techelevator.util.WebUtils;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TenmoService {

    private final String baseUrl;
    private final RestTemplate restTemplate =  new RestTemplate();

    public TenmoService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public BigDecimal getBalanceByUserName(AuthenticatedUser currentUser) {
        ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "account/balance", HttpMethod.GET, makeGetEntity(currentUser), BigDecimal.class);
        return response.getBody();
    }

    public Account getAccountByUserId(AuthenticatedUser currentUser, Long userId){
        ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "account/" + userId, HttpMethod.GET, makeGetEntity(currentUser), Account.class);
        return response.getBody();
    }

    public User[] getAllUsers(AuthenticatedUser currentUser){
        ResponseEntity<User[]> response = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, makeGetEntity(currentUser), User[].class);
        return response.getBody();
    }

    public User getUserByAccountId(AuthenticatedUser currentUser, Long accountId){
        ResponseEntity<User> response = restTemplate.exchange(baseUrl + "account/" + accountId + "/user", HttpMethod.GET, makeGetEntity(currentUser), User.class);
        return response.getBody();
    }

    public void handleTransfer(AuthenticatedUser currentUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());

        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        try{
            restTemplate.postForObject(baseUrl + "transfer", entity, Void.class);
        } catch (RestClientResponseException | ResourceAccessException ex){
            String message = WebUtils.getResponseErrorMessage(ex.getMessage());
            BasicLogger.log(message);
            System.out.println(message);
        }

    }

    public void handleRequestTransfer(AuthenticatedUser currentUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());

        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        try{
            restTemplate.postForObject(baseUrl + "transfer/request", entity, Void.class);
        } catch (RestClientResponseException | ResourceAccessException ex){
            String message = WebUtils.getResponseErrorMessage(ex.getMessage());
            BasicLogger.log(message);
            System.out.println(message);
        }

    }

    public Transfer[] getTransfersByAccountId(AuthenticatedUser currentUser, Long userId) {
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/" + userId, HttpMethod.GET, makeGetEntity(currentUser), Transfer[].class);
        return response.getBody();
    }
    public Transfer[] getPendingTransfersByAccountId(AuthenticatedUser currentUser){
        ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "transfer/pending", HttpMethod.GET, makeGetEntity(currentUser), Transfer[].class);
        return response.getBody();
    }

    public String getTransferDetails(AuthenticatedUser currentUser, Transfer transfer) {
        String from = getUserByAccountId(currentUser, transfer.getAccountFrom()).getUsername();
        String to = getUserByAccountId(currentUser, transfer.getAccountTo()).getUsername();

        String transferStatus = "Approved";
        String transferType = "Send";

        return "-------------------------------------------\nTransfer Details\n-------------------------------------------" +
                "\nId: " + transfer.getTransferId() +
                "\nType: " + transferType +
                "\nStatus: " + transferStatus +
                "\nFrom: " + from +
                "\nTo: " + to +
                "\nAmount: $" + transfer.getAmount() ;
    }

    public void approveTransfer(AuthenticatedUser currentUser, Long transferId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());

        HttpEntity<Long> entity = new HttpEntity<>(transferId, headers);
        try {
            restTemplate.put(baseUrl + "transfer/approve", entity);
        } catch (RestClientResponseException | ResourceAccessException ex){
            String message = WebUtils.getResponseErrorMessage(ex.getMessage());
            BasicLogger.log(message);
            System.out.println(message);
        }

    }

    public void rejectTransfer(AuthenticatedUser currentUser, Long transferId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());

        HttpEntity<Long> entity = new HttpEntity<>(transferId, headers);
        try {
            restTemplate.put(baseUrl + "transfer/reject", entity);
        } catch (RestClientResponseException | ResourceAccessException ex){
            String message = WebUtils.getResponseErrorMessage(ex.getMessage());
            BasicLogger.log(message);
            System.out.println(message);
        }

    }

    public Transfer generateNewTransfer(AuthenticatedUser currentUser, Long userTo, Long userFrom, Long transferType, Long transferStatus, BigDecimal amount){
        Transfer transfer = new Transfer();
        transfer.setAccountTo(getAccountByUserId(currentUser, userTo).getAccountId());
        transfer.setAccountFrom(getAccountByUserId(currentUser,userFrom).getAccountId());
        transfer.setTransferTypeId(transferType);
        transfer.setTransferStatusId(transferStatus);
        transfer.setAmount(amount);
        return transfer;
    }

    private HttpEntity<Void> makeGetEntity(AuthenticatedUser user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());

        return new HttpEntity<>(headers);
    }

}
