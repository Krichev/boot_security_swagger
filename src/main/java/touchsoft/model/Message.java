package touchsoft.model;

import lombok.Data;

@Data
public class Message {
    private String name;
    private String text;
//    private String role;

    @java.beans.ConstructorProperties({"name", "text"})
    public Message(String name, String text) {
        this.name = name;
        this.text = text;
//        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

//    public String getRole() {
//        return role;
//    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static class MessageBuilder {
        private String name;
        private String text;
//        private String role;

        MessageBuilder() {
        }

        public MessageBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MessageBuilder text(String text) {
            this.text = text;
            return this;
        }

//        public MessageBuilder role(String role) {
//            this.role = role;
//            return this;
//        }

        public Message build() {
            return new Message(name, text);
        }

        public String toString() {
            return "Message.MessageBuilder(name=" + this.name + ", text=" + this.text + ")";
        }
    }
}
