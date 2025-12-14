package com.example.chatroom.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/chat.html", "/sendMessage", "/getMessages", "/getOnlineUsers"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthenticationFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        System.out.println("Filter checking: " + requestURI);

        // 检查用户是否已登录
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("用户未登录，重定向到登录页面");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.html");
            return;
        }

        // 用户已登录，继续处理请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthenticationFilter 销毁");
    }
}