package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    Long findIdByUsername(String username);

//    User findUserByUserId(Long userId);

    User findUserByAccountId(Long accountId);

    boolean create(String username, String password);
}
