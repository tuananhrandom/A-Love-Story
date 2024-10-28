package anh.nguyen.alovestory.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import anh.nguyen.alovestory.entities.User;
import anh.nguyen.alovestory.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void changeFullName(Long userId, String fullName) {
        if (userRepository.existsById(userId)) {
            User thisUser = userRepository.findById(userId).get();
            thisUser.setUsername(fullName);
            userRepository.save(thisUser);
            System.out.println("Name updated:" + fullName);
        } else {
            System.err.println("no user");
        }
    }

    public void createNewUser(User user) {
        userRepository.save(user);
    }

    public boolean Login(String username, String password) {
        User thisUser = userRepository.findByUsername(username);
        if (userRepository.existsById(thisUser.getUserId())) {
            if (thisUser.getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void changeAvatar(Long userId, String avatarUrl) {
        User thisUser = userRepository.findById(userId).get();
        thisUser.setAvatar(avatarUrl);
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
