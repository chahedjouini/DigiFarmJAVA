package services;

import entities.User;
import java.util.List;

public interface IUserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(int id);
    User getUserById(int id);
    List<User> getAllUsers();
    User login(String email, String password);
    void logout();
    boolean isEmailUnique(String email);
} 