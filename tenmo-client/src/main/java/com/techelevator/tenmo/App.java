package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private TenmoService tenmoService = new TenmoService(API_BASE_URL);

    private static final Long REQUEST_ID = 1L;
    private static final Long SEND_ID = 2L;

    private static final Long PENDING_ID = 1L;
    private static final Long APPROVED_ID = 2L;
    private static final Long REJECTED_ID = 3L;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        System.out.println("Your current account balance is: $" + tenmoService.getBalanceByUserName(currentUser));
	}

	private void viewTransferHistory() {
        System.out.println(
                "-------------------------------------------\n" +
                "Transfers\n" +
                "ID        From/To                  Amount\n" +
                "-------------------------------------------");
        Transfer[] transfers = tenmoService.getTransfersByAccountId(currentUser, currentUser.getUser().getId());
        for( Transfer transfer : transfers) {
            Long transferId = transfer.getTransferId();
            String userNameTo = tenmoService.getUserByAccountId(currentUser, transfer.getAccountTo()).getUsername();
            String userNameFrom = tenmoService.getUserByAccountId(currentUser, transfer.getAccountFrom()).getUsername();
            String amount = transfer.getAmount().setScale(2, RoundingMode.HALF_UP).toString();

            if (transfer.getAccountFrom().equals(tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId()))  {
                System.out.printf("%d     To:   %-15s %10s%n", transferId, userNameTo, "-$" + amount);
            } else {
                System.out.printf("%d     From: %-15s %10s%n", transferId, userNameFrom, "+$" + amount);
            }
        }
        viewTransferDetails(transfers);
	}

    private void viewTransferDetails(Transfer[] transfers){
        System.out.println("-------------------------------------------\n");
        Integer transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");

        if(transferId.equals(0)){
            return;
        }
        for( Transfer transfer : transfers) {
            if(transfer.getTransferId().equals(Long.valueOf(transferId))) {
                System.out.println(tenmoService.getTransferDetails(currentUser,transfer));
                return;
            }
        }
        System.out.println("Invalid Transfer ID");
    }

	private void viewPendingRequests() {
        System.out.println("-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID To Amount\n" +
                "-------------------------------------------\n");

        Transfer[] transfers = tenmoService.getPendingTransfersByAccountId(currentUser);
        for( Transfer transfer : transfers) {
            Long transferId = transfer.getTransferId();
            String userNameTo = tenmoService.getUserByAccountId(currentUser, transfer.getAccountTo()).getUsername();
            //String userNameFrom = tenmoService.getUserByAccountId(currentUser, transfer.getAccountFrom()).getUsername();
            String amount = transfer.getAmount().setScale(2, RoundingMode.HALF_UP).toString();

            if (transfer.getAccountFrom().equals(tenmoService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId()))  {
                System.out.printf("%d     To:   %-15s %10s%n", transferId, userNameTo, "-$" + amount);
            }
        }
        Integer transferId = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (transferId.equals(0)) {
            return;
        }
        int menuSelection = consoleService.promptForMenuSelection(
                "1: Approve\n" +
                "2: Reject\n" +
                "0: Don't approve or reject\n" +
                "---------\n" +
                        "Please choose an option:");

        //menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
        if (menuSelection == 1) {
            tenmoService.approveTransfer(currentUser, Long.valueOf(transferId));
        } else if (menuSelection == 2) {
            tenmoService.rejectTransfer(currentUser, Long.valueOf(transferId));
        } else if (menuSelection == 0) {
            return;
        } else {
            System.out.println("Invalid Selection");
        }




	}

	private void sendBucks() {
        System.out.println("-------------------------------------------\n" +
                "Users\n" +
                "ID             Name\n" +
                "-------------------------------------------");
        User[] users = tenmoService.getAllUsers(currentUser);
        for(User user : users){
            System.out.println(user.getId() + "             " + user.getUsername());
        }
        Integer sendUserId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if(sendUserId.equals(0)){
            return;
        }

        BigDecimal amountToSend = consoleService.promptForBigDecimal("Enter amount: ");

        if(tenmoService.getAccountByUserId(currentUser, Long.valueOf(sendUserId)) != null){
            Transfer transfer = tenmoService.generateNewTransfer(currentUser, Long.valueOf(sendUserId), currentUser.getUser().getId(), SEND_ID, APPROVED_ID, amountToSend);
            tenmoService.handleTransfer(currentUser, transfer);

        } else{
            System.out.println("Invalid Account ID");
        }
	}

	private void requestBucks() {
        System.out.println("-------------------------------------------\n" +
                "Users\n" +
                "ID             Name\n" +
                "-------------------------------------------");
        User[] users = tenmoService.getAllUsers(currentUser);
        for(User user : users){
            System.out.println(user.getId() + "             " + user.getUsername());
        }
        Integer sendUserId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if(sendUserId.equals(0)){
            return;
        }

        BigDecimal amountToSend = consoleService.promptForBigDecimal("Enter amount: ");

        if(tenmoService.getAccountByUserId(currentUser, Long.valueOf(sendUserId)) != null){
            Transfer transfer = tenmoService.generateNewTransfer(currentUser, currentUser.getUser().getId(), Long.valueOf(sendUserId), REQUEST_ID, PENDING_ID, amountToSend);
            tenmoService.handleRequestTransfer(currentUser, transfer);

        } else{
            System.out.println("Invalid Account ID");
        }
		
	}

}
