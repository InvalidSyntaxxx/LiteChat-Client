/*
 * @Descripttion:
 * @version:
 * @Author: 王远昭
 * @Date: 2023-03-03 14:41:57
 * @LastEditors: 王远昭
 * @LastEditTime: 2023-03-10 22:02:25
 */
/**
 * 启用机器人对话，引入GPT-3
 * 1.发送消息api得到gpt回话
 * 2.机器人用户向群组回话
 */
import axios from 'axios';
export const DEFAULT_CHATGPT_USERID = '26db8ec2-301d-4672-9de2-b6ef074b505f';

export async function PostGPT(messages: Array<Object>): Promise<string> {
  return axios
    .post(
      'https://api.openai.com/v1/chat/completions',
      {
        messages: messages,
        max_tokens: 2048,
        model: 'gpt-3.5-turbo-0301',
      },
      {
        headers: {
          'content-type': 'application/json',
          Authorization: 'Bearer ' + '',
        },
        // timeout: 600000, // 默认值是 `0` (永不超时)
      }
    )
    .then((response) => {
      return regexGPT(response.data['choices'][0]['message']['content']);
    })
    .catch((err) => {
      console.log(err);
      return '';
    });
}
/**
 * ChatGPT回话修改
 */
function regexGPT(answer: string): string {
  answer = answer.replace(/^['?','!','。','.',']/, '');
  return answer;
}

/**
 * ChatGPT向群组回话
 */
export function GPT2Group(socket: SocketIOClient.Socket, Id: string, content: string) {
  socket.emit('groupMessage', {
    userId: DEFAULT_CHATGPT_USERID,
    groupId: Id,
    content: content,
    messageType: 'text',
  });
}

/**
 * ChatGPT向个人回话
 */
export function GPT2User(socket: SocketIOClient.Socket, Id: string, content: string) {
  socket.emit('friendMessage', {
    userId: DEFAULT_CHATGPT_USERID,
    friendId: Id,
    content: content,
    messageType: 'text',
  });
}
