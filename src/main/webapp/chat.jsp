<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>在线聊天室</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="chat-container">
    <div class="chat-header">
        <h2>在线聊天室</h2>
        <div class="user-info">
            <span>欢迎, ${sessionScope.username}</span>
            <a href="${pageContext.request.contextPath}/logout">退出</a>
        </div>
    </div>

    <div class="chat-main">
        <div id="messages" class="messages-container"></div>

        <form id="messageForm">
            <div class="message-input-group">
                <select id="receiverSelect" name="receiver">
                    <option value="all">所有人</option>
                </select>
                <input type="text" name="message" placeholder="输入消息..." required>
                <button type="submit">发送</button>
            </div>
        </form>
    </div>

    <div class="online-users">
        <h3>在线用户 (<span id="onlineCount">0</span>)</h3>
        <ul id="userList"></ul>
    </div>
</div>

<script>
    const contextPath = '${pageContext.request.contextPath}';
    const currentUsername = '${sessionScope.username}';
    let messageInterval, userInterval;

    // 检查登录
    if (!currentUsername) {
        window.location.href = contextPath + '/index.jsp';
    }

    // 获取消息
    function fetchMessages() {
        fetch(contextPath + '/message')
            .then(response => response.text())
            .then(data => {
                if (data) {
                    document.getElementById('messages').innerHTML = data;
                    document.getElementById('messages').scrollTop =
                        document.getElementById('messages').scrollHeight;
                }
            })
            .catch(error => console.error('获取消息失败:', error));
    }

    // 获取在线用户
    function fetchOnlineUsers() {
        fetch(contextPath + '/getOnlineUsers')
            .then(response => response.text())
            .then(data => {
                const select = document.getElementById('receiverSelect');
                const userList = document.getElementById('userList');

                select.innerHTML = '<option value="all">所有人</option>';
                userList.innerHTML = '';

                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = data;
                const options = tempDiv.querySelectorAll('option');

                let userCount = 0;
                options.forEach(option => {
                    if (option.value !== 'all') {
                        select.appendChild(option.cloneNode(true));

                        const li = document.createElement('li');
                        li.textContent = option.textContent;
                        userList.appendChild(li);
                        userCount++;
                    }
                });

                document.getElementById('onlineCount').textContent = userCount;
            })
            .catch(error => console.error('获取在线用户失败:', error));
    }

    // 发送消息函数
    function sendMessage() {
        const messageInput = document.querySelector('input[name="message"]');
        const receiverSelect = document.getElementById('receiverSelect');
        const message = messageInput.value.trim();
        const receiver = receiverSelect.value;

        if (!message) {
            alert('请输入消息内容');
            return;
        }

        fetch(contextPath + '/message', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                'message': message,
                'receiver': receiver
            })
        })
            .then(response => {
                if (response.ok) {
                    messageInput.value = '';
                    fetchMessages();
                } else {
                    alert('发送失败，状态码: ' + response.status);
                }
            })
            .catch(error => {
                console.error('发送错误:', error);
                alert('网络错误: ' + error.message);
            });
    }

    // 通知用户离开
    function notifyUserLeave() {
        if (!currentUsername || currentUsername === '') return;

        const data = new URLSearchParams({
            'username': currentUsername
        });

        if (navigator.sendBeacon) {
            navigator.sendBeacon(contextPath + '/userLeave', data);
        } else {
            try {
                const xhr = new XMLHttpRequest();
                xhr.open('POST', contextPath + '/userLeave', false);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.send(data.toString());
            } catch (err) {
                console.error('发送离开通知失败:', err);
            }
        }
    }

    // 页面加载
    document.addEventListener('DOMContentLoaded', function() {
        console.log('聊天室页面加载完成，用户:', currentUsername);

        // 立即执行一次
        fetchMessages();
        fetchOnlineUsers();

        // 设置轮询
        messageInterval = setInterval(fetchMessages, 5000);
        userInterval = setInterval(fetchOnlineUsers, 5000);

        // 表单提交
        const messageForm = document.getElementById('messageForm');
        if (messageForm) {
            messageForm.addEventListener('submit', function(e) {
                e.preventDefault();
                sendMessage();
            });
        }

        // 回车发送
        const messageInput = document.querySelector('input[name="message"]');
        if (messageInput) {
            messageInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }
    });

    // 页面关闭
    window.addEventListener('beforeunload', function() {
        console.log('页面即将关闭');

        if (messageInterval) clearInterval(messageInterval);
        if (userInterval) clearInterval(userInterval);

        notifyUserLeave();
    });

    // 页面可见性变化
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            fetchMessages();
            fetchOnlineUsers();
        }
    });
</script>
</body>
</html>