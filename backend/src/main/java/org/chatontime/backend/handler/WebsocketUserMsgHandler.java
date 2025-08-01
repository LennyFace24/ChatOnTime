package org.chatontime.backend.handler;

import org.chatontime.backend.model.ChatUser;
import org.chatontime.backend.model.FeedbackToUseMsg;
import org.chatontime.backend.model.UserMsg;
import org.chatontime.backend.repository.ChatUserRepo;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebsocketUserMsgHandler {

    private final ChatUserRepo chatUserRepo;

    public WebsocketUserMsgHandler(ChatUserRepo chatUserRepo) {
        this.chatUserRepo = chatUserRepo;
    }

    @MessageMapping("/login") // 处理 /app/login
    @SendTo("/topic/login-response") // 响应到 /topic/login-response
    public FeedbackToUseMsg handleLogin(UserMsg userMsg){
        // 如果没找到就返回false
        List<ChatUser> chatUser = chatUserRepo.findByName(userMsg.getName());

        if(chatUser.isEmpty()){
            System.out.println("你失败了11111");
            return new FeedbackToUseMsg(false,"登录失败",userMsg);
        } else if (chatUser.get(0).getPassword().equals(userMsg.getPassword())) {
            System.out.println("你成功了22222");
            return new FeedbackToUseMsg(true,"登录成功",userMsg);
        }else {
            return new FeedbackToUseMsg(false,"出现未知错误",null);
        }
    }

    @MessageMapping("/register") // 处理 /app/login
    @SendTo("/topic/register-response") // 响应到 /topic/login-response
    public FeedbackToUseMsg handleRegister(UserMsg userMsg){
        // 如果没找到就返回false
        if(chatUserRepo.findByName(userMsg.getName()).isEmpty()){
            ChatUser chatUser = new ChatUser();
            chatUser.setName(userMsg.getName());
            chatUser.setPassword(userMsg.getPassword());
            chatUserRepo.save(chatUser);
            return new FeedbackToUseMsg(true,"注册成功",userMsg);
        }else{
            return new FeedbackToUseMsg(false,"注册失败",userMsg);
        }

    }



}
