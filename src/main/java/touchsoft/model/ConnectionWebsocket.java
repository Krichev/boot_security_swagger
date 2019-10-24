package touchsoft.model;

import javax.websocket.Session;

public class ConnectionWebsocket extends ConnectionRest {

    private final Session SESSION;

    public Session getSession() {
        return SESSION;
    }

    public ConnectionWebsocket(User user, Session session) {
        super(user);
        this.SESSION = session;
    }


}
