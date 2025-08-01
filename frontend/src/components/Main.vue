<template>
    <div class="centered-div">
        <div class="title">聊天室</div>
        <div class="horizontal-line" style="margin-top: 16px;"></div>
        <div class="chat">
            <div class="people"></div>
            <div class="vertical-line"></div>
            <div class="chatbox">
                <div class="chatbox-content">
                    <div v-for="(msg, index) in messages" :key="index"
                        :class="msg.self ? 'my-message' : 'other-message'">
                        {{ msg.self ? msg.content : `${msg.sender} : ${msg.content}` }}
                    </div>
                </div>
                <div class="horizontal-line"></div>
                <div class="submitbox">
                    <form @submit.prevent="sendMessage"> <!-- 阻止默认提交行为 -->
                        <input type="text" v-model="inputMessage" placeholder="请输入消息" class="input" maxlength="40">
                        <button type="submit" class="submit">发送</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { StompClientKey } from '@/main';
import type { Client } from '@stomp/stompjs';
import { ref, onMounted, onUnmounted, inject } from 'vue';

// self 为之后显示是否是自己发的消息做准备
const messages = ref<{ content: string, sender: string, self: boolean, sendTime?: string }[]>([]);
const inputMessage = ref('');
const stompClient = inject(StompClientKey) as Client

let subscription: any = null


const getUsername = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : { name: '错误用户' };
}


const subscribeToMessages = () => {
    subscription = stompClient.subscribe("/topic/chatmsgrecord", (msg: any) => {
        try {
            const chatMessageList = JSON.parse(msg.body);

            // ✅ 修正字段映射（根据你的后端字段）
            const currentUser = getUsername()
            const displayMessages = chatMessageList.map((chatMessage: any) => ({
                content: chatMessage.message,     // ChatMessageDetail.message
                sender: chatMessage.userName,     // ChatMessageDetail.userName
                self: chatMessage.userName === currentUser.name
            }));

            messages.value = displayMessages;
            console.log('收到消息列表:', displayMessages);
        } catch (error) {
            console.error('解析消息失败:', error);
        }
    });
};

//一直加载历史信息
const loadHistoryMessages = () => {
    stompClient.publish({
        destination: "/app/loadhistory",
        body: JSON.stringify({}),
        headers: { "content-type": "application/json" }
    })
}

// 发送信息
const sendMessage = () => {
    if (inputMessage.value == '') {
        return
    }
    const currentUser = getUsername()
    const messageObj = {
        sender: currentUser.name,
        content: inputMessage.value,
    }
    stompClient.publish({
        destination: "/app/chatmsg",
        body: JSON.stringify(messageObj),
        headers: { "content-type": "application/json" }
    });
    inputMessage.value = ''
}

onMounted(() => {
    if (stompClient && stompClient.connected) {
        subscribeToMessages();
        loadHistoryMessages(); // ✅ 加载历史记录
    } else {
        stompClient.onConnect = () => {
            subscribeToMessages();
            loadHistoryMessages(); // ✅ 加载历史记录
        };
    }
})

onUnmounted(() => {
    subscription.unsubscribe();
})
</script>

<style scoped>
.centered-div {
    background-color: azure;
    border: 5px solid #000;
    border-radius: 10px;
    text-align: center;
    border-color: black;
    width: 1000px;
    height: 900px;
    margin: 0 auto;
    /*让div水平居中*/
    position: absolute;
    left: 0;
    right: 0;
    display: flex;
    flex-direction: column;
    /* 垂直排列子元素 */
}

.title {
    margin-top: 16px;
    font-size: 32px;
}

.horizontal-line {
    width: 100%;
    height: 3px;
    background-color: black;
}

.vertical-line {
    width: 3px;
    height: 100%;
    background-color: black;
}

.chat {
    display: flex;
    flex: 1;
}

.people {
    width: 200px;
    background-color: #f0f0f0;
    border-bottom-left-radius: 5px;
}

.chatbox {
    flex: 1;
    background-color: #f0f0f0;
    border-bottom-right-radius: 5px;
    display: flex;
    flex-direction: column;
    /* 垂直排列子元素 */
}

.chatbox-content {
    overflow-y: auto;
    width: 100%;
    height: 600px;
    /* box-sizing: border-box; */
}

.submitbox {
    flex: 1;
}

.input {
    width: 600px;
    height: 150px;
    border-radius: 5px;
    border: 1px solid #ccc;
    padding: 10px;
    box-sizing: border-box;
    /* 包括内边距和边框在内的宽度计算 */
    margin: 16px auto;

}

.submit {
    height: 150px;
    width: 100px;
}

.my-message {
    display: flex;
    height: 20px;
    width: fit-content;
    margin-top: 10px;
    margin-bottom: 10px;
    padding-left: 16px;
    padding-right: 16px;
    align-items: center;
    border: 2px solid black;
    border-radius: 9px;
    margin-left: auto;
    margin-right: 16px;
}

.other-message {
    display: flex;
    height: 20px;
    width: fit-content;
    margin-top: 10px;
    margin-bottom: 10px;
    padding-left: 16px;
    padding-right: 16px;
    align-items: center;
    border: 2px solid black;
    border-radius: 9px;
    /* margin-left: auto;
    margin-right: 16px; */
    margin-left: 16px;
}
</style>
