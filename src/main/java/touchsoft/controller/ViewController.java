package touchsoft.controller;

import springfox.documentation.annotations.ApiIgnore;
import touchsoft.repository.UserRepository;
import touchsoft.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ApiIgnore
@Controller
public class ViewController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        String errorMessge = null;
        if (error != null) {
//            errorMessge = "Username or Password is incorrect !!";
            errorMessge = error.toString();
        }
        if (logout != null) {
            errorMessge = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessge", errorMessge);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {

        return "register";
    }

    @GetMapping("/check")
    public ResponseEntity<Void> checkPage() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/chat")
    public String chatPage() {

        return "chat";

    }
}
//    @CrossOrigin(origins = "http://localhost:8080")
//    @PostMapping("/chat")
//    public String chatPage(@RequestBody AuthName authName) { {
//        String name = jwtTokenProvider.getUsername(authName.getToken());
//        User us = userRepository.findByUsername(name);
//        if (us.getUsername().equals(name)) {
//            System.out.println("////////////////////////////sssssssssssssssssssssdddddddddddddddddddddddddd");
//            return "chat";
//        } else return "login";
//    }
//    @CrossOrigin(origins = "http://localhost:8080")
//    @GetMapping("/chat")
//    public String chatPage(@RequestParam(value = "token") String token,
//                           @RequestParam(value = "name") String nameReq) {
//        {
//            String userName = jwtTokenProvider.getUsername(token);
//            User us = userRepository.findByUsername(userName);
//            if (us.getUsername().equals(nameReq)) {
//                System.out.println("////////////////////////////sssssssssssssssssssssdddddddddddddddddddddddddd");
//                return "chat";
//            } else return "login";
//        }


//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.Optional;
//
//@Controller
//public class ViewController {
//
////    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
//
//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
////        LOGGER.debug("Getting login page, error={}", error);
//        return new ModelAndView("login", "error", error);
//    }
//
//}

