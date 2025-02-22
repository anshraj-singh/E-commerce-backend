package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public void saveEntry(UserEntry userEntry){
        userRepository.save(userEntry);
    }

    public List<UserEntry> getAllUser(){
        return userRepository.findAll();
    }

    public Optional<UserEntry> getById(String myId){
        return userRepository.findById(myId);
    }

    public void deleteById(String myId){
        userRepository.deleteById(myId);
    }
}
