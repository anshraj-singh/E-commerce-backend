package com.quickcart.ecommerce.service;

import com.quickcart.ecommerce.entity.Cart;
import com.quickcart.ecommerce.entity.Order;
import com.quickcart.ecommerce.entity.UserEntry;
import com.quickcart.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(UserEntry userEntry) {
        userEntry.setPassword(passwordEncoder.encode(userEntry.getPassword())); // Encrypt password
        userEntry.setRoles(List.of("USER"));
        userRepository.save(userEntry);
    }

    public void saveAdmin(UserEntry user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
    }

    public List<UserEntry> getAllUser () {
        return userRepository.findAll();
    }

    public Optional<UserEntry> getById(String myId) {
        return userRepository.findById(myId);
    }

    public void deleteById(String myId) {
        userRepository.deleteById(myId);
    }

    public Optional<UserEntry> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }
}