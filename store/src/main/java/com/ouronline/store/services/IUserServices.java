package com.ouronline.store.services;

import com.ouronline.store.models.User;

import java.util.List;

public interface IUserServices {
    List<User> getUser();
    void createUser(User user);
    void updatePerson(Long id, User user);
    void deleteUser(Long id);
}
