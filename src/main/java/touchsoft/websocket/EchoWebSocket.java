package touchsoft.websocket;


import lombok.extern.log4j.Log4j2;
import touchsoft.model.Message;
import touchsoft.coders.MessageDecoder;
import touchsoft.coders.MessageEncoder;
import touchsoft.configuration.SpringContext;
import touchsoft.model.ConnectionWebsocket;
import touchsoft.model.User;
import touchsoft.repository.InMemoryChatRepository;
import touchsoft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Log4j2
@ServerEndpoint(value = "/chat/{user}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class EchoWebSocket {

    Boolean leave = false;
    String userName = "anonymous";
    Session session = null;
    String role = "anonymous";
    String fullName = "name_role";
    ConnectionWebsocket connectionWebsocket;

    @Autowired
    public EchoWebSocket() {
        this.userService = (UserService) SpringContext.getApplicationContext().getBean("userService");
        this.inMemoryChatRepository = (InMemoryChatRepository) SpringContext.getApplicationContext().getBean("inMemoryChatRepository");
    }

    private UserService userService;

    private InMemoryChatRepository inMemoryChatRepository;


    @OnOpen
    public void OnOpen(Session session, @PathParam("user") String nameRole) {

        this.fullName = nameRole;
        this.userName = nameRole.split("_")[0];
        this.role = nameRole.split("_")[1];
        this.session = session;
        User user = userService.search(userName);
        log.info(fullName + " in open" + session);
        connectionWebsocket = new ConnectionWebsocket(user, session);
        inMemoryChatRepository.addConnection(fullName, connectionWebsocket);

    }

    @OnClose
    public void OnClose(Session session) {
        log.info(fullName + " close session");
        if (fullName != null && connectionWebsocket != null) {
            inMemoryChatRepository.closeLinkAgentClient(connectionWebsocket);
        }
        this.session = null;
        session = null;

    }

    @OnError
    public void OnError(Session session, Throwable throwable) {
        log.error(throwable.getCause() + " cause " + throwable.getMessage() + " Exception in WS " + fullName);
    }

    @OnMessage
    public void OnMessage(Session session, Message msg) {
        log.info(msg + " from ");
        if (leave) {
            inMemoryChatRepository.addConnection(fullName, connectionWebsocket);
            leave = false;
        }

        if (msg.getText().contains("/leave")) {
            log.info(fullName + " leave chat");
            leave = true;
            inMemoryChatRepository.closeLinkAgentClient(connectionWebsocket);
            return;
        }

        if (msg.getText().contains("/exit")) {
            OnClose(session);
            return;

        }

        inMemoryChatRepository.sendMessage(connectionWebsocket, msg);

    }


}

