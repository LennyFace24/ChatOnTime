<template>
    <div class="flex items-center justify-center min-h-[80vh]">
        <div class="w-full max-w-5xl bg-white/90 shadow-2xl rounded-2xl p-8 flex flex-col">
            <div class="text-3xl font-bold text-gray-800 mb-2 tracking-wide">聊天室</div>
            <div class="h-1 w-full bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 rounded mb-6"></div>
            <div class="flex flex-1 max-h-[600px]">
                <div
                    class="w-56 bg-gradient-to-b from-blue-100 to-purple-100 rounded-l-2xl p-4 flex flex-col items-center border-r border-gray-200">
                    <div class="text-lg font-semibold text-gray-700 mb-2">在线成员</div>
                </div>
                <div class="flex-1 flex flex-col bg-gradient-to-b from-white to-pink-50 rounded-r-2xl p-4">
                    <div ref="messagesContainer" class="flex-1 overflow-y-auto space-y-2 pb-2">
                        <div v-for="(msg, index) in messages" :key="index"
                            :class="msg.self ? 'flex justify-end' : 'flex justify-start'">
                            <div :class="{ 'flex flex-col items-end': msg.self }">
                                <div
                                    class="font-semibold text-blue-600 text-sm mr-4">
                                    {{ msg.sender }}
                                </div>
                                <div
                                    :class="msg.self
                                        ? 'bg-blue-400 text-white rounded-br-2xl rounded-tl-2xl rounded-bl-md px-4 py-2 mr-4 shadow-md '
                                        : 'bg-gray-200 text-gray-800 rounded-bl-2xl rounded-tr-2xl rounded-br-md px-4 py-2  shadow '">
                                    {{ msg.content }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <form @submit.prevent="sendMessage" class="flex gap-2 mt-4">
                        <input type="text" v-model="inputMessage" placeholder="请输入消息" maxlength="40"
                            class="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400 transition bg-white/90 shadow" />
                        <button type="submit"
                            class="bg-blue-500 hover:bg-blue-600 text-white font-semibold px-6 py-2 rounded-lg shadow transition">发送</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { StompClientKey } from '@/main';
import type { Client } from '@stomp/stompjs';
import { ref, onMounted, onUnmounted, inject, nextTick, watch } from 'vue';

// self 为之后显示是否是自己发的消息做准备
const messages = ref<{ content: string, sender: string, self: boolean, sendTime?: string }[]>([]);
const inputMessage = ref('');
const stompClient = inject(StompClientKey) as Client
const messagesContainer = ref<HTMLElement>()

let subscription: any = null


const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
    })
}

// 监听消息数组变化，自动滚动到底部
watch(messages, () => {
    scrollToBottom()
}, { deep: true })

const getUsername = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : { name: '错误用户' };
}


const subscribeToMessages = () => {
    subscription = stompClient.subscribe("/topic/chatmsgrecord", (msg: any) => {
        try {
            const chatMessageList = JSON.parse(msg.body);

            //  修正字段映射（根据你的后端字段）
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
        loadHistoryMessages(); //  加载历史记录
    } else {
        stompClient.onConnect = () => {
            subscribeToMessages();
            loadHistoryMessages(); //  加载历史记录
        };
    }
})

onUnmounted(() => {
    subscription.unsubscribe();
})
</script>

<style scoped></style>
