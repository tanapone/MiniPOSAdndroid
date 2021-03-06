package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class MessageModel {
    @Expose
    private Message message;

    public MessageModel(){}

    public Message getMessage() {
        return message;
    }

    public MessageModel(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        message = gson.fromJson(jsonResponse,Message.class);
    }
    public class Message{
        @Expose
        private String message;

        public String getMessageText() {
            return message;
        }

        public void setMessageText(String messageText) {
            this.message = messageText;
        }
    }
}
