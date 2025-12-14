package com.example.chatroom.controller;

import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/userLeave")
public class UserLeaveController extends HttpServlet {
    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");

        if (username != null && !username.trim().isEmpty()) {
            boolean removed = userService.logout(username);
            if (removed) {
                messageService.addSystemMessage(username + " 已离开聊天室");
                System.out.println("💡 前端通知：用户 " + username + " 立即离开");
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}