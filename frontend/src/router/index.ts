import { createRouter, createWebHistory } from "vue-router";


import Main from "@/components/Main.vue";
import Login from "@/components/Login.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: (to) => {
                const user = localStorage.getItem('user');
                return user ? '/chatweb' : '/login'
            }
        },
        {
            path: '/chatweb',
            component: Main,
            meta: { requiresAuth: true }
        },
        {
            path: '/login',
            component: Login
        }
    ]
})

router.beforeEach((to, from, next) => {
    // 检查即将进入的路由是否需要登录
    if (to.meta.requiresAuth) {
        // 从 localStorage 获取并尝试解析用户数据
        const userString = localStorage.getItem('user');
        let isValidUser = false;

        // 只有当 userString 存在时才尝试解析
        if (userString) {
            try {
                const user = JSON.parse(userString);
                // 检查解析出的对象是否有效（非空对象）
                if (user && Object.keys(user).length > 0) {
                    isValidUser = true;
                }
            } catch (e) {
                // 解析失败则 isValidUser 保持为 false
                console.error('解析 localStorage 中的用户数据失败', e);
            }
        }

        // 根据最终判断结果决定路由走向
        if (isValidUser) {
            // 用户已登录且数据有效，允许跳转
            next();
        } else {
            // 未登录或数据无效，重定向到登录页
            console.log('未登录或用户数据无效，重定向到登录页');
            next('/login');
        }
    } else {
        // 如果路由不需要登录，直接跳转
        next();
    }
});

export default router
