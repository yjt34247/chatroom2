package com.example.chatroom.service;

import com.example.chatroom.entity.Message;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageService {
    private static final CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
    private static final int MAX_MESSAGES = 1000; // 最大消息数量限制
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    public void saveMessage(Message message) {
        messages.add(message);

        // 限制消息数量，防止内存溢出
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    public List<Message> getMessagesForUser(String username) {
        List<Message> userMessages = new ArrayList<>();

        for (Message message : messages) {
            // 系统消息所有人都能看到
            if (message.isSystemMessage()) {
                userMessages.add(message);
            }
            // 群聊消息（receiver为"all"）
            else if ("all".equals(message.getReceiver())) {
                userMessages.add(message);
            }
            // 私聊消息：当前用户是发送者或接收者
            else if (username.equals(message.getSender()) || username.equals(message.getReceiver())) {
                userMessages.add(message);
            }
        }

        return userMessages;
    }

    public String getFormattedMessagesForUser(String username) {
        StringBuilder sb = new StringBuilder();
        List<Message> userMessages = getMessagesForUser(username);

        for (Message message : userMessages) {
            String time = message.getTimestamp().format(formatter);

            if (message.isSystemMessage()) {
                sb.append("<div class='system-message'>[")
                        .append(time)
                        .append("] ")
                        .append(message.getContent())
                        .append("</div>");
            } else if ("all".equals(message.getReceiver())) {
                sb.append("<div><b>[")
                        .append(time)
                        .append("] ")
                        .append(message.getSender())
                        .append(":</b> ")
                        .append(message.getContent())
                        .append("</div>");
            } else {
                // 私聊消息特殊标记
                String sender = message.getSender();
                String receiver = message.getReceiver();
                String privateTo = username.equals(sender) ? receiver : sender;

                sb.append("<div class='private-message'><b>[")
                        .append(time)
                        .append("] ")
                        .append(sender)
                        .append(" → ")
                        .append(privateTo)
                        .append(" (私聊):</b> ")
                        .append(message.getContent())
                        .append("</div>");
            }
        }

        return sb.toString();
    }

    public void addSystemMessage(String content) {
        Message systemMsg = Message.createSystemMessage(content);
        saveMessage(systemMsg);
    }

    public void clearAllMessages() {
        messages.clear();
    }

    public int getMessageCount() {
        return messages.size();
    }
}