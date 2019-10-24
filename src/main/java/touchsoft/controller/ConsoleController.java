package touchsoft.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import touchsoft.dto.RoleTokenDTO;
import touchsoft.dto.UserDataDTO;
import touchsoft.model.User;
import touchsoft.repository.InMemoryChatRepository;
import touchsoft.service.UserService;

@ApiIgnore
@Log4j2
@RestController
@RequestMapping("/web")
public class ConsoleController {
    @Autowired
    UserService userService;
    @Autowired
    InMemoryChatRepository inMemoryChatRepository;
    @Autowired
    private ModelMapper modelMapper;


    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/register")
    @ApiOperation(value = "${UserController.register}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Username is already in use"), //
    })
    public String registerUser(@ApiParam("Register User") @RequestBody UserDataDTO userDTO) {
        String username = userDTO.getUsername();
        log.info(username + " has log in");
        return userService.registerUser(modelMapper.map(userDTO, User.class));

    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/login")
    @ApiOperation(value = "${UserController.loginConsole}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public RoleTokenDTO loginConsoleUser(@ApiParam("Register User") @RequestBody UserDataDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        User user = userService.search(username);
        String role = user.getRoles().toString();
        if (role.contains("AGENT")) {
            role = "agent";
        } else if (role.contains("CLIENT")) {
            role = "client";
        } else throw new RuntimeException("incorrect role in user " + user);
        String token = userService.processConsoleLogin(username, password);
        return new RoleTokenDTO(role, token);//stub

    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/refresh")
    @ApiOperation(value = "${UserController.loginConsole}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public String refreshUser(@ApiParam("Refresh User") @RequestBody String username) {
        return userService.refresh(username);
    }
}