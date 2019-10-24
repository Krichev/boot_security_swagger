package touchsoft.coders;

import com.fasterxml.jackson.databind.ObjectMapper;
import touchsoft.model.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

//@Log4j
public class MessageDecoder implements Decoder.Text<Message> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Message decode(String s) throws DecodeException {
//        log.info(s+" in Decode message");
        Message message = null;
        try {
            message = mapper.readValue(s, Message.class);
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return message;
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
