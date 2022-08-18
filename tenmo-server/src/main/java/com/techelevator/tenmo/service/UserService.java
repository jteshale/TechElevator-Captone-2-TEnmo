package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {
    private UserDao userDao;
    private AccountDao accountDao;

    public UserService(UserDao userDao,AccountDao accountDao){
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    public Long findIdByUsername(String userName){
        return userDao.findIdByUsername(userName);
    }

    public User findUserByAccountId(Long accountId) {
        return  userDao.findUserByAccountId(accountId);
    }
}
