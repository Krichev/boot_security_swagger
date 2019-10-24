package touchsoft.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import touchsoft.model.ConnectionRest;
import touchsoft.repository.InMemoryChatRepository;
import touchsoft.model.Message;
import touchsoft.model.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import touchsoft.dto.UserDataDTO;
import touchsoft.dto.UserResponseDTO;
import touchsoft.model.User;
import touchsoft.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Log4j2
@RestController
@RequestMapping("/touch")
@Api(tags = "touch")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    InMemoryChatRepository inMemoryChatRepository;
    private ConnectionRest connection;


    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/login")
    @ApiOperation(value = "${UserController.login}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public String loginUser(//
                            @ApiParam("Username") @RequestParam String username, //
                            @ApiParam("Password") @RequestParam String password) {

        return userService.processLogin(username, password);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/register")
    @ApiOperation(value = "${UserController.register}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Username is already in use"), //
    })
    public String registerUser(@ApiParam("Register User") @RequestBody UserDataDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        userService.registerUser(modelMapper.map(userDTO, User.class));
        log.info(username + " has log in");
        return userService.processLogin(username, password);
    }


    @DeleteMapping(value = "/delete/agent/{username}")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @ApiOperation(value = "${UserController.deleteAgent}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String deleteAgent(@ApiParam("Username") @PathVariable String username) {
        userService.deleteUser(username);
        return username;
    }

    @DeleteMapping(value = "/delete/client/{username}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.deleteClient}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String deleteClient(@ApiParam("Username") @PathVariable String username) {
        userService.deleteUser(username);
        return username;
    }

    @GetMapping(value = "/search/agent/{username}")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @ApiOperation(value = "${UserController.searchAgent}", response = UserResponseDTO.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO searchAgent(@ApiParam("Username") @PathVariable String username) {
        return modelMapper.map(userService.searchByRoleAndName(new ArrayList<Role>(Arrays.asList(Role.ROLE_AGENT)), username), UserResponseDTO.class);
    }

    @GetMapping(value = "/search/client/{username}")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.searchClient}", response = UserResponseDTO.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO searchClient(@ApiParam("Username") @PathVariable String username) {
        return modelMapper.map(userService.searchByRoleAndName(new ArrayList<Role>(Arrays.asList(Role.ROLE_CLIENT)), username), UserResponseDTO.class);
    }

    @GetMapping(value = "/search/all/clients")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @ApiOperation(value = "${UserController.searchAllClients}", response = UserResponseDTO.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    @ResponseBody
    public List<User> searchAllClients() {
        return userService.searchAllUsersByRole(new ArrayList<Role>(Arrays.asList(Role.ROLE_CLIENT)));
    }

    @GetMapping(value = "/search/all/agents")
    @PreAuthorize("hasRole('ROLE_AGENT')")
    @ApiOperation(value = "${UserController.searchAllAgents}", response = UserResponseDTO.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    @ResponseBody
    public List<User> searchAllAgents() {
        return userService.searchAllUsersByRole(new ArrayList<Role>(Arrays.asList(Role.ROLE_AGENT)));
    }




    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping(value = "/send_message", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiOperation(value = "${UserController.sendMessage}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})

    public String sendMessage(@ApiParam("Send Message") @RequestBody String text, HttpServletRequest req) {
        User user = userService.findMyself(req);
        //create connectionRest instead of original in map, after seeking it will be delete
        ConnectionRest connectionRest = new ConnectionRest(user);
        String fullName = InMemoryChatRepository.getFullName(user);
        Message message = new Message(fullName, text);
        inMemoryChatRepository.sendMessage(connectionRest, message);
        return HttpStatus.OK.getReasonPhrase();
    }


    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/receive_message")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    @ApiOperation(value = "${UserController.getMessage}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})

    public ResponseEntity<List<Message>> getMessage(HttpServletRequest req) {
        User user = userService.findMyself(req);
        ConnectionRest connectionRest = new ConnectionRest(user);
        String fullName = InMemoryChatRepository.getFullName(user);
        List<Message> messages = inMemoryChatRepository.getMessages(fullName, connectionRest).orElseGet(ArrayList::new);
        if (messages.isEmpty()) {
            messages.add(new Message("Server", "you have not any unread messages"));
        }
        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
    }

    @GetMapping("/leave")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<Void> leaveUser(HttpServletRequest req) {
        User user = userService.findMyself(req);
        ConnectionRest connectionRe1st = new ConnectionRest(user);
        inMemoryChatRepository.closeConversation(connection);
        return new ResponseEntity<>(HttpStatus.OK);//maybe other more suitable answer
    }

    @GetMapping("/exit")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<Void> exitAgent(HttpServletRequest req) {
        User user = userService.findMyself(req);
        ConnectionRest connectionRe1st = new ConnectionRest(user);
        inMemoryChatRepository.closeLinkAgentClient(connectionRe1st);
        return new ResponseEntity<>(HttpStatus.OK);//maybe other more suitable answer
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_AGENT') or hasRole('ROLE_CLIENT')")
    @ApiImplicitParam(name = "Authorization", value = "Access Token",
            paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public String refreshUser(HttpServletRequest req) {
        return userService.refresh(req.getRemoteUser());
    }


}
