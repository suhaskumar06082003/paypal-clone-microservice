package com.paypal.user_service.controller;


import com.paypal.user_service.entity.User;
import com.paypal.user_service.service.Userservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private Userservice userservice;

    public UserController(Userservice userservice){
        this.userservice=userservice;
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){

        User savedUser = userservice.CreateUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

//return ResponseEntity.status(HttpStatus.CREATED).body(createUser(user).getBody());
   @GetMapping("/{id}")
    public ResponseEntity<User>getuserByid(@PathVariable Long id){
        return userservice.getUserByid((id)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
   }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> users = userservice.getAllusers();

        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(users);
    }




}
