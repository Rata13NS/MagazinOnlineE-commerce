package com.ouronline.store.services;

import com.ouronline.store.repositories.UserRepository;
import com.ouronline.store.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<com.ouronline.store.models.User> getUser() {
        return userRepository.findAll();
    }


    public void createUser(User user) {
        validateUsername(user.getUsername());
        validateEmailAdress(user.getEmailAdress());
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(String.format("Person with id %d not found", id)));
    }


    public void updateUser(Long id, User user) {
        User userToUpdate = userRepository.findById(id).orElseThrow(
                () -> new IllegalStateException(String.format("Person with id %d not found", id)));

        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setEmailAdress(user.getEmailAdress());
        userRepository.save(userToUpdate);
    }

    private void validateUsername(String username) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if(userOptional.isPresent()) {
            throw new IllegalStateException(String.format("Person with username %s already exists", username));
        }
    }

    private void validateEmailAdress(String emailAdress) {
        Optional<User> userOptional = userRepository.getUserByEmailAdress(emailAdress);
        if(userOptional.isPresent()) {
            throw new IllegalStateException(String.format("Person with email adress %s already exists", emailAdress));
        }
    }

    public void deleteUser(Long id) {
        boolean userExists = userRepository.existsById(id);
        if(!userExists) {
            throw new IllegalStateException(String.format("Person with id %d not found", id));
        }
        userRepository.deleteById(id);
    }

    public void validateExistingUsername(String username) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if (!userOptional.isPresent()) {
            throw new IllegalStateException(String.format("Username %s does not exist", username));
        }
    }

    public void validatePassword(String username, String password) {
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getPassword().equals(password)) {
                throw new IllegalStateException("Incorrect password");
            }
        }
    }

    public Optional<com.ouronline.store.models.User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }



    public void updateUserDetails(User updatedUser) {
        User userToUpdate = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new IllegalStateException(String.format("Person with id %d not found", updatedUser.getId())));

        userToUpdate.setEmailAdress(updatedUser.getEmailAdress());
        userRepository.save(userToUpdate);
    }

    public void validateEmailForUpdate(Long id, String emailAdress) {
        Optional<User> userOptional = userRepository.getUserByEmailAdress(emailAdress);
        if (userOptional.isPresent() && !userOptional.get().getId().equals(id)) {
            throw new IllegalStateException(String.format("Person with email address %s already exists", emailAdress));
        }
    }

    public void updateUserByAdmin(Long id, User user) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        User userToUpdate = userRepository.findById(id).orElseThrow(
                () -> new IllegalStateException(String.format("User with id %d not found", id))
        );

        if (user.getUsername() != null) {
            userToUpdate.setUsername(user.getUsername());
        }
        if (user.getEmailAdress() != null) {
            userToUpdate.setEmailAdress(user.getEmailAdress());
        }
        if (user.getPassword() != null) {
            userToUpdate.setPassword(user.getPassword());
        }
        if (user.getStatus() != null) {
            userToUpdate.setStatus(user.getStatus());
        }

        userRepository.save(userToUpdate);
    }
}

