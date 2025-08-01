import { createRouter, createWebHistory } from "vue-router";


import Main from "@/components/Main.vue";
import Login from "@/components/Login.vue";

const router = createRouter({
    history : createWebHistory(),
    routes:[
        {
            path:'/',
            redirect:(to) =>{
                const user = localStorage.getItem('user');
                return user ? '/chat' : '/login'
            }
        },
        {
            path:'/chat',
            component:Main
        },
        {
            path:'/login',
            component:Login
        }
    ]
})

export default router