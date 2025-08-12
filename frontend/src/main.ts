import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Client } from '@stomp/stompjs'
import type { InjectionKey } from 'vue'

import App from './App.vue'
import router from './router'
import './assets/input.css'

//用于注入的key
export const StompClientKey: InjectionKey<Client> = Symbol('StompClient');

const app = createApp(App)


const getWebSocketUrl = () => {
    const protocol = window.location.protocol === 'http:' ? 'ws:' : 'wss:';
    const host = window.location.host;
    return `${protocol}//${host}/chat`;
}


const stompClient = new Client({
    brokerURL: getWebSocketUrl(),
    connectHeaders: {},
    reconnectDelay: 5000, // 5秒后尝试重连
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
})



stompClient.onConnect = (frame) => {
    console.log('STOMP 连接成功', frame);
};

stompClient.onStompError = (frame) => {
    console.error('STOMP 协议错误:', frame);
};

stompClient.onWebSocketError = (event) => {
    console.error('WebSocket 错误:', event);
};

stompClient.onWebSocketClose = (event) => {
    console.warn('🔌 WebSocket 关闭 - Code:', event.code, 'Reason:', event.reason);
    // 添加关闭代码的含义
    const closeMessages = {
        1000: '正常关闭',
        1001: '端点离开',
        1002: '协议错误',
        1003: '不支持的数据类型',
        1006: '异常关闭',
        1011: '服务器错误',
        1012: '服务重启'
    };

    const reason = closeMessages[event.code as keyof typeof closeMessages] || '未知原因';
    console.warn('关闭原因:', reason);

};

stompClient.activate();




app.use(router)

app.use(createPinia())

app.provide(StompClientKey, stompClient)

app.mount('#app')







