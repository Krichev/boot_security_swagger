package touchsoft.coders;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import touchsoft.model.Message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

//@Log4j
public class MessageEncoder implements Encoder.Text<Message> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(Message message) throws EncodeException {
//        log.info(message+" in Encode class");
//        return gson.toJson(message);
        String messageStr = null;
        try {
            messageStr = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            log.error
        }
        return messageStr;

    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
