

package touchsoft.repository;

import lombok.extern.log4j.Log4j2;
import touchsoft.model.ConnectionRest;
import touchsoft.model.ConnectionWebsocket;
import touchsoft.model.Message;
import touchsoft.model.User;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Component
//@Repository
public class InMemoryChatRepository {

    static Queue<ConnectionRest> sessionListAgent = new ConcurrentLinkedQueue<>();
    static Queue<ConnectionRest> sessionListClient = new ConcurrentLinkedQueue();
    static Map<String, List<Message>> mapOfMsg = new ConcurrentHashMap<>();
    static Map<ConnectionRest, ConnectionRest> agentClient = new ConcurrentHashMap<>();

    public synchronized void addConnection(String fullName, ConnectionRest currentConnection) {
        log.info(currentConnection + " registered/logged in new user");
        if (fullName.endsWith("_client")) {

            if (sessionListAgent.size() > 0) {

                ConnectionRest agentConnection = sessionListAgent.poll();
                agentClient.put(agentConnection, currentConnection);
                if (currentConnection instanceof ConnectionWebsocket) {
                    sendQueueMessagesFrom_Whom(agentConnection, (ConnectionWebsocket) currentConnection);
                }
            } else {
                    sessionListClient.add(currentConnection);
            }
        } else if (fullName.endsWith("_agent")) {
            if (sessionListClient.size() > 0) {
                ConnectionRest clientConnection = sessionListClient.poll();
                agentClient.put(currentConnection, clientConnection);
                if (currentConnection instanceof ConnectionWebsocket) {
                    sendQueueMessagesFrom_Whom(clientConnection, (ConnectionWebsocket) currentConnection);
                } else {
                }
                    sessionListAgent.add(currentConnection);
                    System.out.println(sessionListAgent.peek()+" afebt session");
                    System.out.println(sessionListAgent.size());
            }
        }

    }


    public synchronized void closeLinkAgentClient(ConnectionRest currentConnection) {
        String fullName = getFullName(currentConnection.getUser());
        if (fullName == null && currentConnection == null) {
            return;
        }
        mapOfMsg.remove(fullName);
        if (fullName.endsWith("_agent")) {
            ConnectionRest clientConnection = agentClient.get(currentConnection);
            if (clientConnection != null) {
                agentClient.remove(currentConnection);
                log.info(clientConnection + " this client become free");
                log.info(currentConnection + " this agent become free");
                sessionListClient.add(clientConnection);
                sessionListAgent.add(currentConnection);
            }
        } else if (fullName.endsWith("_client")) {
            ConnectionRest connectionAgent = findKeyMapByValue(currentConnection);
            if (connectionAgent != null) {
                agentClient.remove(connectionAgent);
                sessionListAgent.add(connectionAgent);
                sessionListClient.add(currentConnection);
                log.info(connectionAgent + " this agent become free");
            }
        }

    }
    public synchronized void closeConversation(ConnectionRest currentConnection) {
        String fullName = getFullName(currentConnection.getUser());
        if (fullName == null && currentConnection == null) {
            return;
        }
        log.info(currentConnection + " this connection has closed");
        mapOfMsg.remove(fullName);
        if (fullName.endsWith("_agent")) {
            ConnectionRest clientConnection = agentClient.get(currentConnection);
            if (clientConnection != null) {
                agentClient.remove(currentConnection);
                log.info(clientConnection + " this client become free");
                sessionListClient.add(clientConnection);
            }
        } else if (fullName.endsWith("_client")) {
            ConnectionRest connectionAgent = findKeyMapByValue(currentConnection);
            if (connectionAgent != null) {
                sessionListAgent.add(connectionAgent);
                log.info(connectionAgent + " this agent become free");
                agentClient.remove(connectionAgent);
            }
        }
        currentConnection = null;
    }


    private synchronized ConnectionRest findKeyMapByValue(ConnectionRest connectionValue) {
        ConnectionRest connectionResult = null;
        Set<Map.Entry<ConnectionRest, ConnectionRest>> entrySet = agentClient.entrySet();
        for (Map.Entry<ConnectionRest, ConnectionRest> map : entrySet) {
            if (connectionValue.equals(map.getValue())) {
                connectionResult = map.getKey();
            }
        }
        log.info(connectionResult + " map find this");
        return connectionResult;
    }

