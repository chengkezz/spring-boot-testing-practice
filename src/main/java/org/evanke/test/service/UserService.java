package org.evanke.test.service;

import java.util.List;
import org.evanke.test.dto.CreateUserRequest;
import org.evanke.test.entity.User;
import org.evanke.test.exception.UserNotFoundException;
import org.evanke.test.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public synchronized User createUser(CreateUserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
        }

        User newUser = User.createUser(userRequest);
        return userRepository.save(newUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
    }
}
