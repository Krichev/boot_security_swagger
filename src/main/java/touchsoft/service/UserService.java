package touchsoft.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import touchsoft.exception.CustomException;
import touchsoft.model.ConnectionRest;
import touchsoft.model.Role;
import touchsoft.model.User;
import touchsoft.repository.InMemoryChatRepository;
import touchsoft.repository.UserRepository;
import touchsoft.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Log4j2
@Service
public class UserService {

    @Autowired
    InMemoryChatRepository inMemoryChatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String processLogin(String username, String password) {
        User user = search(username);
        log.info(user + " has log in");
        ConnectionRest connectionRest = new ConnectionRest(user);
        String fullName = InMemoryChatRepository.getFullName(user);
        inMemoryChatRepository.addConnection(fullName, connectionRest);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, user.getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String processConsoleLogin(String username, String password) {
        User user = search(username);
        log.info(user + " has log in");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, user.getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String registerUser(User user) {
        if (!userRepository.existsByUsername(user.getUsername())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    public User search(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User searchByRoleAndName(List<Role> role, String username) {
        User user = userRepository.findByRolesAndUsername(role, username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public List<User> searchAllUsersByRole(List<Role> role) {
        List<User> allUsers = userRepository.findAllByRoles(role);
        if (allUsers == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return allUsers;
    }

    public User findMyself(HttpServletRequest req) {
        String name = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
        User us = userRepository.findByUsername(name);
        return us;
    }

    public String refresh(String username) {
        return jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getRoles());
    }

}