    private synchronized void sendQueueMessagesFrom_Whom(ConnectionRest fromConnection, ConnectionWebsocket toConnection) {
        String fromName = getFullName(fromConnection.getUser());
        Session toSession = toConnection.getSession();
        try {
            log.info(fromName + "sendQueueMessagesFrom_Whom");
            if (mapOfMsg.containsKey(fromName)) {
                List<Message> listOfMess = mapOfMsg.get(fromName);
                for (int i = 0; i < listOfMess.size(); i++) {
                    toSession.getBasicRemote().sendObject(listOfMess.get(i));
                    mapOfMsg.remove(fromName);
                }
            }
        } catch (IOException | EncodeException e) {
            log.error(e + "sendQueueMessages to "+toSession);

        }


    }

    public synchronized Optional<List<Message>> getMessages(String fullName, ConnectionRest currentConnection) {
        ConnectionRest connectionClient;
        if (fullName.endsWith("_agent")) {
            connectionClient = agentClient.get(currentConnection);
           log.info(connectionClient + "connection client sesson");
            if (connectionClient != null) {
                String fullNameCompanion = getFullName(connectionClient.getUser());
                List<Message> listOfMessage = mapOfMsg.get(fullNameCompanion);
                mapOfMsg.remove(fullNameCompanion);
                return Optional.ofNullable(listOfMessage);
            } else return Optional.empty();

        } else {
            ConnectionRest connectionAgent = findKeyMapByValue(currentConnection);
            if (connectionAgent != null) {
                String fullNameCompanion = getFullName(connectionAgent.getUser());
                List<Message> listOfMessage = mapOfMsg.get(fullNameCompanion);
                mapOfMsg.remove(fullNameCompanion);
                return Optional.ofNullable(listOfMessage);
            } else return Optional.empty();


        }
    }


    public synchronized void sendMessage(ConnectionRest currentConnection, Message msg) {
        try {
            msg.setText(msg.getText().replaceAll("\n", ""));
            log.info(currentConnection + "  send message" + msg);
            String fullName = getFullName(currentConnection.getUser());
            if (fullName.endsWith("_agent")) {
                ConnectionRest clientConnection = agentClient.get(currentConnection);
                if (clientConnection != null && clientConnection instanceof ConnectionWebsocket) {
                    ((ConnectionWebsocket)clientConnection).getSession().getBasicRemote().sendObject(msg);
                    log.info(fullName + currentConnection + msg.getText() + "start sending  ");

                } else {
                    addMessage(currentConnection, msg);
                }
            } else if (fullName.endsWith("_client")) {
                ConnectionRest connectionAgent = findKeyMapByValue(currentConnection);
                log.info(connectionAgent + " speak with " + fullName);
                if (connectionAgent != null && connectionAgent instanceof ConnectionWebsocket) {
                    ((ConnectionWebsocket) connectionAgent).getSession().getBasicRemote().sendObject(msg);
                } else {
                    addMessage(currentConnection, msg);

                }
            }
        } catch (IOException | EncodeException e) {
           log.error(e+ "while sending message from" +currentConnection);
        }

    }

    private synchronized void addMessage(ConnectionRest currentConnection, Message message) {
        String fullName = getFullName(currentConnection.getUser());
        System.out.println(currentConnection.getUser());
//        System.out.println("addmessage//////////////////////////////////");
        if (!mapOfMsg.containsKey(fullName)) {
            mapOfMsg.computeIfAbsent(fullName, l -> new ArrayList<>()).add(message);
            return;
        }
        mapOfMsg.computeIfPresent(fullName, (s, strings) -> mapOfMsg.get(fullName)).add(message);

    }


    public static synchronized String getFullName(User user) {
        String role;
        if (user.getRoles().toString().contains("AGENT")) {
            role = "agent";
        } else {
            role = "client";
        }
        String s = user.getUsername() + "_" + role;
        return s;
    }
}
