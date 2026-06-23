package com.paypal.user_service.service;

import com.paypal.user_service.entity.User;
import com.paypal.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserserviceImpl implements Userservice {

    private final UserRepository userRepository;

    public UserserviceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User CreateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByid(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllusers() {
        return userRepository.findAll();
    }
}