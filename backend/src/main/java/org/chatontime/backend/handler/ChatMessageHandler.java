package org.chatontime.backend.handler;


import org.chatontime.backend.model.ChatMessageDetail;
import org.chatontime.backend.repository.ChatMessageRepo;
import org.chatontime.backend.repository.ChatUserRepo;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatMessageHandler {
    public ChatMessageHandler(ChatMessageRepo chatMessageRepo) {
        this.chatMessageRepo = chatMessageRepo;
    }


    private static class MessageInfo{
        private String sender;
        private String content;

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private final ChatMessageRepo chatMessageRepo;

    @MessageMapping("/chatmsg")
    @SendTo("/topic/chatmsgrecord")
    public ArrayList<ChatMessageDetail> acceptAndSendMessage(MessageInfo messageInfo){
        ChatMessageDetail chatMessageDetail = new ChatMessageDetail();
        chatMessageDetail.setUserName(messageInfo.sender);
        chatMessageDetail.setMessage(messageInfo.content);
        chatMessageDetail.setSendTime(new Timestamp(System.currentTimeMillis()));
        chatMessageRepo.save(chatMessageDetail);

        List<ChatMessageDetail> allMessages = chatMessageRepo.findAllByOrderBySendTimeAsc();

        return new ArrayList<>(allMessages);
    }

    @MessageMapping("/loadhistory")
    @SendTo("/topic/chatmsgrecord")
    public ArrayList<ChatMessageDetail> loadHistoryMessage(Object obj){
        List<ChatMessageDetail> allMessages = chatMessageRepo.findAllByOrderBySendTimeAsc();
        return new ArrayList<>(allMessages);
    }
}
