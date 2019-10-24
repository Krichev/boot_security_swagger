package touchsoft;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import touchsoft.model.Role;
import touchsoft.model.User;
import touchsoft.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Log4j2
@SpringBootApplication
public class JwtAuthServiceApp implements CommandLineRunner {// interface to override run method

    @Autowired
    UserService userService;

    public static void main(String[] args) {
        log.debug("start application////////////////////////////////////////////////////");
        SpringApplication.run(JwtAuthServiceApp.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... params) throws Exception {
        log.info("add agent to DB");
        User agent = new User();
        agent.setUsername("agent");
        agent.setPassword("agent");
        agent.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_AGENT)));
        userService.registerUser(agent);
//        log.info("add client to DB");
//
//        User client = new User();
//        client.setUsername("client");
//        client.setPassword("client");
//        client.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_CLIENT)));
//        userService.registerUser(client);


    }

}
