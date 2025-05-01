<template>
    <div class="centered-div">
        <div class="title">聊天室</div>
        <div class="horizontal-line" style="margin-top: 16px;"></div>
        <div class="chat">
            <div class="people"></div>
            <div class="vertical-line"></div>
            <div class="chatbox">
                <div class="chatbox-content"></div>
                <div class="horizontal-line"></div>
                <div class="submitbox">
                <form @submit.prevent="sendMessage"> <!-- 阻止默认提交行为 -->
                    <input type="text" v-model="inputMessage" placeholder="请输入消息" class="input">
                    <button type="submit" class="submit">发送</button>
                </form>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const socket = new WebSocket('ws://localhost:8080/chat');
const inputMessage = ref('');
const messages = ref<string[]>([]); 

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    socket.send(inputMessage.value);
    inputMessage.value = '';
  }
};

socket.onmessage = (event) => {
  messages.value.push(event.data);
};
</script>

<style scoped>
.centered-div{
    background-color: azure;
    border : 5px solid #000;
    border-radius: 10px;
    text-align: center;
    border-color: black;
	width: 1000px;
	height: 900px;
	margin: 0 auto; /*让div水平居中*/
	position: absolute;
    left: 0;
    right: 0;
    display: flex;
    flex-direction: column; /* 垂直排列子元素 */
}
.title{
    margin-top: 16px;
    font-size: 32px;
}
.horizontal-line{
    width: 100%;
    height: 3px;
    background-color: black;
}
.vertical-line{
    width: 3px;
    height: 100%;
    background-color: black;
}
.chat{
    display: flex;
    flex: 1;
}
.people{
    width: 200px;
    background-color: #f0f0f0;
    border-bottom-left-radius: 5px;
}
.chatbox{
    flex: 1;
    background-color: #f0f0f0;
    border-bottom-right-radius: 5px;
    display: flex;
    flex-direction: column; /* 垂直排列子元素 */
}
.chatbox-content{
    width: 100%;
    min-height: 600px;
}
.submitbox{
    flex: 1;
}
.input{
    width: 600px;
    height: 150px;
    border-radius: 5px;
    border: 1px solid #ccc;
    padding: 10px;
    box-sizing: border-box; /* 包括内边距和边框在内的宽度计算 */
    margin: 16px auto;
}
.submit{
    height: 150px;
    width: 100px;
}
</style>
