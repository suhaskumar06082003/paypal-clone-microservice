package com.paypal.user_service.service;

import com.paypal.user_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface Userservice {
    User CreateUser(User user);

    Optional<User>getUserByid(Long id);
    List<User> getAllusers();





}
