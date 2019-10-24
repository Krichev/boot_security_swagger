package touchsoft.model;

import java.util.Objects;

public class ConnectionRest {
    private final User USER;

    public ConnectionRest(User user) {
        USER = user;
    }

    public User getUser() {
        return USER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionRest that = (ConnectionRest) o;
        return USER.getId().equals(that.USER.getId()) /*&& //equals user should have only one connection
                Objects.equals(session, that.session)*/;
    }


    @Override
    public int hashCode() {
        return Objects.hash(USER);
    }

}
