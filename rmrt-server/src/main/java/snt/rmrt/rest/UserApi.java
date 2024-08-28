package snt.rmrt.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.user.User;
import snt.rmrt.repositories.UserRepository;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserApi {

    private final UserRepository userRepository;

    @Autowired
    public UserApi(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Principal currentUser(Principal principal) {
        return principal;
    }

    @GetMapping("users")
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @DeleteMapping
    public void deleteUser(@RequestParam String username) {
        userRepository.deleteById(username);
    }

}
