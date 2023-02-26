/*
 * @Descripttion: 
 * @version: 
 * @Author: 王远昭
 * @Date: 2023-02-25 12:17:23
 * @LastEditors: 王远昭
 * @LastEditTime: 2023-02-26 14:36:13
 */
import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'Chat',
    component: () => import('@/views/GenalChat.vue'),
  },
];

const router = new VueRouter({
  mode: 'hash', // HbuilderX 打包APP空白屏
  // mode: 'history', // 网页端
  base: process.env.BASE_URL,
  routes,
});

export default router;
