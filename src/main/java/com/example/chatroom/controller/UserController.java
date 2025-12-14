package com.example.chatroom.controller;

import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/getOnlineUsers")
public class UserController extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter().write("<option value=\"all\">所有人</option>");
            return;
        }

        // 获取当前用户名（用于排除自己）
        String currentUser = (String) session.getAttribute("username");

        // 1. 更新当前用户的活动时间
        if (currentUser != null) {
            userService.updateUserActivity(currentUser);
            System.out.println("更新用户活动时间: " + currentUser);
        }

        // 2. 关键：清理超过8秒无活动的用户（处理异常退出）
        int cleanedCount = userService.cleanupInactiveUsers();
        if (cleanedCount > 0) {
            System.out.println("✅ 清理了 " + cleanedCount + " 个不活跃用户");
        }

        // 3. 获取清理后的在线用户列表
        List<String> onlineUsers = userService.getOnlineUsernames();

        // 4. 生成HTML选项
        StringBuilder html = new StringBuilder();

        // 群聊选项
        html.append("<option value=\"all\">所有人</option>");

        // 在线用户选项
        if (onlineUsers != null && !onlineUsers.isEmpty()) {
            for (String user : onlineUsers) {
                // 不显示当前用户自己
                if (!user.equals(currentUser)) {
                    html.append("<option value=\"").append(user).append("\">")
                            .append(user).append("</option>");
                }
            }
        } else {
            System.out.println("⚠️ 在线用户列表为空");
        }

        // 5. 返回HTML
        response.getWriter().write(html.toString());

        // 调试信息
        System.out.println("返回在线用户选项，总数: " + (onlineUsers != null ? onlineUsers.size() : 0));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}