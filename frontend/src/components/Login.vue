<template>
    <div class="centered-div">
        <div class="title">
            登录页面
        </div>
        <div class="login-box">
            <div class="input">
                用户名：
                <input type="text" v-model="nameValue" placeholder="张三" maxlength="10" />
                密码：
                <input type="password" v-model="passwordValue" placeholder="123456" maxlength="30">
            </div>
            <button @click="loginFunc">登录</button>
            <button @click="registerFunc">注册</button>
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
        router.push('/chat');
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
        router.push('/chat')
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

.login-box {
    background-color: azure;
    border: 5px solid #000;
    border-radius: 10px;
    text-align: center;
    border-color: black;
    width: 500px;
    height: 450px;
    margin: 1rem auto;
    /*让div水平居中*/
    left: 0;
    right: 0;
    display: flex;
    flex-direction: column;
    /* 垂直排列子元素 */
}

.input {
    width: fit-content + 10px;
    margin: 1rem auto;
}
</style>
