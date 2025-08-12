
<template>
    <div class="flex flex-col items-center justify-center min-h-[80vh]">
        <div class="bg-white/80 shadow-2xl border border-gray-200 rounded-2xl w-full max-w-xl p-10 flex flex-col items-center">
            <div class="font-bold text-3xl text-gray-800 mb-8 tracking-wide">登录页面</div>
            <div class="w-full flex flex-col gap-6">
                <div class="flex flex-col gap-2">
                    <label class="text-lg text-gray-700 font-medium text-left">用户名</label>
                    <input type="text" v-model="nameValue" placeholder="张三" maxlength="10"
                        class="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400 transition w-full bg-white/90" />
                </div>
                <div class="flex flex-col gap-2">
                    <label class="text-lg text-gray-700 font-medium text-left">密码</label>
                    <input type="password" v-model="passwordValue" placeholder="123456" maxlength="30"
                        class="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400 transition w-full bg-white/90" />
                </div>
                <div class="flex gap-4 mt-4">
                    <button @click="loginFunc"
                        class="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg shadow transition">登录</button>
                    <button @click="registerFunc"
                        class="flex-1 bg-green-500 hover:bg-green-600 text-white font-semibold py-2 rounded-lg shadow transition">注册</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, inject } from 'vue';
import type User from '@/interface/user';
import { StompClientKey } from '@/main';
import { Client } from '@stomp/stompjs';
import { computed } from 'vue';
import { useRouter } from 'vue-router';

const nameValue = ref<string>('');
const passwordValue = ref<string>('');
const router = useRouter();
const stompClient = inject(StompClientKey) as Client;

const msg = computed<User>(() => ({
    name: nameValue.value,
    password: passwordValue.value,
}));

/*// 存储数据
localStorage.setItem('key', 'value');

// 获取数据
const value = localStorage.getItem('key'); // 返回字符串或null

// 删除数据
localStorage.removeItem('key');

// 清空所有数据
localStorage.clear(); */

//接收登录的相应
const handleLoginResponce = (resp: any) => {
    if (resp.success) {
        console.log('登录成功')
        localStorage.setItem('user', JSON.stringify(msg.value));
        router.push('/chatweb');
        nameValue.value = '';
        passwordValue.value = '';
        return;
    } else {
        alert("登录失败！请检查你的密码或用户名是否填写错误！")
        return;
    }
}
//接收注册的相应
const handleRegisterResponce = (resp: any) => {
    if (resp.success) {
        console.log('注册成功')
        localStorage.setItem('user', JSON.stringify(msg.value));
        router.push('/chatweb')
        nameValue.value = '';
        passwordValue.value = '';
        return;
    } else {
        alert("已经存在相同名称的用户了")
        return;
    }
}

// 登录按钮按下后的相应函数
const loginFunc = () => {
    if(msg.value.name == "" && msg.value.password == ""){
        return 
    }
    if (!stompClient || !stompClient.connected) {
        console.warn('STOMP 客户端未连接，无法发送消息。');
        return;
    }
    //监听服务器该路由
    const subscription = stompClient.subscribe('/topic/login-response', (message) => {
        const resp = JSON.parse(message.body);
        console.log(resp)
        handleLoginResponce(resp);
        subscription.unsubscribe();
    })
    stompClient.publish({
        destination: '/app/login',
        body: JSON.stringify(msg.value),
        headers: { 'content-type': 'application/json' }
    })
    console.log('已经发送')
}

// 注册按钮按下后的相应函数
const registerFunc = () => {
    if(msg.value.name == "" && msg.value.password == ""){
        return 
    }
    
    if (!stompClient || !stompClient.connected) {
        console.warn('STOMP 客户端未连接，无法发送消息。');
        return;
    }
    const subscription = stompClient.subscribe('/topic/register-response', (message) => {
        const resp = JSON.parse(message.body);
        handleRegisterResponce(resp);
        subscription.unsubscribe();
    })
    stompClient.publish({
        destination: '/app/register',
        body: JSON.stringify(msg.value),
        headers: { 'content-type': 'application/json' }
    })
}


// onMounted(() => {

// });


// onUnmounted(() => {

// })
</script>

<style scoped>
</style>
