/*
 * @Descripttion: 
 * @version: 
 * @Author: 王远昭
 * @Date: 2023-02-25 12:17:23
 * @LastEditors: 王远昭
 * @LastEditTime: 2023-03-01 20:03:52
 */
import axios, { AxiosInstance } from 'axios';
import { requestSuccess, requestFail, responseSuccess, responseFail } from './interceptors';
import { socketUrl } from './config';

const fetch: AxiosInstance = axios.create({
  timeout: 30000, // 超时时间30s
  baseURL: socketUrl,
  headers: {
    'Cache-Control': 'no-cache',
    Pragma: 'no-cache',
  },
});

fetch.interceptors.request.use(requestSuccess, requestFail);
fetch.interceptors.response.use(responseSuccess, responseFail);

export default fetch;
