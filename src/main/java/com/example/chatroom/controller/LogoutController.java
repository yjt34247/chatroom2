package com.example.chatroom.controller;

import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");

            if (username != null) {
                boolean removed = userService.logout(username);
                if (removed) {
                    // 添加用户离开的系统消息
                    messageService.addSystemMessage(username + " 已退出聊天室");
                    System.out.println("用户 " + username + " 主动退出");
                }
            }

            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/logout.jsp");
    }
}